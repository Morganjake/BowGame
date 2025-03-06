package game.bow.bowgame.Upgrades;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DefenceUpgradesGUI extends MainUpgradesGUI {

    public static void OpenDefenceUpgradesGUI(Player player) {
        Inventory gui = Bukkit.createInventory(null, 27, "§6§l✶ §e§l999");

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
                        "Current Health",
                        "100",
                        "110",
                        "5"
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
                        "Current Defence",
                        "0",
                        "10",
                        "25"
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
                        "10",
                        "12",
                        "75"
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
                        "Current Regeneration",
                        "0",
                        "2",
                        "125"
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
                        true),
                "§fClick to go back to the main menu!"
        );
        gui.setItem(26, returnToMain);

        // Open the GUI
        player.openInventory(gui);
    }
}
