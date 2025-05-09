package game.bow.bowgame.Game;


import game.bow.bowgame.BowGame;
import game.bow.bowgame.Upgrades.MainUpgradesGUI;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import static game.bow.bowgame.Boilerplate.SandboxPlayers;
import static game.bow.bowgame.Classes.ClassHandler.*;
import static game.bow.bowgame.Classes.Mage.MagicOverloadActive;
import static game.bow.bowgame.Classes.Mage.ShootIllusionArrow;
import static game.bow.bowgame.Classes.SpaceWeaver.*;
import static game.bow.bowgame.Commands.StartSelectPlayers;
import static game.bow.bowgame.Game.ArrowEffectHandler.CheckForEffects;
import static game.bow.bowgame.Game.ArrowEffectHandler.CheckForEnchant;
import static game.bow.bowgame.Game.DeathMessagesHandler.DamageTaken;
import static game.bow.bowgame.Game.GameUIHandler.*;
import static game.bow.bowgame.Game.ItemHandler.SlowedPlayers;
import static game.bow.bowgame.Game.PlayerHandler.*;
import static game.bow.bowgame.Upgrades.ClassGUI.SelectClass;
import static game.bow.bowgame.Upgrades.UpgradeHandler.PlayerUpgrades;
import static game.bow.bowgame.Upgrades.UpgradeHandler.ResetStats;


public class GameHandler implements Listener {

    public static boolean GameEnded = false;
    public static int BlueScore = 0;
    public static int RedScore = 0;
    public static int PrevMap = -1;

    public static HashMap<Player, Integer> Kills = new HashMap<>();
    public static HashMap<Player, Integer> Deaths = new HashMap<>();

    public static BukkitRunnable ArrowGenerator = null;
    public static BukkitRunnable PlayerHealer = null;

    public static Boolean GracePeriod = false;

    // Holds how many bounces an arrow has left
    public static HashMap<Arrow, Integer> ArrowBounces = new HashMap<>();

    public static ArrayList<Arrow> ExplosiveArrows = new ArrayList<>();
    public static ArrayList<Arrow> DisablingArrows = new ArrayList<>();
    public static HashMap<Player, List<Arrow>> IllusionArrows = new HashMap<>();
    public static ArrayList<Arrow> InvisibilityArrows = new ArrayList<>();
    public static ArrayList<Arrow> ConfusionArrows = new ArrayList<>();

    public static boolean BlueTeamHacked = false;
    public static boolean RedTeamHacked = false;

    // Each location is in the format:
    // [0] - Blue spawn
    // [1] - Red spawn
    // [2] - Ult orb spawn
    public static Location[][] Maps = {
            {
                    new Location(Bukkit.getWorld("world"), -1141, 90, -191, 90, 0),
                    new Location(Bukkit.getWorld("world"), -1195, 90, -191, -90, 0),
                    new Location(Bukkit.getWorld("world"), -1168, 86, -191, -90, 0)
            },
            {
                    new Location(Bukkit.getWorld("world"), -1141, 95, -225, 90, 0),
                    new Location(Bukkit.getWorld("world"), -1195, 95, -225, -90, 0),
                    new Location(Bukkit.getWorld("world"), -1168, 99, -225, -90, 0)
            },
            {
                    new Location(Bukkit.getWorld("world"), -1141, 99, -259, 90, 0),
                    new Location(Bukkit.getWorld("world"), -1195, 99, -259, -90, 0),
                    new Location(Bukkit.getWorld("world"), -1168, 86, -259, -90, 0)
            },
            {
                    new Location(Bukkit.getWorld("world"), -1141, 104, -293, 90, 0),
                    new Location(Bukkit.getWorld("world"), -1195, 104, -293, -90, 0),
                    new Location(Bukkit.getWorld("world"), -1168, 105, -293, -90, 0)
            },
            {
                    new Location(Bukkit.getWorld("world"), -1141, 101, -327, 90, 0),
                    new Location(Bukkit.getWorld("world"), -1195, 101, -327, -90, 0),
                    new Location(Bukkit.getWorld("world"), -1168, 106, -327, -90, 0)
            },
            {
                    new Location(Bukkit.getWorld("world"), -1141, 86, -395, 90, 0),
                    new Location(Bukkit.getWorld("world"), -1195, 86, -395, -90, 0),
                    new Location(Bukkit.getWorld("world"), -1168, 81, -395, -90, 0)
            }
    };

