package game.bow.bowgame.Game;


import game.bow.bowgame.BowGame;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.Objects;

import static game.bow.bowgame.Game.PlayerHandler.*;

public class GameHandler {

    public static int BlueScore = 0;
    public static int RedScore = 0;

    public static Location[][] Maps = {
            {
                    new Location(Bukkit.getWorld("world"), -1141, 90, -191, 90, 0),
                    new Location(Bukkit.getWorld("world"), -1195, 90, -191, -90, 0)
            },
            {
                    new Location(Bukkit.getWorld("world"), -1141, 95, -225, 90, 0),
                    new Location(Bukkit.getWorld("world"), -1195, 95, -225, -90, 0)
            },
            {
                    new Location(Bukkit.getWorld("world"), -1141, 99, -259, 90, 0),
                    new Location(Bukkit.getWorld("world"), -1195, 99, -259, -90, 0)
            }
    };


    public static void StartGame() {

        boolean PutOnBlueTeam = true;

        for (Player Player : Bukkit.getOnlinePlayers()) {

            if (PutOnBlueTeam) {
                AddPlayerToGame(Player, "Blue");
            }
            else {
                AddPlayerToGame(Player, "Red");
            }

            PutOnBlueTeam = !PutOnBlueTeam;
        }

        Bukkit.broadcastMessage(BlueTeam.toString());
        Bukkit.broadcastMessage(RedTeam.toString());

        NextRound(2);
    }


    public static void StopGame() {

        for (Player Player : Players) {
            Player.getInventory().clear();
        }

        Players = new ArrayList<>();
        BlueTeam = new ArrayList<>();
        RedTeam = new ArrayList<>();
    }


    public static void NextRound(int Winner) {

        if (Winner == 0) {
            BlueScore += 1;
        }
        else if (Winner == 1) {
           RedScore += 1;
        }

        Bukkit.broadcastMessage("Blue score: " + BlueScore);
        Bukkit.broadcastMessage("Red score: " + RedScore);

        int NextMap = (int) Math.floor(Math.random() * Maps.length);

        for (Player Player : BlueTeam) {
            Player.teleport(Maps[NextMap][0]);
            ResetPlayer(Player);
        }

        for (Player Player : RedTeam) {
            Player.teleport(Maps[NextMap][1]);
            ResetPlayer(Player);
        }

        new BukkitRunnable() {

            @Override
            public void run() {

                // Checks if all the players of a team is dead
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
                    cancel();
                }

                else if (RedTeamDead) {
                    cancel();
                }

                for (Player Player : Players) {

                    if (Player.getGameMode() == GameMode.SPECTATOR) { continue; }

                    Inventory Inventory = Player.getInventory();

                    if (Inventory.getItem(1) == null || Objects.requireNonNull(Inventory.getItem(1)).getAmount() <= 64) {
                        Inventory.addItem(new ItemStack(Material.ARROW));
                    }
                }
            }
        }.runTaskTimer(BowGame.GetPlugin(), 40L, 20L);
    }
}
