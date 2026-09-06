package net.pufferlab.primal.world.structures;

import java.util.*;

import net.minecraft.block.Block;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.pufferlab.primal.utils.BlockUtils;
import net.pufferlab.primal.utils.NBTType;

public class StructureBlockList {

    private final Map<String, StructureBlock> blockMap = new HashMap<>();

    public StructureBlockList() {

    }

    public StructureBlockList(NBTTagCompound tag) {
        readFromNBT(tag);
    }

    public void addStructureBlock(StructureBlock block) {
        blockMap.put(block.toString(), block);
    }

    public void addBlockCoord(Block block, int metadata, NBTTagCompound nbt, int x, int y, int z) {
        String identifier = BlockUtils.getNameFromBlock(block, metadata, nbt);

        StructureBlock structureBlock = blockMap.get(identifier);
        if (structureBlock == null) {
            structureBlock = new StructureBlock(block, metadata, nbt);
            blockMap.put(identifier, structureBlock);
        }

        structureBlock.addCoord(x, y, z);
    }

    public Collection<StructureBlock> values() {
        return blockMap.values();
    }

    public void writeToNBT(NBTTagCompound nbt) {
        NBTTagList tagList = new NBTTagList();
        writeToNBT(tagList);
        nbt.setTag("blocks", tagList);
    }

    public void writeToNBT(NBTTagList tagList) {
        for (StructureBlock block : blockMap.values()) {
            NBTTagCompound tag = new NBTTagCompound();
            block.writeToNBT(tag);
            tagList.appendTag(tag);
        }
    }

    public void readFromNBT(NBTTagCompound nbt) {
        NBTTagList blocks = nbt.getTagList("blocks", NBTType.TagCompound);

        readFromNBT(blocks);
    }

    public void readFromNBT(NBTTagList blocks) {
        for (int j = 0; j < blocks.tagCount(); j++) {
            NBTTagCompound blockInfo = blocks.getCompoundTagAt(j);
            StructureBlock structureBlock = new StructureBlock(blockInfo);
            blockMap.put(structureBlock.toString(), structureBlock);
        }
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        for (StructureBlock block : values()) {
            builder.append(block.toString());
            builder.append("|");
        }
        return builder.toString();
    }
}