    public static void StartGame(boolean SelectedBlueTeam) {

        StopGame();

        BlueScore = 0;
        RedScore = 0;

        boolean PutOnBlueTeam = !SelectedBlueTeam;

        if (SelectedBlueTeam) {
            for (Player Player : StartSelectPlayers) {
                AddPlayerToGame(Player, "Blue");
                Kills.put(Player, 0);
                Deaths.put(Player, 0);
            }
        }

        for (Player Player : Bukkit.getOnlinePlayers()) {

            if (PutOnBlueTeam) {
                AddPlayerToGame(Player, "Blue");
            }
            else {
                if (!SelectedBlueTeam || !StartSelectPlayers.contains(Player)) {
                    AddPlayerToGame(Player, "Red");
                }
            }

            Kills.put(Player, 0);
            Deaths.put(Player, 0);

            if (!SelectedBlueTeam) {
                PutOnBlueTeam = !PutOnBlueTeam;
            }
        }

        // Removes all the players in sandbox mode before starting
        SandboxPlayers = new ArrayList<>();

        Bukkit.broadcastMessage(BlueTeam.toString());
        Bukkit.broadcastMessage(RedTeam.toString());

        InitBossBar();

        int[] i = {0};

        new BukkitRunnable() {
            @Override
            public void run() {

                // Waits 20 seconds or until all players has selected a class
                boolean AllPlayersSelected = true;
                for (Player Player : Players) {
                    SelectClass(Player);
                    if (!Classes.containsKey(Player)) {
                        AllPlayersSelected = false;
                        break;
                    }
                }

                if (i[0] == 200 || AllPlayersSelected) {

                    // Sets the default class as the space weaver if the player hasn't selected a class
                    for (Player Player : Players) {
                        if (!Classes.containsKey(Player)) {
                            Classes.put(Player, "Space Weaver");
                        }
                    }

                    NextRound(2, null);
                    cancel();
                }
                i[0]++;
            }
        }.runTaskTimer(BowGame.GetPlugin(), 0L, 4L);
    }


    public static void StopGame() {

        for (Player Player : Players) {
            Player.getInventory().clear();
        }

        Players = new ArrayList<>();
        DeadPlayers = new ArrayList<>();
        BlueTeam = new ArrayList<>();
        RedTeam = new ArrayList<>();

        Classes = new HashMap<>();
        UltPoints = new HashMap<>();

        DamageTaken = new HashMap<>();
        SlowedPlayers = new ArrayList<>();
        MagicOverloadActive = new ArrayList<>();

        if (ScoreBossBar != null) {
            ScoreBossBar.removeAll();
        }

        for (Block CorpseBit : CorpseBits) {
            CorpseBit.setType(Material.AIR);
        }

        CorpseBits = new ArrayList<>();

        if (ArrowGenerator != null) { ArrowGenerator.cancel(); }
        if (PlayerHealer != null) { PlayerHealer.cancel(); }
    }


    public static void NextRound(int Winner, Integer SelectedMap) {

        if (Winner == 0) {
            BlueScore += 1;
        }
        else if (Winner == 1) {
            RedScore += 1;
        }

        UpdateBossbar();
        UpdateScoreBoard();

        DeadPlayers = new ArrayList<>();
        DamageTaken = new HashMap<>();
        SlowedPlayers = new ArrayList<>();
        SpaceWarpLocations = new HashMap<>();
        WarpedEnemies = new ArrayList<>();
        Cooldowns = new HashMap<>();
        MagicOverloadActive = new ArrayList<>();

        if (ArrowGenerator != null) { ArrowGenerator.cancel(); }
        if (PlayerHealer != null) { PlayerHealer.cancel(); }

        GameEnded = true;

        // Waits two seconds for the next round to begin
        new BukkitRunnable() {

            @Override
            public void run() {
                GameEnded = false;
                NextRoundInner(Winner, SelectedMap);
            }

        }.runTaskLater(BowGame.GetPlugin(), 40L);
    }


