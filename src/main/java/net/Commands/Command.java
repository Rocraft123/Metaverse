package net.Commands;

import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class Command implements CommandExecutor, TabCompleter {

    private final List<CommandExtension> extensions = new ArrayList<>();

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull org.bukkit.command.Command command, @NotNull String label, @NotNull String @NotNull [] args) {
        if (args.length == 0) {
            sender.sendMessage("no not 0 args dumbness");
            return true;
        }

        for (CommandExtension extension : extensions) {
            if (extension.getArg().equalsIgnoreCase(args[0])) {
                if (extension.requiresOp() && !sender.isOp())
                    return true;
                return extension.onCommand(sender, command, label, args);
            }
        }

        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull org.bukkit.command.Command command, @NotNull String label, @NotNull String @NotNull[] args) {
        List<CommandExtension> accessibleExtensions = new ArrayList<>();

        for (CommandExtension extension : this.extensions) {
            if (!extension.requiresOp() || sender.isOp()) {
                accessibleExtensions.add(extension);
            }
        }

        if (args.length == 1) {
            String currentInput = args[0].toLowerCase();
            List<String> matches = new ArrayList<>();

            for (CommandExtension extension : accessibleExtensions) {
                String arg = extension.getArg();
                if (arg.toLowerCase().startsWith(currentInput)) {
                    matches.add(arg);
                }
            }
            return matches;
        }

        if (args.length >= 2) {
            String subCommand = args[0];

            String currentInput = args[args.length - 1].toLowerCase();
            List<String> matches = new ArrayList<>();

            for (CommandExtension extension : accessibleExtensions) {
                if (extension.getArg().equalsIgnoreCase(subCommand)) {
                    List<String> currentList = extension.onTabComplete(sender, command, label, args);

                    if (currentList == null) continue;
                    for (String string : currentList) {
                        if (string.toLowerCase().startsWith(currentInput)) {
                            matches.add(string);
                        }
                    }
                }
            }
            return matches;
        }

        return new ArrayList<>();
    }


    public void registerExtensions(CommandExtension extension) {
        extensions.add(extension);
    }

    public void unRegisterExtensions(CommandExtension extension) {
        extensions.remove(extension);
    }
}
