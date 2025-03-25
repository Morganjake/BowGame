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
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static game.bow.bowgame.Classes.ClassHandler.*;
import static game.bow.bowgame.Classes.SpaceWeaver.SpatialTearLocations;
import static game.bow.bowgame.Classes.SpaceWeaver.WarpedEnemies;
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

        Inventory.setItem(0, new ItemStack(Material.BOW));
        Inventory.setItem(1, GetClassItem(Player));

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

            if (Player.getGameMode() == GameMode.SPECTATOR) { return; }

            double BowPullback = Arrow.getVelocity().length() / (Math.pow(1.1, PlayerUpgrades.get(Player).get("Velocity")) * 3);
            double Damage = (PlayerUpgrades.get(Player).get("Damage") + 10) * BowPullback;
            Damage *= Math.pow(0.9, PlayerUpgrades.get(Victim).get("Defense"));

            if (PlayerUpgrades.get(Player).get("Poison") > 0 && BowPullback > 0.9) {
                Victim.addPotionEffect(new PotionEffect(PotionEffectType.POISON, PlayerUpgrades.get(Player).get("Poison") * 40, 1));
            }

            if (PlayerUpgrades.get(Player).get("Backstab") > 0) {

                double ArrowAngle = Math.abs(Victim.getLocation().getYaw() - Arrow.getLocation().getYaw());

                if (ArrowAngle > 135 && ArrowAngle < 225) {
                    Damage *= 1 + PlayerUpgrades.get(Player).get("Backstab") * 0.3;
                    Player.playSound(Player, Sound.ENTITY_PLAYER_LEVELUP, 1, 1);
                }
            }

            if (WarpedEnemies.contains(Victim)) {
                Damage *= 1.5;
            }

            Event.setDamage(Damage);

            if (ExplosiveArrows.contains(Arrow)) {

                for (Player OtherPlayer : Players) {
                    if (OtherPlayer == Victim) { continue; }
                    if (Arrow.getLocation().distance(OtherPlayer.getLocation()) < 2) {
                        OtherPlayer.damage((double) PlayerUpgrades.get((Player) Arrow.getShooter()).get("Damage") + 5, (Player) Arrow.getShooter());
                    }
                }
                Arrow.getWorld().spawnParticle(Particle.EXPLOSION_EMITTER, Arrow.getLocation(), 1);
                Arrow.getWorld().playSound(Arrow.getLocation(), Sound.ENTITY_GENERIC_EXPLODE, 1, 1);
                ExplosiveArrows.remove(Arrow);
            }

            else if (DisablingArrows.contains(Arrow)) {

                Victim.getInventory().setItem(1, new ItemStack(Material.BARRIER));

                Bukkit.getScheduler().runTaskLater(BowGame.GetPlugin(), () -> {
                    Victim.getInventory().setItem(1, GetClassItem(Victim));
                }, 200L);
            }
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


    public static void KillPlayer(Player Player, Player Attacker) {

        if (DeadPlayers.contains(Player)) { return; }

        DeadPlayers.add(Player);
        Player.setGameMode(GameMode.SPECTATOR);
        Player.getInventory().setItem(0, null);

        SendDeathMessage(Player, Attacker);

        if (Player == Attacker) {
            if (BlueTeam.contains(Player)) {
                Attacker.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText("§4§l☠ You killed §b§lyourself"));
            }
            else {
                Attacker.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText("§4§l☠ You killed §c§lyourself"));
            }
        }
        else {
            if (BlueTeam.contains(Player)) {
                Attacker.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText("§4§l🗡 You killed §b§l" + Player.getName()));
            }
            else {
                Attacker.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText("§4§l🗡 You killed §c§l" + Player.getName()));
            }

            if (BlueTeam.contains(Attacker)) {
                Player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText("§4☠ §lYou died to §b§l" + Attacker.getName()));
            }
            else {
                Player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText("§4☠ §lYou died to §c§l" + Attacker.getName()));
            }
        }

        if (SpatialTearLocations.containsKey(Attacker)) {
            Attacker.teleport(SpatialTearLocations.get(Attacker));
            SpatialTearLocations.remove(Attacker);

            Bukkit.getScheduler().runTaskLater(BowGame.GetPlugin(), () -> {
                Attacker.setGameMode(GameMode.ADVENTURE);
            }, 1L);
        }

        if (SpatialTearLocations.containsKey(Player)) {
            Player.teleport(SpatialTearLocations.get(Player));
            SpatialTearLocations.remove(Player);

            Bukkit.getScheduler().runTaskLater(BowGame.GetPlugin(), () -> {
                Player.setGameMode(GameMode.SPECTATOR);
            }, 1L);
        }

        Deaths.replace(Player, Deaths.get(Player) + 1);
        Kills.replace(Attacker, Kills.get(Attacker) + 1);

        AddUltPoints(Attacker, 1);
        AddUltPoints(Player, 1);

        UpdateScoreBoard();

        for (int i = 0; i < 100; i++) {
            Location ParticleLocation = Player.getLocation().clone().add(new Vector(0, Math.random() * 1.8, 0));
            Player.getWorld().spawnParticle(Particle.ITEM, ParticleLocation, 1, new ItemStack(Material.REDSTONE));
            Player.getWorld().spawnParticle(Particle.SMOKE, ParticleLocation, 3, Math.random() - 0.5, Math.random() - 0.5, Math.random() - 0.5, 0.03);
        }

        Player.playSound(Player.getLocation(), Sound.ENTITY_FIREWORK_ROCKET_LARGE_BLAST, 1, 1);
        Attacker.playSound(Player.getLocation(), Sound.ENTITY_FIREWORK_ROCKET_LARGE_BLAST, 1, 1);

        for (Player OtherPlayer : Bukkit.getOnlinePlayers()) {
            if (OtherPlayer != Player && OtherPlayer != Attacker) {
                OtherPlayer.playSound(Player.getLocation(), Sound.ENTITY_FIREWORK_ROCKET_BLAST_FAR, 1, 1);
            }
        }

        Location HeadLocation = Player.getLocation();

        while (Player.getWorld().getBlockAt(HeadLocation).getType() != Material.AIR) {
            HeadLocation.add(new Vector(0, 1, 0));
        }

        while (Player.getWorld().getBlockAt(HeadLocation).getType() == Material.AIR) {
            HeadLocation.add(new Vector(0, -1, 0));
        }

        Block HeadBlock = Player.getWorld().getBlockAt(HeadLocation.add(new Vector(0, 1, 0)));
        HeadBlock.setType(Material.PLAYER_HEAD);
        Skull Head = (Skull) HeadBlock.getState();
        Head.setOwningPlayer(Player);
        Head.update();

        CorpseBits.add(HeadBlock);

        for (int x = -3; x <= 3; x++) {
            for (int y = -3; y <= 3; y++) {
                for (int z = -3; z <= 3; z++) {
                    Location BlockLocation = Player.getLocation().clone().add(x, y, z);
                    Block Block = Player.getWorld().getBlockAt(BlockLocation);

                    // Creates a circle shape
                    if (Block.getLocation().distance(Player.getLocation()) < 3) {
                        Block PotentialBlock = Player.getWorld().getBlockAt(BlockLocation.add(new Vector(0, 1, 0)));
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
            NextRound(1);
        }

        else if (RedTeamDead) {
            Bukkit.broadcastMessage("Red team lost");
            NextRound(0);
        }
    }
}
