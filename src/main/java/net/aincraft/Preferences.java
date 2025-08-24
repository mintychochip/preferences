package net.aincraft;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.TypeLiteral;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import net.aincraft.types.PreferenceTypes;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

public final class Preferences extends JavaPlugin {

  @Override
  public void onEnable() {
    Injector injector = Guice.createInjector(new PluginModule(this));
    Bukkit.getServicesManager()
        .register(PreferenceService.class, injector.getInstance(PreferenceService.class), this,
            ServicePriority.High);
    LiteralArgumentBuilder<CommandSourceStack> sourceStack = injector.getInstance(
        Key.get(new TypeLiteral<>() {
        }));
    PreferenceService service = injector.getInstance(PreferenceService.class);
    service.register(this, new Preference<Material>() {
      @Override
      public PreferenceType<Material> getType() {
        return PreferenceTypes.MATERIAL;
      }

      @Override
      public String getName() {
        return "test";
      }

      @Override
      public @NotNull Material getDefault() {
        return Material.STONE;
      }
    });
    getLifecycleManager().registerEventHandler(LifecycleEvents.COMMANDS,
        c -> c.registrar().register(sourceStack.build()));
  }
}
