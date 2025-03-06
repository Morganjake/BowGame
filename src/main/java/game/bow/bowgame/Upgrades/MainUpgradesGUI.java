package game.bow.bowgame.Upgrades;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.List;

public class MainUpgradesGUI extends GUIHandler {

    public static void OpenUpgradesGUI(Player player) {
        Inventory gui = Bukkit.createInventory(null, 27, "§6§l✶ §e§l100");

        // Create glass pane BG
        List<Integer> redGlassIndexes = Arrays.asList(0, 1, 2, 9, 11, 18, 19, 20);
        List<Integer> blueGlassIndexes = Arrays.asList(3, 4, 5, 12, 14, 21, 22, 23);
        List<Integer> greenGlassIndexes = Arrays.asList(6, 7, 8, 15, 17, 24, 25, 26);
        ItemStack redGlass = SetIcon(Material.RED_STAINED_GLASS_PANE);
        ItemStack blueGlass = SetIcon(Material.LIGHT_BLUE_STAINED_GLASS_PANE);
        ItemStack greenGlass = SetIcon(Material.LIME_STAINED_GLASS_PANE);
        ApplyIcons(gui, redGlass, redGlassIndexes);
        ApplyIcons(gui, blueGlass, blueGlassIndexes);
        ApplyIcons(gui, greenGlass, greenGlassIndexes);

        // Upgrade icons
        ItemStack bowUpgrades = SetIcon(
                Material.BOW,
                TextColorGradient(
                        "Bow Upgrades",
                        Arrays.asList("#AB0707", "#CC0808", "#AC0707"),
                        true),
                "§fBuy bow upgrades here!"
        );
        ItemStack defUpgrades = SetIcon(
                Material.DIAMOND_CHESTPLATE,
                TextColorGradient(
                        "Defence Upgrades",
                        Arrays.asList("#0093FF", "#2EA6FF", "#0093FF"),
                        true),
                "§fBuy defence upgrades here!"
        );
        ItemStack items = SetIcon(
                Material.TNT,
                TextColorGradient(
                        "Items",
                        Arrays.asList("#00A821", "#00C226", "#00A821"),
                        true),
                "§fBuy items here!"
        );
        gui.setItem(10, bowUpgrades);
        gui.setItem(13, defUpgrades);
        gui.setItem(16, items);

        // Open the GUI
        player.openInventory(gui);
    }

    static List<String> CreateUpgradeLore(String tooltip, String toIncrease, String prevValue, String postValue, String cost) {

        String tooltipLine = "§7" + tooltip;
        String upgradeLine = "§7" + toIncrease + ": §f" + prevValue + " §f» §3§l" + postValue;
        String costLine = "§6Cost: §6§l✶ " + cost;

        return Arrays.asList(tooltipLine, upgradeLine, costLine);
    }

    static List<String> CreateItemLore(String tooltip, String cost) {

        String tooltipLine = "§7" + tooltip;
        String costLine = "§6Cost: §6§l✶ " + cost;

        return Arrays.asList(tooltipLine, costLine);
    }
}
