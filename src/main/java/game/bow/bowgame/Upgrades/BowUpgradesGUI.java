package game.bow.bowgame.Upgrades;

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

import static game.bow.bowgame.Upgrades.UpgradeHandler.*;

public class BowUpgradesGUI extends MainUpgradesGUI implements Listener {

    public static void OpenBowUpgradesGUI(Player Player) {
        Inventory GUI = Bukkit.createInventory(null, 27, "§6§l✶ §e§l" + Money.get(Player));

        // Create glass pane BG
        List<Integer> grayGlassIndexes = new ArrayList<>();
        for (int i = 0; i <= 26; i++) {
            grayGlassIndexes.add(i);
        }

        ItemStack grayGlass = SetIcon(Material.GRAY_STAINED_GLASS_PANE);
        ApplyIcons(GUI, grayGlass, grayGlassIndexes);

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
                        String.valueOf(Math.pow(1.1, PlayerUpgrades.get(Player).get("Velocity"))),
                        String.valueOf(Math.pow(1.1, PlayerUpgrades.get(Player).get("Velocity") + 1)),
                        String.valueOf(5 + 10 * PlayerUpgrades.get(Player).get("Velocity"))
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
                        String.valueOf(PlayerUpgrades.get(Player).get("Damage") * 0.5 + 5),
                        String.valueOf(PlayerUpgrades.get(Player).get("Damage") * 0.5 + 5.5),
                        String.valueOf(5 + 10 * PlayerUpgrades.get(Player).get("Damage"))
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
                        "Damage multiplier",
                        String.valueOf(PlayerUpgrades.get(Player).get("Backstab") * 0.3 + 1),
                        String.valueOf(PlayerUpgrades.get(Player).get("Backstab") * 0.3 + 1.3),
                        String.valueOf(50 + 40 * PlayerUpgrades.get(Player).get("Backstab"))
                )
        );
        ItemStack bleed = MainUpgradesGUI.SetIcon(
                Material.POINTED_DRIPSTONE,
                TextColorGradient(
                        "Bleed",
                        upgradeGradient, // Orange-red gradient
                        true),
                CreateUpgradeLore(
                        "Increases bleed duration",
                        "Bleed duration",
                        String.valueOf(PlayerUpgrades.get(Player).get("Bleed") * 2),
                        String.valueOf(PlayerUpgrades.get(Player).get("Bleed") * 2 + 2),
                        String.valueOf(60 + 60 * PlayerUpgrades.get(Player).get("Bleed"))
                )
        );
        ItemStack bounce = MainUpgradesGUI.SetIcon(
                Material.SLIME_BALL,
                TextColorGradient(
                        "Bounce",
                        upgradeGradient, // Orange-red gradient
                        true),
                CreateUpgradeLore(
                        "Increases arrow bounces",
                        "Bounces",
                        String.valueOf(PlayerUpgrades.get(Player).get("Bounce")),
                        String.valueOf(PlayerUpgrades.get(Player).get("Bounce") + 1),
                        String.valueOf(70 + 35 * PlayerUpgrades.get(Player).get("Bounce"))
                )
        );
        GUI.setItem(4, velocity);
        GUI.setItem(10, arrowDamage);
        GUI.setItem(16, backstab);
        GUI.setItem(21, bleed);
        GUI.setItem(23, bounce);

        ItemStack returnToMain = SetIcon(
                Material.BARRIER,
                TextColorGradient(
                        "Return to Main Menu",
                        Arrays.asList("#FF0000", "#CC0000", "#990000"),
                        true)
        );
        GUI.setItem(26, returnToMain);

        Player.openInventory(GUI);
    }

    @EventHandler
    public void OnInventoryClick(InventoryClickEvent Event) {

        Player Player = (Player) Event.getWhoClicked();

        if (Event.getCurrentItem() == null || !Event.getView().getTitle().contains("✶")) {
            return;
        }

        Material ClickedItem = Event.getCurrentItem().getType();

        if (ClickedItem == Material.ARROW) {
            BuyUpgrade(Player, "Damage", 5 + 10 * PlayerUpgrades.get(Player).get("Damage"), "Bow");
        }
        else if (ClickedItem == Material.FIREWORK_ROCKET) {
            BuyUpgrade(Player, "Velocity", 5 + 10 * PlayerUpgrades.get(Player).get("Velocity"), "Bow");
        }
        else if (ClickedItem == Material.IRON_SWORD) {
            BuyUpgrade(Player, "Backstab", 50 + 40 * PlayerUpgrades.get(Player).get("Backstab"), "Bow");
        }
        else if (ClickedItem == Material.POINTED_DRIPSTONE) {
            BuyUpgrade(Player, "Bleed", 60 + 60 * PlayerUpgrades.get(Player).get("Bleed"), "Bow");
        }
        else if (ClickedItem == Material.SLIME_BALL) {
            BuyUpgrade(Player, "Bounce", 70 + 35 * PlayerUpgrades.get(Player).get("Bounce"), "Bow");
        }
    }
}
