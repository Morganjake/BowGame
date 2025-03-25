package game.bow.bowgame.Upgrades;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static game.bow.bowgame.Upgrades.UpgradeHandler.*;
import static game.bow.bowgame.Upgrades.UpgradeHandler.BuyUpgrade;

public class DefenceUpgradesGUI extends MainUpgradesGUI implements Listener {

    public static void OpenDefenceUpgradesGUI(Player Player) {
        Inventory gui = Bukkit.createInventory(null, 27, "§6§l✶ §e§l" + Money.get(Player));

        // Create glass pane BG
        List<Integer> grayGlassIndexes = new ArrayList<>();
        for (int i = 0; i <= 26; i++) {
            grayGlassIndexes.add(i);
        }
        ItemStack grayGlass = SetIcon(Material.GRAY_STAINED_GLASS_PANE);
        ApplyIcons(gui, grayGlass, grayGlassIndexes);

        List<String> upgradeGradient = Arrays.asList("#0093FF", "#2EA6FF", "#2EA6FF", "#0093FF");

        ItemStack health = MainUpgradesGUI.SetIcon(
                Material.APPLE,
                TextColorGradient(
                        "Health",
                        upgradeGradient,
                        true),
                CreateUpgradeLore(
                        "Increases Health",
                        "Health",
                        String.valueOf(PlayerUpgrades.get(Player).get("Health") + 10),
                        String.valueOf(PlayerUpgrades.get(Player).get("Health") + 11),
                        String.valueOf(5 + 5 * PlayerUpgrades.get(Player).get("Health"))
                )
        );
        ItemStack defence = MainUpgradesGUI.SetIcon(
                Material.GOLDEN_CHESTPLATE,
                TextColorGradient(
                        "Defence",
                        upgradeGradient,
                        true),
                CreateUpgradeLore(
                        "Decreases damage taken",
                        "Damage taken",
                        String.valueOf(Math.pow(0.9, PlayerUpgrades.get(Player).get("Defense"))),
                        String.valueOf(Math.pow(0.9, PlayerUpgrades.get(Player).get("Defense") + 1)),
                        String.valueOf(75 + 50 * PlayerUpgrades.get(Player).get("Defense"))
                )
        );
        ItemStack speed = MainUpgradesGUI.SetIcon(
                Material.LEATHER_BOOTS,
                TextColorGradient(
                        "Speed",
                        upgradeGradient,
                        true),
                CreateUpgradeLore(
                        "Increases speed",
                        "Current Speed",
                        String.valueOf(PlayerUpgrades.get(Player).get("Speed") * 0.1 + 1),
                        String.valueOf(PlayerUpgrades.get(Player).get("Speed") * 0.1 + 1.1),
                        String.valueOf(25 + 10 * PlayerUpgrades.get(Player).get("Speed"))
                )
        );
        ItemStack regeneration = MainUpgradesGUI.SetIcon(
                Material.GOLDEN_APPLE,
                TextColorGradient(
                        "Regeneration",
                        upgradeGradient,
                        true),
                CreateUpgradeLore(
                        "Increases health regeneration",
                        "Current Regeneration every 5 seconds",
                        String.valueOf(PlayerUpgrades.get(Player).get("Regeneration") * 0.5 + 0.5),
                        String.valueOf(PlayerUpgrades.get(Player).get("Regeneration") * 0.5 + 1),
                        String.valueOf(30 + 30 * PlayerUpgrades.get(Player).get("Regeneration"))
                )
        );
        gui.setItem(10, health);
        gui.setItem(12, defence);
        gui.setItem(14, speed);
        gui.setItem(16, regeneration);

        ItemStack returnToMain = SetIcon(
                Material.BARRIER,
                TextColorGradient(
                        "Return to Main Menu",
                        Arrays.asList("#FF0000", "#CC0000", "#990000"),
                        true)
        );
        gui.setItem(26, returnToMain);

        Player.openInventory(gui);
    }

    @EventHandler
    public void OnInventoryClick(InventoryClickEvent Event) {

        Player Player = (Player) Event.getWhoClicked();
        if (Event.getCurrentItem() == null || !Event.getView().getTitle().contains("✶")) {
            return;
        }

        Material ClickedItem = Event.getCurrentItem().getType();

        if (ClickedItem == Material.APPLE) {
            if (BuyUpgrade(Player, "Health", 5 + 5 * PlayerUpgrades.get(Player).get("Health"), "Defence")) {
                Player.setMaxHealth(Player.getMaxHealth() + 2);
                Player.setHealth(Player.getMaxHealth());
            }
        }
        else if (ClickedItem == Material.GOLDEN_CHESTPLATE) {
            BuyUpgrade(Player, "Defense", 75 + 50 * PlayerUpgrades.get(Player).get("Defense"), "Defence");
        }
        else if (ClickedItem == Material.LEATHER_BOOTS) {
            if (BuyUpgrade(Player, "Speed", 25 + 10 * PlayerUpgrades.get(Player).get("Speed"), "Defence")) {
                Player.sendMessage(String.valueOf(Player.getWalkSpeed()));
                Player.setWalkSpeed(Player.getWalkSpeed() + 0.02f);
            }
        }
        else if (ClickedItem == Material.GOLDEN_APPLE) {
            BuyUpgrade(Player, "Regeneration", 30 + 30 * PlayerUpgrades.get(Player).get("Regeneration"), "Defence");
        }
    }
}
