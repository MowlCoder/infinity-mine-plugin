package me.mowlcoder.infinitymine.events;

import me.mowlcoder.infinitymine.mine.Mine;
import me.mowlcoder.infinitymine.mine.MineManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;

public class PlayerEvents implements Listener {

    @EventHandler
    public void onBreakBlock(BlockBreakEvent event) {
        Mine mine = MineManager.getInstance().getMineByLocation(event.getBlock().getLocation());

        if (mine == null) {
            return;
        }

        if (!mine.isOpen()) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlaceBlock(BlockPlaceEvent event) {
        Mine mine = MineManager.getInstance().getMineByLocation(event.getBlock().getLocation());

        if (mine != null) {
            event.setCancelled(true);
        }
    }
}
