package game.bow.bowgame.Classes;

import game.bow.bowgame.BowGame;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Arrow;
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

import java.util.*;

import static game.bow.bowgame.Boilerplate.SandboxPlayers;
import static game.bow.bowgame.Classes.ClassHandler.*;
import static game.bow.bowgame.Classes.ClassHandler.AddUltPoints;
import static game.bow.bowgame.Game.GameHandler.*;
import static game.bow.bowgame.Game.PlayerHandler.*;
import static game.bow.bowgame.Upgrades.UpgradeHandler.PlayerUpgrades;

public class Mage implements Listener {

    public static HashMap<Player, Integer> SelectedSpell = new HashMap<>();
    public static List<Player> MagicOverloadActive = new ArrayList<>();

    @EventHandler
    public void OnLeftClick(PlayerInteractEvent Event) {

        Player Player = Event.getPlayer();
        ItemStack Item = Event.getItem();

        if (!Players.contains(Player)) { return; }

        if (Item == null || Item.getType() != Material.NETHERITE_SHOVEL) { return; }

        if (Event.getAction() == Action.LEFT_CLICK_AIR || Event.getAction() == Action.LEFT_CLICK_BLOCK) {

            if (Player.isSneaking()) {
                SwitchSpell(Player);
                return;
            }

            ItemMeta BowMeta = Objects.requireNonNull(Player.getInventory().getItem(0)).getItemMeta();

            Enchantment BowEnchant = switch (SelectedSpell.get(Player)) {
                case 1 -> Enchantment.AQUA_AFFINITY;
                case 2 -> Enchantment.BANE_OF_ARTHROPODS;
                default -> Enchantment.BLAST_PROTECTION;
            };

            if (BowEnchant == Enchantment.BANE_OF_ARTHROPODS && IllusionArrows.containsKey(Player) &&
                    !IllusionArrows.get(Player).isEmpty() && !MagicOverloadActive.contains(Player)) {
                ShootIllusionArrow(Player, IllusionArrows.get(Player).get(0));
                IllusionArrows.replace(Player, new ArrayList<>());
                Objects.requireNonNull(Player.getInventory().getItem(0)).removeEnchantments();
                return;
            }

            if (Cooldowns.containsKey(Player)) {
                Player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText("§7§lYou still have " + Cooldowns.get(Player) + " seconds left!"));
                return;
            }

            PutPlayerOnCooldown(Player);

            Objects.requireNonNull(BowMeta).addEnchant(BowEnchant, 1, true);
            BowMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            Objects.requireNonNull(Player.getInventory().getItem(0)).setItemMeta(BowMeta);

        }

        else if ((Event.getAction() == Action.RIGHT_CLICK_AIR || Event.getAction() == Action.RIGHT_CLICK_BLOCK) &&
                (UltPoints.get(Player) == 6) || SandboxPlayers.contains(Player)) {
            AddUltPoints(Player, -6);

            MagicOverloadActive.add(Player);

            Map<String, Integer> NewPlayerUpgrades = new HashMap<>(PlayerUpgrades.get(Player));
            NewPlayerUpgrades.put("Cooldown", NewPlayerUpgrades.get("Cooldown") / 2);

            PlayerUpgrades.put(Player, NewPlayerUpgrades);
        }
    }

    private static void SwitchSpell(Player Player) {

        if (SelectedSpell.get(Player) == 3) {
            Player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText("§d§lSwitched spell to Confusion Arrow!"));
            SelectedSpell.put(Player, 1);
        }
        else if (SelectedSpell.get(Player) == 1) {
            Player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText("§d§lSwitched spell to Illusion Arrow!"));
            SelectedSpell.put(Player, 2);
        }
        else if (SelectedSpell.get(Player) == 2) {
            Player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText("§d§lSwitched spell to Invisibility Arrow!"));
            SelectedSpell.put(Player, 3);
        }
    }

    public static void ConfusionArrow(Player Victim) {
        final int[] i = {0};

        new BukkitRunnable() {

            @Override
            public void run() {

                if (i[0] > 200) {
                    cancel();
                    return;
                }
                else {
                    i[0]++;
                }

                if (Math.random() < 0.7) {

                    Vector SoundOffset = new Vector(
                            Math.random() * (1 - Math.floor(Math.random() * 2) * 2) * 5,
                            Math.random() * (1 - Math.floor(Math.random() * 2) * 2) * 5,
                            Math.random() * (1 - Math.floor(Math.random() * 2) * 2) * 5
                    );

                    if (Math.random() < 0.15) {
                        Victim.playSound(Victim.getLocation().add(SoundOffset), Sound.BLOCK_WOOD_STEP, 1, 1);
                    }
                    else if (Math.random() < 0.3) {
                        Victim.playSound(Victim.getLocation().add(SoundOffset), Sound.BLOCK_SAND_STEP, 1, 1);
                    }
                    else if (Math.random() < 0.45) {
                        Victim.playSound(Victim.getLocation().add(SoundOffset), Sound.BLOCK_SNOW_STEP, 1, 1);
                    }
                    else {
                        Victim.playSound(Victim.getLocation().add(SoundOffset), Sound.BLOCK_STONE_STEP, 1, 1);
                    }
                }

                if (Math.random() < 0.08) {

                    Vector SoundOffset = new Vector(
                            Math.random() * (1 - Math.floor(Math.random() * 2) * 2) * 5,
                            Math.random() * (1 - Math.floor(Math.random() * 2) * 2) * 5,
                            Math.random() * (1 - Math.floor(Math.random() * 2) * 2) * 5
                    );

                    Victim.playSound(Victim.getLocation().add(SoundOffset), Sound.ENTITY_ARROW_SHOOT, 1, 1);
                }

                if (Math.random() < 0.05) {

                    Vector SoundOffset = new Vector(
                            Math.random() * (1 - Math.floor(Math.random() * 2) * 2) * 5,
                            Math.random() * (1 - Math.floor(Math.random() * 2) * 2) * 5,
                            Math.random() * (1 - Math.floor(Math.random() * 2) * 2) * 5
                    );

                    Victim.playSound(Victim.getLocation().add(SoundOffset), Sound.ENTITY_GENERIC_HURT, 1, 1);
                }

                if (Math.random() < 0.05) {
                    Victim.addPotionEffect(new PotionEffect(PotionEffectType.DARKNESS, 50, 0, false, false));
                }

                if (Math.random() < 0.025) {
                    Victim.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 30, 0, false, false));
                }
            }
        }.runTaskTimer(BowGame.GetPlugin(), 0, 1L);
    }

    public static void ShootIllusionArrow(Player Player, Arrow Arrow) {

        Arrow NewArrow = Arrow.getWorld().spawnArrow(
                Arrow.getLocation(),
                Arrow.getVelocity(),
                (float) Arrow.getVelocity().length(),
                0
        );

        ArrowBounces.put(NewArrow, PlayerUpgrades.get(Player).get("Bounce"));

        NewArrow.setShooter(Player);

    }
}
