package game.bow.bowgame.Game;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static game.bow.bowgame.Game.GameHandler.NextRound;

public class PlayerHandler implements Listener {

    public static List<Player> Players = new ArrayList<>();
    public static List<Player> BlueTeam = new ArrayList<>();
    public static List<Player> RedTeam = new ArrayList<>();


    public static void AddPlayerToGame(Player Player, String Team) {

        Player.setGameMode(GameMode.ADVENTURE);

        if (Objects.equals(Team, "Blue")) {
            Players.add(Player);
            BlueTeam.add(Player);
        }
        else {
            Players.add(Player);
            RedTeam.add(Player);
        }
    }


    public static void ResetPlayer(Player Player) {

        Player.setGameMode(GameMode.ADVENTURE);
        Player.setHealth(20);
        Player.setFoodLevel(20);

        for (PotionEffect Potion : Player.getActivePotionEffects()) {
            Player.removePotionEffect(Potion.getType());
        }

        Inventory Inventory = Player.getInventory();
        Player.getInventory().clear();

        Inventory.setItem(0, new ItemStack(Material.BOW));

        Inventory.setItem(3, new ItemStack(Material.EMERALD));
        Inventory.setItem(4, new ItemStack(Material.EMERALD));
        Inventory.setItem(5, new ItemStack(Material.EMERALD));

        ItemMeta Item1Meta = Objects.requireNonNull(Inventory.getItem(3)).getItemMeta();
        ItemMeta Item2Meta = Objects.requireNonNull(Inventory.getItem(4)).getItemMeta();
        ItemMeta Item3Meta = Objects.requireNonNull(Inventory.getItem(5)).getItemMeta();

        Objects.requireNonNull(Item1Meta).setDisplayName("Item 1");
        Objects.requireNonNull(Item2Meta).setDisplayName("Item 2");
        Objects.requireNonNull(Item3Meta).setDisplayName("Item 3");

        Objects.requireNonNull(Inventory.getItem(3)).setItemMeta(Item1Meta);
        Objects.requireNonNull(Inventory.getItem(4)).setItemMeta(Item2Meta);
        Objects.requireNonNull(Inventory.getItem(5)).setItemMeta(Item3Meta);
    }


    @EventHandler
    public static void OnDamage(EntityDamageEvent Event) {

        if (!(Event.getEntity() instanceof Player)) { return; }
        Player Player = (Player) Event.getEntity();
        if (!Players.contains(Player)) { return; }
        if (Player.getHealth() - Event.getFinalDamage() > 0) { return; }

        Event.setCancelled(true);

        Player.setGameMode(GameMode.SPECTATOR);

        Bukkit.broadcastMessage(Player + " died");

        boolean BlueTeamDead = true;
        boolean RedTeamDead = true;

        for (Player BluePlayer : BlueTeam) {
            if (BluePlayer.getGameMode() == GameMode.ADVENTURE) {
                BlueTeamDead = false;
                break;
            }
        }

        for (Player RedPlayer : RedTeam) {
            if (RedPlayer.getGameMode() == GameMode.ADVENTURE) {
                RedTeamDead = false;
                break;
            }
        }

        if (BlueTeamDead) {
            Bukkit.broadcastMessage("Blue team lost");
            NextRound(1);
        }

        else if (RedTeamDead) {
            Bukkit.broadcastMessage("Red team lost");
            NextRound(0);
        }
    }
}
