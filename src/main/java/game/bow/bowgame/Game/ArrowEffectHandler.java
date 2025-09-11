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
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;

import static game.bow.bowgame.Classes.Cannoneer.*;
import static game.bow.bowgame.Classes.ClassHandler.ClassWeapons;
import static game.bow.bowgame.Classes.Mage.*;
import static game.bow.bowgame.Classes.SpaceWeaver.SpaceManipulation;
import static game.bow.bowgame.Classes.SpaceWeaver.SpaceManipulationPlayers;
import static game.bow.bowgame.Game.GameHandler.*;
import static game.bow.bowgame.Game.PlayerHandler.Players;
import static game.bow.bowgame.Game.PlayerHandler.SetPlayerArmour;
import static game.bow.bowgame.GUIs.UpgradeHandler.PlayerUpgrades;

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

            if (!ArrowBounces.containsKey(Arrow)) {
                ExplosiveArrows.remove(Arrow);
            }
        }

        if (SpaceManipulationPlayers.contains(Player) && (!ArrowBounces.containsKey(Arrow) || ArrowBounces.get(Arrow) == 0) && !DirectHit) {
            SpaceManipulation(Player, Arrow);
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
                    OtherPlayer.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 200, 0, false, false));

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
            }.runTaskLater(BowGame.GetPlugin(), 200L);

            InvisibilityArrows.remove(Arrow);
        }
    }

    public static boolean CheckForEnchant(Player Player, ItemStack Bow, Arrow Arrow, float Force) {
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

        else if (Bow.containsEnchantment(Enchantment.MENDING)) {

            PlayersUsingCannon.remove(Player);

            if (Force > 2.9) {
                SavedLocation.put(Player, Player.getEyeLocation().clone());
                ShootCannon(Player, CannonStrength.get(Player));
            }
            else {
                Player.damage((double) 10 + (CannonStrength.get(Player)) * 6, Player);
                Player.getWorld().spawnParticle(Particle.EXPLOSION_EMITTER, Player.getLocation(), 1);
                Player.getWorld().playSound(Player.getLocation(), Sound.ENTITY_GENERIC_EXPLODE, 1, 1);
                Player.getVelocity().add(new Vector(0, 1, 0));
            }

            Bow.removeEnchantments();
            return true;
        }

        return false;
    }
}
