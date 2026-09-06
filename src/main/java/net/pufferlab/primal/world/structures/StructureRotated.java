package net.pufferlab.primal.world.structures;

import net.minecraft.block.Block;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.world.World;
import net.pufferlab.primal.utils.Mth;
import net.pufferlab.primal.utils.NBTType;
import net.pufferlab.primal.world.VirtualBlock;

import org.joml.Matrix4f;
import org.joml.Vector3f;

public class StructureRotated {

    public StructureBlockList[] structureLists = new StructureBlockList[4];

    public StructureRotated() {

    }

    public StructureRotated(NBTTagCompound nbt) {
        readFromNBT(nbt);
    }

    public StructureBlockList getList(int facing) {
        return structureLists[facing];
    }

    public void addBlockCoord(Block block, int metadata, NBTTagCompound nbt, int x, int y, int z) {
        if (structureLists[0] == null) {
            structureLists[0] = new StructureBlockList();
        }
        structureLists[0].addBlockCoord(block, metadata, nbt, x, y, z);
    }

    public final Matrix4f matrix = new Matrix4f();

    public void rotateStructure(World world) {
        for (int k = 0; k < 3; k++) {
            structureLists[k + 1] = new StructureBlockList();
        }
        for (StructureBlock block : structureLists[0].values()) {
            byte[] coords = block.coords;
            byte[][] coordsRotated = new byte[3][coords.length];
            for (int j = 0; j < coords.length; j += 3) {
                int x0 = coords[j];
                int y0 = coords[j + 1];
                int z0 = coords[j + 2];
                Vector3f coord = new Vector3f(x0, y0, z0);
                for (int k = 0; k < 3; k++) {
                    coord.set(x0, y0, z0);
                    matrix.identity();
                    int p = k;
                    if (k == 2) {
                        p = 1;
                    }
                    if (k == 1) {
                        p = 2;
                    }
                    for (int l = 0; l < (p + 1); l++) {
                        matrix.rotateY(-(float) Math.PI / 2);
                        matrix.transformPosition(coord);
                        int x = Mth.floor(coord.x);
                        int y = Mth.floor(coord.y);
                        int z = Mth.floor(coord.z);
                        coord.set(x, y, z);
                    }
                    coordsRotated[k][j] = (byte) Mth.floor(coord.x);
                    coordsRotated[k][j + 1] = (byte) Mth.floor(coord.y);
                    coordsRotated[k][j + 2] = (byte) Mth.floor(coord.z);
                }
            }
            for (int k = 0; k < 3; k++) {
                StructureBlock rotatedBlockInfo = rotateBlockInfo(world, block, coordsRotated[k], k + 1);
                structureLists[k + 1].addStructureBlock(rotatedBlockInfo);
            }
        }
    }

    public static VirtualBlock virtualBlock = new VirtualBlock(0, 0, 0);

    public static StructureBlock rotateBlockInfo(World world, StructureBlock blockInfo, byte[] newCoords,
        int rotation) {
        StructureBlock structureBlockRotated = new StructureBlock(blockInfo);

        virtualBlock
            .placeBlock(world, structureBlockRotated.block, structureBlockRotated.metadata, structureBlockRotated.tag);

        for (int i = 0; i < rotation; i++) {
            virtualBlock.rotateBlock(world);
        }

        int newMeta = virtualBlock.getBlockMetadata(world);
        structureBlockRotated.metadata = newMeta;

        NBTTagCompound newTag = virtualBlock.getTileEntityNBT(world);
        if (newTag != null) {
            structureBlockRotated.tag = newTag;
        }

        structureBlockRotated.coords = newCoords;

        return structureBlockRotated;
    }

    public void writeToNBT(NBTTagCompound nbt) {
        NBTTagList tagList = new NBTTagList();
        for (int i = 0; i < structureLists.length; i++) {
            NBTTagCompound blockListTag = new NBTTagCompound();
            StructureBlockList blockList = structureLists[i];
            blockList.writeToNBT(blockListTag);
            blockListTag.setInteger("facing", i);
            tagList.appendTag(blockListTag);
        }
        nbt.setTag("blocks", tagList);
    }

    public void readFromNBT(NBTTagCompound nbt) {
        NBTTagList tagList = nbt.getTagList("blocks", NBTType.TagCompound);
        for (int i = 0; i < tagList.tagCount(); i++) {
            NBTTagCompound blockListTag = tagList.getCompoundTagAt(i);
            StructureBlockList blockList = new StructureBlockList(blockListTag);
            int facing = blockListTag.getInteger("facing");
            structureLists[facing] = blockList;
        }
    }
}
