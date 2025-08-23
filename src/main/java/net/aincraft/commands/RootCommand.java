package net.aincraft.commands;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;

public class RootCommand implements Command {

  private final GetCommand getCommand;
  private final SetCommand setCommand;

  public RootCommand(GetCommand getCommand, SetCommand setCommand) {
    this.getCommand = getCommand;
    this.setCommand = setCommand;
  }

  @Override
  public LiteralArgumentBuilder<CommandSourceStack> build() {
    return Commands.literal("prefs").then(setCommand.build()).then(getCommand.build());
  }
}
