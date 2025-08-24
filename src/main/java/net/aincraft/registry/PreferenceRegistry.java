package net.aincraft.registry;

import java.util.Optional;
import java.util.Set;
import net.aincraft.Preference;
import net.kyori.adventure.key.Key;
import org.jetbrains.annotations.ApiStatus.Internal;
import org.jetbrains.annotations.NotNull;

@Internal
public interface PreferenceRegistry {

  boolean isRegistered(Key key);

  void register(Key key, Preference<?> preference);

  @NotNull
  Preference<?> getOrThrow(Key key) throws IllegalArgumentException;

  @NotNull
  Optional<Preference<?>> get(Key key);

  Set<Key> keySet();

}
