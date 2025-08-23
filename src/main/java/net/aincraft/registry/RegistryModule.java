package net.aincraft.registry;

import com.google.inject.AbstractModule;
import com.google.inject.Singleton;

public class RegistryModule extends AbstractModule {

  @Override
  protected void configure() {
    bind(PreferenceRegistry.class).to(PreferenceRegistryImpl.class).in(Singleton.class);
  }
}
