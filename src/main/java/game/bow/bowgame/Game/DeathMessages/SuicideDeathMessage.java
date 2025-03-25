package game.bow.bowgame.Game.DeathMessages;

import org.bukkit.Bukkit;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

public class SuicideDeathMessage {

    public static void RandomDeathMessage(String Player) {

        ArrayList<String> DeathMessages = new ArrayList<>(Arrays.asList(
                "§4☠ » §c" + Player + " killed themself",
                "§4☠ » §c" + Player + " never left their emo phase",
                "§4☠ » §c" + Player + " was never meant for this world",
                "§4☠ » §c" + Player + " committed seppuku",
                "§4☠ » §c" + Player + " hates his team"
        ));

        Bukkit.broadcastMessage(DeathMessages.get((new Random()).nextInt(DeathMessages.size())));
    }
}
