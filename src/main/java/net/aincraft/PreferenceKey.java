package net.aincraft;

import net.kyori.adventure.key.Key;

public sealed interface PreferenceKey<T> permits PreferenceKeyImpl {

  PreferenceType<T> type();

  Key key();
}
