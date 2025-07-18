package game.bow.bowgame.Game;

import org.bukkit.Bukkit;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.*;

import java.util.Arrays;

import static game.bow.bowgame.Classes.ClassHandler.Classes;
import static game.bow.bowgame.Classes.ClassHandler.UltPoints;
import static game.bow.bowgame.Classes.Mage.SelectedSpell;
import static game.bow.bowgame.Game.GameHandler.*;
import static game.bow.bowgame.Game.PlayerHandler.*;
import static game.bow.bowgame.Upgrades.GUIHandler.TextColorGradient;

public class GameUIHandler {

    public static BossBar ScoreBossBar = null;


    public static void InitBossBar() {

        ScoreBossBar = Bukkit.createBossBar("§b0 §a- §c0", BarColor.GREEN, BarStyle.SOLID);

        for (Player Player : Bukkit.getOnlinePlayers()) {
            ScoreBossBar.addPlayer(Player);
        }
    }


    public static void UpdateBossbar() {

        if (BlueScore > RedScore) {
            ScoreBossBar.setTitle("§b§l" + BlueScore + " - §c" + RedScore);
            ScoreBossBar.setColor(BarColor.BLUE);
        }
        else if (RedScore > BlueScore) {
            ScoreBossBar.setTitle("§b" + BlueScore + "§c§l - " + RedScore);
            ScoreBossBar.setColor(BarColor.RED);
        }
        else {
            ScoreBossBar.setTitle("§b" + BlueScore + "§a - §c" + RedScore);
            ScoreBossBar.setColor(BarColor.GREEN);
        }
    }

    public static void UpdateScoreBoard() {
        for (Player Player : Players) {
            UpdatePlayerScoreBoard(Player);
        }
    }

    public static void UpdatePlayerScoreBoard(Player PlayerToUpdate) {

        ScoreboardManager ScoreboardManager = Bukkit.getScoreboardManager();
        Scoreboard ScoreBoard = ScoreboardManager.getNewScoreboard();

        Objective Objective = ScoreBoard.registerNewObjective("Players", "");
        Objective.setDisplaySlot(DisplaySlot.SIDEBAR);
        Objective.setDisplayName(TextColorGradient(
                "===《Players 》===",
                Arrays.asList("#C68800", "#FFF300", "#C68800"),
                true
        ));

        Score o = Objective.getScore(" ");
        o.setScore(5);

        for (Player Player : BlueTeam) {

            String Ult = GetUltPointDisplay(Player, "9", "");

            if (Deaths.get(Player) == 0) {
                Score a = Objective.getScore("§9§l" + Player.getName() + " " + Ult + ":§9 " + Kills.get(Player) + " / " + Deaths.get(Player) + " / " + Kills.get(Player));
                a.setScore(4);
            }
            else {
                Score a = Objective.getScore(
                        "§9§l" + Player.getName() + " " + Ult + ":§9 " + Kills.get(Player) + " / " + Deaths.get(Player) + " / " + Kills.get(Player) / Deaths.get(Player));
                a.setScore(4);
            }
        }

        for (Player Player : DisconnectedBlueTeam) {

            String Ult = GetUltPointDisplay(Player, "1", "§m");

            if (Deaths.get(Player) == 0) {
                Score a = Objective.getScore("§1§l§m" + Player.getName() + " " + Ult + ":§1§m " + Kills.get(Player) + " / " + Deaths.get(Player) + " / " + Kills.get(Player));
                a.setScore(3);
            }
            else {
                Score a = Objective.getScore(
                        "§1§l§m" + Player.getName() + " " + Ult + ":§1§m " + Kills.get(Player) + " / " + Deaths.get(Player) + " / " + Kills.get(Player) / Deaths.get(Player));
                a.setScore(3);
            }
        }

        for (Player Player : RedTeam) {

            String Ult = GetUltPointDisplay(Player, "c", "");

            if (Deaths.get(Player) == 0) {
                Score a = Objective.getScore("§c§l" + Player.getName() + " " + Ult + ":§c " + Kills.get(Player) + " / " + Deaths.get(Player) + " / " + Kills.get(Player));
                a.setScore(2);
            }
            else {
                Score a = Objective.getScore(
                        "§c§l" + Player.getName() + " " + Ult + ":§c " + Kills.get(Player) + " / " + Deaths.get(Player) + " / " + Kills.get(Player) / Deaths.get(Player));
                a.setScore(2);
            }
        }

        for (Player Player : DisconnectedRedTeam) {

            String Ult = GetUltPointDisplay(Player, "4", "§m");

            if (Deaths.get(Player) == 0) {
                Score a = Objective.getScore("§4§l§m" + Player.getName() + " " + Ult + ":§4§m " + Kills.get(Player) + " / " + Deaths.get(Player) + " / " + Kills.get(Player));
                a.setScore(1);
            }
            else {
                Score a = Objective.getScore(
                        "§4§l§m" + Player.getName() + " " + Ult + ":§4§m " + Kills.get(Player) + " / " + Deaths.get(Player) + " / " + Kills.get(Player) / Deaths.get(Player));
                a.setScore(1);
            }
        }

        if (Classes.get(PlayerToUpdate).equals("Mage")) {
            Score a = Objective.getScore(switch (SelectedSpell.get(PlayerToUpdate)) {
                case 1 -> "§dConfusion arrow selected";
                case 2 -> "§dIllusion arrow selected";
                case 3 -> "§dInvisibility arrow selected";
                default -> "";
            });
            a.setScore(0);
        }

        PlayerToUpdate.setScoreboard(ScoreBoard);
    }

    private static String GetUltPointDisplay(Player Player, String TeamColour, String Strikethrough) {
        String Ult = "";

        for (int i = 0; i < UltPoints.get(Player); i++) {
            Ult += "§" + TeamColour + Strikethrough + "§l" + Strikethrough + "♦";
        }
        for (int i = 0; i < 6 - UltPoints.get(Player); i++) {
            Ult += "§7" + Strikethrough +"♦";
        }

        return Ult;
    }
}
