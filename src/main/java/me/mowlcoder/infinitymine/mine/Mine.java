package me.mowlcoder.infinitymine.mine;

import me.mowlcoder.infinitymine.InfinityMine;
import me.mowlcoder.infinitymine.utils.ChatUtil;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public class Mine {
    private final String id;
    private String name;
    private final int width;
    private final int height;
    private final int deepSize;
    private final Location location;
    private Location playerSpawnLocation;
    private BukkitRunnable thread;
    private boolean isOpen;

    private long mineResetDelay = 200L;

    private Material defaultBlock = Material.STONE;
    private List<MineBlockDrop> blockDrops = new ArrayList<>();

    public Mine(String id, int width, int height, int deepSize, Location location) {
        this.id = id;
        this.name = id;
        this.width = width;
        this.height = height;
        this.deepSize = deepSize;
        this.location = location.clone();
        this.playerSpawnLocation = location.clone();
        this.isOpen = false;

        blockDrops.add(new MineBlockDrop(Material.DIAMOND_ORE, 0.1));
        blockDrops.add(new MineBlockDrop(Material.GOLD_ORE, 0.2));
        blockDrops.add(new MineBlockDrop(Material.IRON_ORE, 0.5));
    }

    public void generateMine() {
        int bottomY = (int) location.getY() - deepSize;
        int maxX = (int) location.getX() + width;
        int maxZ = (int) location.getZ() + height;

        Random random = new Random();

        for (int y = (int) location.getY(); y > bottomY; y--) {
            for (int x = (int) location.getX(); x < maxX; x++) {
                for (int z = (int) location.getZ(); z < maxZ; z++) {
                    Location l = new Location(
                            location.getWorld(),
                            x,
                            y,
                            z
                    );

                    double randomNumber = random.nextDouble();
                    Material resultBlock = defaultBlock;

                    List<MineBlockDrop> sortedDropsByChance = blockDrops.stream().sorted(MineBlockDrop::compareByChance).toList();

                    for (MineBlockDrop mineBlockDrop : sortedDropsByChance) {
                        if (randomNumber >= 1.0 - mineBlockDrop.getChance()) {
                            resultBlock = mineBlockDrop.getMaterial();
                            break;
                        }
                    }

                    l.getBlock().setType(resultBlock);
                }
            }
        }

        for (Player player : Bukkit.getOnlinePlayers()) {
            if (isPlayerInMine(player)) {
                player.teleport(playerSpawnLocation);
            }
        }
    }

    public void openMine() {
        this.isOpen = true;
        ChatUtil.broadCastMessage(
                InfinityMine.getInstance().getConfig()
                        .getString("messages.openMine")
                        .replace("%mine_name%", getName())
        );
        this.startMineThread();
    }

    public void closeMine() {
        this.isOpen = false;

        if (this.thread != null) {
            this.thread.cancel();
        }

        this.generateMine();
        ChatUtil.broadCastMessage(
                InfinityMine.getInstance().getConfig()
                        .getString("messages.closeMine")
                        .replace("%mine_name%", getName())
        );
    }

    public void prepareForDelete() {
        if (this.thread != null) {
            this.thread.cancel();
        }

        this.generateMine();
    }

    public void startMineThread() {
        this.thread = new BukkitRunnable() {
            int seconds = 10;

            @Override
            public void run() {
                if (seconds <= 0) {
                    cancel();
                    generateMine();
                    startMineThread();

                    ChatUtil.broadCastMessage(
                            InfinityMine.getInstance().getConfig()
                                    .getString("messages.regenerateMine")
                                    .replace("%mine_name%", getName())
                    );

                    return;
                } else if (seconds == 10 || seconds == 5 || seconds <= 3) {
                    ChatUtil.broadCastMessage(
                            InfinityMine.getInstance().getConfig()
                                    .getString("messages.mineRegenerateIn")
                                    .replace("%mine_name%", getName())
                                    .replace("%seconds%", String.valueOf(seconds))
                    );
                }

                seconds--;
            }
        };

        this.thread.runTaskTimer(InfinityMine.getInstance(), mineResetDelay, 20L);
    }

    public boolean isPlayerInMine(Player player) {
        return isLocationInMine(player.getLocation());
    }

    public boolean isLocationInMine(Location loc) {
        double minY = Math.ceil(location.getY() - deepSize);
        double maxX = Math.ceil(location.getX() + width);
        double maxZ = Math.ceil(location.getZ() + height);
        double maxY = Math.floor(location.getY());
        double minX = Math.floor(location.getX());
        double minZ = Math.floor(location.getZ());

        return (minX <= loc.getX() && loc.getX() <= maxX) && (minZ <= loc.getZ() && loc.getZ() <= maxZ) && (minY <= loc.getY() && loc.getY() <= maxY);
    }

    public void setPlayerSpawnLocation(Location playerSpawnLocation) {
        this.playerSpawnLocation = playerSpawnLocation;
    }

    public void setMineResetDelay(long mineResetDelay) {
        this.mineResetDelay = mineResetDelay;
    }

    public void setBlockDrops(List<MineBlockDrop> blockDrops) {
        this.blockDrops = blockDrops;
    }

    public Material getDefaultBlock() {
        return defaultBlock;
    }

    public void setDefaultBlock(Material defaultBlock) {
        this.defaultBlock = defaultBlock;
    }

    public List<MineBlockDrop> getBlockDrops() {
        return blockDrops;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isOpen() {
        return isOpen;
    }

    public String getId() {
        return id;
    }

    public int getWidth() {
        return width;
    }

    public long getMineResetDelay() {
        return mineResetDelay;
    }

    public int getHeight() {
        return height;
    }

    public int getDeepSize() {
        return deepSize;
    }

    public Location getLocation() {
        return location;
    }

    public Location getPlayerSpawnLocation() {
        return playerSpawnLocation;
    }
}
