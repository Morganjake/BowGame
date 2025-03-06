package game.bow.bowgame.Upgrades;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import net.md_5.bungee.api.ChatColor;
import java.util.Arrays;
import java.util.List;

public class GUIHandler {

    // Example of a GUI
    public static void OpenExampleGUI(Player player) {
        Inventory gui = Bukkit.createInventory(null, 27, "§fExample GUI");

        // Create Icon
        ItemStack icon1 = SetIcon(
                Material.DIAMOND,
                "this is a really long sentence to check if the linear interpolation is working",
                Arrays.asList("#000000", "#FFFFFF", "#FFFFFF", "#000000"));
        gui.setItem(12, icon1); // Place the item in the GUI

        // Open the GUI
        player.openInventory(gui);
    }

    // Creates a new icon to be used in a GUI (w/ text & colors)
    static ItemStack SetIcon(Material mat, String text, List<String> colors, Boolean bold) {
        // Create item and extract metadata (if blank then return barrier icon)
        ItemStack icon = new ItemStack(mat);
        ItemMeta meta = icon.getItemMeta();
        if (meta == null) return (new ItemStack(Material.BARRIER));

        // Set the custom name with a hex color (example: #FF00FF for purple)
        String coloredName = TextColorGradient(text, colors, bold);

        // Set the display name of the item
        meta.setDisplayName(coloredName);

        // Apply the modified meta to the item & return
        icon.setItemMeta(meta);
        return icon;
    }

    // Creates a new icon to be used in a GUI (w/ text & colors)
    static ItemStack SetIcon(Material mat, String text, List<String> colors) {
        // Create item and extract metadata (if blank then return barrier icon)
        ItemStack icon = new ItemStack(mat);
        ItemMeta meta = icon.getItemMeta();
        if (meta == null) return (new ItemStack(Material.BARRIER));

        // Set the custom name with a hex color (example: #FF00FF for purple)
        String coloredName = TextColorGradient(text, colors, false);

        // Set the display name of the item
        meta.setDisplayName(coloredName);

        // Apply the modified meta to the item & return
        icon.setItemMeta(meta);
        return icon;
    }

    // Creates a new icon to be used in a GUI (w/ text)
    static ItemStack SetIcon(Material mat, String text) {
        // Create item and extract metadata (if blank then return barrier icon)
        ItemStack icon = new ItemStack(mat);
        ItemMeta meta = icon.getItemMeta();
        if (meta == null) return (new ItemStack(Material.BARRIER));

        // Set the display name of the item
        meta.setDisplayName(text);

        // Apply the modified meta to the item & return
        icon.setItemMeta(meta);
        return icon;
    }

    // Creates a new icon to be used in a GUI (blank name)
    static ItemStack SetIcon(Material mat) {
        // Create item and extract metadata (if blank then return barrier icon)
        ItemStack icon = new ItemStack(mat);
        ItemMeta meta = icon.getItemMeta();
        if (meta == null) return (new ItemStack(Material.BARRIER));

        // Set the display name of the item to blank
        meta.setDisplayName(" ");

        // Apply the modified meta to the item & return
        icon.setItemMeta(meta);
        return icon;
    }

    // Apply the same icon to multiple indexes in a GUI
    static void ApplyIcons(Inventory gui, ItemStack icon, List<Integer> indexes) {
        for (Integer index : indexes) {
            gui.setItem(index, icon);
        }
    }

    // Creates a specified text colour gradient
    private static String TextColorGradient(String text, List<String> colors, Boolean bold) {
        StringBuilder gradientText = new StringBuilder();

        // Split text into segments to be interpolated through
        int segSplit = (int)Math.ceil((float)text.length() / (colors.size() - 1));
        for (int color = 0; color < colors.size() - 1; color++) {

            String subText = text.substring((color) * segSplit, Math.min(text.length(), (color + 1) * segSplit));

            // Convert hex colors to Color objects
            int r1 = Integer.parseInt(colors.get(color).substring(1, 3), 16); // Red component of color1
            int g1 = Integer.parseInt(colors.get(color).substring(3, 5), 16); // Green component of color1
            int b1 = Integer.parseInt(colors.get(color).substring(5, 7), 16); // Blue component of color1

            int r2 = Integer.parseInt(colors.get(color + 1).substring(1, 3), 16); // Red component of color2
            int g2 = Integer.parseInt(colors.get(color + 1).substring(3, 5), 16); // Green component of color2
            int b2 = Integer.parseInt(colors.get(color + 1).substring(5, 7), 16); // Blue component of color2

            // Loop through each character in the text and apply the gradient
            for (int i = 0; i < subText.length(); i++) {
                // Calculate the ratio of the current position
                float ratio = (float) i / (subText.length() - 1);

                // Interpolate each color component
                int r = (int) (r1 + (r2 - r1) * ratio);
                int g = (int) (g1 + (g2 - g1) * ratio);
                int b = (int) (b1 + (b2 - b1) * ratio);

                // Build the hex color string
                String hexColor = String.format("#%02X%02X%02X", r, g, b);

                // Apply the gradient color to the current character
                gradientText.append(ChatColor.of(hexColor));
                if (bold) gradientText.append("§l");
                gradientText.append(subText.charAt(i));

            }
        }

        return gradientText.toString();
    }
}
