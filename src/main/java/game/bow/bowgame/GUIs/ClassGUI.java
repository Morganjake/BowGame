package game.bow.bowgame.GUIs;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import static game.bow.bowgame.Classes.Cannoneer.CannonStrength;
import static game.bow.bowgame.Classes.ClassHandler.ClassWeapons;
import static game.bow.bowgame.Classes.ClassHandler.Classes;
import static game.bow.bowgame.Classes.Mage.SelectedSpell;
import static game.bow.bowgame.Game.PlayerHandler.BlueTeam;
import static game.bow.bowgame.Game.PlayerHandler.Players;

public class ClassGUI extends MainUpgradesGUI  {

    public static HashMap<Player, String> SelectedClasses = new HashMap<>();

    public static void SelectClass(Player Player) {
        Inventory GUI = Bukkit.createInventory(null, 45, "§9§lSelect your class");

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
                        "Space Weaver ★★☆☆☆",
                        Arrays.asList("#554AB5", "#E81817", "#554AB5"),
                        true
                ),
                Arrays.asList(
                        "§1§lSpacial Rearrangement: §9Arrows curve slightly towards players",
                        "§1§lSpace Warp: §9Create a wormhole to teleport back to instantly and create a wormhole at your location",
                        "§1§lSpace Manipulation: §9Arrows that miss travel through wormholes and redirect themselves towards enemies"
                )
        );

        ItemStack Demolitionist = MainUpgradesGUI.SetIcon(
                Material.GUNPOWDER,
                TextColorGradient(
                        "Demolitionist ★☆☆☆☆",
                        Arrays.asList("#A41513", "#E81817", "#A41513"),
                        true
                ),
                Arrays.asList(
                        "§4§lBomb Man: §cExplosives are cheaper",
                        "§4§lExplosive Arrow: §cLets you shoot an explosive arrow dealing AOE damage",
                        "§4§lAirstrike: §cCall an airstrike down on your enemies"
                )
        );

        ItemStack Astronaut = MainUpgradesGUI.SetIcon(
                Material.NETHER_STAR,
                TextColorGradient(
                        "Astronaut ★★☆☆☆",
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
                        "Hacker ★★★☆☆",
                        Arrays.asList("#6D7A92", "#5F6A7f", "#6D7A92"),
                        true
                ),
                Arrays.asList(
                        "§7§lGlitch: §fNearby enemies might get their bow jammed",
                        "§7§lDisabling Arrow: §fShoot an arrow that tracks the enemy and disables their abilities",
                        "§7§lShutdown: §fDrastically reduce your enemies velocity"
                )
        );

        ItemStack Mage = MainUpgradesGUI.SetIcon(
                Material.NETHERITE_SHOVEL,
                TextColorGradient(
                        "Mage ★★★★☆",
                        Arrays.asList("#A12092", "#DB25C6", "#A12092"),
                        true
                ),
                Arrays.asList(
                        "§5§lSwitch Magic: §dCrouch left click to switch the currently equipped spell",
                        "   §5§l- Confusion Arrow: §dConfused the enemies by playing player sounds all around them",
                        "   §5§l- Illusion Arrow: §dHolds the arrow in space and shoots it next time the player uses the ability",
                        "   §5§l- Invisibility Arrow: §dTurns effected players invisible",
                        "§5§lMagic Arrow: §dShoot an arrow with the currently selected spell",
                        "§5§lMagic Overload: §dIllusion arrows don't go away and shoot every time you shoot"
                )
        );

        ItemStack Cannoneer = MainUpgradesGUI.SetIcon(
                Material.BLACK_DYE,
                TextColorGradient(
                        "Cannoneer ★★★☆☆",
                        Arrays.asList("#1a1c1f", "#25272b", "#1a1c1f"),
                        true
                ),
                Arrays.asList(
                        "§8§lHeavy Rounds: §7Enemies will receive slowness every 3rd shot, slowness gets worse the more shots they receive",
                        "§8§lCannon: §7Turn your bow into a block piercing cannon",
                        "§8§lSuper Duper Mega Galaxy Collapsing Cannon: §7Use your ult points to charge up your cannon up to maximum power obliterating your enemies",
                        "§4Make sure you fully pull back your bow before shooting the cannon, it may be unstable otherwise"
                )
        );

        if (SelectedClasses.containsKey(Player)) {
            ItemStack Confirm = MainUpgradesGUI.SetIcon(
                    Material.GREEN_STAINED_GLASS_PANE,
                    TextColorGradient("Confirm", List.of("#17c914", "#17c914"), true),
                    List.of("§aConfirming will stop you from changing your class")
            );
            GUI.setItem(44, Confirm);
        }

        GUI.setItem(4, Help);
        GUI.setItem(11, SpaceWeaver);
        GUI.setItem(12, Demolitionist);
        GUI.setItem(13, Astronaut);
        GUI.setItem(14, Hacker);
        GUI.setItem(15, Mage);
        
        GUI.setItem(30, Cannoneer);

        for (Player OtherPlayer : Players) {
            if (BlueTeam.contains(Player) == BlueTeam.contains(OtherPlayer)) {
                if (!SelectedClasses.containsKey(OtherPlayer)) { continue; }

                ItemStack PlayerHead = new ItemStack(Material.PLAYER_HEAD);
                SkullMeta HeadMeta = (SkullMeta) PlayerHead.getItemMeta();
                Objects.requireNonNull(HeadMeta).setDisplayName(OtherPlayer.getDisplayName());
                Objects.requireNonNull(HeadMeta).setOwningPlayer(OtherPlayer);
                PlayerHead.setItemMeta(HeadMeta);

                GUI.setItem(switch (SelectedClasses.get(OtherPlayer)) {
                            case "Space Weaver" -> 20;
                            case "Demolitionist" -> 21;
                            case "Astronaut" -> 22;
                            case "Hacker" -> 23;
                            case "Mage" -> 24;
                            case "Cannoneer" -> 39;
                            default -> 25;
                        },
                        PlayerHead);
            }
        }

        Player.openInventory(GUI);
    }


    @EventHandler
    public void OnInventoryClick(InventoryClickEvent Event) {

        Player Player = (Player) Event.getWhoClicked();
        ItemStack ClickedItem = Event.getCurrentItem();

        if (ClickedItem == null || !Event.getView().getTitle().equals("§9§lSelect your class")) {
            return;
        }

        if (Classes.containsKey(Player)) { return; }

        Event.setCancelled(true);

        Material Item = ClickedItem.getType();

        if (Item == Material.ECHO_SHARD) {
           if (SetClass(Player, "Space Weaver")) {
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
                                "§1§lSpace Manipulation: §9Arrows that miss travel through wormholes and redirect themselves towards enemies"
                        )
                ));
           }
        }
        else if (Item == Material.GUNPOWDER) {
            if (SetClass(Player, "Demolitionist")) {
                ClassWeapons.put(Player, MainUpgradesGUI.SetIcon(
                        Material.GUNPOWDER,
                        TextColorGradient(
                                "Boom Boom",
                                Arrays.asList("#A41513", "#E81817", "#A41513"),
                                true
                        ),
                        Arrays.asList(
                                "§4§lBomb man: §cExplosives are cheaper",
                                "§4§lExplosive Arrow: §cLets you shoot an explosive arrow dealing AOE damage",
                                "§4§lAirstrike: §cCall an airstrike down on your enemies"
                        )
                ));
            }
        }
        else if (Item == Material.NETHER_STAR) {
            if (SetClass(Player, "Astronaut")) {
                ClassWeapons.put(Player, MainUpgradesGUI.SetIcon(
                        Material.BREEZE_ROD,
                        TextColorGradient(
                                "Gravity Stick",
                                Arrays.asList("#231E49", "#4F368F", "#231E49"),
                                true
                        ),
                        Arrays.asList(
                                "§1§lLow Gravity: §9You can jump higher",
                                "§1§lFloatation Bomb: §9Throw a bomb that makes the targets float",
                                "§1§lUntouchable: §9Create a shield of gravity repelling arrows away from you"
                        )
                ));
            }
        }
        else if (Item == Material.LEATHER_HELMET) {
            if (SetClass(Player, "Hacker")) {
                ClassWeapons.put(Player, MainUpgradesGUI.SetIcon(
                        Material.REDSTONE_TORCH,
                        TextColorGradient(
                                "Hacking Tool",
                                Arrays.asList("#6D7A92", "#5F6A7f", "#6D7A92"),
                                true
                        ),
                        Arrays.asList(
                                "§7§lGlitch: §fNearby enemies might get their bow jammed",
                                "§7§lDisabling Arrow: §fShoot an arrow that tracks the enemy and disables their abilities",
                                "§7§lShutdown: §fDrastically reduce your enemies velocity"
                        )
                ));
            }
        }
        else if (Item == Material.NETHERITE_SHOVEL) {
            if (SetClass(Player, "Mage")) {
                ClassWeapons.put(Player, MainUpgradesGUI.SetIcon(
                        Material.NETHERITE_SHOVEL,
                        TextColorGradient(
                                "New Magic Wand",
                                Arrays.asList("#A12092", "#DB25C6", "#A12092"),
                                true
                        ),
                        Arrays.asList(
                                "§5§lSwitch Spell: §dCrouch left click to switch the currently equipped spell",
                                "   §5§l- Confusion Arrow: §dConfused the enemies by playing player sounds all around them",
                                "   §5§l- Illusion Arrow: §dHolds the arrow in space and shoots it next time the player uses the ability",
                                "   §5§l- Invisibility Arrow: §dTurns effected players invisible",
                                "§5§lMagic Arrow: §dShoot an arrow with the currently selected spell",
                                "§5§lMagic Overload: §dIllusion arrows don't go away and shoot every time you shoot"
                        )
                ));
                SelectedSpell.put(Player, 1);
            }
        }
        else if (Item == Material.BLACK_DYE) {
            if (SetClass(Player, "Cannoneer")) {
                ClassWeapons.put(Player, MainUpgradesGUI.SetIcon(
                        Material.BLACK_DYE,
                        TextColorGradient(
                                "Big Cannon",
                                Arrays.asList("#1a1c1f", "#25272b", "#1a1c1f"),
                                true
                        ),
                        Arrays.asList(
                                "§8§lHeavy Rounds: §7Enemies will receive slowness every 3rd shot, slowness gets worse the more shots they receive",
                                "§8§lCannon: §7Turn your bow into a block piercing cannon",
                                "§8§lSuper Duper Mega Galaxy Collapsing Cannon: §7Use your ult points to charge up your cannon up to maximum power obliterating your enemies",
                                "§4Make sure you fully pull back your bow before shooting the cannon, it may be unstable otherwise"
                        )
                ));
                CannonStrength.put(Player, 0);
            }
        }
        else if (Item == Material.GREEN_STAINED_GLASS_PANE) {
            Classes.put(Player, SelectedClasses.get(Player));
            Player.sendMessage("§a§lYou have selected the " + Classes.get(Player) + " class!");
            Player.playSound(Player, Sound.ENTITY_PLAYER_LEVELUP, 1, 1);
        }

        for (Player OtherPlayer : Players) {
            if (BlueTeam.contains(Player) == BlueTeam.contains(OtherPlayer)) {
                SelectClass(OtherPlayer);
            }
        }
    }

    // Returns true if the player selected the class, false if the player is unselecting of another player has already chosen the class
    private Boolean SetClass(Player Player, String Class) {

        if (SelectedClasses.containsKey(Player) && Objects.equals(SelectedClasses.get(Player), Class)) {
            SelectedClasses.remove(Player);
            ClassWeapons.remove(Player);
            Player.playSound(Player, Sound.ENTITY_VILLAGER_NO, 1, 1);
            return false;
        }
        else {
            SelectedClasses.put(Player, Class);

            // Checks other players on the team to see if they have already selected that class
            for (Player OtherPlayer : Players) {
                if (BlueTeam.contains(Player) == BlueTeam.contains(OtherPlayer) && Player != OtherPlayer) {
                    if (!SelectedClasses.containsKey(Player) || !SelectedClasses.containsKey(OtherPlayer)) { continue; }
                    if (Objects.equals(SelectedClasses.get(Player), SelectedClasses.get(OtherPlayer))) {
                        Player.sendMessage("§4§l" + OtherPlayer.getName() + " has already selected the " + SelectedClasses.get(Player) + " class!");
                        SelectedClasses.remove(Player);
                        ClassWeapons.remove(Player);
                        Player.playSound(Player, Sound.ENTITY_VILLAGER_NO, 1, 1);
                        return false;
                    }
                }
            }

            Player.playSound(Player, Sound.BLOCK_NOTE_BLOCK_PLING, 1, 1);
            return true;
        }
    }
}
