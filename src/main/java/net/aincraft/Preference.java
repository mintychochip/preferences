package net.aincraft;

import net.kyori.adventure.key.Key;
import org.jetbrains.annotations.NotNull;

public interface Preference<T> {

  PreferenceType<T> getType();

  String getName();

  @NotNull
  T getDefault();

  interface Key<T> {

    net.kyori.adventure.key.Key getKey();
  }
}
