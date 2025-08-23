package net.aincraft;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import java.util.HashMap;
import java.util.Map;
import net.aincraft.commands.CommandModule;
import net.aincraft.registry.RegistryModule;
import net.aincraft.repository.PreferenceRepository;
import net.kyori.adventure.key.Key;
import org.bukkit.plugin.Plugin;

public class PluginModule extends AbstractModule {

  private final Plugin plugin;

  public PluginModule(Plugin plugin) {
    this.plugin = plugin;
  }

  @Override
  protected void configure() {
    bind(PreferenceService.class).to(PreferenceServiceImpl.class).in(Singleton.class);
    bind(Plugin.class).toInstance(plugin);
    bind(PreferenceRepository.class).toProvider(PreferenceRepositoryProvider.class)
        .in(Singleton.class);
    install(new RegistryModule());
    install(new CommandModule());
  }
}
