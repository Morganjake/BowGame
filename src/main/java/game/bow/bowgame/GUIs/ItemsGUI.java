package game.bow.bowgame.GUIs;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static game.bow.bowgame.Classes.ClassHandler.Classes;
import static game.bow.bowgame.GUIs.UpgradeHandler.*;
import static game.bow.bowgame.GUIs.UpgradeHandler.BuyUpgrade;

public class ItemsGUI extends MainUpgradesGUI implements Listener {

    public static void OpenItemsGUI(Player Player) {
        Inventory gui = Bukkit.createInventory(null, 27, "§6§l✶ §e§l" + Money.get(Player));

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
                        "Throws a bomb that freezes enemies for 3 seconds",
                        String.valueOf(60 + 40 * PlayerUpgrades.get(Player).get("Freeze Grenade"))
                )
        );
        ItemStack fragGrenade = MainUpgradesGUI.SetIcon(
                Material.GUNPOWDER,
                TextColorGradient(
                        "Explosive",
                        upgradeGradient,
                        true),
                CreateItemLore(
                        "Throws a bomb that deals 8 damage",
                        String.valueOf(40 + (Classes.get(Player).equals("Demolitionist")  ? 15 : 30) * PlayerUpgrades.get(Player).get("Explosive"))
                )
        );

        ItemStack smokeGrenade = MainUpgradesGUI.SetIcon(
                Material.INK_SAC,
                TextColorGradient(
                        "Smoke Bomb",
                        upgradeGradient,
                        true),
                CreateItemLore(
                        "Throws a bomb that creates a cloud of smoke",
                        String.valueOf(30 + 40 * PlayerUpgrades.get(Player).get("Smoke Bomb"))
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
                        String.valueOf(500 + 250 * PlayerUpgrades.get(Player).get("APD"))
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
                        String.valueOf(70 + 50 * PlayerUpgrades.get(Player).get("Medkit"))
                )
        );

        gui.setItem(4, freezeGrenade);
        gui.setItem(10, fragGrenade);
        gui.setItem(16, smokeGrenade);
        gui.setItem(21, apd);
        gui.setItem(23, medkit);

        ItemStack returnToMain = SetIcon(
                Material.BARRIER,
                TextColorGradient(
                        "Return to Main Menu",
                        Arrays.asList("#FF0000", "#CC0000", "#990000"),
                        true)
        );

        gui.setItem(26, returnToMain);

        // Open the GUI
        Player.openInventory(gui);
    }

    @EventHandler
    public void OnInventoryClick(InventoryClickEvent Event) {

        Player Player = (Player) Event.getWhoClicked();

        if (Event.getCurrentItem() == null || !Event.getView().getTitle().contains("✶")) {
            return;
        }

        Material ClickedItem = Event.getCurrentItem().getType();

        if (ClickedItem == Material.GUNPOWDER) {
            if (BuyUpgrade(Player, "Explosive", 40 + (Classes.get(Player).equals("Demolitionist")  ? 15 : 30) * PlayerUpgrades.get(Player).get("Explosive"), "Items")) {

            }
        }
        else if (ClickedItem == Material.PRISMARINE_SHARD) {
            if (BuyUpgrade(Player, "Freeze Grenade", 60 + 40 * PlayerUpgrades.get(Player).get("Freeze Grenade"), "Items")) {

            }
        }
        else if (ClickedItem == Material.INK_SAC) {
            if (BuyUpgrade(Player, "Smoke Bomb", 30 + 40 * PlayerUpgrades.get(Player).get("Smoke Bomb"), "Items")) {

            }
        }
        else if (ClickedItem == Material.BLAZE_ROD) {
            if (BuyUpgrade(Player, "APD", 500 + 250 * PlayerUpgrades.get(Player).get("APD"), "Items")) {

            }
        }
        else if (ClickedItem == Material.RED_DYE) {
            if (BuyUpgrade(Player, "Medkit", 70 + 50 * PlayerUpgrades.get(Player).get("Medkit"), "Items")) {

            }
        }
    }
}
