package game.bow.bowgame.Upgrades;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;

import static game.bow.bowgame.Classes.ClassHandler.ClassWeapons;
import static game.bow.bowgame.Classes.ClassHandler.Classes;

public class ClassGUI extends MainUpgradesGUI  {

    public static void SelectClass(Player Player) {
        Inventory GUI = Bukkit.createInventory(null, 27, "§9§lSelect your class");

        ItemStack Help = MainUpgradesGUI.SetIcon(
                Material.BOOK,
                TextColorGradient(
                        "Help",
                        Arrays.asList("#FFFFFF", "#FFFFFF"),
                        true
                ),
                Arrays.asList(
                        "§fFirst row - Passive Ability - Always active",
                        "§fSecond row - Normal Ability - Has a 30 second cooldown",
                        "§fThird row - Ultimate Ability - You need 5 ult points to use it",
                        "§fLeft click to use the normal ability, right click to use the ultimate"
                )
        );

        ItemStack SpaceWeaver = MainUpgradesGUI.SetIcon(
                Material.ECHO_SHARD,
                TextColorGradient(
                        "Space Weaver",
                        Arrays.asList("#554AB5", "#E81817", "#554AB5"),
                        true
                ),
                Arrays.asList(
                        "§1§lSpacial Rearrangement: §9Arrows curve slightly towards players",
                        "§1§lSpace Warp: §9Create a wormhole to teleport back to instantly and create a wormhole at your location",
                        "§1§lSpatial Tear: §9Bring an enemy into another world where you extra damage"
                )
        );

        ItemStack Demolitionist = MainUpgradesGUI.SetIcon(
                Material.GUNPOWDER,
                TextColorGradient(
                        "Demolitionist",
                        Arrays.asList("#A41513", "#E81817", "#A41513"),
                        true
                ),
                Arrays.asList(
                        "§4§lBomb man: §cExplosives are cheaper",
                        "§4§lExplosive Arrow: §cLets you shoot an explosive arrow dealing AOE damage",
                        "§4§lAirstrike: §cCall an airstrike down on your enemies"
                )
        );

        ItemStack Astronaut = MainUpgradesGUI.SetIcon(
                Material.NETHER_STAR,
                TextColorGradient(
                        "Astronaut",
                        Arrays.asList("#231E49", "#4F368F", "#231E49"),
                        true
                ),
                Arrays.asList(
                        "§1§lLow Gravity: §9You can jump higher",
                        "§1§lFloatation Bomb: §9Throw a bomb that makes the targets float",
                        "§1§lUntouchable: §9Create a shield of gravity repelling arrows away from you"
                )
        );

        ItemStack Hacker = MainUpgradesGUI.SetIcon(
                Material.LEATHER_HELMET,
                TextColorGradient(
                        "Hacker",
                        Arrays.asList("#6D7A92", "#5F6A7f", "#6D7A92"),
                        true
                ),
                Arrays.asList(
                        "§7§lGlitch: §fNearby enemy might get their bow jammed",
                        "§7§lDisabling Arrow: §fShoot an arrow that tracks the enemy and disables their abilities",
                        "§7§lShutdown: §fDrastically reduce your enemies velocity"
                )
        );

        GUI.setItem(4, Help);
        GUI.setItem(11, SpaceWeaver);
        GUI.setItem(12, Demolitionist);
        GUI.setItem(13, Astronaut);
        GUI.setItem(14, Hacker);
        Player.openInventory(GUI);
    }

    @EventHandler
    public void OnInventoryClick(InventoryClickEvent Event) {

        Player Player = (Player) Event.getWhoClicked();
        ItemStack ClickedItem = Event.getCurrentItem();

        if (ClickedItem == null || !Event.getView().getTitle().equals("§9§lSelect your class")) {
            return;
        }

        Event.setCancelled(true);

        Material Item = ClickedItem.getType();

        if (Item == Material.ECHO_SHARD) {
            Classes.put(Player, "Space Weaver");
            ClassWeapons.put(Player, MainUpgradesGUI.SetIcon(
                    Material.ECHO_SHARD,
                    TextColorGradient(
                            "Space Shard",
                            Arrays.asList("#554AB5", "#E81817", "#554AB5"),
                            true
                    ),
                    Arrays.asList(
                            "§1§lSpacial Rearrangement: §9Arrows curve slightly towards players",
                            "§1§lSpace Warp: §9Create a wormhole to teleport back to instantly and create a wormhole at your location",
                            "§1§lSpatial Tear: §9Bring an enemy into another world where you extra damage"
                    )
            ));
            Player.playSound(Player, Sound.BLOCK_NOTE_BLOCK_PLING, 1, 1);
        }
        else if (Item == Material.GUNPOWDER) {
            Classes.put(Player, "Demolitionist");
            ClassWeapons.put(Player, MainUpgradesGUI.SetIcon(
                    Material.GUNPOWDER,
                    TextColorGradient(
                            "Demolitionist",
                            Arrays.asList("#A41513", "#E81817", "#A41513"),
                            true
                    ),
                    Arrays.asList(
                            "§4§lBomb man: §cExplosives are cheaper",
                            "§4§lExplosive Arrow: §cLets you shoot an explosive arrow dealing AOE damage",
                            "§4§lAirstrike: §cCall an airstrike down on your enemies"
                    )
            ));
            Player.playSound(Player, Sound.BLOCK_NOTE_BLOCK_PLING, 1, 1);
        }
        else if (Item == Material.NETHER_STAR) {
            Classes.put(Player, "Astronaut");
            ClassWeapons.put(Player, MainUpgradesGUI.SetIcon(
                    Material.BREEZE_ROD,
                    TextColorGradient(
                            "Astronaut",
                            Arrays.asList("#231E49", "#4F368F", "#231E49"),
                            true
                    ),
                    Arrays.asList(
                            "§1§lLow Gravity: §9You can jump higher",
                            "§1§lFloatation Bomb: §9Throw a bomb that makes the targets float",
                            "§1§lUntouchable: §9Create a shield of gravity repelling arrows away from you"
                    )
            ));
            Player.playSound(Player, Sound.BLOCK_NOTE_BLOCK_PLING, 1, 1);
        }
        else if (Item == Material.LEATHER_HELMET) {
            Classes.put(Player, "Hacker");
            ClassWeapons.put(Player, MainUpgradesGUI.SetIcon(
                    Material.REDSTONE_TORCH,
                    TextColorGradient(
                            "Hacker",
                            Arrays.asList("#6D7A92", "#5F6A7f", "#6D7A92"),
                            true
                    ),
                    Arrays.asList(
                            "§7§lGlitch: §fNearby enemy might get their bow jammed",
                            "§7§lDisabling Arrow: §fShoot an arrow that tracks the enemy and disables their abilities",
                            "§7§lShutdown: §fDrastically reduce your enemies velocity"
                    )
            ));
            Player.playSound(Player, Sound.BLOCK_NOTE_BLOCK_PLING, 1, 1);
        }
    }
}
