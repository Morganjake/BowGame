package game.bow.bowgame.Classes;

import game.bow.bowgame.BowGame;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.Particle;
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
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static game.bow.bowgame.Classes.ClassHandler.*;
import static game.bow.bowgame.Classes.ClassHandler.AddUltPoints;
import static game.bow.bowgame.Game.GameHandler.BlueTeamHacked;
import static game.bow.bowgame.Game.GameHandler.RedTeamHacked;
import static game.bow.bowgame.Game.PlayerHandler.*;

public class Hacker implements Listener {

    @EventHandler
    public void OnLeftClick(PlayerInteractEvent Event) {

        Player Player = Event.getPlayer();
        ItemStack Item = Event.getItem();

        if (!Players.contains(Player)) { return; }

        if (Item == null || Item.getType() != Material.REDSTONE_TORCH) { return; }

        if (Event.getAction() == Action.LEFT_CLICK_AIR || Event.getAction() == Action.LEFT_CLICK_BLOCK) {

            if (Cooldowns.containsKey(Player)) {
                Player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText("§7§lYou still have " + Cooldowns.get(Player) + " seconds left!"));
                return;
            }

            PutPlayerOnCooldown(Player);

            ItemMeta BowMeta = Objects.requireNonNull(Player.getInventory().getItem(0)).getItemMeta();
            BowMeta.addEnchant(Enchantment.LUCK_OF_THE_SEA, 1, true);
            BowMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            Objects.requireNonNull(Player.getInventory().getItem(0)).setItemMeta(BowMeta);
        }

        else if ((Event.getAction() == Action.RIGHT_CLICK_AIR || Event.getAction() == Action.RIGHT_CLICK_BLOCK) && UltPoints.get(Player) == 5) {
            AddUltPoints(Player, -5);

            if (BlueTeam.contains(Player)) {
                RedTeamHacked = true;
            }
            else {
                BlueTeamHacked = true;
            }

            int[] i = {0};

            new BukkitRunnable() {

                @Override
                public void run() {

                    if (i[0] == 200) {
                        if (BlueTeam.contains(Player)) {
                            RedTeamHacked = false;
                        }
                        else {
                            BlueTeamHacked = false;
                        }
                        cancel();
                        return;
                    }
                    i[0]++;

                    List<Player> Team = BlueTeam.contains(Player) ? RedTeam : BlueTeam;

                    for (Player Enemy : Team) {
                        Player.getWorld().spawnParticle(Particle.ANGRY_VILLAGER, Enemy.getEyeLocation(), 1, 0.5, 0.5, 0.5);
                    }
                }

            }.runTaskTimer(BowGame.GetPlugin(), 0L, 1L);
        }
    }
}
