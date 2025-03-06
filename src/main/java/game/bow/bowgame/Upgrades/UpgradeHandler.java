package game.bow.bowgame.Upgrades;


import org.bukkit.entity.Player;

import java.util.Map;

import static game.bow.bowgame.Game.PlayerHandler.Players;

public class UpgradeHandler {

    public static Map<Player, Map<String, Integer>> PlayerUpgrades;

    public static Map<String, Integer> GetDefaultStats() {

        return Map.ofEntries(

                Map.entry("Damage", 3),
                Map.entry("Velocity", 1),
                Map.entry("Backstab", 0),
                Map.entry("Poison", 0),
                Map.entry("Bounce", 0),
                Map.entry("Health", 10),
                Map.entry("Defense", 1),
                Map.entry("Speed", 1),
                Map.entry("Regeneration", 1)
        );
    }

    public static void ResetStats() {

        for (Player Player : Players ) {
            PlayerUpgrades.put(Player, GetDefaultStats());
        }
    }
}
