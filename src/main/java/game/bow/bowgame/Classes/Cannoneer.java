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
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

import static game.bow.bowgame.Boilerplate.SandboxPlayers;
import static game.bow.bowgame.Classes.ClassHandler.*;
import static game.bow.bowgame.Classes.ClassHandler.AddUltPoints;
import static game.bow.bowgame.Game.PlayerHandler.BlueTeam;
import static game.bow.bowgame.Game.PlayerHandler.Players;

public class Cannoneer implements Listener {

    public static final HashMap<Player, Integer> CannonStrength = new HashMap<>();
    public static ArrayList<Player> PlayersUsingCannon = new ArrayList<>();
    public static HashMap<Player, Integer> CannoneerHitCount = new HashMap<>();

    // Used to save where the player was looking at for the cannon delay
    public static final HashMap<Player, Location> SavedLocation = new HashMap<>();

    @EventHandler
    public void OnLeftClick(PlayerInteractEvent Event) {

        Player Player = Event.getPlayer();
        ItemStack Item = Event.getItem();

        if (!Players.contains(Player)) { return; }

        if (Item == null || Item.getType() != Material.BLACK_DYE) { return; }

        if (Event.getAction() == Action.LEFT_CLICK_AIR || Event.getAction() == Action.LEFT_CLICK_BLOCK) {

            if (Cooldowns.containsKey(Player)) {
                Player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText("§7§lYou still have " + Cooldowns.get(Player) + " seconds left!"));
                return;
            }

            PutPlayerOnCooldown(Player);

            ItemMeta BowMeta = Objects.requireNonNull(Player.getInventory().getItem(0)).getItemMeta();
            BowMeta.addEnchant(Enchantment.MENDING, 1, true);
            BowMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            Objects.requireNonNull(Player.getInventory().getItem(0)).setItemMeta(BowMeta);
        }

