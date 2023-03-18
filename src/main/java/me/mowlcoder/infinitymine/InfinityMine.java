package me.mowlcoder.infinitymine;

import me.mowlcoder.infinitymine.commands.MineCMD;
import me.mowlcoder.infinitymine.events.PlayerEvents;
import me.mowlcoder.infinitymine.mine.MineManager;
import org.bukkit.plugin.java.JavaPlugin;

public final class InfinityMine extends JavaPlugin {

    private static InfinityMine instance;

    public static InfinityMine getInstance() {
        return instance;
    }

    @Override
    public void onEnable() {
        instance = this;

        MineManager.getInstance().loadAllMinesFromConfig();

        getCommand("mine").setExecutor(new MineCMD());

        getServer().getPluginManager().registerEvents(new PlayerEvents(), this);

        saveDefaultConfig();
    }

    @Override
    public void onDisable() {
        MineManager.getInstance().saveAllMinesToConfig();
    }
}
