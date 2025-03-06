package game.bow.bowgame;

import game.bow.bowgame.Game.PlayerHandler;
import game.bow.bowgame.Upgrades.GUIListener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;

public final class BowGame extends JavaPlugin {

    public static Plugin Plugin;

    @Override
    public void onEnable() {

        Plugin = this;

        getLogger().info("Bow game loaded");

        Objects.requireNonNull(this.getCommand("bowgame")).setExecutor(new Commands());

        getServer().getPluginManager().registerEvents(new Boilerplate() ,this);
        getServer().getPluginManager().registerEvents(new PlayerHandler() ,this);
        getServer().getPluginManager().registerEvents(new GUIListener(), this);

    }

    public static Plugin GetPlugin() {
        return Plugin;
    }
}