    public static void NextRoundInner(int Winner, Integer SelectedMap) {

        ResetStats(Winner);

        DeadPlayers = new ArrayList<>();
        DamageTaken = new HashMap<>();
        SlowedPlayers = new ArrayList<>();
        SpaceWarpLocations = new HashMap<>();
        WarpedEnemies = new ArrayList<>();
        Cooldowns = new HashMap<>();

        int NextMap = 0;

        if (SelectedMap != null && SelectedMap > 0 && SelectedMap < Maps.length) {
            NextMap = SelectedMap;
        }
        else {
            do {
                NextMap = (int) Math.floor(Math.random() * Maps.length);
            } while (NextMap == PrevMap);
        }


        PrevMap = NextMap;

        for (Player Player : BlueTeam) {
            Player.teleport(Maps[NextMap][0]);
            ResetPlayer(Player);

            if (Winner != 2) {
                MainUpgradesGUI.OpenUpgradesGUI(Player);
            }
        }

        for (Player Player : RedTeam) {
            Player.teleport(Maps[NextMap][1]);
            ResetPlayer(Player);

            if (Winner != 2) {
                MainUpgradesGUI.OpenUpgradesGUI(Player);
            }
        }

        SetUltOrb(Maps[NextMap][2]);

        for (Block CorpseBit : CorpseBits) {
            CorpseBit.setType(Material.AIR);
        }

        CorpseBits = new ArrayList<>();

        ArrowGenerator = new BukkitRunnable() {

            @Override
            public void run() {

                for (Player Player : Players) {

                    if (Player.getGameMode() == GameMode.SPECTATOR) { continue; }

                    Inventory Inventory = Player.getInventory();

                    if (Inventory.getItem(2) == null || Objects.requireNonNull(Inventory.getItem(2)).getAmount() < 64) {
                        Inventory.addItem(new ItemStack(Material.ARROW));
                    }
                }
            }
        };

        PlayerHealer = new BukkitRunnable() {

            @Override
            public void run() {

                for (Player Player : Players) {

                    if (Player.getGameMode() == GameMode.SPECTATOR) { continue; }

                    if ((Player.getHealth() + 1 + (double) PlayerUpgrades.get(Player).get("Regeneration")) > Player.getMaxHealth()) {
                        Player.setHealth(Player.getMaxHealth());
                    }
                    else {
                        Player.setHealth(Player.getHealth() + 1 + (double) PlayerUpgrades.get(Player).get("Regeneration"));
                    }
                }
            }
        };

        // 5-second grace period
        GracePeriod = true;
        int[] i = {0};

        new BukkitRunnable() {

            @Override
            public void run() {

                i[0]++;

                if (i[0] > 6) {
                    GracePeriod = false;
                    cancel();
                }

                else if (i[0] > 5) {
                    Bukkit.broadcastMessage("§9§lSTART!!!");
                    GracePeriod = false;
                    ArrowGenerator.runTaskTimer(BowGame.GetPlugin(), 0L, 20L);
                    PlayerHealer.runTaskTimer(BowGame.GetPlugin(), 100L, 100L);
                    cancel();
                }
                else {
                    Bukkit.broadcastMessage("§9§lGame starting in: " + (5 - i[0]));
                }
            }
        }.runTaskTimer(BowGame.GetPlugin(), 0L, 20L);
    }


