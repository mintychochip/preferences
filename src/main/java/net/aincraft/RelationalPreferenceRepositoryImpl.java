package net.aincraft;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Duration;
import java.util.concurrent.ExecutionException;
import net.aincraft.repository.ConnectionSource;
import net.aincraft.repository.PreferenceRepository;
import org.jetbrains.annotations.Nullable;

public final class RelationalPreferenceRepositoryImpl implements PreferenceRepository {


  private final Cache<String, PreferenceRecord> readCache = CacheBuilder.newBuilder()
      .expireAfterAccess(
          Duration.ofMinutes(10)).maximumSize(1_000).build();

  private final ConnectionSource connectionSource;

  public RelationalPreferenceRepositoryImpl(ConnectionSource connectionSource) {
    this.connectionSource = connectionSource;
  }

  @Override
  public @Nullable PreferenceRecord load(String playerId, String preferenceKey)
      throws ExecutionException {
    String key = playerId + preferenceKey;
    return readCache.get(key, () -> {
      try (Connection connection = connectionSource.getConnection();
          PreparedStatement ps = connection.prepareStatement(
              "SELECT preference_value FROM preferences WHERE player_id=? AND preference_key=?;")) {
        ps.setString(1, playerId);
        ps.setString(2, preferenceKey);
        try (ResultSet rs = ps.executeQuery()) {
          if (!rs.next()) {
            return null;
          }
          String value = rs.getString("preference_value");
          return new PreferenceRecord(playerId, preferenceKey, value);
        }
      } catch (SQLException e) {
        throw new RuntimeException(e);
      }
    });
  }

  @Override
  public void save(PreferenceRecord record) {
    try (Connection connection = connectionSource.getConnection();
        PreparedStatement ps = connection.prepareStatement(
            "INSERT INTO preferences (player_id,preference_key,preference_value) VALUES (?,?,?) ON CONFLICT (player_id,preference_key) DO UPDATE SET preference_value = excluded.preference_value;")) {
      ps.setString(1, record.playerId());
      ps.setString(2, record.preferenceKey());
      ps.setString(3, record.value());
      ps.executeUpdate();
      readCache.put(record.playerId() + record.preferenceKey(), record);
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public void delete(String playerId, String preferenceKey) {
    try (Connection connection = connectionSource.getConnection();
        PreparedStatement ps = connection.prepareStatement(
            "DELETE FROM preferences WHERE player_id=? AND preference_key=?;")) {
      ps.setString(1, playerId);
      ps.setString(2, preferenceKey);
      ps.executeUpdate();
      readCache.invalidate(playerId + preferenceKey);
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }
}
