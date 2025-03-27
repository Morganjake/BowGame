package game.bow.bowgame;

import game.bow.bowgame.Upgrades.MainUpgradesGUI;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.player.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static game.bow.bowgame.Classes.Astronaut.FloatationBombs;
import static game.bow.bowgame.Game.GameHandler.GracePeriod;
import static game.bow.bowgame.Game.ItemHandler.ItemOwners;
import static game.bow.bowgame.Game.ItemHandler.SlowedPlayers;
import static game.bow.bowgame.Game.PlayerHandler.*;

public class Boilerplate implements Listener {

    public static List<Player> SandboxPlayers = new ArrayList<>();

    @EventHandler
    public void OnInventoryInteract(InventoryClickEvent Event) {
        if (Players.contains((Player) Event.getWhoClicked()) && !SandboxPlayers.contains((Player) Event.getWhoClicked())) {
            Event.setCancelled(true);
        }
    }

    @EventHandler
    public void OnInventoryDrag(InventoryDragEvent Event) {
        if (Players.contains((Player) Event.getWhoClicked()) && !SandboxPlayers.contains((Player) Event.getWhoClicked())) {
            Event.setCancelled(true);
        }
    }

    @EventHandler
    public void OnOffhandPlace(InventoryClickEvent Event) {
        if (!(Event.getWhoClicked() instanceof Player)) { return; }
        if (Players.contains((Player) Event.getWhoClicked()) && !SandboxPlayers.contains((Player) Event.getWhoClicked()) && Event.getClick() == ClickType.SWAP_OFFHAND) {
            Event.setCancelled(true);
        }
    }

    @EventHandler
    public void OnItemDrop(PlayerDropItemEvent Event) {
        if (Players.contains(Event.getPlayer()) && !SandboxPlayers.contains(Event.getPlayer())) {
            Event.setCancelled(true);
        }
    }

    @EventHandler
    public void OnHungerChange(FoodLevelChangeEvent Event) {
        if (!(Event.getEntity() instanceof Player)) { return; }
        if (Players.contains((Player) Event.getEntity()) && !SandboxPlayers.contains((Player) Event.getEntity())) {
            Event.setCancelled(true);
        }
    }

    @EventHandler
    public void OnHealthRegen(EntityRegainHealthEvent Event) {
        if (!(Event.getEntity() instanceof Player)) { return; }
        if (Players.contains((Player) Event.getEntity()) && !SandboxPlayers.contains((Player) Event.getEntity())
                && Event.getRegainReason() == EntityRegainHealthEvent.RegainReason.SATIATED) {
            Event.setCancelled(true);
        }
    }

    @EventHandler
    public void OnDamage(EntityDamageEvent Event) {
        if (!(Event.getEntity() instanceof Player)) { return; }
        if (Players.contains((Player) Event.getEntity()) && !SandboxPlayers.contains((Player) Event.getEntity())
                && Event.getCause() == EntityDamageEvent.DamageCause.FALL) {
            Event.setCancelled(true);
        }

        else if (Players.contains((Player) Event.getEntity()) && !SandboxPlayers.contains((Player) Event.getEntity())
                && Event.getCause() == EntityDamageEvent.DamageCause.VOID) {
            Event.setCancelled(true);
            KillPlayer((Player) Event.getEntity(), (Player) Event.getEntity());
        }

        else if (Players.contains((Player) Event.getEntity()) && !SandboxPlayers.contains((Player) Event.getEntity())
                && Event.getCause() == EntityDamageEvent.DamageCause.LAVA) {
            Event.setCancelled(true);
            KillPlayer((Player) Event.getEntity(), (Player) Event.getEntity());
        }
    }

    @EventHandler
    public void OnArrowPickup(PlayerPickupArrowEvent Event) {
        if (Players.contains(Event.getPlayer()) && !SandboxPlayers.contains(Event.getPlayer())) {
            Event.setCancelled(true);
        }
    }

    @EventHandler
    public void OnMove(PlayerMoveEvent Event) {
        Player Player = Event.getPlayer();
        if (Players.contains(Player) && !SandboxPlayers.contains(Event.getPlayer())) {

            if (GracePeriod) {
                MainUpgradesGUI.OpenUpgradesGUI(Player);
                Event.setCancelled(true);
                return;
            }

            if (SlowedPlayers.contains(Player) && Event.getTo() != null && Event.getTo().getY() > Event.getFrom().getY()) {
                Event.getPlayer().teleport(new Location(
                        Player.getWorld(),
                        Event.getFrom().getX(),
                        Event.getFrom().getY(),
                        Event.getFrom().getZ(),
                        Event.getTo().getYaw(),
                        Event.getTo().getPitch()
                ));
            }

            if (Event.getPlayer().getGameMode() == GameMode.ADVENTURE && Player.getLocation().getY() < 85.1) {
                KillPlayer(Player, Player);
            }
        }
    }

    @EventHandler
    public void onArrowHit(ProjectileHitEvent Event) {
        if (!(Event.getEntity() instanceof Arrow) && !ItemOwners.containsKey(Event.getEntity()) && !FloatationBombs.contains(Event.getEntity())) { return; }

        Block Block = Event.getHitBlock();
        if (Block != null && (Block.getType() == Material.CHORUS_PLANT || Block.getType() == Material.CHORUS_FLOWER || Block.getType() == Material.DECORATED_POT)) {
            Event.setCancelled(true);
        }
    }

    @EventHandler
    public void OnPlayerTeleport(PlayerTeleportEvent Event) {
        Player Player = Event.getPlayer();

        if (DeadPlayers.contains(Player)) {
            Bukkit.getScheduler().runTaskLater(BowGame.GetPlugin(), () -> {
                Player.setGameMode(GameMode.SPECTATOR);
            }, 1L);
        }
    }

    @EventHandler
    public void OnBlockInteract(PlayerInteractEvent Event) {
        Player Player = Event.getPlayer();
        if (Event.getClickedBlock() == null) { return; }

        if (Players.contains(Player) && !SandboxPlayers.contains(Event.getPlayer())) {
            if (Event.getAction() == Action.RIGHT_CLICK_BLOCK) {
                Event.setCancelled(true);
            }
        }
    }
}
