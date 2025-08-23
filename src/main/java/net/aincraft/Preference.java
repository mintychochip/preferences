package net.aincraft;

import net.kyori.adventure.key.Key;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface Preference<T> {

  PreferenceType<T> getType();

  String getName();

  @NotNull
  T getDefault();
}
