package game.bow.bowgame.Game;

import game.bow.bowgame.BowGame;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockSupport;
import org.bukkit.block.Skull;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import static game.bow.bowgame.Classes.Cannoneer.AddSlowness;
import static game.bow.bowgame.Classes.Cannoneer.CannonStrength;
import static game.bow.bowgame.Classes.ClassHandler.*;
import static game.bow.bowgame.Classes.SpaceWeaver.SpatialTearLocations;
import static game.bow.bowgame.Classes.SpaceWeaver.WarpedEnemies;
import static game.bow.bowgame.Game.ArrowEffectHandler.CheckForEffects;
import static game.bow.bowgame.Game.DeathMessagesHandler.AddDamage;
import static game.bow.bowgame.Game.DeathMessagesHandler.SendDeathMessage;
import static game.bow.bowgame.Game.GameHandler.*;
import static game.bow.bowgame.Game.GameUIHandler.UpdateScoreBoard;
import static game.bow.bowgame.Upgrades.ClassGUI.SelectClass;
import static game.bow.bowgame.Upgrades.UpgradeHandler.PlayerUpgrades;

public class PlayerHandler implements Listener {

    public static List<Player> Players = new ArrayList<>();
    public static List<Player> DeadPlayers = new ArrayList<>();
    public static List<Player> BlueTeam = new ArrayList<>();
    public static List<Player> RedTeam = new ArrayList<>();
    public static List<Block> CorpseBits = new ArrayList<>();

    private static HashMap<Player, Integer> BleedDuration =  new HashMap<>();


