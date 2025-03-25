package game.bow.bowgame.Game;

import org.bukkit.Bukkit;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.*;

import java.util.Arrays;

import static game.bow.bowgame.Classes.ClassHandler.UltPoints;
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
        ScoreboardManager ScoreboardManager = Bukkit.getScoreboardManager();
        Scoreboard ScoreBoard = ScoreboardManager.getNewScoreboard();

        Objective Objective = ScoreBoard.registerNewObjective("Players", "");
        Objective.setDisplaySlot(DisplaySlot.SIDEBAR);
        Objective.setDisplayName(TextColorGradient(
                "===《Players 》===",
                Arrays.asList("#C68800", "#FFF300", "#C68800"),
                true
        ));

        for (Player Player : BlueTeam) {

            String Ult = "";

            for (int i = 0; i < UltPoints.get(Player); i++) {
                Ult += "§9§l♦";
            }
            for (int i = 0; i < 5 - UltPoints.get(Player); i++) {
                Ult += "§7♦";
            }

            if (Deaths.get(Player) == 0) {
                Score a = Objective.getScore("§9§l" + Player.getName() + " " + Ult + ":§9 " + Kills.get(Player) + " / " + Deaths.get(Player) + " / " + Kills.get(Player));
                a.setScore(0);
            }
            else {
                Score a = Objective.getScore(
                        "§9§l" + Player.getName() + " " + Ult + ":§9 " + Kills.get(Player) + " / " + Deaths.get(Player) + " / " + Kills.get(Player) / Deaths.get(Player));
                a.setScore(0);
            }
        }

        Score o = Objective.getScore(" ");
        o.setScore(0);

        for (Player Player : RedTeam) {

            String Ult = "";

            for (int i = 0; i < UltPoints.get(Player); i++) {
                Ult += "§c§l♦";
            }
            for (int i = 0; i < 5 - UltPoints.get(Player); i++) {
                Ult += "§7♦";
            }

            if (Deaths.get(Player) == 0) {
                Score a = Objective.getScore("§c§l" + Player.getName() + " " + Ult + ":§c " + Kills.get(Player) + " / " + Deaths.get(Player) + " / " + Kills.get(Player));
                a.setScore(0);
            }
            else {
                Score a = Objective.getScore(
                        "§c§l" + Player.getName() + " " + Ult + ":§c " + Kills.get(Player) + " / " + Deaths.get(Player) + " / " + Kills.get(Player) / Deaths.get(Player));
                a.setScore(0);
            }
        }

        for (Player Player : Players) {
            Player.setScoreboard(ScoreBoard);
        }
    }
}
