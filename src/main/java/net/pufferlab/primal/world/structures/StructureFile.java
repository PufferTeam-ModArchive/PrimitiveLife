package net.pufferlab.primal.world.structures;

import java.io.File;
import java.util.*;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.nbt.*;
import net.minecraft.world.World;
import net.pufferlab.primal.Primal;
import net.pufferlab.primal.utils.*;

public class StructureFile {

    public enum LoadingPosition {
        normal,
        command,
        ground
    }

    public boolean hasLoaded = false;
    public String name;
    public File file;
    public int height;
    public int version = 1;
    public StructureRotated rotateStructure;

    public StructureFile(String name) {
        this.name = name;
        try {
            this.file = IOUtils.createStructureFile(this.name, "nbt");
        } catch (Exception e) {}
        this.rotateStructure = new StructureRotated();
    }

    public StructureBlockList getList(int facing) {
        return rotateStructure.getList(facing);
    }

    public void addBlockCoord(Block block, int meta, NBTTagCompound nbt, int x, int y, int z) {
        rotateStructure.addBlockCoord(block, meta, nbt, x, y, z);
    }

    public void rotateStep(World world) {
        rotateStructure.rotateStructure(world);
    }

    public int getStructureHeight() {
        return this.height;
    }

    public void setStructureHeight(int height) {
        this.height = height;
    }

    public void writeToNBT(NBTTagCompound nbt) {
        nbt.setString("name", this.name);
        nbt.setInteger("height", this.height);
        nbt.setInteger("version", this.version);
        NBTTagCompound nbt2 = new NBTTagCompound();
        this.rotateStructure.writeToNBT(nbt2);
        nbt.setTag("blocks", nbt2);
    }

    public void readFromNBT(NBTTagCompound nbt) {
        this.name = nbt.getString("name");
        this.height = nbt.getInteger("height");
        this.version = nbt.getInteger("version");
        if (this.version == 0) {
            this.rotateStructure = new StructureRotated();
            NBTTagList blocks = null;
            for (int i = 0; i < 4; i++) {
                if (i == 0) {
                    blocks = nbt.getTagList("blocks", NBTType.TagCompound);
                } else {
                    blocks = nbt.getTagList("blocks_" + i, NBTType.TagCompound);
                }
                StructureBlockList blockList = new StructureBlockList();
                blockList.readFromNBT(blocks);
                this.rotateStructure.structureLists[i] = blockList;
            }
            Primal.LOG.warn("Using outdated structure format, consider updating by saving again.");
        }
        if (this.version == 1) {
            NBTTagCompound nbt3 = nbt.getCompoundTag("blocks");
            this.rotateStructure = new StructureRotated(nbt3);
        }
    }

    public void saveFile() {
        NBTTagCompound nbt = new NBTTagCompound();
        writeToNBT(nbt);
        IOUtils.writeNBTFile(file, nbt);
    }

    public void loadFile() {
        if (this.hasLoaded) return;
        NBTTagCompound nbt = null;
        try {
            if (file.exists()) {
                nbt = IOUtils.readNBTFile(file);
                Primal.LOG.warn("Loading from local structure folder");
            } else {
                nbt = IOUtils.readNBTFile("/data/structures/" + this.name + ".nbt");
            }
        } catch (Exception e) {
            Primal.LOG.error("Cannot load structure file");
        }
        if (nbt != null) {
            readFromNBT(nbt);
            this.hasLoaded = true;
        }
    }

    public static Map<String, StructureFile> cachedStructure = new HashMap<>();

    public static void putStructureFile(String name) {
        cachedStructure.put(name, new StructureFile(name));
    }

    public static void saveStructure(String name, int x1, int y1, int z1, int x2, int y2, int z2, World world) {
        saveStructure(new StructureFile(name), x1, y1, z1, x2, y2, z2, world);
        putStructureFile(name);
    }

    public static void saveStructure(StructureFile file, int x1, int y1, int z1, int x2, int y2, int z2, World world) {
        int minX = Math.min(x1, x2);
        int minY = Math.min(y1, y2);
        int minZ = Math.min(z1, z2);
        int maxX = Math.max(x1, x2);
        int maxY = Math.max(y1, y2);
        int maxZ = Math.max(z1, z2);

        int middleX = (minX + maxX) / 2;
        int middleY = (minY + maxY) / 2;
        int middleZ = (minZ + maxZ) / 2;

        file.setStructureHeight(maxY - minY);

        for (int x = minX; x <= maxX; x++) {
            for (int y = minY; y <= maxY; y++) {
                for (int z = minZ; z <= maxZ; z++) {
                    Block block = world.getBlock(x, y, z);
                    if (block.getMaterial() == Material.air) continue;
                    int meta = world.getBlockMetadata(x, y, z);
                    NBTTagCompound teData = WorldUtils.getTileEntityNBT(world, x, y, z, block, meta);
                    file.addBlockCoord(block, meta, teData, x - middleX, y - middleY, z - middleZ);
                }
            }
        }

        file.rotateStep(world);
        file.saveFile();
    }

    public static StructureFile getStructureFile(String name) {
        StructureFile file = cachedStructure.get(name);
        if (file == null) {
            file = new StructureFile(name);
            cachedStructure.put(name, file);
        }
        return file;
    }

    public static void loadStructure(String name, int x, int y, int z, World world, int facing) {
        loadStructure(name, x, y, z, world, facing, LoadingPosition.normal);
    }

    public static void loadStructure(String name, int x, int y, int z, World world, int facing,
        LoadingPosition loadingPosition) {
        StructureFile file = getStructureFile(name);
        loadStructure(file, x, y, z, world, facing, loadingPosition);
    }

    public static void loadStructure(StructureFile file, int x, int y, int z, World world, int facing,
        LoadingPosition loadingPosition) {
        file.loadFile();

        int height = file.getStructureHeight() / 2;
        int offsetY = 0;
        if (loadingPosition == LoadingPosition.ground) {
            offsetY = height;
        }

        StructureBlockList blockList = file.getList(facing);
        for (StructureBlock block : blockList.blockMap.values()) {
            byte[] coords = block.coords;
            for (int j = 0; j < coords.length; j += 3) {
                int x0 = coords[j];
                int y0 = coords[j + 1];
                int z0 = coords[j + 2];
                if (loadingPosition == LoadingPosition.command) {
                    WorldUtils
                        .setBlock(world, x0 + x, y0 + offsetY + y, z0 + z, block.block, block.metadata, block.tag);
                } else {
                    WorldUtils.setBlockStructure(
                        world,
                        x0 + x,
                        y0 + offsetY + y,
                        z0 + z,
                        block.block,
                        block.metadata,
                        block.tag);
                }
            }
        }
    }
}
