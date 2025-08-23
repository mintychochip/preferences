package net.aincraft.commands;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.multibindings.Multibinder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import java.util.Set;

public class CommandModule extends AbstractModule {

  @Override
  protected void configure() {
    Multibinder<Command> binder = Multibinder.newSetBinder(binder(), Command.class);
    binder.addBinding().to(GetCommand.class);
    binder.addBinding().to(SetCommand.class);
  }

  @Provides
  @Singleton
  public LiteralArgumentBuilder<CommandSourceStack> root(Set<Command> commands) {
    LiteralArgumentBuilder<CommandSourceStack> root = Commands.literal("prefs");
    for (Command command : commands) {
      root.then(command.build());
    }
    return root;
  }
}
