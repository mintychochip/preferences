package net.aincraft.types;

import net.aincraft.PreferenceType;
import net.kyori.adventure.bossbar.BossBar;
import org.bukkit.Material;
import org.bukkit.Registry;
import org.bukkit.entity.EntityType;

public class PreferenceTypes {

  public static final PreferenceType<EntityType> ENTITY_TYPE = new BukkitRegistryType<>(
      Registry.ENTITY_TYPE);

  public static final PreferenceType<Material> MATERIAL = new BukkitRegistryType<>(
      Registry.MATERIAL);

  public static final PreferenceType<BossBar.Color> BOSS_BAR_COLOR = new EnumReferenceType<>(
      BossBar.Color.class);

}
