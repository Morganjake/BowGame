package game.bow.bowgame;

import game.bow.bowgame.Classes.Astronaut;
import game.bow.bowgame.Classes.Demolitionist;
import game.bow.bowgame.Classes.Hacker;
import game.bow.bowgame.Classes.SpaceWeaver;
import game.bow.bowgame.Game.GameHandler;
import game.bow.bowgame.Game.ItemHandler;
import game.bow.bowgame.Game.PlayerHandler;
import game.bow.bowgame.Upgrades.*;
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
        Objects.requireNonNull(getCommand("BowGame")).setTabCompleter(new Commands());

        getServer().getPluginManager().registerEvents(new Boilerplate() ,this);
        getServer().getPluginManager().registerEvents(new PlayerHandler() ,this);
        getServer().getPluginManager().registerEvents(new GameHandler() ,this);
        getServer().getPluginManager().registerEvents(new GUIListener(), this);
        getServer().getPluginManager().registerEvents(new BowUpgradesGUI(), this);
        getServer().getPluginManager().registerEvents(new DefenceUpgradesGUI(), this);
        getServer().getPluginManager().registerEvents(new ClassGUI(), this);
        getServer().getPluginManager().registerEvents(new ItemsGUI(), this);
        getServer().getPluginManager().registerEvents(new ItemHandler(), this);

        getServer().getPluginManager().registerEvents(new SpaceWeaver(), this);
        getServer().getPluginManager().registerEvents(new Demolitionist(), this);
        getServer().getPluginManager().registerEvents(new Astronaut(), this);
        getServer().getPluginManager().registerEvents(new Hacker(), this);
    }

    public static Plugin GetPlugin() {
        return Plugin;
    }
}
