package game.bow.bowgame.Classes;

import game.bow.bowgame.BowGame;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.Objects;

import static game.bow.bowgame.Boilerplate.SandboxPlayers;
import static game.bow.bowgame.Game.GameUIHandler.UpdateScoreBoard;
import static game.bow.bowgame.Game.PlayerHandler.Players;

public class ClassHandler implements Listener {

    public static HashMap<Player, String> Classes = new HashMap<>();
    public static HashMap<Player, ItemStack> ClassWeapons = new HashMap<>();
    public static HashMap<Player, Integer> Cooldowns = new HashMap<>();
    public static HashMap<Player, Integer> UltPoints = new HashMap<>();

    public static boolean UltOrbTaken = false;


    public static void AddUltPoints(Player Player, int Amount) {
        UltPoints.replace(Player, Math.max(0, Math.min(UltPoints.get(Player) + Amount, 6)));
        Player.setExp((float) UltPoints.get(Player) * 17 / 6 / 17);
        Player.setLevel(UltPoints.get(Player));

        // Ultimate is ready
        if (UltPoints.get(Player) == 6) {
            Player.playSound(Player, Sound.ITEM_FIRECHARGE_USE, 1, 1);
            Player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText("§b§lYour ultimate is ready!"));
        }
        UpdateScoreBoard();
    }

    public static void PutPlayerOnCooldown(Player Player) {

        if (SandboxPlayers.contains(Player)) {
            return;
        }

        Cooldowns.put(Player, 30);

        new BukkitRunnable() {

            @Override
            public void run() {

                if (!Cooldowns.containsKey(Player)) {
                    cancel();
                }

                else if (Cooldowns.get(Player) == 0) {
                    Player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText("§2§lYour ability is off cooldown!"));
                    Cooldowns.remove(Player);
                    cancel();
                }

                else if (Cooldowns.containsKey(Player)) {
                    Cooldowns.replace(Player, Cooldowns.get(Player) - 1);
                }
            }

        }.runTaskTimer(BowGame.GetPlugin(), 20L, 20L);
    }

    public static void SetUltOrb(Location OrbLocation) {
        UltOrbTaken = false;

        new BukkitRunnable() {
            @Override
            public void run() {

                if (UltOrbTaken) {
                    cancel();
                    return;
                }

                for (int j = 0; j < 10; j++) {
                    Location ParticleLocation = OrbLocation.clone().add(new Vector(0, 0.5, 0));
                    ParticleLocation.setYaw((float) (180 - Math.random() * 360));
                    ParticleLocation.setPitch((float) (90 - Math.random() * 180));
                    ParticleLocation.add(ParticleLocation.getDirection().multiply(0.25));
                    Objects.requireNonNull(OrbLocation.getWorld()).spawnParticle(Particle.DUST, ParticleLocation, 1, new Particle.DustOptions(Color.FUCHSIA, 1));
                }
            }
        }.runTaskTimer(BowGame.GetPlugin(), 0L, 1L);
    }

    @EventHandler
    public static void OnToggleCrouch(PlayerToggleSneakEvent Event) {
        Player Player = Event.getPlayer();
        if (UltOrbTaken) { return; }
        if (!Players.contains(Player)) { return; }
        if (Player.getGameMode() != GameMode.ADVENTURE) { return; }

        int[] SneakingDuration = {0};

        // For some reason isSneaking() is true when the player is standing up
        if (!Player.isSneaking() && Player.getWorld().getBlockAt(Player.getLocation().add(new Vector(0, -1, 0))).getType() == Material.DIAMOND_BLOCK) {

            new BukkitRunnable() {

                @Override
                public void run() {
                    if (!Player.isSneaking() || Player.getWorld().getBlockAt(Player.getLocation().add(new Vector(0, -1, 0))).getType() != Material.DIAMOND_BLOCK) {
                        // Adds 0 ult points to the player to reset their exp bar to their ult point count
                        AddUltPoints(Player, 0);
                        cancel();
                        return;
                    }

                    SneakingDuration[0]++;
                    if (SneakingDuration[0] == 60) {
                        UltOrbTaken = true;
                        AddUltPoints(Player, 1);
                        Player.getWorld().playSound(Player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1, 1);
                        cancel();
                    }
                    else {
                        Player.setExp((float) SneakingDuration[0] / 60);
                        Player.playSound(Player, Sound.BLOCK_NOTE_BLOCK_BASS, 1, 1 + (float) SneakingDuration[0] / 60);
                    }
                }
            }.runTaskTimer(BowGame.GetPlugin(), 0L, 1L);
        }
    }
}
