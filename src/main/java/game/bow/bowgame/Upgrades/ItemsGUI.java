package game.bow.bowgame.Upgrades;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;

public class ItemsGUI extends MainUpgradesGUI {

    public static void OpenItemsGUI(Player player) {
        Inventory gui = Bukkit.createInventory(null, 27, "§6§l✶ §e§l420");

        ItemStack explosiveArrow = MainUpgradesGUI.SetIcon(
                Material.TNT,
                TextColorGradient(
                        "Explosive Arrow",
                        Arrays.asList("#FFD700", "#FFC107", "#FFB300"), // Golden gradient for an explosive effect
                        true),
                CreateItemLore(
                        "The next arrow you shoot will explode upon impact",
                        "100"
                )
        );
        gui.setItem(13, explosiveArrow);

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