        else if ((Event.getAction() == Action.RIGHT_CLICK_AIR || Event.getAction() == Action.RIGHT_CLICK_BLOCK) &&
                (UltPoints.get(Player) > 0) || SandboxPlayers.contains(Player)) {

            if (CannonStrength.get(Player) < 6) {
                AddUltPoints(Player, -1);
                CannonStrength.replace(Player, CannonStrength.get(Player) + 1);
                Player.getWorld().playSound(Player, Sound.BLOCK_NOTE_BLOCK_BASS, 1, 0.5f + CannonStrength.get(Player) * 0.3f);

                if (CannonStrength.get(Player) == 6) {
                    Player.playSound(Player, Sound.ITEM_TOTEM_USE, 1, 1);
                }
            }
            else {
                Player.sendMessage("§e§lYour cannon is at maxed power!!");
                Player.playSound(Player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1, 1);
            }
        }
    }

    public static void ShootCannon(Player Player, Integer Power) {


        if (!SavedLocation.containsKey(Player)) { return; }

        Location Location = Player.getEyeLocation().clone();
        Vector Direction = Location.clone().getDirection().normalize();
        World World = Location.getWorld();

        new BukkitRunnable() {

            @Override
            public void run() {

                World.playSound(Location, Sound.ENTITY_GENERIC_EXPLODE, 1, 1);

                ArrayList<Player> HitEnemies = new ArrayList<>();

                for (int i = 0; i < 200; i++) {
                    Location.add(Direction.clone());

                    if (i % 2 == 0) {
                        if (Power < 2) {
                            World.spawnParticle(Particle.EXPLOSION, Location, 1);
                        }
                        else if (Power < 4) {
                            World.spawnParticle(Particle.EXPLOSION, Location, 3, 0.5, 0.5, 0.5);
                        }
                        else if (Power < 6) {
                            World.spawnParticle(Particle.EXPLOSION, Location, 6, 1, 1, 1);
                        }
                        else {
                            World.spawnParticle(Particle.EXPLOSION_EMITTER, Location, 2);
                        }
                    }

                    if (Power < 2) {
                        int DoNothing = 0;
                    }
                    else if (Power < 4) {
                        World.spawnParticle(Particle.SMOKE, Location, 3, 0.5, 0.5, 0.5, 0.2);
                    }
                    else if (Power < 6) {
                        World.spawnParticle(Particle.SMOKE, Location, 6, 0.5, 0.5, 0.5, 0.2);
                    }
                    else {
                        World.spawnParticle(Particle.SMOKE, Location, 10, 2, 2, 2, 0.2);
                    }

                    for (Player OtherPlayer : Players) {
                        if (BlueTeam.contains(Player) == BlueTeam.contains(OtherPlayer)) { continue; }

                        if (Location.distance(OtherPlayer.getLocation().add(new Vector(0, 1, 0))) < (double) (Power + 2) / 2.0 && !HitEnemies.contains(OtherPlayer)) {
                            HitEnemies.add(OtherPlayer);
                            OtherPlayer.damage((double) 10 + (Power) * 6, Player);
                        }
                    }
                }

                CannonStrength.replace(Player, 0);
                SavedLocation.remove(Player);
            }
        }.runTaskLater(BowGame.GetPlugin(), 4L * Power);
    }

    @EventHandler
    public void OnPlayerDrawbackBow(PlayerInteractEvent Event) {
        Player Player = Event.getPlayer();
        if (!Players.contains(Player)) { return; }
        if (!CannonStrength.containsKey(Player)) { return; }

        ItemStack Item = Event.getItem();
        if (Item == null) { return; }

        if (Item.getType() == Material.BOW && (Event.getAction() == Action.RIGHT_CLICK_AIR || Event.getAction() == Action.RIGHT_CLICK_BLOCK)) {
            if (Item.getEnchantments().containsKey(Enchantment.MENDING)) {

                if (PlayersUsingCannon.contains(Player)) {
                    return;
                }

                PlayersUsingCannon.add(Player);

                int ParticleCount = switch (CannonStrength.get(Player)) {
                    case 0, 1 -> 4;
                    case 2, 3 -> 5;
                    case 4, 5 -> 6;
                    case 6 -> 8;
                    default -> 0;
                };

                final int[] Offset = {0};

                new BukkitRunnable() {

                    @Override
                    public void run() {

                        if (Player.getInventory().getItemInMainHand().getType() != Material.BOW && !SavedLocation.containsKey(Player)) {
                            PlayersUsingCannon.remove(Player);
                            cancel();
                            return;
                        }

                        if (!PlayersUsingCannon.contains(Player) && !SavedLocation.containsKey(Player)) {
                            cancel();
                            return;
                        }

                        int InnerParticleCount = ParticleCount;

                        Location Location;


                        if (SavedLocation.containsKey(Player)) {
                            Location = SavedLocation.get(Player).clone();
                            InnerParticleCount *= 2;
                            Offset[0] += 15;
                        }
                        else {
                            Location = Player.getEyeLocation();
                            Offset[0] += 3;
                        }

                        Vector Direction = Location.getDirection().normalize();

                        Vector Right = Direction.clone().crossProduct(new Vector(0, 1, 0)).normalize();
                        Vector PerpendicularUp = Right.clone().crossProduct(Direction).normalize();

                        for (int j = 0; j < 50; j++) {
                            Location.add(Direction.clone().multiply(4));

                            Particle.DustOptions DustColour;

                            if (SavedLocation.containsKey(Player)) {
                                DustColour = new Particle.DustOptions(Color.RED, 2);
                            }
                            else {
                                if (j % 2 == 0) {
                                    DustColour = new Particle.DustOptions(Color.BLACK, 1);
                                }
                                else {
                                    DustColour = new Particle.DustOptions(Color.YELLOW, 1);
                                }
                            }

                            for (int i = 0; i < InnerParticleCount; i++) {

                                double Angle = Math.toRadians(((double) 360 / InnerParticleCount) * i);
                                Angle += j % 2 == 0 ? Offset[0] : -Offset[0];

                                double X = Math.cos(Angle) * (CannonStrength.get(Player) + 1) / 2;
                                double Y = Math.sin(Angle) * (CannonStrength.get(Player) + 1) / 2;

                                Vector Offset = Right.clone().multiply(X).add(PerpendicularUp.clone().multiply(Y));
                                Location ParticleLocation = Location.clone().add(Offset);

                                Player.getWorld().spawnParticle(Particle.DUST, ParticleLocation, 1, DustColour);
                            }
                        }
                    }
                }.runTaskTimer(BowGame.GetPlugin(), 0L, 1L);
            }
        }
    }

    public static void AddSlowness(Player Victim) {

        if (CannoneerHitCount.containsKey(Victim)) {
            CannoneerHitCount.replace(Victim, CannoneerHitCount.get(Victim) + 1);
        }
        else {
            CannoneerHitCount.put(Victim, 1);
        }

        if (CannoneerHitCount.get(Victim) % 3 == 0) {
            Victim.playSound(Victim, Sound.BLOCK_ANVIL_LAND, 1, 1);
            Victim.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 200, CannoneerHitCount.get(Victim) / 3, false ,false));
        }
    }
}