    @EventHandler
    public static void OnShoot(EntityShootBowEvent Event) {
        if (!(Event.getEntity() instanceof Player)) { return; }
        if (!(Event.getProjectile() instanceof Arrow)) { return; }
        if (!Players.contains((Player) Event.getEntity())) { return; }

        Player Player = (Player) Event.getEntity();

        Arrow Arrow = (Arrow) Event.getProjectile();
        Arrow.setVelocity(Arrow.getVelocity().multiply(Math.pow(1.1, PlayerUpgrades.get(Player).get("Velocity"))));

        List<Player> EnemyTeam = BlueTeam.contains(Player) ? RedTeam : BlueTeam;

        // Checks nearby players to check for hackers
        for (Player Enemy : EnemyTeam) {
            if (Enemy.getGameMode() != GameMode.SPECTATOR && Classes.get(Enemy).equals("Hacker") && Arrow.getLocation().distance(Enemy.getLocation()) < 10) {
                if (Math.random() < 0.25) {
                    Arrow.setVelocity(Arrow.getVelocity().multiply(0.4));
                    Player.playSound(Player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1, 1);
                    break; // Break the loop to avoid hackers stacking their passives
                }
            }
        }

        if ((BlueTeam.contains(Player) && BlueTeamHacked) || (RedTeam.contains(Player) && RedTeamHacked)) {
            Arrow.setVelocity(Arrow.getVelocity().multiply(0.25));
            Player.playSound(Player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1, 1);
        }

        boolean StopShootArrow = CheckForEnchant(Player, Objects.requireNonNull(Player.getInventory().getItem(0)), Arrow);
        
        if (StopShootArrow) {
            Event.setCancelled(true);
            return;
        }

        ArrowBounces.put(Arrow, PlayerUpgrades.get(Player).get("Bounce"));

        if (Classes.get(Player).equals("Space Weaver")) {
            SpacialRearrangement(Player, Arrow);
        }

        if (MagicOverloadActive.contains(Player)) {
            if (IllusionArrows.containsKey(Player)) {
                for (Arrow IllusionArrow : IllusionArrows.get(Player)) {
                    ShootIllusionArrow(Player, IllusionArrow);
                }
            }
        }
    }

    @EventHandler
    public static void OnHit(ProjectileHitEvent Event) {
        if (!(Event.getEntity() instanceof Arrow)) { return; }

        if (Event.getHitBlock() == null) { return; }

        Arrow Arrow = (Arrow) Event.getEntity();
        if (!(Arrow.getShooter() instanceof Player)) { return; }

        CheckForEffects((Player) Arrow.getShooter(), null, Arrow, false);


        if (!ArrowBounces.containsKey(Arrow)) { return; }

        if (ArrowBounces.get(Arrow) > 0) {
            
            BlockFace HitFace = Event.getHitBlockFace();
            if (HitFace == null) { return; }
            
            Arrow NewArrow = Arrow.getWorld().spawn(Arrow.getLocation(), Arrow.class);

            Vector BlockNormal = HitFace.getDirection();

            double X = BlockNormal.getX() == 0 ? 1 : -1;
            double Y = BlockNormal.getY() == 0 ? 1 : -1;
            double Z = BlockNormal.getZ() == 0 ? 1 : -1;

            NewArrow.teleport(Arrow.getLocation().add(BlockNormal.multiply(0.05)));
            NewArrow.setVelocity(Arrow.getVelocity().multiply(new Vector(X, Y, Z)));
            NewArrow.setTicksLived(1);
            NewArrow.setCritical(Arrow.isCritical());
            NewArrow.setShooter(Arrow.getShooter());

            Arrow.getWorld().playSound(Arrow.getLocation(), Sound.ENTITY_SLIME_SQUISH_SMALL, 1.0f, 1.0f);
            Arrow.getWorld().spawnParticle(Particle.ITEM_SLIME, Arrow.getLocation(), 5);

            ArrowBounces.put(NewArrow, ArrowBounces.get(Arrow) - 1);

            if (ExplosiveArrows.contains(Arrow)) {
                ExplosiveArrows.add(NewArrow);
            }

            Arrow.remove();
        }

        ArrowBounces.remove(Arrow);
        ExplosiveArrows.remove(Arrow);
    }
}
