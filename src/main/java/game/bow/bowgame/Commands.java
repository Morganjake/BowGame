package game.bow.bowgame;

import game.bow.bowgame.Upgrades.MainUpgradesGUI;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

import static game.bow.bowgame.Boilerplate.SandboxPlayers;
import static game.bow.bowgame.Classes.ClassHandler.*;
import static game.bow.bowgame.Game.GameHandler.*;
import static game.bow.bowgame.Game.GameUIHandler.UpdateScoreBoard;
import static game.bow.bowgame.Game.PlayerHandler.Players;
import static game.bow.bowgame.Upgrades.UpgradeHandler.*;

public class Commands implements CommandExecutor, TabCompleter {

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

        switch (Args[0].toLowerCase()) {
            case "start":
                StopGame();
                StartGame();
                return true;

            case "stop":
                StopGame();
                return true;

            case "next":
                NextRound(0);
                return true;

            case "upgrades":
                MainUpgradesGUI.OpenUpgradesGUI((Player) CommandSender);
                return true;

            case "money":
                if (Args.length >= 2) {
                    Money.replace((Player) CommandSender, Integer.valueOf(Args[1]));
                }
                return true;

            case "ultpoints":
                if (Args.length >= 2) {
                    AddUltPoints((Player) CommandSender, Integer.valueOf(Args[1]));
                    UpdateScoreBoard();
                }
                return true;

            case "sandbox":

                if (!SandboxPlayers.contains((Player) CommandSender)) {
                    Players.add(((Player) CommandSender));
                    SandboxPlayers.add((Player) CommandSender);
                    PlayerUpgrades.put((Player) CommandSender, GetDefaultStats());
                    Classes.put((Player) CommandSender, "ALL");
                    Money.put((Player) CommandSender, 999999999);
                    UltPoints.put((Player) CommandSender, 0);
                }
                else {
                    Players.remove((Player) CommandSender);
                    SandboxPlayers.remove((Player) CommandSender);
                }
                return true;

            case "resetstats":
                PlayerUpgrades.put((Player) CommandSender, GetDefaultStats());
                return true;
        }

        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender commandSender, Command command, String s, String[] strings) {
        List<String> suggestions = new ArrayList<>();

        suggestions.add("start");
        suggestions.add("stop");
        suggestions.add("next");
        suggestions.add("upgrades");
        suggestions.add("money");
        suggestions.add("ultpoints");
        suggestions.add("sandbox");
        suggestions.add("resetstats");

        return suggestions;
    }
}
