package net.aincraft.types;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import net.aincraft.PreferenceType;

public class EnumReferenceType<E extends Enum<E>> implements PreferenceType<E> {

  private final Class<E> clazz;

  public EnumReferenceType(Class<E> clazz) {
    this.clazz = clazz;
  }

  @Override
  public E parse(String input) {
    return Enum.valueOf(clazz, input.toUpperCase(Locale.ENGLISH));
  }

  @Override
  public String toValue(Object object) {
    E e = (E) object;
    return e.name().toLowerCase(Locale.ENGLISH);
  }

  @Override
  public List<String> suggestValues() {
    return Arrays.stream(clazz.getEnumConstants())
        .map(e -> e.toString().toLowerCase(Locale.ENGLISH)).toList();
  }
}
