package net.aincraft;

import com.google.inject.Inject;
import com.google.inject.Provider;
import net.aincraft.config.YamlConfiguration;
import net.aincraft.repository.ConnectionSourceFactory;
import net.aincraft.repository.PreferenceRepository;
import org.bukkit.plugin.Plugin;

public class PreferenceRepositoryProvider implements Provider<PreferenceRepository> {

  private final Plugin plugin;

  @Inject
  public PreferenceRepositoryProvider(Plugin plugin) {
    this.plugin = plugin;
  }

  @Override
  public PreferenceRepository get() {
    YamlConfiguration config = YamlConfiguration.create(plugin, "config.yml");
    ConnectionSourceFactory factory = new ConnectionSourceFactory(plugin, config);
    return WriteBackPreferenceRepositoryImpl.create(plugin,
        new RelationalPreferenceRepositoryImpl(factory.create()));
  }
}
