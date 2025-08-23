package net.aincraft.repository;

import java.util.concurrent.ExecutionException;
import org.jetbrains.annotations.Nullable;

public interface PreferenceRepository {

  @Nullable
  PreferenceRecord load(String playerId, String preferenceKey) throws ExecutionException;

  void save(PreferenceRecord record);

  void delete(String playerId, String preferenceKey);

  record PreferenceRecord(String playerId, String preferenceKey, String value) {

  }
}