    public static void AddPlayerToGame(Player Player, String Team) {

        Player.setGameMode(GameMode.ADVENTURE);

        UltPoints.put(Player, 0);
        SelectClass(Player);

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
        Player.setMaxHealth(20);
        Player.setHealth(Player.getMaxHealth());
        Player.setWalkSpeed(0.2f);
        Player.setFoodLevel(20);

        for (PotionEffect Potion : Player.getActivePotionEffects()) {
            Player.removePotionEffect(Potion.getType());
        }

        Inventory Inventory = Player.getInventory();
        Player.getInventory().clear();

        SetPlayerArmour(Player);

        Inventory.setItem(0, new ItemStack(Material.BOW));
        Inventory.setItem(1, ClassWeapons.get(Player));

        ItemMeta BowMeta = Objects.requireNonNull(Inventory.getItem(0)).getItemMeta();

        Objects.requireNonNull(BowMeta).setUnbreakable(true);

        Objects.requireNonNull(Inventory.getItem(0)).setItemMeta(BowMeta);

        Inventory.setItem(3, new ItemStack(Material.EMERALD));
        Inventory.setItem(4, new ItemStack(Material.EMERALD));

        ItemMeta Item1Meta = Objects.requireNonNull(Inventory.getItem(3)).getItemMeta();
        ItemMeta Item2Meta = Objects.requireNonNull(Inventory.getItem(4)).getItemMeta();

        Objects.requireNonNull(Item1Meta).setDisplayName("§2Item 1");
        Objects.requireNonNull(Item2Meta).setDisplayName("§2Item 2");

        Objects.requireNonNull(Inventory.getItem(3)).setItemMeta(Item1Meta);
        Objects.requireNonNull(Inventory.getItem(4)).setItemMeta(Item2Meta);

        Player.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 999999, 0, false, false));

        if (Classes.get(Player).equals("Astronaut")) {
            Player.addPotionEffect(new PotionEffect(PotionEffectType.JUMP_BOOST, 999999, 1, false, false));
        }
    }

    public static void SetPlayerArmour(Player Player) {
        ItemStack Helmet = new ItemStack(Material.LEATHER_HELMET);
        ItemStack Chestplate = new ItemStack(Material.LEATHER_CHESTPLATE);
        ItemStack Leggings = new ItemStack(Material.LEATHER_LEGGINGS);
        ItemStack Boots = new ItemStack(Material.LEATHER_BOOTS);

        LeatherArmorMeta HelmetMeta = (LeatherArmorMeta) Helmet.getItemMeta();
        LeatherArmorMeta ChestplateMeta = (LeatherArmorMeta) Chestplate.getItemMeta();
        LeatherArmorMeta LeggingsMeta = (LeatherArmorMeta) Leggings.getItemMeta();
        LeatherArmorMeta BootsMeta = (LeatherArmorMeta) Boots.getItemMeta();

        if (BlueTeam.contains(Player)) {
            HelmetMeta.setColor(Color.BLUE);
            ChestplateMeta.setColor(Color.BLUE);
            LeggingsMeta.setColor(Color.BLUE);
            BootsMeta.setColor(Color.BLUE);
        }
        else {
            HelmetMeta.setColor(Color.RED);
            ChestplateMeta.setColor(Color.RED);
            LeggingsMeta.setColor(Color.RED);
            BootsMeta.setColor(Color.RED);
        }

        HelmetMeta.setUnbreakable(true);
        ChestplateMeta.setUnbreakable(true);
        LeggingsMeta.setUnbreakable(true);
        BootsMeta.setUnbreakable(true);

        Helmet.setItemMeta(HelmetMeta);
        Chestplate.setItemMeta(ChestplateMeta);
        Leggings.setItemMeta(LeggingsMeta);
        Boots.setItemMeta(BootsMeta);

        Player.getInventory().setHelmet(Helmet);
        Player.getInventory().setChestplate(Chestplate);
        Player.getInventory().setLeggings(Leggings);
        Player.getInventory().setBoots(Boots);
    }


    @EventHandler
    public static void OnDamage(EntityDamageByEntityEvent Event) {

        Player Player = null;
        if (Event.getDamager() instanceof Player) {
            Player = (Player) Event.getDamager();
        }
        else if (Event.getDamager() instanceof Arrow) {
            Player = (Player) ((Arrow) Event.getDamager()).getShooter();
        }

        if (!(Event.getEntity() instanceof Player)) { return; }
        Player Victim = (Player) Event.getEntity();

        if (Player == null) { return; }
        if (!Players.contains(Player)) { return; }
        if (!PlayerUpgrades.containsKey(Player)) { return; }

        if (Event.getDamager() instanceof Arrow) {

            Event.setDamage(0);

            Arrow Arrow = (Arrow) Event.getDamager();

            if (Player == Victim) {
                Player.sendMessage("§4You can't shoot yourself!");
                Event.setCancelled(true);
                return;
            }
            else if (BlueTeam.contains(Player) && BlueTeam.contains(Victim)) {
                Player.sendMessage("§4You can't shoot your teammate!");
                Event.setCancelled(true);
                return;
            }
            else if (RedTeam.contains(Player) && RedTeam.contains(Victim)) {
                Player.sendMessage("§4You can't your teammate!");
                Event.setCancelled(true);
                return;
            }

            if (DeadPlayers.contains(Player)) { return; }

            double BowPullback = Arrow.getVelocity().length() / (Math.pow(1.1, PlayerUpgrades.get(Player).get("Velocity")) * 3);
            double Damage = (PlayerUpgrades.get(Player).get("Damage") + 10) * BowPullback;
            Damage *= Math.pow(0.9, PlayerUpgrades.get(Victim).get("Defense"));

            if (PlayerUpgrades.get(Player).get("Bleed") > 0 && BowPullback > 0.9) {

                if (BleedDuration.containsKey(Victim)) {
                    BleedDuration.replace(Victim, PlayerUpgrades.get(Player).get("Bleed") * 2);
                }
                else {
                    BleedDuration.put(Victim, PlayerUpgrades.get(Player).get("Bleed") * 2);

                    Player FinalPlayer = Player;

                    new BukkitRunnable() {

                        @Override
                        public void run() {

                            if (BleedDuration.get(Victim) > 0) {
                                BleedDuration.replace(Victim, BleedDuration.get(Victim) - 1);

                                if (Victim.getHealth() - 1 <= 0) {
                                    BleedDuration.remove(Victim);
                                    KillPlayer(Victim, FinalPlayer);
                                    cancel();
                                    return;
                                }

                                Victim.setHealth(Victim.getHealth() - 1.0);
                                Victim.getWorld().playSound(Victim.getLocation(), Sound.ENTITY_GENERIC_HURT,1, 1);

                                Victim.getWorld().spawnParticle(Particle.ITEM, Victim.getLocation(), 20, 0, 0, 0, 0, new ItemStack(Material.REDSTONE));
                            }
                            else {
                                cancel();
                            }
                        }
                    }.runTaskTimer(BowGame.GetPlugin(), 20L, 20L);
                }
            }

            if (PlayerUpgrades.get(Player).get("Backstab") > 0) {

                double ArrowAngle = Victim.getLocation().getYaw() - Arrow.getLocation().getYaw() * -1;

                if ((ArrowAngle > -45 && ArrowAngle < 45) || ArrowAngle < -315 || ArrowAngle > 315) {
                    Damage *= 1 + PlayerUpgrades.get(Player).get("Backstab") * 0.3;
                    Player.playSound(Player, Sound.ENTITY_PLAYER_LEVELUP, 1, 1);
                }
            }

            if (Classes.get(Player).equals("Cannoneer") && BowPullback > 0.9) {
                AddSlowness(Victim);
            }

            if (WarpedEnemies.contains(Victim)) {
                Damage *= 1.5;
            }

            Event.setDamage(Damage);

            CheckForEffects(Player, Victim, Arrow, true);
        }
        else {
            Event.setDamage(Event.getDamage() * Math.pow(0.9, PlayerUpgrades.get(Victim).get("Defense")));
        }

        if (Victim.getHealth() - Event.getDamage() < 0) {
            Event.setCancelled(true);
            AddDamage(Victim, Player, Victim.getHealth());
            KillPlayer(Victim, Player);
        }
        else {
            AddDamage(Victim, Player, Event.getDamage());
        }
    }


    public static void KillPlayer(Player Victim, Player Attacker) {

        if (DeadPlayers.contains(Victim)) { return; }

        DeadPlayers.add(Victim);
        Victim.setGameMode(GameMode.SPECTATOR);
        Victim.getInventory().setItem(0, null);

        SendDeathMessage(Victim, Attacker);

        if (Victim == Attacker) {
            if (BlueTeam.contains(Victim)) {
                Attacker.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText("§4§l☠ You killed §b§lyourself"));
            }
            else {
                Attacker.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText("§4§l☠ You killed §c§lyourself"));
            }
        }
        else {
            if (BlueTeam.contains(Victim)) {
                Attacker.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText("§4§l🗡 You killed §b§l" + Victim.getName()));
            }
            else {
                Attacker.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText("§4§l🗡 You killed §c§l" + Victim.getName()));
            }

            if (BlueTeam.contains(Attacker)) {
                Victim.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText("§4☠ §lYou died to §b§l" + Attacker.getName()));
            }
            else {
                Victim.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText("§4☠ §lYou died to §c§l" + Attacker.getName()));
            }
        }

        if (SpatialTearLocations.containsKey(Attacker)) {
            Attacker.teleport(SpatialTearLocations.get(Attacker));
            SpatialTearLocations.remove(Attacker);

            Attacker.setHealth(Attacker.getMaxHealth());

            Bukkit.getScheduler().runTaskLater(BowGame.GetPlugin(), () -> {
                Attacker.setGameMode(GameMode.ADVENTURE);
            }, 1L);
        }

        if (SpatialTearLocations.containsKey(Victim)) {
            Victim.teleport(SpatialTearLocations.get(Victim));
            SpatialTearLocations.remove(Victim);

            Bukkit.getScheduler().runTaskLater(BowGame.GetPlugin(), () -> {
                Victim.setGameMode(GameMode.SPECTATOR);
            }, 1L);
        }

        if (CannonStrength.containsKey(Victim)) {
            CannonStrength.replace(Victim, 0);
        }

        Deaths.replace(Victim, Deaths.get(Victim) + 1);
        Kills.replace(Attacker, Kills.get(Attacker) + 1);

        AddUltPoints(Attacker, 1);
        AddUltPoints(Victim, 1);

        UpdateScoreBoard();

        for (int i = 0; i < 100; i++) {
            Location ParticleLocation = Victim.getLocation().clone().add(new Vector(0, Math.random() * 1.8, 0));
            Victim.getWorld().spawnParticle(Particle.ITEM, ParticleLocation, 1, new ItemStack(Material.REDSTONE));
            Victim.getWorld().spawnParticle(Particle.SMOKE, ParticleLocation, 3, Math.random() - 0.5, Math.random() - 0.5, Math.random() - 0.5, 0.03);
        }

        Victim.playSound(Victim.getLocation(), Sound.ENTITY_FIREWORK_ROCKET_LARGE_BLAST, 1, 1);
        Attacker.playSound(Victim.getLocation(), Sound.ENTITY_FIREWORK_ROCKET_LARGE_BLAST, 1, 1);

        for (Player OtherPlayer : Bukkit.getOnlinePlayers()) {
            if (OtherPlayer != Victim && OtherPlayer != Attacker) {
                OtherPlayer.playSound(Victim.getLocation(), Sound.ENTITY_FIREWORK_ROCKET_BLAST_FAR, 1, 1);
            }
        }

        Location HeadLocation = Victim.getLocation();

        while (Victim.getWorld().getBlockAt(HeadLocation).getType() != Material.AIR) {
            HeadLocation.add(new Vector(0, 1, 0));
        }

        while (Victim.getWorld().getBlockAt(HeadLocation).getType() == Material.AIR) {
            HeadLocation.add(new Vector(0, -1, 0));
        }

        Block HeadBlock = Victim.getWorld().getBlockAt(HeadLocation.add(new Vector(0, 1, 0)));
        HeadBlock.setType(Material.PLAYER_HEAD);
        Skull Head = (Skull) HeadBlock.getState();
        Head.setOwningPlayer(Victim);
        Head.update();

        CorpseBits.add(HeadBlock);

        for (int x = -3; x <= 3; x++) {
            for (int y = -3; y <= 3; y++) {
                for (int z = -3; z <= 3; z++) {
                    Location BlockLocation = Victim.getLocation().clone().add(x, y, z);
                    Block Block = Victim.getWorld().getBlockAt(BlockLocation);

                    // Creates a circle shape
                    if (Block.getLocation().distance(Victim.getLocation()) < 3) {
                        Block PotentialBlock = Victim.getWorld().getBlockAt(BlockLocation.add(new Vector(0, 1, 0)));
                        if (PotentialBlock.getType() == Material.AIR && !Block.isPassable() && Block.getBlockData().getMaterial().isBlock()
                        && Block.getBlockData().isFaceSturdy(BlockFace.UP, BlockSupport.FULL)) {
                            if (Math.floor(Math.random() * 3) == 1) {
                                PotentialBlock.setType(Material.REDSTONE_WIRE);
                                CorpseBits.add(PotentialBlock);
                            }
                        }
                    }
                }
            }
        }

        boolean BlueTeamDead = true;
        boolean RedTeamDead = true;

        for (Player BluePlayer : BlueTeam) {
            if (!DeadPlayers.contains(BluePlayer)) {
                BlueTeamDead = false;
                break;
            }
        }

        for (Player RedPlayer : RedTeam) {
            if (!DeadPlayers.contains(RedPlayer)) {
                RedTeamDead = false;
                break;
            }
        }

        if (BlueTeamDead) {
            Bukkit.broadcastMessage("Blue team lost");
            NextRound(1, null);
        }

        else if (RedTeamDead) {
            Bukkit.broadcastMessage("Red team lost");
            NextRound(0, null);
        }
    }
}
