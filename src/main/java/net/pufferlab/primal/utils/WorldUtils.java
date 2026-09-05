package net.pufferlab.primal.utils;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.chunk.storage.ExtendedBlockStorage;
import net.minecraft.world.gen.ChunkProviderFlat;
import net.minecraft.world.gen.ChunkProviderServer;
import net.minecraft.world.gen.NoiseGeneratorPerlin;
import net.pufferlab.primal.world.scheduling.ChunkPlacerData;
import net.pufferlab.primal.world.terrafirma.WorldTypeTF;

public class WorldUtils {

    public static Block getChunkBlock(ExtendedBlockStorage array, int x, int y, int z) {
        return array.getBlockByExtId(x, y & 15, z);
    }

    public static Block getChunkBlock(Chunk chunk, int x, int y, int z) {
        ExtendedBlockStorage storage = WorldUtils.getStorage(chunk, y);
        if (storage == null) return null;
        return getChunkBlock(storage, x, y, z);
    }

    public static int getChunkBlockMetadata(ExtendedBlockStorage array, int x, int y, int z) {
        return array.getExtBlockMetadata(x, y & 15, z);
    }

    public static int getChunkBlockMetadata(Chunk chunk, int x, int y, int z) {
        ExtendedBlockStorage storage = WorldUtils.getStorage(chunk, y);
        if (storage == null) return 0;
        return getChunkBlockMetadata(storage, x, y, z);
    }

    public static void setChunkBlock(ExtendedBlockStorage array, int x, int y, int z, Block block, int meta) {
        array.func_150818_a/* setExtBlockID */(x, y & 15, z, block);
        array.setExtBlockMetadata(x, y & 15, z, meta & 15);
    }

    public static void setChunkBlock(Chunk chunk, int x, int y, int z, Block block, int meta) {
        ExtendedBlockStorage storage = WorldUtils.getStorage(chunk, y);
        if (storage == null) return;
        setChunkBlock(storage, x, y, z, block, meta);
    }

    public static Block getBlock(World world, int x, int y, int z) {
        Chunk chunk = world.getChunkFromChunkCoords(x >> 4, z >> 4);
        int x2 = x & 15;
        int z2 = z & 15;
        return WorldUtils.getChunkBlock(chunk, x2, y, z2);
    }

    public static int getBlockMetadata(World world, int x, int y, int z) {
        Chunk chunk = world.getChunkFromChunkCoords(x >> 4, z >> 4);
        int x2 = x & 15;
        int z2 = z & 15;
        return getChunkBlockMetadata(chunk, x2, y, z2);
    }

    public static void setBlock(World world, int x, int y, int z, Block block, int meta, NBTTagCompound nbt) {
        world.setBlock(x, y, z, block, meta, 2);
        WorldUtils.setTileEntityNBT(world, x, y, z, block, meta, nbt);
    }

    public static void setBlockStructure(World world, int x, int y, int z, Block block, int meta, NBTTagCompound nbt) {
        ChunkPlacerData.addBlock(world, x, y, z, block, meta, nbt);
    }

    public static void setBlockWorldgen(World world, int x, int y, int z, Block block, int meta) {
        ChunkPlacerData.addBlockFast(world, x, y, z, block, meta);
    }

    public static boolean isChunkLoaded(World world, int x, int z) {
        IChunkProvider provider = world.getChunkProvider();
        if (provider instanceof ChunkProviderServer server) {
            long k = ChunkCoordIntPair.chunkXZ2Int(x, z);
            return !server.loadingChunks.contains(k);
        }
        return false;
    }

    public static void destroyBlock(EntityPlayer player, int x, int y, int z) {
        if (player instanceof EntityPlayerMP playerMP) {
            playerMP.theItemInWorldManager.tryHarvestBlock(x, y, z);
        }
    }

    public static void setBlock(World world, int x, int y, int z, Block block, int meta) {
        Chunk chunk = world.getChunkFromChunkCoords(x >> 4, z >> 4);
        int x2 = x & 15;
        int z2 = z & 15;

        WorldUtils.setChunkBlock(chunk, x2, y, z2, block, meta);
    }

    public static void setTileEntityNBT(World world, int x, int y, int z, NBTTagCompound nbt) {
        TileEntity te = world.getTileEntity(x, y, z);
        if (te != null) {
            nbt.setInteger("x", x);
            nbt.setInteger("y", y);
            nbt.setInteger("z", z);
            te.readFromNBT(nbt);
            te.markDirty();
        }
    }

    public static void setTileEntityNBT(World world, int x, int y, int z, Block block, int meta, NBTTagCompound nbt) {
        if (block.hasTileEntity(meta)) {
            TileEntity te = world.getTileEntity(x, y, z);
            if (te != null) {
                nbt.setInteger("x", x);
                nbt.setInteger("y", y);
                nbt.setInteger("z", z);
                te.readFromNBT(nbt);
                te.markDirty();
            }
        }
    }

