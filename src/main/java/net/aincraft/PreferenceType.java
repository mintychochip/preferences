package net.aincraft;

import java.util.List;

public interface PreferenceType<T> {

  T parse(String input);

  String toValue(Object object);

  List<String> suggestValues();
}
