package me.mowlcoder.infinitymine.mine;

import org.bukkit.Material;

public class MineBlockDrop {
    private Material material;
    private double chance;

    public MineBlockDrop(Material material, double chance) {
        this.material = material;
        this.chance = chance;
    }

    public int compareByChance(MineBlockDrop blockDrop2) {
        return Double.compare(chance, blockDrop2.getChance());
    }

    public Material getMaterial() {
        return material;
    }

    public double getChance() {
        return chance;
    }
}
