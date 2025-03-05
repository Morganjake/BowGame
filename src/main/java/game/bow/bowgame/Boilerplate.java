package game.bow.bowgame;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.player.PlayerDropItemEvent;

import static game.bow.bowgame.Game.PlayerHandler.Players;

public class Boilerplate implements Listener {

    @EventHandler
    public void onInventoryInteract(InventoryClickEvent Event) {
        if (Players.contains((Player) Event.getWhoClicked())) {
            Event.setCancelled(true);
        }
    }

    @EventHandler
    public void onInventoryDrag(InventoryDragEvent Event) {
        if (Players.contains((Player) Event.getWhoClicked())) {
            Event.setCancelled(true);
        }
    }

    @EventHandler
    public void onOffhandPlace(InventoryClickEvent Event) {
        if (Players.contains((Player) Event.getWhoClicked()) && Event.getClick() == ClickType.SWAP_OFFHAND) {
            Event.setCancelled(true);
        }
    }

    @EventHandler
    public void onItemDrop(PlayerDropItemEvent Event) {
        if (Players.contains(Event.getPlayer())) {
            Event.setCancelled(true);
        }
    }

    @EventHandler
    public void OnHungerChange(FoodLevelChangeEvent Event) {
        if (Players.contains((Player) Event.getEntity())) {
            Event.setCancelled(true);
        }
    }

    @EventHandler
    public void onHealthRegen(EntityRegainHealthEvent Event) {
        if (Players.contains((Player) Event.getEntity()) && Event.getRegainReason() == EntityRegainHealthEvent.RegainReason.SATIATED) {
            Event.setCancelled(true);
        }
    }
}
