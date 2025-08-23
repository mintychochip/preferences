package net.aincraft;

import net.kyori.adventure.key.Key;

record PreferenceKeyImpl<T>(PreferenceType<T> type, Key key) implements PreferenceKey<T> {

}
