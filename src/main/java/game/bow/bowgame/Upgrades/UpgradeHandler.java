package game.bow.bowgame.Upgrades;


import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static game.bow.bowgame.Game.GameHandler.BlueScore;
import static game.bow.bowgame.Game.GameHandler.RedScore;
import static game.bow.bowgame.Game.PlayerHandler.*;
import static game.bow.bowgame.Upgrades.BowUpgradesGUI.OpenBowUpgradesGUI;
import static game.bow.bowgame.Upgrades.DefenceUpgradesGUI.OpenDefenceUpgradesGUI;
import static game.bow.bowgame.Upgrades.ItemsGUI.OpenItemsGUI;

public class UpgradeHandler {

    public static Map<Player, Map<String, Integer>> PlayerUpgrades = new HashMap<>();
    public static Map<Player, Integer> Money = new HashMap<>();

    public static Map<String, Integer> GetDefaultStats() {

        return Map.ofEntries(

                Map.entry("Damage", 0),
                Map.entry("Velocity", 0),
                Map.entry("Backstab", 0),
                Map.entry("Bleed", 0),
                Map.entry("Bounce", 0),
                Map.entry("Health", 0),
                Map.entry("Defense", 0),
                Map.entry("Speed", 0),
                Map.entry("Regeneration", 0),
                Map.entry("Ability Cooldown", 0),
                Map.entry("Explosive", 0),
                Map.entry("Freeze Grenade", 0),
                Map.entry("Smoke Bomb", 0),
                Map.entry("APD", 0),
                Map.entry("Medkit", 0)
        );
    }

    public static void ResetStats(int Winner) {

        for (Player Player : Players ) {
            PlayerUpgrades.put(Player, GetDefaultStats());

            if (BlueTeam.contains(Player) && Winner == 1) {
                Money.put(Player, ((BlueScore + RedScore) * 50 + 50));
            }
            else if (RedTeam.contains(Player) && Winner == 0) {
                Money.put(Player, ((BlueScore + RedScore) * 50 + 50));
            }
            else {
                Money.put(Player, (BlueScore + RedScore) * 50);
            }

            // Balances money depending on the size of each team
            if (BlueTeam.contains(Player)) {
                Money.replace(Player, (int) ((float) Money.get(Player) * (float) Math.max(1, 1 + (RedTeam.size() / BlueTeam.size() - 1) / 2)));
            }
            else {
                Money.replace(Player, (int) ((float) Money.get(Player) * (float) Math.max(1, 1 + (BlueTeam.size() / RedTeam.size() - 1) / 2)));
            }
        }
    }

    public static boolean BuyUpgrade(Player Player, String Upgrade, Integer Cost, String GUI) {

        if (Cost <= Money.get(Player) && (!Objects.equals(GUI, "Items") || CanGetItem(Player, Upgrade))) {

            Money.replace(Player, Money.get(Player) - Cost);
            Player.playSound(Player, Sound.BLOCK_NOTE_BLOCK_PLING, 1, 1);

            Map<String, Integer> NewPlayerUpgrades = new HashMap<>(PlayerUpgrades.get(Player));
            NewPlayerUpgrades.put(Upgrade, NewPlayerUpgrades.get(Upgrade) + 1);

            PlayerUpgrades.put(Player, NewPlayerUpgrades);

            if (Objects.equals(GUI, "Bow")) {
                OpenBowUpgradesGUI(Player);
            }
            else if (Objects.equals(GUI, "Defence")) {
                OpenDefenceUpgradesGUI(Player);
            }
            else if (Objects.equals(GUI, "Items")) {
                OpenItemsGUI(Player);
                BuyItem(Player, Upgrade);
            }

            return true;
        }
        else{
            Player.playSound(Player, Sound.BLOCK_ANVIL_LAND, 1, 1);
            Player.sendMessage("§4You are too poor to afford this upgrade!");
            return false;
        }
    }

    public static boolean CanGetItem(Player Player, String Upgrade) {

        if (Objects.requireNonNull(Player.getInventory().getItem(3)).getType() == Material.EMERALD) {
            return true;
        }
        else if (Objects.requireNonNull(Player.getInventory().getItem(4)).getType() == Material.EMERALD) {
            return true;
        }

        Material Item = switch (Upgrade) {
            case "Explosive" -> Material.TNT;
            case "Freeze Grenade" -> Material.PRISMARINE_SHARD;
            case "Smoke Bomb" -> Material.INK_SAC;
            case "APD" -> Material.BLAZE_ROD;
            case "Medkit" -> Material.RED_DYE;
            default -> Material.BARRIER;
        };

        if (Objects.requireNonNull(Player.getInventory().getItem(3)).getType() == Item) {
            return true;
        }
        else return Objects.requireNonNull(Player.getInventory().getItem(4)).getType() == Item;
    }

    public static void BuyItem(Player Player, String Upgrade) {

        ItemStack Item = switch (Upgrade) {
            case "Explosive" -> new ItemStack(Material.TNT);
            case "Freeze Grenade" -> new ItemStack(Material.PRISMARINE_SHARD);
            case "Smoke Bomb" -> new ItemStack(Material.INK_SAC);
            case "APD" -> new ItemStack(Material.BLAZE_ROD);
            case "Medkit" -> new ItemStack(Material.RED_DYE);
            default -> new ItemStack(Material.BARRIER);
        };

        ItemMeta ItemMeta = Item.getItemMeta();
        Objects.requireNonNull(ItemMeta).setDisplayName(Upgrade);

        if (Objects.requireNonNull(Player.getInventory().getItem(3)).getType() == Material.EMERALD) {
            Player.getInventory().setItem(3, Item);
        }
        else if (Objects.requireNonNull(Player.getInventory().getItem(3)).getType() == Item.getType()) {
            if (Objects.requireNonNull(Player.getInventory().getItem(3)).getAmount() < 64) {
                Player.getInventory().addItem(Item);
            }
            else {
                Player.sendMessage("§4You don't have enough space to fit this item!");
            }
        }
        else if (Objects.requireNonNull(Player.getInventory().getItem(4)).getType() == Material.EMERALD) {
            Player.getInventory().setItem(4, Item);
        }
        else if (Objects.requireNonNull(Player.getInventory().getItem(4)).getType() == Item.getType()) {
            if (Objects.requireNonNull(Player.getInventory().getItem(4)).getAmount() < 64) {
                Player.getInventory().addItem(Item);
            }
            else {
                Player.sendMessage("§4You don't have enough space to fit this item!");
            }
        }
        else {
            Player.sendMessage("§4You don't have enough space to fit this item!");
        }
    }
}
