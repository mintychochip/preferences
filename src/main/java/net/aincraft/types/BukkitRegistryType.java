package net.aincraft.types;

import java.util.List;
import net.aincraft.PreferenceType;
import org.bukkit.Keyed;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;

public final class BukkitRegistryType<E extends Enum<E> & Keyed> implements PreferenceType<E> {

  private final Registry<E> registry;
  private final List<String> suggestionValues;

  public BukkitRegistryType(Registry<E> registry) {
    this.registry = registry;
    this.suggestionValues = registry.stream().map(e -> e.key().toString()).toList();
  }

  @Override
  public E parse(String input) {
    NamespacedKey key = NamespacedKey.fromString(input);
    return registry.get(key);
  }

  @Override
  public String toValue(Object object) {
    E e = (E) object;
    return e.key().toString();
  }

  @Override
  public List<String> suggestValues() {
    return suggestionValues;
  }
}
