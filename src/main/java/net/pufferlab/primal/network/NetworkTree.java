package net.pufferlab.primal.network;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import net.pufferlab.primal.utils.BlockUtils;
import net.pufferlab.primal.utils.PosList;
import net.pufferlab.primal.utils.WorldUtils;

public class NetworkTree {

    public boolean isValidTree;
    public int logAmount = 0;
    PosList treeBlocks = new PosList();

    public static void generateAndDestroyTree(World world, int x, int y, int z, EntityPlayer player) {
        if (player == null) return;
        NetworkTree network = generateTree(world, x, y, z);
        if (network.isValidTree) {
            int initialDamage = player.getHeldItem()
                .getItemDamage();
            for (int i = 0; i < network.treeBlocks.size(); i++) {
                int x2 = network.treeBlocks.getX(i);
                int y2 = network.treeBlocks.getY(i);
                int z2 = network.treeBlocks.getZ(i);
                if (!(x2 == x && y2 == y && z2 == z)) {
                    WorldUtils.destroyBlock(player, x2, y2, z2);
                }
                for (int offsetX = -2; offsetX <= 2; offsetX++) {
                    for (int offsetY = -2; offsetY <= 2; offsetY++) {
                        for (int offsetZ = -2; offsetZ <= 2; offsetZ++) {
                            Block block = world.getBlock(x2 + offsetX, y2 + offsetY, z2 + offsetZ);
                            boolean isLeave = BlockUtils.isLeaveBlock(block);
                            if (isLeave) {
                                WorldUtils.destroyBlock(player, x2 + offsetX, y2 + offsetY, z2 + offsetZ);
                            }
                        }
                    }
                }

                player.getHeldItem()
                    .setItemDamage(initialDamage);
            }
            ItemStack stack = player.getHeldItem();
            stack.damageItem(network.logAmount, player);
        }
    }

    public static NetworkTree generateTree(World world, int x, int y, int z) {
        NetworkTree network = new NetworkTree();
        network.recurseTile(world, x, y, z);
        if (network.isValidTree) {
            for (int i = 0; i < network.treeBlocks.size(); i++) {
                int x2 = network.treeBlocks.getX(i);
                int y2 = network.treeBlocks.getY(i);
                int z2 = network.treeBlocks.getZ(i);
                Block block = world.getBlock(x2, y2, z2);
                boolean isLog = BlockUtils.isLogBlock(block);
                if (isLog) {
                    network.logAmount++;
                }
            }
        }
        return network;
    }

    public void recurseTile(World world, int x, int y, int z) {
        PosList list = getConnectedTiles(world, x, y, z);
        for (int i = 0; i < list.size(); i++) {
            int x2 = list.getX(i);
            int y2 = list.getY(i);
            int z2 = list.getZ(i);
            if (!this.treeBlocks.contains(x2, y2, z2)) {
                this.treeBlocks.add(x2, y2, z2);
                recurseTile(world, x2, y2, z2);
            }
        }
    }

    public PosList getConnectedTiles(World world, int x, int y, int z) {
        PosList connected = new PosList();
        connected.add(x, y, z);
        for (ForgeDirection direction : ForgeDirection.VALID_DIRECTIONS) {
            int offsetX = x + direction.offsetX;
            int offsetY = y + direction.offsetY;
            int offsetZ = z + direction.offsetZ;
            Block block = world.getBlock(offsetX, offsetY, offsetZ);
            boolean isLog = BlockUtils.isLogBlock(block);
            boolean isLeave = BlockUtils.isLeaveBlock(block);
            if (isLeave) {
                this.isValidTree = true;
            }
            if (isLog) {
                connected.add(offsetX, offsetY, offsetZ);
            }
        }

        return connected;
    }
}
