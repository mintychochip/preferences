package net.aincraft.commands;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.command.brigadier.argument.ArgumentTypes;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import net.aincraft.Preference;
import net.aincraft.repository.PreferenceRepository;
import net.aincraft.PreferenceType;
import net.aincraft.repository.PreferenceRepository.PreferenceRecord;
import net.kyori.adventure.key.Key;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class GetCommand implements Command {

  private final Map<Key, Preference<?>> preferences;
  private final PreferenceRepository repository;

  public GetCommand(Map<Key, Preference<?>> preferences, PreferenceRepository repository) {
    this.preferences = preferences;
    this.repository = repository;
  }

  @Override
  public LiteralArgumentBuilder<CommandSourceStack> build() {
    return Commands.literal("get")
        .then(Commands.argument("pref", ArgumentTypes.key()).executes(context -> {
          CommandSourceStack source = context.getSource();
          CommandSender sender = source.getSender();
          Key preferenceKey = context.getArgument("pref",Key.class);
          if (sender instanceof Player player) {
            try {
              PreferenceRecord record =repository.load(player.getUniqueId().toString(),
                  preferenceKey.toString());
              String value = record.value();
              Preference<?> preference = preferences.get(preferenceKey);
              if (value != null) {
                Object parse = preference.getType().parse(value);
                Bukkit.broadcastMessage(parse.toString());
                player.sendMessage("value is: " + value);
              } else {
                PreferenceType<?> type = preference.getType();
                String string = type.toValue(preference.getDefault());
                Bukkit.broadcastMessage(string);
              }
            } catch (ExecutionException e) {
              throw new RuntimeException(e);
            }

          }
          return 1;
        }).suggests((context, builder) -> {
          preferences.keySet().stream().map(Object::toString).forEach(builder::suggest);
          return builder.buildFuture();
        }));
  }
}