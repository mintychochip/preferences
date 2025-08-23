package net.aincraft;

import java.util.HashMap;
import java.util.Map;
import net.kyori.adventure.key.Key;

public class Registry {

  private final Map<Key, Preference<?>> preferences = new HashMap<>();
}
