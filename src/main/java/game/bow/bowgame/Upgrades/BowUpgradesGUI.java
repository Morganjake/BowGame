package game.bow.bowgame.Upgrades;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;

public class BowUpgradesGUI extends MainUpgradesGUI {

    public static void OpenBowUpgradesGUI(Player player) {
        Inventory gui = Bukkit.createInventory(null, 27, "§6§l✶ §e§l3");

        ItemStack arrowDamageUpgrade = MainUpgradesGUI.SetIcon(
                Material.ARROW,
                TextColorGradient(
                        "Arrow Damage Upgrade",
                        Arrays.asList("#FF4500", "#FF6347", "#FF7F50"), // Orange-red gradient
                        true),
                CreateUpgradeLore(
                        "Increases arrow damage",
                        "Damage",
                        "1",
                        "2",
                        "10"
                )
        );
        gui.setItem(13, arrowDamageUpgrade);

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
