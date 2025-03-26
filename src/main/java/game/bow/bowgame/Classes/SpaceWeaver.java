package game.bow.bowgame.Classes;

import game.bow.bowgame.BowGame;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static game.bow.bowgame.Classes.ClassHandler.*;
import static game.bow.bowgame.Classes.ClassHandler.PutPlayerOnCooldown;
import static game.bow.bowgame.Game.PlayerHandler.*;

public class SpaceWeaver implements Listener {

    public static HashMap<Player, Location> SpaceWarpLocations = new HashMap<>();
    public static final HashMap<Player, Location> SpatialTearLocations = new HashMap<>();
    public static ArrayList<Player> WarpedEnemies = new ArrayList<>();

    @EventHandler
    public void OnLeftClick(PlayerInteractEvent Event) {

        Player Player = Event.getPlayer();
        ItemStack Item = Event.getItem();

        if (!Players.contains(Player)) { return; }

        if (Item == null || Item.getType() != Material.ECHO_SHARD) { return; }

        if (Event.getAction() == Action.LEFT_CLICK_AIR || Event.getAction() == Action.LEFT_CLICK_BLOCK) {

            if (Cooldowns.containsKey(Player)) {
                Player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText("§7§lYou still have " + Cooldowns.get(Player) + " seconds left!"));
                return;
            }

            SpaceWarp(Player, Event.getClickedBlock());
        }

