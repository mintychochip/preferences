package net.aincraft;

import java.util.UUID;
import java.util.concurrent.ExecutionException;
import org.jetbrains.annotations.NotNull;

public interface PreferenceService {

  @NotNull
  <T> T getPreference(UUID playerId, PreferenceKey<T> preferenceKey)
      throws IllegalStateException, ExecutionException;

  <T> PreferenceKey<T> register(String namespace, Preference<T> preference) throws IllegalArgumentException;
}
