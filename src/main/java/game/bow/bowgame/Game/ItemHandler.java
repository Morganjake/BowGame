package game.bow.bowgame.Game;

import game.bow.bowgame.BowGame;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.HashMap;

import static game.bow.bowgame.Classes.SpaceWeaver.WarpedEnemies;
import static game.bow.bowgame.Game.DeathMessagesHandler.AddDamage;
import static game.bow.bowgame.Game.PlayerHandler.KillPlayer;
import static game.bow.bowgame.Game.PlayerHandler.Players;
import static game.bow.bowgame.Upgrades.UpgradeHandler.PlayerUpgrades;

public class ItemHandler implements Listener {

    // Holds who shot an entity
    public static HashMap<Entity, Player> ItemOwners = new HashMap<>();
    public static ArrayList<Player> SlowedPlayers = new ArrayList<>();

    public static ArrayList<Entity> FreezeGrenades = new ArrayList<>();

    private static final ArrayList<Player> Debounce = new ArrayList<>();

    @EventHandler
    public void OnRightClick(PlayerInteractEvent Event) {
        if (Event.getAction() == Action.RIGHT_CLICK_AIR || Event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            Player Player = Event.getPlayer();
            if (!Players.contains(Player)) { return; }
            if (Debounce.contains(Player)) { return; }

            ItemStack Item = Player.getInventory().getItemInMainHand();

            if (Item.getType() == Material.TNT) {
                Item.setAmount(Item.getAmount() - 1);
                Event.setCancelled(true);

                TNTPrimed Tnt = Player.getWorld().spawn(Player.getEyeLocation(), TNTPrimed.class);
                Tnt.setVelocity(Player.getLocation().getDirection().multiply(Math.pow(1.1, PlayerUpgrades.get(Player).get("Velocity"))));
                Tnt.setFuseTicks(30);
                ItemOwners.put(Tnt, Player);

                SetDebounce(Player);
            }
            else if (Item.getType() == Material.PRISMARINE_SHARD) {
                Item.setAmount(Item.getAmount() - 1);
                Event.setCancelled(true);

                Snowball Snowball = Player.launchProjectile(Snowball.class);
                ItemOwners.put(Snowball, Player);
                FreezeGrenades.add(Snowball);

                SetDebounce(Player);
            }
            else if (Item.getType() == Material.INK_SAC) {
                Item.setAmount(Item.getAmount() - 1);
                Event.setCancelled(true);

                ItemOwners.put(Player.launchProjectile(Egg.class), Player);

                SetDebounce(Player);
            }
            else if (Item.getType() == Material.BLAZE_ROD) {
                if (Event.getClickedBlock() == null) { return; }
                if (Event.getClickedPosition() == null) { return; }

                Item.setAmount(Item.getAmount() - 1);
                Event.setCancelled(true);

                Block Block = Event.getClickedBlock();
                Location Location = Event.getClickedBlock().getLocation().add(Event.getClickedPosition());
                Location.add(new Vector(0, 0.1, 0));
                Location PentagonRimLocation = Location.clone().add(Location.getDirection().multiply(8));


                int[] CircleIterations = {0};

                new BukkitRunnable() {
                    @Override
                    public void run() {

                        if (CircleIterations[0] >= 20) {

                            for (int i = 0; i < 5; i++) {
                                PentagonRimLocation.setYaw(PentagonRimLocation.getYaw() - 145);

                                for (int j = 0; j < 20; j++) {
                                    Location ParticleLocation = PentagonRimLocation.clone().add(new Vector(-2.5, 0, 0));
                                    Block.getWorld().spawnParticle(Particle.DUST, ParticleLocation, 1, new Particle.DustOptions(Color.RED, 25));
                                    PentagonRimLocation.add(PentagonRimLocation.getDirection().multiply(0.8));
                                }
                            }

                            Location BlastCenter = Location.clone();

                            BlastCenter.setPitch((float) (-85 + Math.random() * 25));
                            BlastCenter.setYaw((float) (180 - Math.random() * 360));

                            Location BlastUp = BlastCenter.clone();
                            Location BlastDown = BlastCenter.clone();

                            ArrayList<Location> BlastLocations = new ArrayList<>();

                            while (BlastUp.getY() < 324) {
                                BlastLocations.add(BlastUp.clone());
                                BlastUp.add(BlastUp.getDirection().multiply(4));
                            }

                            while (BlastDown.getY() > -64) {
                                BlastLocations.add(BlastDown.clone());
                                BlastDown.add(BlastDown.getDirection().multiply(-4));
                            }

                            for (Location BlastLocation : BlastLocations) {
                                Block.getWorld().spawnParticle(Particle.EXPLOSION_EMITTER, BlastLocation, 1);
                                Block.getWorld().playSound(BlastLocation, Sound.ENTITY_GENERIC_EXPLODE, 1, 1);
                                Block.getWorld().playSound(BlastLocation, Sound.ITEM_TRIDENT_THUNDER, 1, 1);
                            }

                            Block.getWorld().spawnParticle(Particle.FLAME, Location, 2000, 0, 0, 0, 1);
                            Block.getWorld().spawnParticle(Particle.CRIMSON_SPORE, Location, 1000, 0, 0, 0, 1.5);
                            Block.getWorld().spawnParticle(Particle.LARGE_SMOKE, Location, 1000, 0, 0, 0, 1);
                            Block.getWorld().spawnParticle(Particle.SMOKE, Location, 1000, 0, 0, 0, 1);

                            for (Player Hit: Players) {
                                if (Location.distance(Hit.getLocation()) < 6) {

                                    if (WarpedEnemies.contains(Hit)) {
                                        Hit.damage(20 + 5 * (6 - Location.distance(Hit.getLocation())), Player);
                                    }
                                    else {
                                        Hit.damage((20 + 5 * (6 - Location.distance(Hit.getLocation())) * 1.5), Player);
                                    }
                                }

                                if (Location.distance(Hit.getLocation()) < 10) {
                                    if (Hit.isOnGround()) {
                                        Hit.setVelocity(Hit.getVelocity().add(new Vector(0, 0.7, 0)));
                                    }
                                    else {
                                        Hit.setVelocity(Hit.getVelocity().add(new Vector(0, 0.4, 0)));
                                    }
                                }
                            }

                            cancel();
                        }
                        CircleIterations[0]++;

                        for (int i = 0; i < 90; i++) {
                            Location ParticleLocation = Location.clone();
                            ParticleLocation.setYaw(i * 6);
                            ParticleLocation.add(ParticleLocation.getDirection().multiply(6));

                            Block.getWorld().spawnParticle(Particle.DUST, ParticleLocation, 1, new Particle.DustOptions(Color.BLACK, 1));

                            ParticleLocation.subtract(ParticleLocation.getDirection().multiply(CircleIterations[0] * 0.3));

                            if (CircleIterations[0] > 14) {
                                Block.getWorld().spawnParticle(Particle.DUST, ParticleLocation, 1, new Particle.DustOptions(Color.RED, 1));
                            }
                            else if (CircleIterations[0] > 7) {
                                Block.getWorld().spawnParticle(Particle.DUST, ParticleLocation, 1, new Particle.DustOptions(Color.ORANGE, 1));
                            }
                            else {
                                Block.getWorld().spawnParticle(Particle.DUST, ParticleLocation, 1, new Particle.DustOptions(Color.YELLOW, 1));
                            }
                        }

                        Block.getWorld().playSound(Location, Sound.BLOCK_NOTE_BLOCK_BASS, 1, 1 + (float) CircleIterations[0] / 20);
                    }

                }.runTaskTimer(BowGame.GetPlugin(), 0L, 3L);

                SetDebounce(Player);

            }
            else if (Item.getType() == Material.RED_DYE) {
                Item.setAmount(Item.getAmount() - 1);
                Event.setCancelled(true);

                final int[] i = {1};

                new BukkitRunnable() {

                    @Override
                    public void run() {

                        if (i[0] == 25) { cancel(); }

                        if (i[0] % 5 == 0) {
                            if (Player.getHealth() + Player.getMaxHealth() * 0.08 > Player.getMaxHealth()) {
                                Player.setHealth(Player.getMaxHealth());
                            }
                            else {
                                Player.setHealth(Player.getHealth() + Player.getMaxHealth() * 0.08);
                            }
                        }

                        i[0] += 1;

                        Player.getWorld().spawnParticle(Particle.HEART, Player.getEyeLocation(), 1, 0.2, 0.2, 0.2);
                    }
                }.runTaskTimer(BowGame.GetPlugin(), 2L, 2L);

                SetDebounce(Player);
            }
        }
    }

