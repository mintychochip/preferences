package net.aincraft.registry;

import com.google.common.base.Preconditions;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import net.aincraft.Preference;
import net.aincraft.PreferenceProvider;
import net.kyori.adventure.key.Key;
import org.bukkit.NamespacedKey;
import org.jetbrains.annotations.NotNull;

public class PreferenceRegistryImpl implements PreferenceRegistry {

  private final Map<Key, Preference<?>> registeredPreferences = new HashMap<>();

  @Override
  public boolean isRegistered(Key key) {
    return registeredPreferences.containsKey(key);
  }

  @Override
  public void register(Key key, Preference<?> preference) {
    registeredPreferences.put(key, preference);
  }

  @Override
  public @NotNull Preference<?> getOrThrow(Key key) throws IllegalArgumentException {
    Preconditions.checkArgument(isRegistered(key));
    return registeredPreferences.get(key);
  }

  @Override
  public @NotNull Optional<Preference<?>> get(Key key) {
    return Optional.ofNullable(registeredPreferences.get(key));
  }

  @Override
  public Set<Key> keySet() {
    return registeredPreferences.keySet();
  }
}
