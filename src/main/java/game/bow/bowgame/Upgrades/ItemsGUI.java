package game.bow.bowgame.Upgrades;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ItemsGUI extends MainUpgradesGUI {

    public static void OpenItemsGUI(Player player) {
        Inventory gui = Bukkit.createInventory(null, 27, "§6§l✶ §e§l420");

        // Create glass pane BG
        List<Integer> grayGlassIndexes = new ArrayList<>();
        for (int i = 0; i <= 26; i++) {
            grayGlassIndexes.add(i);
        }
        ItemStack grayGlass = SetIcon(Material.GRAY_STAINED_GLASS_PANE);
        ApplyIcons(gui, grayGlass, grayGlassIndexes);

        List<String> upgradeGradient = Arrays.asList("#00A821", "#00C226", "#00A821");

        ItemStack freezeGrenade = MainUpgradesGUI.SetIcon(
                Material.PRISMARINE_SHARD,
                TextColorGradient(
                        "Freeze Grenade",
                        upgradeGradient,
                        true),
                CreateItemLore(
                        "Throws a bomb that freezes enemies for 5 seconds",
                        "60"
                )
        );
        ItemStack fragGrenade = MainUpgradesGUI.SetIcon(
                Material.GUNPOWDER,
                TextColorGradient(
                        "Frag Grendade",
                        upgradeGradient,
                        true),
                CreateItemLore(
                        "Throws a bomb that deals 8 damage",
                        "40"
                )
        );
        ItemStack explosiveArrow = MainUpgradesGUI.SetIcon(
                Material.TNT,
                TextColorGradient(
                        "Explosive Arrow",
                        upgradeGradient,
                        true),
                CreateItemLore(
                        "The next arrow you shoot will explode upon impact",
                        "150"
                )
        );
        ItemStack smokeGrenade = MainUpgradesGUI.SetIcon(
                Material.INK_SAC,
                TextColorGradient(
                        "Smoke Grenade",
                        upgradeGradient,
                        true),
                CreateItemLore(
                        "Throws a bomb that creates a cloud of smoke",
                        "40"
                )
        );
        ItemStack apd = MainUpgradesGUI.SetIcon(
                Material.BLAZE_ROD,
                TextColorGradient(
                        "APD",
                        upgradeGradient,
                        true),
                CreateItemLore(
                        "Aerial People Deleter",
                        "500"
                )
        );
        ItemStack medkit = MainUpgradesGUI.SetIcon(
                Material.RED_DYE,
                TextColorGradient(
                        "Medkit",
                        upgradeGradient,
                        true),
                CreateItemLore(
                        "Heals 40% of your health over time",
                        "70"
                )
        );
        gui.setItem(4, freezeGrenade);
        gui.setItem(10, fragGrenade);
        gui.setItem(13, explosiveArrow);
        gui.setItem(16, smokeGrenade);
        gui.setItem(21, apd);
        gui.setItem(23, medkit);

        ItemStack returnToMain = SetIcon(
                Material.BARRIER,
                TextColorGradient(
                        "Return to Main Menu",
                        Arrays.asList("#FF0000", "#CC0000", "#990000"),
                        true),
                "§fClick to go back to the main menu!"
        );
        gui.setItem(26, returnToMain);

        // Open the GUI
        player.openInventory(gui);
    }
}
