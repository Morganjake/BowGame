package game.bow.bowgame.Game;

import game.bow.bowgame.BowGame;
import org.bukkit.*;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.checkerframework.checker.units.qual.A;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static game.bow.bowgame.Classes.ClassHandler.ClassWeapons;
import static game.bow.bowgame.Classes.Mage.*;
import static game.bow.bowgame.Game.GameHandler.*;
import static game.bow.bowgame.Game.PlayerHandler.Players;
import static game.bow.bowgame.Game.PlayerHandler.SetPlayerArmour;
import static game.bow.bowgame.Upgrades.UpgradeHandler.PlayerUpgrades;

public class ArrowEffectHandler {

    public static void CheckForEffects(Player Player, Player Victim, Arrow Arrow, Boolean DirectHit) {
        if (ExplosiveArrows.contains(Arrow)) {

            for (Player OtherPlayer : Players) {
                if (OtherPlayer == Victim) { continue; }
                if (Arrow.getLocation().distance(OtherPlayer.getLocation()) < 2) {
                    OtherPlayer.damage((double) PlayerUpgrades.get(Player).get("Damage") + 5, Player);
                }
            }

            Arrow.getWorld().spawnParticle(Particle.EXPLOSION_EMITTER, Arrow.getLocation(), 1);
            Arrow.getWorld().playSound(Arrow.getLocation(), Sound.ENTITY_GENERIC_EXPLODE, 1, 1);
            ExplosiveArrows.remove(Arrow);
        }

        else if (DirectHit && Victim != null && DisablingArrows.contains(Arrow)) {

            Victim.getInventory().setItem(1, new ItemStack(Material.BARRIER));

            Bukkit.getScheduler().runTaskLater(BowGame.GetPlugin(), () -> {
                Victim.getInventory().setItem(1, ClassWeapons.get(Victim));
            }, 200L);
        }

        else if (DirectHit && Victim != null && ConfusionArrows.contains(Arrow)) {
            ConfusionArrow(Victim);
        }

        else if (InvisibilityArrows.contains(Arrow)) {

            List<Player> InvisiblePlayers = new ArrayList<>();

            Arrow.getWorld().playSound(Arrow.getLocation(), Sound.ENTITY_SPLASH_POTION_BREAK, 1, 1);

            for (Player OtherPlayer : Players) {
                if (Arrow.getLocation().distance(OtherPlayer.getLocation()) < 2) {
                    InvisiblePlayers.add(OtherPlayer);
                    OtherPlayer.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 300, 0, false, false));

                    OtherPlayer.getInventory().setHelmet(null);
                    OtherPlayer.getInventory().setChestplate(null);
                    OtherPlayer.getInventory().setLeggings(null);
                    OtherPlayer.getInventory().setBoots(null);
                }
            }

            new BukkitRunnable() {
                @Override
                public void run() {
                    for (Player InvisiblePlayer : InvisiblePlayers) {
                        SetPlayerArmour(InvisiblePlayer);
                    }
                }
            }.runTaskLater(BowGame.GetPlugin(), 300L);

            InvisibilityArrows.remove(Arrow);
        }
    }

    public static boolean CheckForEnchant(Player Player, ItemStack Bow, Arrow Arrow) {
        if (Bow.containsEnchantment(Enchantment.LURE)) {
            ExplosiveArrows.add(Arrow);
            Bow.removeEnchantments();
        }

        else if (Bow.containsEnchantment(Enchantment.LUCK_OF_THE_SEA)) {
            DisablingArrows.add(Arrow);
            Bow.removeEnchantments();
        }

        else if (Bow.containsEnchantment(Enchantment.AQUA_AFFINITY)) {
            ConfusionArrows.add(Arrow);
            Bow.removeEnchantments();
        }

        else if (Bow.containsEnchantment(Enchantment.BANE_OF_ARTHROPODS)) {

            if (IllusionArrows.containsKey(Player)) {

                List<Arrow> NewIllusionArrows = new ArrayList<>(IllusionArrows.get(Player));
                NewIllusionArrows.add(Arrow);
                IllusionArrows.put(Player, NewIllusionArrows);
            }
            else {
                IllusionArrows.put(Player, List.of(Arrow));
            }

            Bow.removeEnchantments();
            return true;
        }

        else if (Bow.containsEnchantment(Enchantment.BLAST_PROTECTION)) {
            InvisibilityArrows.add(Arrow);
            Arrow.getWorld().playSound(Arrow.getLocation(), Sound.ENTITY_SPLASH_POTION_THROW, 1, 1);
            Bow.removeEnchantments();
        }

        return false;
    }
}
