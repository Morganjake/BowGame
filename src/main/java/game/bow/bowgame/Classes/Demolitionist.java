package game.bow.bowgame.Classes;

import game.bow.bowgame.BowGame;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.*;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static game.bow.bowgame.Boilerplate.SandboxPlayers;
import static game.bow.bowgame.Classes.ClassHandler.*;
import static game.bow.bowgame.Classes.ClassHandler.PutPlayerOnCooldown;
import static game.bow.bowgame.Game.GameHandler.GameEnded;
import static game.bow.bowgame.Game.PlayerHandler.*;

public class Demolitionist implements Listener {

    @EventHandler
    public void OnLeftClick(PlayerInteractEvent Event) {

        Player Player = Event.getPlayer();
        ItemStack Item = Event.getItem();

        if (!Players.contains(Player)) { return; }

        if (Item == null || Item.getType() != Material.GUNPOWDER) { return; }

        if (Event.getAction() == Action.LEFT_CLICK_AIR || Event.getAction() == Action.LEFT_CLICK_BLOCK) {

            if (Cooldowns.containsKey(Player)) {
                Player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText("§7§lYou still have " + Cooldowns.get(Player) + " seconds left!"));
                return;
            }
            PutPlayerOnCooldown(Player);

            ItemMeta BowMeta = Objects.requireNonNull(Player.getInventory().getItem(0)).getItemMeta();
            BowMeta.addEnchant(Enchantment.LURE, 1, true);
            BowMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            Objects.requireNonNull(Player.getInventory().getItem(0)).setItemMeta(BowMeta);
        }

        else if ((Event.getAction() == Action.RIGHT_CLICK_AIR || Event.getAction() == Action.RIGHT_CLICK_BLOCK) &&
                (UltPoints.get(Player) == 6) || SandboxPlayers.contains(Player)) {
            AddUltPoints(Player, -6);
            AirStrike(Player);
        }
    }

    private static void AirStrike(Player Player) {

        List<Player> Team;

        if (BlueTeam.contains(Player)) {
            Team = RedTeam;
        }
        else {
            Team = BlueTeam;
        }

        List<Player> EnemyTeam = Team;

        int[] AirStrikeCount = {0};
        int[] AirStrikeTickCount = {0};

        List<Location> AirStrikeLocations = new ArrayList<>();

        new BukkitRunnable() {

            @Override
            public void run() {

                if (GameEnded) {
                    cancel();
                    return;
                }
                if (AirStrikeCount[0] > 5) {
                    cancel();
                    return;
                }
                AirStrikeCount[0]++;

                for (Player Enemy : EnemyTeam) {

                    if (Enemy.getGameMode() == GameMode.SPECTATOR) { continue; }

                    AirStrikeLocations.add(Enemy.getLocation());

                    for (int i = 0; i < 18; i++) {
                        Location ParticleLocation = Enemy.getLocation().add(new Vector(0, 0.2, 0));
                        ParticleLocation.setYaw(i * 20);
                        ParticleLocation.add(ParticleLocation.getDirection().multiply(0.75));
                        Player.getWorld().spawnParticle(Particle.DUST, ParticleLocation, 1, new Particle.DustOptions(Color.RED, 2));
                    }
                }
            }

        }.runTaskTimer(BowGame.GetPlugin(), 40L, 40L);

        new BukkitRunnable() {

            @Override
            public void run() {

                if (GameEnded) {
                    cancel();
                    return;
                }
                if (AirStrikeCount[0] > 5) {
                    cancel();
                    return;
                }

                for (Location AirStrikeLocation : AirStrikeLocations) {
                    Player.getWorld().spawnParticle(Particle.EXPLOSION, AirStrikeLocation, 1);
                    Player.getWorld().spawnParticle(Particle.SMOKE, AirStrikeLocation, 50, 0, 0, 0, 0.2);
                    Player.getWorld().spawnParticle(Particle.LARGE_SMOKE, AirStrikeLocation, 50, 0, 0, 0, 0.2);
                    Player.getWorld().playSound(AirStrikeLocation, Sound.ENTITY_GENERIC_EXPLODE, 1, 1);

                    for (Player OtherPlayer : Players) {
                        if (AirStrikeLocation.distance(OtherPlayer.getLocation()) < 1.5) {
                            OtherPlayer.damage(12, Player);
                        }
                    }
                }

                AirStrikeLocations.clear();
            }

        }.runTaskTimer(BowGame.GetPlugin(), 50L, 40L);

        new BukkitRunnable() {

            @Override
            public void run() {

                if (GameEnded) {
                    cancel();
                    return;
                }
                if (AirStrikeTickCount[0] > 100) {
                    cancel();
                    return;
                }
                AirStrikeTickCount[0]++;

                for (Player Enemy : EnemyTeam) {
                    Enemy.getWorld().spawnParticle(Particle.DUST, Enemy.getLocation(), 1, new Particle.DustOptions(Color.RED, 1));
                    Enemy.playSound(Enemy, Sound.BLOCK_NOTE_BLOCK_PLING, 0.2f, 1);
                }
            }
        }.runTaskTimer(BowGame.GetPlugin(), 0L, 2L);
    }
}
