package game.bow.bowgame;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.util.Objects;

import static game.bow.bowgame.Game.GameHandler.*;

public class Commands implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender CommandSender, Command Command, String Label, String[] Args) {

        if (!Command.getName().equalsIgnoreCase("bowgame")) {
            return false;
        }

        else if (!CommandSender.isOp()) {
            CommandSender.sendMessage("You must be OP to use this command");
            return false;
        }

        else if (Args.length == 0) {
            return false;
        }

        if (Objects.equals(Args[0].toLowerCase(), "start")) {
            StopGame();
            StartGame();
        }

        if (Objects.equals(Args[0].toLowerCase(), "stop")) {
            StopGame();
        }

        if (Objects.equals(Args[0].toLowerCase(), "next")) {
            NextRound(0);
        }

        return false;
    }
}
