package game.bow.bowgame.Upgrades;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;

public class DefenceUpgradesGUI extends MainUpgradesGUI {

    public static void OpenDefenceUpgradesGUI(Player player) {
        Inventory gui = Bukkit.createInventory(null, 27, "§6§l✶ §e§l999");

        ItemStack healthUpgrade = MainUpgradesGUI.SetIcon(
                Material.APPLE,
                TextColorGradient(
                        "Health",
                        Arrays.asList("#0093FF", "#2EA6FF", "#0093FF"), // Blue gradient (same as used in main GUI)
                        true),
                CreateUpgradeLore(
                        "Increases Health",
                        "Current Health",
                        "100",
                        "110",
                        "5"
                )
        );
        gui.setItem(13, healthUpgrade);

        ItemStack returnToMain = SetIcon(
                Material.BARRIER,
                TextColorGradient(
                        "Return to Main Menu",
                        Arrays.asList("#FF0000", "#CC0000", "#990000"),
                        true),
                "§fClick to go back to the main menu!"
        );
        gui.setItem(22, returnToMain);

        // Open the GUI
        player.openInventory(gui);
    }
}
