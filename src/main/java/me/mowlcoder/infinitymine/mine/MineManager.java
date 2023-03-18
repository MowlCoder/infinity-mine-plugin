package me.mowlcoder.infinitymine.mine;

import me.mowlcoder.infinitymine.InfinityMine;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MineManager {

    private final static MineManager instance = new MineManager();

    public static MineManager getInstance() {
        return instance;
    }

    private final static List<Mine> allMines = new ArrayList<>();

    public Mine getMineById(String id) {
        for (Mine mine : allMines) {
            if (mine.getId().equals(id)) {
                return mine;
            }
        }

        return null;
    }

    public List<String> getAllMineIds() {
        List<String> ids = new ArrayList<>();

        for (Mine mine : allMines) {
            ids.add(mine.getId());
        }

        return ids;
    }

    public Mine getMineByLocation(Location location) {
        for (Mine mine : allMines) {
            if (mine.isLocationInMine(location)) {
                return mine;
            }
        }

        return null;
    }

    public void addMine(Mine mine) {
        allMines.add(mine);
    }

    public void deleteMine(Mine mine) {
        allMines.remove(mine);
    }

    public void loadAllMinesFromConfig() {
        File configFile = new File(InfinityMine.getInstance().getDataFolder(), "mines.yml");

        if (!configFile.exists()) {
            try {
                configFile.createNewFile();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        YamlConfiguration configuration = YamlConfiguration.loadConfiguration(configFile);

        configuration.getKeys(false).forEach(key -> {
            String name = configuration.getString(key + ".name");
            int width = configuration.getInt(key + ".width");
            int height = configuration.getInt(key + ".height");
            int deepSize = configuration.getInt(key + ".deepSize");
            long resetDelay = configuration.getLong(key + ".resetDelay");
            Location location = configuration.getLocation(key + ".location");
            Location playerLocation = configuration.getLocation(key + ".playerSpawn");

            ConfigurationSection blocksDropsSection = configuration.getConfigurationSection(key + ".blocksDrop");
            List<MineBlockDrop> blockDrops = new ArrayList<>();

            blocksDropsSection.getKeys(false).forEach(blockMaterial -> {
                Material material = Material.valueOf(blockMaterial);
                Double chance = blocksDropsSection.getDouble(blockMaterial);

                blockDrops.add(new MineBlockDrop(material,chance));
            });

            Mine mine = new Mine(key, width, height, deepSize, location);
            mine.setName(name);
            mine.setPlayerSpawnLocation(playerLocation);
            mine.setMineResetDelay(resetDelay);
            mine.setBlockDrops(blockDrops);

            try {
                String defaultBlockName = configuration.getString(key + ".defaultBlock");
                mine.setDefaultBlock(Material.valueOf(defaultBlockName));
            } catch (IllegalArgumentException | NullPointerException ignored) {}

            allMines.add(mine);
        });
    }

    public void saveAllMinesToConfig() {
        File configFile = new File(InfinityMine.getInstance().getDataFolder(), "mines.yml");
        YamlConfiguration configuration = YamlConfiguration.loadConfiguration(configFile);

        configuration.getKeys(false).forEach(key -> {
            configuration.set(key, null);
        });

        for (Mine mine : allMines) {
            configuration.set(mine.getId() + ".name", mine.getName());
            configuration.set(mine.getId() + ".width", mine.getWidth());
            configuration.set(mine.getId() + ".height", mine.getHeight());
            configuration.set(mine.getId() + ".deepSize", mine.getDeepSize());
            configuration.set(mine.getId() + ".location", mine.getLocation());
            configuration.set(mine.getId() + ".playerSpawn", mine.getPlayerSpawnLocation());
            configuration.set(mine.getId() + ".resetDelay", mine.getMineResetDelay());
            configuration.set(mine.getId() + ".defaultBlock", mine.getDefaultBlock().name());

            List<MineBlockDrop> blockDrops = mine.getBlockDrops();

            for (MineBlockDrop blockDrop : blockDrops) {
                configuration.set(mine.getId() + ".blocksDrop." + blockDrop.getMaterial().name(), blockDrop.getChance());
            }
        }

        try {
            configuration.save(configFile);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