        else if ((Event.getAction() == Action.RIGHT_CLICK_AIR || Event.getAction() == Action.RIGHT_CLICK_BLOCK) && UltPoints.get(Player) == 6) {
            if (!Player.getWorld().getName().equals("world_the_end")) {
                SpatialTear(Player);
            }
        }
    }

    private void SpaceWarp(Player Player, Block ClickedBlock) {

        if (SpaceWarpLocations.containsKey(Player)) {

            if (Player.getWorld().getName().equals("world_the_end")) { return; }

            PutPlayerOnCooldown(Player);

            Location PlayerLocation = Player.getLocation().clone().add(new Vector(0, 1, 0));

            Player.getWorld().spawnParticle(Particle.DUST, Player.getEyeLocation(), 30, 0.2, 0.5, 0.2, 0.01,
                    new Particle.DustOptions(Color.fromRGB(92, 7, 107), 1));
            Player.getWorld().playSound(Player.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 1, 1);
            Player.playSound(Player.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 1, 1);
            Player.teleport(SpaceWarpLocations.get(Player));

            SpaceWarpLocations.remove(Player);

            int[] i = {0};

            new BukkitRunnable() {
                @Override
                public void run() {

                    if (i[0] > 100) {
                        cancel();
                        return;
                    }

                    i[0]++;


                    for (int j = 0; j < 10; j++) {
                        Location DustLocation = PlayerLocation.clone();
                        DustLocation.setPitch((float) (90 - Math.random() * 180));
                        DustLocation.setYaw((float) (180 - Math.random() * 360));
                        DustLocation.add(DustLocation.getDirection().multiply((1 - Math.random() * Math.random())));
                        Player.getWorld().spawnParticle(Particle.DUST, DustLocation, 1, new Particle.DustOptions(Color.BLACK, 1));
                    }

                    for (Player OtherPlayer : Players) {
                        if (PlayerLocation.distance(OtherPlayer.getLocation()) < 4) {
                            Vector ToTarget = PlayerLocation.toVector().subtract(OtherPlayer.getLocation().toVector()).normalize();
                            OtherPlayer.setVelocity(OtherPlayer.getVelocity().add(ToTarget.multiply(0.2)));
                        }
                    }
                }
            }.runTaskTimer(BowGame.GetPlugin(), 0L, 1L);

            return;
        }

        if (ClickedBlock != null && Player.getWorld().getBlockAt(ClickedBlock.getLocation().add(0, 1, 0)).getType() == Material.AIR) {
            SpaceWarpLocations.put(Player, ClickedBlock.getLocation().clone().add(0.5, 1.2, 0.5));
        }
        else {
            SpaceWarpLocations.put(Player, Player.getLocation().clone().add(new Vector(0, 0.2, 0)));
        }

        int[] Offset = {0};

        new BukkitRunnable() {
            @Override
            public void run() {

                if (Player.getGameMode() == GameMode.SPECTATOR) {
                    cancel();
                    return;
                }

                if (!SpaceWarpLocations.containsKey(Player)) {
                    cancel();
                    return;
                }

                for (int i = 0; i < 18; i++) {
                    Location ParticleLocation = SpaceWarpLocations.get(Player).clone();
                    ParticleLocation.setYaw(i * 20 + Offset[0]);
                    ParticleLocation.add(ParticleLocation.getDirection().multiply(0.5));

                    Player.getWorld().spawnParticle(Particle.DUST, ParticleLocation, 1, new Particle.DustOptions(Color.fromRGB(92, 7, 107), 1));
                }
                Offset[0]++;

            }

        }.runTaskTimer(BowGame.GetPlugin(), 0L, 4L);
    }

    private static void SpatialTear(Player Player) {

        List<Player> Team = null;
        if (BlueTeam.contains(Player)) {
            Team = RedTeam;
        }
        else {
            Team = BlueTeam;
        }

        Player WarpedEnemy = null;

        for (Player Enemy : Team) {

            Vector Direction = Player.getLocation().getDirection().normalize();
            Vector ToTarget = Enemy.getLocation().toVector().subtract(Player.getLocation().toVector()).normalize();
            double Degrees = Math.toDegrees(Direction.dot(ToTarget));
            if (Degrees > 50 && Degrees < 70 && Player.getLocation().distance(Enemy.getLocation()) < 10) {
                if (WarpedEnemy == null || Player.getLocation().distance(WarpedEnemy.getLocation()) < Player.getLocation().distance(Enemy.getLocation())) {
                    WarpedEnemy = Enemy;
                }
            }
        }

        if (WarpedEnemy != null) {

            SpatialTearLocations.put(Player, Player.getLocation().clone());
            SpatialTearLocations.put(WarpedEnemy, WarpedEnemy.getLocation().clone());

            AddUltPoints(Player, -6);

            Player.getWorld().spawnParticle(Particle.DUST, Player.getEyeLocation(), 30, 0.2, 0.5, 0.2, 0.01,
                    new Particle.DustOptions(Color.fromRGB(92, 7, 107), 1));
            Player.getWorld().playSound(Player.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 1, 1);
            Player.playSound(Player.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 1, 1);

            WarpedEnemy.getWorld().spawnParticle(Particle.DUST, WarpedEnemy.getEyeLocation(), 30, 0.2, 0.5, 0.2, 0.01,
                    new Particle.DustOptions(Color.fromRGB(92, 7, 107), 1));
            WarpedEnemy.getWorld().playSound(WarpedEnemy.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 1, 1);
            WarpedEnemy.playSound(WarpedEnemy.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 1, 1);

            Player.teleport(new Location(Bukkit.getWorld("world_the_end"), 332.5, 74, -32.5, 0, 0));
            WarpedEnemy.teleport(new Location(Bukkit.getWorld("world_the_end"), 332.5, 74, -0.5, 180, 0));

            Player.setGameMode(GameMode.ADVENTURE);
            WarpedEnemy.setGameMode(GameMode.ADVENTURE);

            Player.getInventory().setItem(0, null);
            WarpedEnemy.getInventory().setItem(0, null);

            Player FinalWarpedEnemy = WarpedEnemy;
            Bukkit.getScheduler().runTaskLater(BowGame.GetPlugin(), () -> {
                Player.getInventory().setItem(0, new ItemStack(Material.BOW));
                FinalWarpedEnemy.getInventory().setItem(0, new ItemStack(Material.BOW));
            }, 5L);

            WarpedEnemies.add(WarpedEnemy);
        }
    }
}
