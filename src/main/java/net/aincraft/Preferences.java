package net.aincraft;

import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import java.util.HashMap;
import java.util.Map;
import net.aincraft.commands.GetCommand;
import net.aincraft.commands.RootCommand;
import net.aincraft.commands.SetCommand;
import net.aincraft.config.YamlConfiguration;
import net.aincraft.repository.ConnectionSourceFactory;
import net.aincraft.types.EnumReferenceType;
import net.aincraft.types.PreferenceTypes;
import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.bossbar.BossBar.Color;
import net.kyori.adventure.key.Key;
import org.bukkit.Material;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

public class Preferences extends JavaPlugin {

  @Override
  public void onEnable() {
    Map<Key, Preference<?>> preferences = new HashMap<>();
    YamlConfiguration configuration = YamlConfiguration.create(this, "config.yml");
    ConnectionSourceFactory connectionSourceFactory = new ConnectionSourceFactory(this,
        configuration);
    RelationalPreferenceRepositoryImpl store = new RelationalPreferenceRepositoryImpl(
        connectionSourceFactory.create());
    PreferenceServiceImpl service = new PreferenceServiceImpl(preferences, store);
    PreferenceKey<Material> key = service.register("modular_jobs", new Example());
    PreferenceKey<Color> key2 = service.register("preferences", new BossBarColor());
    getLifecycleManager().registerEventHandler(LifecycleEvents.COMMANDS,
        c -> c.registrar().register(new RootCommand(new GetCommand(preferences, store),
            new SetCommand(preferences, store)).build().build()));
  }

  static final class Example implements Preference<Material> {

    @Override
    public PreferenceType<Material> getType() {
      return PreferenceTypes.MATERIAL;
    }

    @Override
    public String getName() {
      return "example";
    }

    @Override
    public @NotNull Material getDefault() {
      return Material.STONE;
    }
  }

  static final class BossBarColor implements Preference<BossBar.Color> {

    @Override
    public PreferenceType<BossBar.Color> getType() {
      return new EnumReferenceType<>(BossBar.Color.class);
    }

    @Override
    public String getName() {
      return "bossbar-color";
    }

    @Override
    public @NotNull BossBar.Color getDefault() {
      return BossBar.Color.BLUE;
    }
  }
}
