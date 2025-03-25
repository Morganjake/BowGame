package game.bow.bowgame.Game.DeathMessages;

import org.bukkit.Bukkit;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

public class NormalDeathMessage {

    public static void RandomDeathMessage(String Player, String Attacker) {

        ArrayList<String> DeathMessages = new ArrayList<>(Arrays.asList(
                "§4☠ » §c" + Player + " §7was killed by §c" + Attacker,
                "§4☠ » §c" + Player + " §7was murdered by §c" + Attacker,
                "§4☠ » §c" + Player + " §7died at the hands of §c" + Attacker,
                "§4☠ » §c" + Player + " §7died to §c" + Attacker,
                "§4☠ » §c" + Player + " §7got jumped by §c" + Attacker,
                "§4☠ » §c" + Player + " §7got shot by §c" + Attacker,
                "§4☠ » §c" + Player + " §7edged too hard to §c" + Attacker,
                "§4☠ » §c" + Player + " §7got cheffed up by §c" + Attacker
        ));

        Bukkit.broadcastMessage(DeathMessages.get((new Random()).nextInt(DeathMessages.size())));
    }
}
