package net.pufferlab.primal.utils;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.pufferlab.primal.Constants;
import net.pufferlab.primal.Primal;

public class WoodType implements IPrimalType {

    public static final BlockMap.Single<WoodType> blockMap = new BlockMap.Single<>();

    public String name;
    public String[] types;
    public String[] thinTypes;
    public boolean hasLog = true;

    public Block log;
    public int logMeta;
    public Block strippedLog;
    public int strippedLogMeta;
    public Block wood;
    public int woodMeta;
    public Block strippedWood;
    public int strippedWoodMeta;

    public Block logThin;
    public int logThinMeta;
    public Block strippedLogThin;
    public int strippedLogThinMeta;
    public Block woodThin;
    public int woodThinMeta;
    public Block strippedWoodThin;
    public int strippedWoodThinMeta;

    public Item bark;
    public int barkMeta;

    public WoodType(String name) {
        this.name = name;
        this.types = new String[] { this.name + "_log", "stripped_" + this.name + "_log", this.name + "_wood",
            "stripped_" + this.name + "_wood" };
        this.thinTypes = new String[] { this.name + "_thin_log", "stripped_" + this.name + "_thin_log",
            this.name + "_thin_wood", "stripped_" + this.name + "_thin_wood" };
    }

    @Override
    public String getName() {
        return name;
    }

    public WoodType hasNoLog() {
        this.hasLog = false;
        return this;
    }

    public static WoodType getWoodType(Block block, int meta) {
        return blockMap.get(block, getMeta(meta));
    }

    public Block getLogBlock(Block block, int meta) {
        int correctMeta = getMeta(meta);
        if (block == wood && correctMeta == woodMeta) {
            return log;
        }
        if (block == strippedWoodThin && correctMeta == strippedWoodThinMeta) {
            return strippedLogThin;
        }
        return null;
    }

    public Block getStrippedBlock(Block block, int meta) {
        int correctMeta = getMeta(meta);
        if (block == log && correctMeta == logMeta) {
            return strippedLog;
        }
        if (block == wood && correctMeta == woodMeta) {
            return strippedWood;
        }
        if (block == logThin && correctMeta == logThinMeta) {
            return strippedLogThin;
        }
        if (block == woodThin && correctMeta == woodThinMeta) {
            return strippedWoodThin;
        }
        return null;
    }

    public int getLogMeta(Block block, int meta) {
        int correctMeta = getMeta(meta);
        int offsetMeta = getOffset(meta);
        if (block == wood && correctMeta == woodMeta) {
            return logMeta + offsetMeta;
        }
        if (block == strippedWoodThin && correctMeta == strippedWoodThinMeta) {
            return strippedLogThinMeta + offsetMeta;
        }
        return -1;
    }

    public int getStrippedMeta(Block block, int meta) {
        int correctMeta = getMeta(meta);
        int offsetMeta = getOffset(meta);
        if (block == log && correctMeta == logMeta) {
            return strippedLogMeta + offsetMeta;
        }
        if (block == wood && correctMeta == woodMeta) {
            return strippedWoodMeta + offsetMeta;
        }
        if (block == logThin && correctMeta == logThinMeta) {
            return strippedLogThinMeta + offsetMeta;
        }
        if (block == woodThin && correctMeta == woodThinMeta) {
            return strippedWoodMeta + offsetMeta;
        }
        return -1;
    }

    public WoodType setLogBlock(Block log, int meta) {
        this.log = log;
        this.logMeta = meta;
        putMap(this.log, this.logMeta);
        return this;
    }

    public WoodType setLogThinBlock(Block log, int meta) {
        this.logThin = log;
        this.logThinMeta = meta;
        putMap(this.logThin, this.logThinMeta);
        return this;
    }

    public WoodType setStrippedLogBlock(Block log, int meta) {
        this.strippedLog = log;
        this.strippedLogMeta = meta;
        putMap(this.strippedLog, this.strippedLogMeta);
        return this;
    }

    public WoodType setStrippedLogThinBlock(Block log, int meta) {
        this.strippedLogThin = log;
        this.strippedLogThinMeta = meta;
        putMap(this.strippedLogThin, this.strippedLogThinMeta);
        return this;
    }

    public WoodType setWoodBlock(Block log, int meta) {
        this.wood = log;
        this.woodMeta = meta;
        putMap(this.wood, this.woodMeta);
        return this;
    }

    public WoodType setWoodThinBlock(Block log, int meta) {
        this.woodThin = log;
        this.woodThinMeta = meta;
        putMap(this.woodThin, this.woodThinMeta);
        return this;
    }

    public WoodType setStrippedWoodBlock(Block log, int meta) {
        this.strippedWood = log;
        this.strippedWoodMeta = meta;
        putMap(this.strippedWood, this.strippedWoodMeta);
        return this;
    }

    public WoodType setStrippedWoodThinBlock(Block log, int meta) {
        this.strippedWoodThin = log;
        this.strippedWoodThinMeta = meta;
        putMap(this.strippedWoodThin, this.strippedWoodThinMeta);
        return this;
    }

    public WoodType setBarkItem(Item item, int meta) {
        this.bark = item;
        this.barkMeta = meta;
        return this;
    }

    public void putMap(Block log, int logMeta) {
        blockMap.put(log, logMeta, this);
    }

    public static int getMeta(int meta) {
        return meta & 3;
    }

    public static int getOffset(int meta) {
        return (meta & 12);
    }

    public ItemStack getLogBlock() {
        return new ItemStack(this.log, 1, this.logMeta);
    }

    public static String[] getNames(WoodType[] woodTypes) {
        String[] woods = new String[woodTypes.length];
        for (int i = 0; i < woodTypes.length; i++) {
            woods[i] = woodTypes[i].name;
        }
        return woods;
    }

    public String getSideTexture(boolean stripped) {
        return getActualTextureName(stripped);
    }

    public String getTopTexture(boolean stripped) {
        return getActualTextureName(stripped) + "_top";
    }

    public String getActualTextureName(boolean stripped) {
        String name = this.name;
        if (stripped) {
            if (Utils.contains(Constants.vanillaWoodTypes, name)) {
                return "minecraft:stripped_" + name + "_log";
            }
            return Primal.MODID + ":stripped_" + name + "_log";
        } else {
            if (Utils.contains(Constants.vanillaWoodTypes, name)) {
                if (name.equals("dark_oak")) {
                    name = "big_oak";
                }
                return "minecraft:log_" + name;
            }
            return Primal.MODID + ":" + name + "_log";
        }
    }
}
