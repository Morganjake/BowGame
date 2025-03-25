package game.bow.bowgame.Game;

import game.bow.bowgame.Game.DeathMessages.EmbarrassingDeathMessage;
import game.bow.bowgame.Game.DeathMessages.MildDeathMessages;
import game.bow.bowgame.Game.DeathMessages.NormalDeathMessage;
import game.bow.bowgame.Game.DeathMessages.SuicideDeathMessage;
import org.bukkit.entity.Player;

import java.util.HashMap;

public class DeathMessagesHandler {

    public static HashMap<Player, HashMap<Player, Double>> DamageTaken = new HashMap<>();

    public static void AddDamage(Player Player, Player Attacker, Double Damage) {

        if (!DamageTaken.containsKey(Player)) {
            DamageTaken.put(Player, new HashMap<>());
        }

        if (DamageTaken.get(Player).containsKey(Attacker)) {
            DamageTaken.get(Player).replace(Attacker, DamageTaken.get(Player).get(Attacker) + Damage);
        }
        else {
            DamageTaken.get(Player).put(Attacker, Damage);
        }
    }


    public static void SendDeathMessage(Player Player, Player Attacker) {

        if (Player == Attacker) {
            SuicideDeathMessage.RandomDeathMessage(Player.getName());
            return;
        }

        double TotalDamage = 0;
        for (double Damage : DamageTaken.get(Player).values()) {
            TotalDamage += Damage;
        }

        boolean AttackerTakenDamageFromPlayer = true;
        if (!DamageTaken.containsKey(Attacker)) {
            AttackerTakenDamageFromPlayer = false;
        }
        else if (!DamageTaken.get(Attacker).containsKey(Player)) {
            AttackerTakenDamageFromPlayer = false;
        }

        if (DamageTaken.get(Player).size() == 1 && !AttackerTakenDamageFromPlayer) {
            EmbarrassingDeathMessage.RandomDeathMessage(Player.getName(), Attacker.getName());
        }
        else if (DamageTaken.get(Player).get(Attacker) / TotalDamage > 0.5 && !AttackerTakenDamageFromPlayer) {
            MildDeathMessages.RandomDeathMessage(Player.getName(), Attacker.getName());
        }
        else if (DamageTaken.get(Player).get(Attacker) / TotalDamage > 0.5 && DamageTaken.get(Player).get(Attacker) / 2 >= DamageTaken.get(Attacker).get(Player)) {
            MildDeathMessages.RandomDeathMessage(Player.getName(), Attacker.getName());
        }
        else {
            NormalDeathMessage.RandomDeathMessage(Player.getName(), Attacker.getName());
        }
    }
}