    private static void SetDebounce(Player Player) {
        Debounce.add(Player);

        Bukkit.getScheduler().runTaskLater(BowGame.GetPlugin(), () -> {
            Debounce.remove(Player);
        }, 1L);
    }


    @EventHandler
    public void OnTntDamage(EntityDamageByEntityEvent Event) {
        if (!(Event.getDamager() instanceof TNTPrimed)) { return; }
        if (!ItemOwners.containsKey(Event.getDamager())) { return; }
        if (!(Event.getEntity() instanceof Player)) { return; }

        Player Victim = (Player) Event.getEntity();

        double Distance = Event.getDamager().getLocation().distance(Victim.getLocation());

        // Stops the player from getting damaged from too far away
        if (Distance > 4) {
            Event.setCancelled(true);
            return;
        }

        Event.setDamage((4 - Distance) * 4);
        Event.setDamage(Event.getDamage() * Math.pow(0.9, PlayerUpgrades.get(Victim).get("Defense")));

        if (WarpedEnemies.contains(Victim)) {
            Event.setDamage(Event.getDamage() * 1.5);
        }

        if (Victim.getHealth() - Event.getDamage() <= 0) {
            Event.setCancelled(true);
            AddDamage(Victim, ItemOwners.get(Event.getDamager()), Victim.getHealth());
            KillPlayer(Victim, ItemOwners.get(Event.getDamager()));
        }
        else {
            AddDamage(Victim, ItemOwners.get(Event.getDamager()), Event.getDamage());
        }
    }


