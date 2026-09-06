package net.pufferlab.primal.utils;

import net.minecraft.block.Block;

import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.TObjectIntMap;
import gnu.trove.map.hash.TIntObjectHashMap;
import gnu.trove.map.hash.TObjectIntHashMap;

public class BlockMap {

    public static int lastID;
    public static final TObjectIntMap<Block> blockMap = new TObjectIntHashMap<>();

    public static int getBlockID(Block block) {
        if (blockMap.containsKey(block)) {
            return blockMap.get(block);
        }
        int blockID = lastID++;
        blockMap.put(block, blockID);
        return blockID;
    }

    public static int getBlockMetaID(Block block, int meta) {
        return (getBlockID(block) << 16) | (meta & 0xFFFF);
    }

    public static class Single<T> {

        private final TIntObjectMap<T> map = new TIntObjectHashMap<>();

        public void put(Block block, int meta, T value) {
            int id = getBlockMetaID(block, meta);
            map.put(id, value);
        }

        public T get(Block block, int meta) {
            int id = getBlockMetaID(block, meta);
            return map.get(id);
        }
    }
}
