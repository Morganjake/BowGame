package game.bow.bowgame.Upgrades;

import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.entity.Player;

public class GUIListener implements Listener {

    @EventHandler
    public void onUpgradeGUIClick(InventoryClickEvent event) {
        // Check if the inventory is the Upgrades GUI
//        Inventory gui = event.getInventory();
        Player player = (Player) event.getWhoClicked();
        ItemStack clickedItem = event.getCurrentItem();

        if (clickedItem == null || !event.getView().getTitle().contains("✶")) {
            return; // Exit if the GUI is null, the clicked item is null, or it's not the Upgrades GUI
        }

        // Prevent item moving in the GUI
        event.setCancelled(true);

        // Determine which item was clicked
        Material material = clickedItem.getType();

        if (material == Material.BOW) {
            // Open Bow Upgrades GUI
            player.closeInventory();
            BowUpgradesGUI.OpenBowUpgradesGUI(player);
        } else if (material == Material.DIAMOND_CHESTPLATE) {
            // Open Defence Upgrades GUI
            player.closeInventory();
            DefenceUpgradesGUI.OpenDefenceUpgradesGUI(player);
        } else if (material == Material.TNT) {
            // Open Items GUI
            player.closeInventory();
            ItemsGUI.OpenItemsGUI(player);
        } else if (material == Material.BARRIER) {
            // Return to main upgrades menu
            player.closeInventory();
            MainUpgradesGUI.OpenUpgradesGUI(player);
        }
    }
}
