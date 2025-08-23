package net.aincraft;

import java.util.List;

public interface PreferenceProvider {

  String getNamespace();

  List<Preference<?>> getPreferences();
}
