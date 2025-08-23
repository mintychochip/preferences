package net.aincraft;

import com.google.common.base.Preconditions;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import net.aincraft.repository.PreferenceRepository;
import net.aincraft.repository.PreferenceRepository.PreferenceRecord;
import net.kyori.adventure.key.Key;
import org.bukkit.NamespacedKey;
import org.jetbrains.annotations.NotNull;

public class PreferenceServiceImpl implements PreferenceService {

  private final Map<Key, Preference<?>> preferences;
  private final PreferenceRepository repository;

  public PreferenceServiceImpl(Map<Key, Preference<?>> preferences,
      PreferenceRepository repository) {
    this.preferences = preferences;
    this.repository = repository;
  }

  @Override
  public <T> @NotNull T getPreference(UUID playerId, PreferenceKey<T> preferenceKey)
      throws IllegalStateException, ExecutionException {
    Key key = preferenceKey.key();
    Preconditions.checkState(preferences.containsKey(key));
    @SuppressWarnings("unchecked")
    Preference<T> preference = (Preference<T>) preferences.get(key);
    Optional<PreferenceRecord> record = Optional.ofNullable(
        repository.load(playerId.toString(), key.toString()));
    PreferenceType<T> type = preference.getType();
    return record.map(r -> type.parse(r.value())).orElse(preference.getDefault());
  }

  @Override
  public <T> PreferenceKey<T> register(String namespace, Preference<T> preference)
      throws IllegalArgumentException {
    Key key = NamespacedKey.fromString(namespace + ":" + preference.getName());
    if (key == null) {
      throw new IllegalArgumentException(
          "illegally formatted key: " + namespace + " " + preference.getName());
    }
    preferences.put(key, preference);
    return new PreferenceKeyImpl<>(preference.getType(), key);
  }
}
