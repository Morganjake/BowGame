package game.bow.bowgame.GUIs;

import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.entity.Player;

public class GUIListener implements Listener {

    @EventHandler
    public void onUpgradeGUIClick(InventoryClickEvent Event) {

        Player Player = (Player) Event.getWhoClicked();
        ItemStack ClickedItem = Event.getCurrentItem();

        if (ClickedItem == null || !Event.getView().getTitle().contains("✶")) {
            return;
        }

        Event.setCancelled(true);

        Material Item = ClickedItem.getType();

        if (Item == Material.BOW) {
            BowUpgradesGUI.OpenBowUpgradesGUI(Player);
        }
        else if (Item == Material.DIAMOND_CHESTPLATE) {
            DefenceUpgradesGUI.OpenDefenceUpgradesGUI(Player);
        }
        else if (Item == Material.TNT) {
            ItemsGUI.OpenItemsGUI(Player);
        }
        else if (Item == Material.BARRIER) {
            MainUpgradesGUI.OpenUpgradesGUI(Player);
        }
    }
}
