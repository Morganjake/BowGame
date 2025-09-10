package game.bow.bowgame.Classes;

import game.bow.bowgame.BowGame;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.*;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Snowball;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.ArrayList;

import static game.bow.bowgame.Boilerplate.SandboxPlayers;
import static game.bow.bowgame.Classes.ClassHandler.*;
import static game.bow.bowgame.Game.GameHandler.GameEnded;
import static game.bow.bowgame.Game.PlayerHandler.DeadPlayers;
import static game.bow.bowgame.Game.PlayerHandler.Players;

public class Astronaut implements Listener {

    public static ArrayList<Entity> FloatationBombs = new ArrayList<>();

    @EventHandler
    public void OnLeftClick(PlayerInteractEvent Event) {

        Player Player = Event.getPlayer();
        ItemStack Item = Event.getItem();

        if (!Players.contains(Player)) { return; }

        if (Item == null || Item.getType() != Material.BREEZE_ROD) { return; }

        if (Event.getAction() == Action.LEFT_CLICK_AIR || Event.getAction() == Action.LEFT_CLICK_BLOCK) {

            if (Cooldowns.containsKey(Player)) {
                Player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText("§7§lYou still have " + Cooldowns.get(Player) + " seconds left!"));
                return;
            }

            PutPlayerOnCooldown(Player);

            FloatationBombs.add(Player.launchProjectile(Snowball.class));
        }

        else if ((Event.getAction() == Action.RIGHT_CLICK_AIR || Event.getAction() == Action.RIGHT_CLICK_BLOCK) &&
                (UltPoints.get(Player) == 6) || SandboxPlayers.contains(Player)) {
            AddUltPoints(Player, -6);
            Untouchable(Player);
        }
    }

    @EventHandler
    public void OnFloatationBombLand(ProjectileHitEvent Event) {
        if (!(Event.getEntity() instanceof Snowball)) { return; }
        if (!FloatationBombs.contains(Event.getEntity())) { return; }

        Snowball Snowball = (Snowball) Event.getEntity();

        Snowball.getWorld().playSound(Snowball.getLocation(), Sound.BLOCK_GLASS_BREAK, 1, 1);

        for (Player Player : Players) {
            if (Snowball.getWorld() != Player.getWorld()) { continue; }
            if (Snowball.getLocation().distance(Player.getLocation()) < 3) {
                Player.addPotionEffect(new PotionEffect(PotionEffectType.LEVITATION, 100, 0));
            }
        }

        FloatationBombs.remove(Snowball);
    }

    private static void Untouchable(Player Player) {

        int[] i = {0};

        Player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 300, 1, false, false));

        new BukkitRunnable() {
            @Override
            public void run() {

                if (GameEnded || DeadPlayers.contains(Player)) { cancel(); return; }
                if (i[0] > 300) { return; }
                i[0]++;

                for (int j = 0; j < 20; j++) {
                    Location ParticleLocation = Player.getLocation().clone().add(new Vector(0, 1, 0));
                    ParticleLocation.setYaw((float) (180 - Math.random() * 360));
                    ParticleLocation.setPitch((float) (90 - Math.random() * 180));
                    ParticleLocation.add(ParticleLocation.getDirection().multiply(2));
                    Player.getWorld().spawnParticle(Particle.DUST, ParticleLocation, 1,
                            new Particle.DustOptions(Color.fromRGB((int) (30 + Math.random() * 50), 3, (int) (30 + Math.random() * 50)), 1));
                }

                for (Entity Entity : Player.getWorld().getEntities()) {
                    if (Player == Entity) { continue; }
                    if (Entity instanceof Arrow && ((Arrow) Entity).getShooter() == Player ) { continue; }

                    Location PlayerMiddle = Player.getLocation().clone().add(new Vector(0, 1, 0));

                    if (PlayerMiddle.distance(Entity.getLocation()) < 3) {
                        Vector ToTarget = Entity.getLocation().toVector().subtract(PlayerMiddle.toVector()).normalize();

                        // Makes the player get pushed back less than other entities
                        if (Entity instanceof Player) {
                            Entity.setVelocity(Entity.getVelocity().add(ToTarget));
                        }
                        else {
                            Entity.setVelocity(Entity.getVelocity().add(ToTarget.multiply(8)));
                        }
                    }
                }
            }
        }.runTaskTimer(BowGame.GetPlugin(), 0L, 1L);
    }
}
