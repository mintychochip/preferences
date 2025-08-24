package net.aincraft;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface Preference<T> {

  PreferenceType<T> getType();

  String getName();

  @Nullable
  default String getDescription() {
    return null;
  }

  @NotNull
  T getDefault();

  interface Key<T> {

    net.kyori.adventure.key.Key getKey();
  }

  interface Builder {

  }
}