    public static NBTTagCompound getTileEntityNBT(World world, int x, int y, int z, Block block, int meta) {
        if (block.hasTileEntity(meta)) {
            TileEntity te = world.getTileEntity(x, y, z);
            if (te != null) {
                NBTTagCompound tag = new NBTTagCompound();
                te.writeToNBT(tag);

                tag.removeTag("x");
                tag.removeTag("y");
                tag.removeTag("z");
                tag.removeTag("xCached");
                tag.removeTag("yCached");
                tag.removeTag("zCached");
                return tag;
            }
        }
        return null;
    }

    public static ExtendedBlockStorage getStorage(Chunk chunk, int y) {
        ExtendedBlockStorage[] storageArray = chunk.getBlockStorageArray();
        int section = y >> 4;
        if (section < 0 || section >= storageArray.length) return null;

        ExtendedBlockStorage storage = storageArray[section];

        if (storage == null) {
            storage = chunk.getBlockStorageArray()[y >> 4] = new ExtendedBlockStorage(
                y >> 4 << 4,
                !chunk.worldObj.provider.hasNoSky);
        }
        return storage;
    }

    public static double getPerlin(NoiseGeneratorPerlin noise, int x, int z, double scale) {
        return noise.func_151601_a(x * scale, z * scale);
    }

    public static BiomeGenBase getBiome(Chunk chunk, int x, int z) {
        return chunk.getBiomeGenForWorldCoords(x, z, chunk.worldObj.getWorldChunkManager());
    }

    public static int getPerlinValue(double noise, int number) {
        double n01 = (noise + 1.0) * 0.5;

        double scaled = n01 * (double) number;

        int choice = Mth.floor(scaled);
        return Math.min(number - 1, Math.max(0, choice));
    }

    public static boolean canDecorate(IChunkProvider chunkProvider, World world) {
        return !isFlatWorld(chunkProvider) || world.getWorldInfo()
            .getGeneratorOptions()
            .contains("decoration");
    }

    protected static final boolean isFlatWorld(IChunkProvider chunkProvider) {
        return chunkProvider instanceof ChunkProviderFlat && !chunkProvider.getClass()
            .getName()
            .equals("com.rwtema.extrautils.worldgen.Underdark.ChunkProviderUnderdark");
    }

    public static boolean isTerraFirma(World world) {
        if (world.provider.terrainType instanceof WorldTypeTF) {
            return true;
        }
        return false;
    }

    public static World getWorld(int dimensionId) {
        return MinecraftServer.getServer()
            .worldServerForDimension(dimensionId);
    }

    /*
     * public static FakePlayer fakePlayer;
     * public static GameProfile gameProfile = new GameProfile(
     * UUID.fromString("8E3D7A12-5F9C-4B6E-A1D8-72F4C0B9E35A"),
     * "[" + Primal.MODNAME + "FakePlayer]");
     * public static FakePlayer getFakePlayer(World world) {
     * if (fakePlayer == null) {
     * if (world instanceof WorldServer worldServer) {
     * fakePlayer = FakePlayerFactory.get(worldServer, gameProfile);
     * }
     * }
     * return fakePlayer;
     * }
     * public static int coordX = 29_000_000;
     * public static int coordY = 0;
     * public static int coordZ = 29_000_000;
     * // WIP
     * public static void setBlockFromFakePlayer(FakePlayer player, World world, Block block, int meta, int side,
     * NBTTagCompound nbt) {
     * Item item = Item.getItemFromBlock(block);
     * if (item instanceof ItemBlock itemBlock) {
     * int x = BlockUtils.getBlockXR(side, coordX);
     * int y = BlockUtils.getBlockYR(side, coordY);
     * int z = BlockUtils.getBlockXR(side, coordZ);
     * world.setBlock(x, y, z, Blocks.stone, 0, 2);
     * player.inventory.currentItem = 0;
     * player.setPosition(x, y, z);
     * player.inventory.setInventorySlotContents(0, new ItemStack(itemBlock, itemBlock.getItemStackLimit(), meta));
     * ItemStack heldItem = player.getHeldItem();
     * heldItem.tryPlaceItemIntoWorld(fakePlayer, world, x, y, z, side, 0.5F, 0.5F, 0.5F);
     * int x1 = BlockUtils.getBlockX(side, coordX);
     * int y1 = BlockUtils.getBlockY(side, coordY);
     * int z1 = BlockUtils.getBlockX(side, coordZ);
     * TileEntity te = world.getTileEntity(x1, y1, z1);
     * if (te != null) {
     * NBTTagCompound nbtCopy;
     * if (nbt == null) {
     * nbtCopy = new NBTTagCompound();
     * } else {
     * nbtCopy = (NBTTagCompound) nbt.copy();
     * }
     * nbtCopy.removeTag("facingMeta");
     * nbtCopy.removeTag("axisMeta");
     * te.readFromNBT(nbtCopy);
     * }
     * }
     * }
     */

}
