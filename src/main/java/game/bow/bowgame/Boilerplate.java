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
import org.bukkit.event.hanging.HangingBreakByEntityEvent;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.player.*;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.ArrayList;
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

            // Kill floor on the end map
            Material BlockUnderPlayer = Player.getWorld().getBlockAt(Player.getLocation().add(new Vector(0, -1,0))).getType();

            if (Event.getPlayer().getGameMode() == GameMode.ADVENTURE && Player.getLocation().getY() < 85.1 && BlockUnderPlayer == Material.OBSIDIAN) {
                KillPlayer(Player, Player);
            }
        }
    }

    @EventHandler
    public void OnArrowHit(ProjectileHitEvent Event) {
        if (!(Event.getEntity() instanceof Arrow) && !ItemOwners.containsKey(Event.getEntity()) && !FloatationBombs.contains(Event.getEntity())) { return; }

        Block Block = Event.getHitBlock();
        if (Block != null && (Block.getType() == Material.CHORUS_PLANT || Block.getType() == Material.CHORUS_FLOWER || Block.getType() == Material.DECORATED_POT)) {
            Event.setCancelled(true);
        }
    }

    @EventHandler
    public void OnPaintingDrop(HangingBreakByEntityEvent Event) {

        if (!(Event.getRemover() instanceof Player)) { return; }
        Player Player = (Player) Event.getRemover();

        if (Players.contains(Player)) {
            Event.setCancelled(true);
        }
    }

    @EventHandler
    public void OnPlayerTeleport(PlayerTeleportEvent Event) {
        Player Player = Event.getPlayer();

        // Sets dead players back to spectator when do to the end
        if (DeadPlayers.contains(Player)) {

            final int[] i = {0};

            new BukkitRunnable() {
                @Override
                public void run() {
                    Player.sendMessage(String.valueOf(Player.getLocation()));
                    if (Player.getWorld().getName().equals("world_the_end")) {
                        Bukkit.getScheduler().runTaskLater(BowGame.GetPlugin(), () -> {
                            Player.setGameMode(GameMode.SPECTATOR);
                        }, 1L);
                        cancel();
                    }
                    else if (i[0] == 100) {
                        cancel();
                    }
                    i[0]++;
                }
            }.runTaskTimer(BowGame.GetPlugin(), 1L, 1L);
        }
    }

    @EventHandler
    public void OnBlockInteract(PlayerInteractEvent Event) {
        Player Player = Event.getPlayer();
        if (Event.getClickedBlock() == null) { return; }

        if (Players.contains(Player) && !SandboxPlayers.contains(Player)) {
            if (Event.getAction() == Action.RIGHT_CLICK_BLOCK) {

                boolean IsNotInteractable = switch (Event.getClickedBlock().getType()) {
                    case CHEST, TRAPPED_CHEST, ENDER_CHEST, BARREL, HOPPER, FURNACE, BLAST_FURNACE, SMOKER, DROPPER,
                         DISPENSER, ANVIL, CHIPPED_ANVIL, DAMAGED_ANVIL, GRINDSTONE, LOOM, CARTOGRAPHY_TABLE, LECTERN,
                         BREWING_STAND, JUKEBOX, STONECUTTER, SMITHING_TABLE, BEACON, CHISELED_BOOKSHELF,
                         OAK_TRAPDOOR, SPRUCE_TRAPDOOR, BIRCH_TRAPDOOR, JUNGLE_TRAPDOOR, ACACIA_TRAPDOOR, DARK_OAK_TRAPDOOR,
                         MANGROVE_TRAPDOOR, CHERRY_TRAPDOOR, BAMBOO_TRAPDOOR, CRIMSON_TRAPDOOR, WARPED_TRAPDOOR, IRON_TRAPDOOR
                            -> true;
                    default -> false;
                };

                if (IsNotInteractable) {
                    Event.setCancelled(true);
                }
            }
        }
    }
}
