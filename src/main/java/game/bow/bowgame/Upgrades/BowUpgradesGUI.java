package game.bow.bowgame.Upgrades;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class BowUpgradesGUI extends MainUpgradesGUI {

    public static void OpenBowUpgradesGUI(Player player) {
        Inventory gui = Bukkit.createInventory(null, 27, "§6§l✶ §e§l3");

        // Create glass pane BG
        List<Integer> grayGlassIndexes = new ArrayList<>();
        for (int i = 0; i <= 26; i++) {
            grayGlassIndexes.add(i);
        }
        ItemStack grayGlass = SetIcon(Material.GRAY_STAINED_GLASS_PANE);
        ApplyIcons(gui, grayGlass, grayGlassIndexes);

        List<String> upgradeGradient = Arrays.asList("#AB0707", "#CC0808", "#AB0707");

        ItemStack velocity = MainUpgradesGUI.SetIcon(
                Material.FIREWORK_ROCKET,
                TextColorGradient(
                        "Velocity",
                        upgradeGradient,
                        true),
                CreateUpgradeLore(
                        "Increases bow velocity",
                        "Velocity",
                        "1",
                        "1.1",
                        "30"
                )
        );
        ItemStack arrowDamage = MainUpgradesGUI.SetIcon(
                Material.ARROW,
                TextColorGradient(
                        "Arrow Damage",
                        upgradeGradient,
                        true),
                CreateUpgradeLore(
                        "Increases arrow damage",
                        "Damage",
                        "1",
                        "2",
                        "10"
                )
        );
        ItemStack backstab = MainUpgradesGUI.SetIcon(
                Material.IRON_SWORD,
                TextColorGradient(
                        "Backstab",
                        upgradeGradient,
                        true),
                CreateUpgradeLore(
                        "Increases damage from behind",
                        "Damage Multiplier",
                        "1",
                        "1.5",
                        "50"
                )
        );
        ItemStack poison = MainUpgradesGUI.SetIcon(
                Material.POISONOUS_POTATO,
                TextColorGradient(
                        "Poison",
                        upgradeGradient, // Orange-red gradient
                        true),
                CreateUpgradeLore(
                        "Increases poison duration",
                        "Duration",
                        "3",
                        "5",
                        "75"
                )
        );
        ItemStack bounce = MainUpgradesGUI.SetIcon(
                Material.SLIME_BALL,
                TextColorGradient(
                        "Bounce",
                        upgradeGradient, // Orange-red gradient
                        true),
                CreateUpgradeLore(
                        "Increases arrow richochet",
                        "Bounces",
                        "0",
                        "1",
                        "125"
                )
        );
        gui.setItem(4, velocity);
        gui.setItem(10, arrowDamage);
        gui.setItem(16, backstab);
        gui.setItem(21, poison);
        gui.setItem(23, bounce);

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
