package game.bow.bowgame.Classes;

import game.bow.bowgame.BowGame;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static game.bow.bowgame.Boilerplate.SandboxPlayers;
import static game.bow.bowgame.Classes.ClassHandler.*;
import static game.bow.bowgame.Classes.ClassHandler.PutPlayerOnCooldown;
import static game.bow.bowgame.Game.PlayerHandler.*;

public class SpaceWeaver implements Listener {

    public static HashMap<Player, Location> SpaceWarpLocations = new HashMap<>();
    public static ArrayList<Player> SpaceManipulationPlayers = new ArrayList<>();
    public static HashMap<Arrow, Integer> WormholeArrows = new HashMap<>();

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

        else if ((Event.getAction() == Action.RIGHT_CLICK_AIR || Event.getAction() == Action.RIGHT_CLICK_BLOCK) &&
                (UltPoints.get(Player) == 6) || SandboxPlayers.contains(Player)) {
            AddUltPoints(Player, -6);
            SpaceManipulationPlayers.add(Player);
        }
    }


    public static void SpacialRearrangement(Player Player, Arrow Arrow) {
        List<Player> Team = BlueTeam.contains(Player) ? RedTeam : BlueTeam;
        final Vector[] ArrowVelocity = {new Vector(-999, -999, -999)};

        new BukkitRunnable() {

            @Override
            public void run() {

                if (ArrowVelocity[0] == Arrow.getVelocity()) {
                    cancel();
                    return;
                }

                ArrowVelocity[0] = Arrow.getVelocity();

                if (Arrow.isDead()) {
                    cancel();
                    return;
                }

                for (Player Enemy : Team) {
                    if (Arrow.getWorld() != Enemy.getWorld()) { continue; }
                    if (Arrow.getLocation().distance(Enemy.getEyeLocation()) < 3) {
                        Vector ToTarget = Enemy.getLocation().add(new Vector(0, 1, 0)).toVector().subtract(Arrow.getLocation().toVector()).normalize();
                        Arrow.setVelocity(Arrow.getVelocity().add(ToTarget.multiply(0.15)));
                    }
                }
            }
        }.runTaskTimer(BowGame.GetPlugin(), 0L, 1L);
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
                        if (DeadPlayers.contains(OtherPlayer)) { continue; }
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

    private static void CreateArrowWormhole(Arrow Arrow) {
        Vector Direction = Arrow.getVelocity().normalize();

        Vector Right = Direction.clone().crossProduct(new Vector(0, 1, 0)).normalize();
        Vector PerpendicularUp = Right.clone().crossProduct(Direction).normalize();

        for (int i = 0; i < 10; i++) {

            double Angle = Math.toRadians(((double) 360 / 10) * i);

            double X = Math.cos(Angle) * 0.4;
            double Y = Math.sin(Angle) * 0.4;

            Vector Offset = Right.clone().multiply(X).add(PerpendicularUp.clone().multiply(Y));
            Location ParticleLocation = Arrow.getLocation().clone().add(Offset);

            Arrow.getWorld().spawnParticle(Particle.DUST, ParticleLocation, 1, new Particle.DustOptions(Color.fromRGB(40, 30, 100), 2));
        }
    }

    private static void CreateArrowWormholeParticles(Arrow Arrow) {

        final Location[] ArrowPosition = {Arrow.getLocation().clone()};

        new BukkitRunnable() {

            @Override
            public void run() {

                Arrow.getWorld().spawnParticle(Particle.DUST, Arrow.getLocation(), 1,new Particle.DustOptions(Color.fromRGB(
                        (int) (40 + (Math.random() - 0.5) * 20),
                        (int) (30 + (Math.random() - 0.5) * 20),
                        (int) (100 + (Math.random() - 0.5) * 20)), 2));

                if (Arrow.getLocation().distance(ArrowPosition[0]) < 0.01) {
                    cancel();
                }
                else {
                    ArrowPosition[0] = Arrow.getLocation().clone();
                }

            }
        }.runTaskTimer(BowGame.GetPlugin(), 2L, 1L);
    }

    public static void SpaceManipulation(Player Player, Arrow Arrow) {

        if (WormholeArrows.containsKey(Arrow) && WormholeArrows.get(Arrow) == 10) {
            return;
        }

        CreateArrowWormhole(Arrow);

        Player Closest = Player;
        double ClosestDist = 999999;

        for (Player OtherPlayer : Players) {
            if (BlueTeam.contains(Player) == BlueTeam.contains(OtherPlayer)) { continue; }
            if (Player.getLocation().distance(OtherPlayer.getLocation()) < ClosestDist) {
                Closest = OtherPlayer;
                ClosestDist = Player.getLocation().distance(OtherPlayer.getLocation());
            }
        }

        Location ArrowWormhole = new Location(Arrow.getWorld(), 0, 0, 0);
        Location Location = Closest.getEyeLocation();
        Location TargetLocation = Closest.getEyeLocation();

        // For loop to avoid an infinite loop
        for (int i = 0; i < 50; i++) {
            double Theta = Math.random() * 2 * Math.PI;
            double Phi = Math.acos(2 * Math.random() - 1);

            double X = 12 * Math.sin(Phi) * Math.cos(Theta);
            double Y = 12 * Math.cos(Phi);
            double Z = 12 * Math.sin(Phi) * Math.sin(Theta);

            ArrowWormhole = Location.clone().add(X, Y, Z);
            TargetLocation = Location.add((Math.random() - 0.5) * 3, (Math.random() - 0.5) * 3, (Math.random() - 0.5) * 3);

            Vector WormholeDirection = ArrowWormhole.toVector().subtract(TargetLocation.toVector()).normalize();
            double Distance = TargetLocation.distance(ArrowWormhole);
            RayTraceResult RayResult = Closest.getWorld().rayTraceBlocks(TargetLocation, WormholeDirection, Distance);

            if (RayResult == null) {
                break;
            }
        }

        Arrow NewArrow = Arrow.getWorld().spawn(ArrowWormhole, Arrow.class);

        NewArrow.setVelocity(TargetLocation.toVector().subtract(ArrowWormhole.toVector()).normalize().multiply(Arrow.getVelocity().length()));
        NewArrow.setTicksLived(1);
        NewArrow.setCritical(Arrow.isCritical());
        NewArrow.setShooter(Arrow.getShooter());

        CreateArrowWormhole(NewArrow);
        CreateArrowWormholeParticles(NewArrow);

        if (WormholeArrows.containsKey(Arrow)) {
            WormholeArrows.put(NewArrow, WormholeArrows.get(Arrow) + 1);
            WormholeArrows.remove(Arrow);
        }
        else {
            WormholeArrows.put(NewArrow, 1);
        }

        Arrow.remove();
    }
}
