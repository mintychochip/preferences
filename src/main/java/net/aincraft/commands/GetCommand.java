package net.aincraft.commands;

import com.google.inject.Inject;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.command.brigadier.argument.ArgumentTypes;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import net.aincraft.Preference;
import net.aincraft.PreferenceType;
import net.aincraft.registry.PreferenceRegistry;
import net.aincraft.repository.PreferenceRepository;
import net.aincraft.repository.PreferenceRepository.PreferenceRecord;
import net.kyori.adventure.key.Key;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class GetCommand implements Command {

  private final PreferenceRegistry preferenceRegistry;
  private final PreferenceRepository repository;

  @Inject
  public GetCommand(PreferenceRegistry preferenceRegistry, PreferenceRepository repository) {
    this.preferenceRegistry = preferenceRegistry;
    this.repository = repository;
  }

  @Override
  public LiteralArgumentBuilder<CommandSourceStack> build() {
    return Commands.literal("get")
        .then(Commands.argument("pref", ArgumentTypes.key()).executes(context -> {
          CommandSourceStack source = context.getSource();
          CommandSender sender = source.getSender();
          Key preferenceKey = context.getArgument("pref", Key.class);
          if (sender instanceof Player player) {
            try {
              PreferenceRecord record = repository.load(player.getUniqueId().toString(),
                  preferenceKey.toString());
              String value = record.value();
              Optional<Preference<?>> preference = preferenceRegistry.get(preferenceKey);
              if (preference.isEmpty()) {
                return 1;
              }
              Preference<?> p = preference.get();
              PreferenceType<?> type = p.getType();
              if (value != null) {
                Object parse = type.parse(value);
                Bukkit.broadcastMessage(parse.toString());
                player.sendMessage("value is: " + value);
              } else {
                String string = type.toValue(p.getDefault());
                Bukkit.broadcastMessage(string);
              }
            } catch (ExecutionException e) {
              throw new RuntimeException(e);
            }

          }
          return 1;
        }).suggests((context, builder) -> {
          preferenceRegistry.keySet().stream().map(Object::toString).forEach(builder::suggest);
          return builder.buildFuture();
        }));
  }
}