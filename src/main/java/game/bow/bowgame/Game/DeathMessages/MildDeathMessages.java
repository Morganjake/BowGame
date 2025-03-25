package game.bow.bowgame.Game.DeathMessages;

import org.bukkit.Bukkit;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

public class MildDeathMessages {

    public static void RandomDeathMessage(String Player, String Attacker) {

        ArrayList<String> DeathMessages = new ArrayList<>(Arrays.asList(
                "§4☠ » §c" + Player + " §6got destroyed by §c" + Attacker,
                "§4☠ » §c" + Player + " §6was obliterated by §c" + Attacker,
                "§4☠ » §c" + Player + " §6got stomped by §c" + Attacker,
                "§4☠ » §c" + Player + " §6never stood a chance against §c" + Attacker,
                "§4☠ » §c" + Player + " §6challenged §c" + Attacker + " §6to a gooning session and lost",
                "§4☠ » §c" + Player + " §6got no-diffed by §c" + Attacker
        ));

        Bukkit.broadcastMessage(DeathMessages.get((new Random()).nextInt(DeathMessages.size())));
    }
}
