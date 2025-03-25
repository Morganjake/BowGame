package game.bow.bowgame.Game.DeathMessages;

import org.bukkit.Bukkit;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

public class EmbarrassingDeathMessage {

    public static void RandomDeathMessage(String Player, String Attacker) {

        ArrayList<String> DeathMessages = new ArrayList<>(Arrays.asList(
                "§4☠ » §c§l" + Player + " §d§lwas absolutely humiliated by §c§l" + Attacker + "§d§l!!!",
                "§4☠ » §c§l" + Player + " §d§lwas shamefully embarrassed by §c§l" + Attacker + "§d§l!!!",
                "§4☠ » §c§l" + Player + " §d§lmissed all their shots on §c§l" + Attacker + " §d§llike a noob!!!",
                "§4☠ » §c§l" + Player + " §d§lput up a pitiful defence against §c§l" + Attacker + "§d§l!!!",
                "§4☠ » §c§l" + Player + " §d§lgot sent to the noob patrol by §c§l" + Attacker + "§d§l!!!",
                "§4☠ » §c§l" + Player + " §d§lneeds to get back on Aimlabs to fight §c§l" + Attacker + "§d§l!!!",
                "§4☠ » §c§l" + Player + " §d§lgot aura farmed by §c§l" + Attacker + "§d§l!!!"
        ));

        Bukkit.broadcastMessage(DeathMessages.get((new Random()).nextInt(DeathMessages.size())));
    }
}
