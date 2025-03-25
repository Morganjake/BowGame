package game.bow.bowgame.Classes;

import game.bow.bowgame.BowGame;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Objects;

import static game.bow.bowgame.Game.GameUIHandler.UpdateScoreBoard;

public class ClassHandler {

    public static HashMap<Player, String> Classes = new HashMap<>();
    public static HashMap<Player, Integer> Cooldowns = new HashMap<>();
    public static HashMap<Player, Integer> UltPoints = new HashMap<>();


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


    public static ItemStack GetClassItem(Player Player) {

        ItemStack Item = null;

        if (Objects.equals(Classes.get(Player), "Space Weaver")) {
            Item = new ItemStack(Material.ECHO_SHARD);
            ItemMeta ItemMeta = Item.getItemMeta();
            Objects.requireNonNull(ItemMeta).setItemName("§9Space Shard");
            Item.setItemMeta(ItemMeta);
        }
        else if (Objects.equals(Classes.get(Player), "Demolitionist")) {
            Item = new ItemStack(Material.GUNPOWDER);
            ItemMeta ItemMeta = Item.getItemMeta();
            Objects.requireNonNull(ItemMeta).setItemName("§cBoom Boom");
            Item.setItemMeta(ItemMeta);
        }
        else if (Objects.equals(Classes.get(Player), "Astronaut")) {
            Item = new ItemStack(Material.BREEZE_ROD);
            ItemMeta ItemMeta = Item.getItemMeta();
            Objects.requireNonNull(ItemMeta).setItemName("§dGravity Stick");
            Item.setItemMeta(ItemMeta);
        }
        else if (Objects.equals(Classes.get(Player), "Hacker")) {
            Item = new ItemStack(Material.REDSTONE_TORCH);
            ItemMeta ItemMeta = Item.getItemMeta();
            Objects.requireNonNull(ItemMeta).setItemName("§7Hacking Tool");
            Item.setItemMeta(ItemMeta);
        }

        return Item;
    }

    public static void PutPlayerOnCooldown(Player Player) {

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
}
