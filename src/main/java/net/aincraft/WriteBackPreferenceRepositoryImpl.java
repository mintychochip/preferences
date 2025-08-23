package net.aincraft;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import io.papermc.paper.threadedregions.scheduler.ScheduledTask;
import java.sql.SQLException;
import java.time.Duration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import net.aincraft.repository.PreferenceRepository;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.Nullable;

public final class WriteBackPreferenceRepositoryImpl implements PreferenceRepository {

  private final PreferenceRepository delegate;
  private final Cache<FlatKey, PreferenceRecord> readCache = CacheBuilder.newBuilder()
      .expireAfterAccess(
          Duration.ofMinutes(1)).maximumSize(100).build();
  private final Map<FlatKey, PreferenceRecord> pendingUpserts = new ConcurrentHashMap<>();
  private final Set<FlatKey> pendingDeletes = ConcurrentHashMap.newKeySet();
  private final int batch = 50;
  private final AtomicBoolean flushing = new AtomicBoolean(false);

  private record FlatKey(String playerId, String preferenceKey) {

  }

  private WriteBackPreferenceRepositoryImpl(PreferenceRepository delegate) {
    this.delegate = delegate;
  }

  public static PreferenceRepository create(Plugin plugin,
      PreferenceRepository delegate) {
    WriteBackPreferenceRepositoryImpl repository = new WriteBackPreferenceRepositoryImpl(delegate);
    Bukkit.getAsyncScheduler().runAtFixedRate(plugin, (ScheduledTask) -> {
      repository.flush();
    }, 0L, 200L, TimeUnit.SECONDS);
    return repository;
  }

  @Override
  public @Nullable PreferenceRecord load(String playerId, String preferenceKey) {
    FlatKey key = new FlatKey(playerId, preferenceKey);
    if (pendingDeletes.contains(key)) {
      return null;
    }
    PreferenceRecord record = pendingUpserts.get(key);
    if (record != null) {
      return record;
    }
    try {
      return readCache.get(key, () -> delegate.load(playerId, preferenceKey));
    } catch (ExecutionException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public void save(PreferenceRecord record) {
    FlatKey key = new FlatKey(record.playerId(), record.preferenceKey());
    pendingDeletes.remove(key);
    pendingUpserts.put(key, record);
    readCache.put(key, record);
  }

  @Override
  public void delete(String playerId, String preferenceKey) {
    FlatKey key = new FlatKey(playerId, preferenceKey);
    pendingDeletes.add(key);
    pendingUpserts.remove(key);
    readCache.invalidate(key);
  }

  public void flush() {
    if (!flushing.compareAndSet(false, true)) {
      return;
    }
    Map<FlatKey, PreferenceRecord> upserts = new HashMap<>();
    Iterator<Entry<FlatKey, PreferenceRecord>> upsertIterator = pendingUpserts.entrySet()
        .iterator();
    while (upsertIterator.hasNext() && upserts.size() < batch) {
      Entry<FlatKey, PreferenceRecord> entry = upsertIterator.next();
      FlatKey key = entry.getKey();
      PreferenceRecord value = entry.getValue();
      if (pendingUpserts.remove(key, value)) {
        upserts.put(key, value);
      }
    }
    Set<FlatKey> deletes = new HashSet<>();
    Iterator<FlatKey> keyIterator = pendingDeletes.iterator();
    while (keyIterator.hasNext() && deletes.size() < batch) {
      FlatKey key = keyIterator.next();
      if (pendingDeletes.remove(key)) {
        deletes.add(key);
      }
    }
    try {
      if (!upserts.isEmpty()) {
        upserts.forEach((k, v) -> delegate.save(v));
      }
      if (!deletes.isEmpty()) {
        deletes.forEach(d -> delegate.delete(d.playerId, d.preferenceKey));
      }
    } catch (Throwable t) {
      upserts.forEach(pendingUpserts::putIfAbsent);
      pendingDeletes.addAll(deletes);
    } finally {
      flushing.set(false);
    }
  }
}
