package net.aincraft.types;

import java.util.List;
import java.util.function.Function;
import net.aincraft.PreferenceType;
import net.kyori.adventure.bossbar.BossBar;
import org.bukkit.Material;
import org.bukkit.Registry;
import org.bukkit.entity.EntityType;

public class PreferenceTypes {

  public static final PreferenceType<EntityType> ENTITY_TYPE = new BukkitEnumReferenceType<>(
      Registry.ENTITY_TYPE);

  public static final PreferenceType<Material> MATERIAL = new BukkitEnumReferenceType<>(
      Registry.MATERIAL);

  public static final PreferenceType<BossBar.Color> BOSS_BAR_COLOR = new EnumReferenceType<>(
      BossBar.Color.class);
}
