package net.aincraft.commands;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.command.brigadier.argument.ArgumentTypes;
import java.util.List;
import java.util.Map;
import net.aincraft.Preference;
import net.aincraft.repository.PreferenceRepository;
import net.aincraft.repository.PreferenceRepository.PreferenceRecord;
import net.kyori.adventure.key.Key;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SetCommand implements Command {

  private final Map<Key, Preference<?>> preferences;
  private final PreferenceRepository repository;

  public SetCommand(Map<Key, Preference<?>> preferences, PreferenceRepository store) {
    this.preferences = preferences;
    this.repository = store;
  }


  @Override
  public LiteralArgumentBuilder<CommandSourceStack> build() {
    return Commands.literal("set")
        .then(
            Commands.argument("pref", ArgumentTypes.key())
                .suggests((context, builder) -> {
                  preferences.keySet()
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

                          Preference<?> preference = preferences.get(pref);
                          if (preference == null) {
                            return builder.buildFuture();
                          }

                          String partialInput = builder.getRemaining(); // User's current partial input
                          List<String> suggestions = preference.getType().suggestValues();
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
