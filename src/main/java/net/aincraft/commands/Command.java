package net.aincraft.commands;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import io.papermc.paper.command.brigadier.CommandSourceStack;

public interface Command {

  LiteralArgumentBuilder<CommandSourceStack> build();
}
