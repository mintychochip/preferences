package net.aincraft;

import com.google.common.base.Preconditions;
import com.google.inject.Inject;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import net.aincraft.registry.PreferenceRegistry;
import net.aincraft.repository.PreferenceRepository;
import net.aincraft.repository.PreferenceRepository.PreferenceRecord;
import net.kyori.adventure.key.Key;
import org.bukkit.NamespacedKey;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

public class PreferenceServiceImpl implements PreferenceService {

  private final PreferenceRegistry registry;
  private final PreferenceRepository repository;

  @Inject
  public PreferenceServiceImpl(PreferenceRegistry registry,
      PreferenceRepository repository) {
    this.registry = registry;
    this.repository = repository;
  }

  @Override
  public <T> @NotNull T getPreference(UUID playerId, Preference.Key<T> preferenceKey)
      throws IllegalStateException, ExecutionException {
    Key key = preferenceKey.getKey();
    Preconditions.checkState(registry.isRegistered(key));
    @SuppressWarnings("unchecked")
    Preference<T> preference = (Preference<T>) registry.getOrThrow(key);
    Optional<PreferenceRecord> record = Optional.ofNullable(
        repository.load(playerId.toString(), key.toString()));
    PreferenceType<T> type = preference.getType();
    return record.map(r -> type.parse(r.value())).orElse(preference.getDefault());
  }

  @Override
  public <T> Preference.Key<T> register(Plugin plugin, Preference<T> preference) {
    Key key = new NamespacedKey(plugin, preference.getName().toLowerCase(Locale.ENGLISH));
    registry.register(key, preference);
    return () -> key;
  }
}
