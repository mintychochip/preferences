package net.aincraft.commands;

import com.google.inject.Inject;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.command.brigadier.argument.ArgumentTypes;
import java.util.List;
import java.util.Optional;
import net.aincraft.Preference;
import net.aincraft.registry.PreferenceRegistry;
import net.aincraft.repository.PreferenceRepository;
import net.aincraft.repository.PreferenceRepository.PreferenceRecord;
import net.kyori.adventure.key.Key;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SetCommand implements Command {

  private final PreferenceRegistry preferenceRegistry;
  private final PreferenceRepository repository;

  @Inject
  public SetCommand(PreferenceRegistry preferenceRegistry, PreferenceRepository store) {
    this.preferenceRegistry = preferenceRegistry;
    this.repository = store;
  }


  @Override
  public LiteralArgumentBuilder<CommandSourceStack> build() {
    return Commands.literal("set")
        .then(
            Commands.argument("pref", ArgumentTypes.key())
                .suggests((context, builder) -> {
                  preferenceRegistry.keySet()
                      .stream()
                      .map(Key::asString)
                      .forEach(builder::suggest);
                  return builder.buildFuture();
                })
                .then(
                    Commands.argument("value", StringArgumentType.greedyString())
                        .suggests((context, builder) -> {
                          // Parse pref argument
                          Key pref = context.getArgument("pref", Key.class);

                          Optional<Preference<?>> preference = preferenceRegistry.get(pref);
                          if (preference.isEmpty()) {
                            return builder.buildFuture();
                          }
                          Preference<?> preference1 = preference.get();
                          String partialInput = builder.getRemaining(); // User's current partial input
                          List<String> suggestions = preference1.getType().suggestValues();
                          suggestions.forEach(builder::suggest);

                          return builder.buildFuture();
                        })
                        .executes(ctx -> {
                          CommandSourceStack source = ctx.getSource();
                          CommandSender sender = source.getSender();
                          if (!(sender instanceof Player player)) {
                            return 0;
                          }
                          Key pref = ctx.getArgument("pref", Key.class);
                          String value = ctx.getArgument("value", String.class);
                          repository.save(
                              new PreferenceRecord(player.getUniqueId().toString(), pref.toString(),
                                  value));
                          player.sendMessage("successfully set");
                          return 1;
                        })
                )
        );
  }

}
