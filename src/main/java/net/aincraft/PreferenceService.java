package net.aincraft;

import java.util.UUID;
import java.util.concurrent.ExecutionException;
import net.kyori.adventure.key.Key;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

public interface PreferenceService {

  @NotNull
  <T> T getPreference(UUID playerId, Preference.Key<T> preferenceKey)
      throws IllegalStateException, ExecutionException;

  <T> Preference.Key<T> register(Plugin plugin, Preference<T> preference);
}