    @EventHandler
    public void OnTntExplode(EntityExplodeEvent Event) {
        if (ItemOwners.containsKey(Event.getEntity())) {

            Event.setCancelled(true);
            Event.getEntity().getWorld().spawnParticle(Particle.EXPLOSION_EMITTER, Event.getEntity().getLocation(), 1);
            Event.getEntity().getWorld().spawnParticle(Particle.LARGE_SMOKE, Event.getEntity().getLocation(), 3);
            Event.getEntity().getWorld().playSound(Event.getEntity().getLocation(), Sound.ENTITY_GENERIC_EXPLODE, 1, 1);

            new BukkitRunnable() {

                @Override
                public void run() {
                    ItemOwners.remove(Event.getEntity());
                }

            }.runTaskLater(BowGame.GetPlugin(), 1L);
        }
    }


    @EventHandler
    public void OnFreezeGrenadeLand(ProjectileHitEvent Event) {
        if (!(Event.getEntity() instanceof Snowball)) { return; }
        if (!ItemOwners.containsKey(Event.getEntity())) { return; }
        if (!FreezeGrenades.contains(Event.getEntity())) { return; }

        HashMap<Block, BlockData> OriginalBlocks = new HashMap<>();

        for (int x = -3; x <= 3; x++) {
            for (int y = -3; y <= 3; y++) {
                for (int z = -3; z <= 3; z++) {
                    Location BlockLocation = Event.getEntity().getLocation().clone().add(x, y, z);
                    Block Block = Event.getEntity().getWorld().getBlockAt(BlockLocation);

                    // Creates a circle shape
                    if (Block.getLocation().distance(Event.getEntity().getLocation()) < 3) {
                        if (Block.getType() != Material.AIR && Block.getType() != Material.BLUE_ICE && !Block.isPassable()) {
                            OriginalBlocks.put(Block, Block.getBlockData());
                            Block.setType(Material.BLUE_ICE, false);
                        }
                    }
                }
            }
        }

        Event.getEntity().getWorld().playSound(Event.getEntity().getLocation(), Sound.BLOCK_GLASS_BREAK, 1, 1);
        Event.getEntity().getWorld().spawnParticle(Particle.SNOWFLAKE, Event.getEntity().getLocation(), 50, 0, 0, 0, 0.2);
        Event.getEntity().getWorld().spawnParticle(Particle.SOUL_FIRE_FLAME, Event.getEntity().getLocation(), 20, 0, 0, 0, 0.15);

        for (Player Player : Players) {
            if (Event.getEntity().getWorld() != Player.getWorld()) { continue; }
            if (Player.getLocation().distance(Event.getEntity().getLocation()) < 3) {
                SlowedPlayers.add(Player);
                Player.setWalkSpeed(Player.getWalkSpeed() / 3);
            }
        }

        new BukkitRunnable() {

            @Override
            public void run() {

                for (var Entry : OriginalBlocks.entrySet()) {
                    Entry.getKey().setBlockData(Entry.getValue(), false);
                }
            }

        }.runTaskLater(BowGame.GetPlugin(), 2L);

        final int[] i = {0};

        new BukkitRunnable() {

            @Override
            public void run() {

                if (i[0] > 30) {

                    for (Player Player: SlowedPlayers) {
                        Player.setWalkSpeed(Player.getWalkSpeed() * 3);
                    }

                    SlowedPlayers = new ArrayList<>();
                    cancel();
                }

                i[0]++;

                for (Player Player: SlowedPlayers) {
                    Event.getEntity().getWorld().spawnParticle(Particle.SNOWFLAKE, Player.getEyeLocation(), 1, 0.2, 0.2, 0.2, 0);
                }
            }
        }.runTaskTimer(BowGame.GetPlugin(), 0L, 2L);
    }


    @EventHandler
    public void OnSmokeBombLand(ProjectileHitEvent Event) {
        if (!(Event.getEntity() instanceof Egg)) { return; }
        if (!ItemOwners.containsKey(Event.getEntity())) { return; }
        final int[] i = {1};

        new BukkitRunnable() {

            @Override
            public void run() {

                if (i[0] == 50) { cancel(); }
                i[0]++;

                Event.getEntity().getWorld().spawnParticle(Particle.EXPLOSION_EMITTER, Event.getEntity().getLocation(), 1);
            }
        }.runTaskTimer(BowGame.GetPlugin(), 0L, 2L);
    }
}
