package net.pufferlab.primal.utils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.minecraft.block.Block;
import net.minecraft.block.BlockBush;
import net.minecraft.block.BlockLog;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.*;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import net.pufferlab.primal.Registry;
import net.pufferlab.primal.blocks.*;

import cpw.mods.fml.common.registry.GameRegistry;

public class BlockUtils {

    public static final ForgeDirection[] sideDirections = new ForgeDirection[] { ForgeDirection.WEST,
        ForgeDirection.EAST, ForgeDirection.SOUTH, ForgeDirection.NORTH, ForgeDirection.DOWN };
    public static final ForgeDirection[] sideXZDirections = new ForgeDirection[] { ForgeDirection.WEST,
        ForgeDirection.EAST, ForgeDirection.SOUTH, ForgeDirection.NORTH };

    public static RegistryNamespaced getBlockRegistry() {
        return Block.blockRegistry;
    }

    public static int getBlockX(int side, int x) {
        if (side == 4) {
            x--;
        }
        if (side == 5) {
            x++;
        }
        return x;
    }

    public static int getBlockY(int side, int y) {
        if (side == 0) {
            y--;
        }
        if (side == 1) {
            y++;
        }
        return y;
    }

    public static int getBlockZ(int side, int z) {
        if (side == 2) {
            z--;
        }
        if (side == 3) {
            z++;
        }
        return z;
    }

    public static int getBlockXR(int side, int x) {
        if (side == 5) {
            x--;
        }
        if (side == 4) {
            x++;
        }
        return x;
    }

    public static int getBlockYR(int side, int y) {
        if (side == 1) {
            y--;
        }
        if (side == 0) {
            y++;
        }
        return y;
    }

    public static int getBlockZR(int side, int z) {
        if (side == 3) {
            z--;
        }
        if (side == 2) {
            z++;
        }
        return z;
    }

    public static String getNameFromBlock(Block block) {
        if (block == null) return "null";
        String mod = getBlockRegistry().getNameForObject(block);
        return mod;
    }

    public static String getNameFromBlock(Block block, int meta) {
        return getNameFromBlock(block) + "|" + meta;
    }

    public static String getNameFromBlock(Block block, int meta, NBTTagCompound nbt) {
        String tagString = "";
        if (nbt != null) {
            tagString = "|" + nbt.toString();
        }
        return getNameFromBlock(block, meta) + tagString;
    }

    public static Map<String, Block> blockMap = new HashMap<>();

    public static Block getBlockFromName(String name) {
        if (name.equals("null")) return Blocks.air;
        Block block = blockMap.get(name);
        if (block == null) {
            String[] blockSplit = name.split(":");
            block = GameRegistry.findBlock(blockSplit[0], blockSplit[1]);
        }
        return block;
    }

    public static boolean hasSolidWallsTop(World world, int x, int y, int z) {
        for (ForgeDirection dir : ForgeDirection.VALID_DIRECTIONS) {
            Block block = world.getBlock(x + dir.offsetX, y + dir.offsetY, z + dir.offsetZ);
            boolean isSolid = block
                .isSideSolid(world, x + dir.offsetX, y + dir.offsetY, z + dir.offsetZ, dir.getOpposite())
                || (block instanceof BlockPile)
                || (block.getMaterial() == Material.fire);
            if (!isSolid) {
                return false;
            }

        }
        return true;
    }

    public static boolean hasSolidWalls(World world, int x, int y, int z) {
        for (ForgeDirection dir : sideDirections) {
            Block block = world.getBlock(x + dir.offsetX, y + dir.offsetY, z + dir.offsetZ);
            if (!block.isSideSolid(world, x + dir.offsetX, y + dir.offsetY, z + dir.offsetZ, dir.getOpposite())) {
                return false;
            }
        }
        return true;
    }

    public static Block getBlockDirection(IBlockAccess world, int x, int y, int z, ForgeDirection... directions) {
        int offsetX = x;
        int offsetY = y;
        int offsetZ = z;
        for (ForgeDirection direction : directions) {
            offsetX += direction.offsetX;
            offsetY += direction.offsetY;
            offsetZ += direction.offsetZ;
        }
        return world.getBlock(offsetX, offsetY, offsetZ);
    }

    public static Block getBlockDirection(IBlockAccess world, int x, int y, int z, ForgeDirection direction,
        ForgeDirection direction2) {
        int offsetX = x + direction.offsetX + direction2.offsetX;
        int offsetY = y + direction.offsetY + direction2.offsetY;
        int offsetZ = z + direction.offsetZ + direction2.offsetZ;
        return world.getBlock(offsetX, offsetY, offsetZ);
    }

    public static Block getBlockDirection(IBlockAccess world, int x, int y, int z, ForgeDirection direction) {
        int offsetX = x + direction.offsetX;
        int offsetY = y + direction.offsetY;
        int offsetZ = z + direction.offsetZ;
        return world.getBlock(offsetX, offsetY, offsetZ);
    }

    public static boolean setBlockDirection(World world, int x, int y, int z, Block block, int meta,
        ForgeDirection... directions) {
        int offsetX = x;
        int offsetY = y;
        int offsetZ = z;
        for (ForgeDirection direction : directions) {
            offsetX += direction.offsetX;
            offsetY += direction.offsetY;
            offsetZ += direction.offsetZ;
        }
        return world.setBlock(offsetX, offsetY, offsetZ, block, meta, 2);
    }

    public static ForgeDirection getDirectionFromFacing(int facingMeta) {
        return switch (facingMeta) {
            case 1 -> ForgeDirection.SOUTH;
            case 2 -> ForgeDirection.EAST;
            case 3 -> ForgeDirection.NORTH;
            case 4 -> ForgeDirection.WEST;
            default -> ForgeDirection.UNKNOWN;
        };
    }

    public static int getFacingFromDirection(ForgeDirection direction) {
        return getFacingFromDirection(direction.ordinal());
    }

    public static int getFacingFromDirection(int direction) {
        return switch (direction) {
            case 3 -> 3;
            case 5 -> 4;
            case 2 -> 1;
            case 4 -> 2;
            default -> 0;
        };
    }

    public static int getFacingMeta(int side, int axis) {
        if (axis == 0) {
            return switch (side) {
                case 3 -> 1;
                case 4 -> 4;
                case 2 -> 3;
                case 5 -> 2;
                default -> 0;
            };
        }
        if (axis == 1) {
            return switch (side) {
                case 0 -> 1;
                case 4 -> 4;
                case 1 -> 3;
                case 5 -> 2;
                default -> 0;
            };
        }
        if (axis == 2) {
            return switch (side) {
                case 3 -> 1;
                case 1 -> 4;
                case 2 -> 3;
                case 0 -> 2;
                default -> 0;
            };
        }
        return 0;
    }

    public static int getSimpleAxisFromFacing(int facingMeta) {
        return switch (facingMeta) {
            case 1, 3 -> 1;
            case 2, 4 -> 2;
            default -> 0;
        };
    }

    public static int getAxis(ForgeDirection direction) {
        return getAxis(direction.ordinal());
    }

    public static int getAxis(int side) {
        if (side == 0 || side == 1) {
            return 0;
        } else if (side == 2 || side == 3) {
            return 1;
        } else if (side == 4 || side == 5) {
            return 2;
        }
        return 0;
    }

    public static boolean isSidePositive(int side) {
        if (side == 1 || side == 3 || side == 5) {
            return true;
        }
        return false;
    }

    public static boolean isSimpleAxisConnected(int facingMeta, int facingMeta2) {
        return getSimpleAxisFromFacing(facingMeta) == getSimpleAxisFromFacing(facingMeta2);
    }

    public static MovingObjectPosition getMovingObjectPositionFromPlayer(World worldIn, EntityPlayer playerIn,
        boolean useLiquids) {
        return ItemDummy.instance.getMovingObjectPositionFromPlayerPublic(worldIn, playerIn, useLiquids);
    }

    public static class ItemDummy extends Item {

        public static ItemDummy instance = new ItemDummy();

        public MovingObjectPosition getMovingObjectPositionFromPlayerPublic(World worldIn, EntityPlayer player,
            boolean useLiquids) {
            return getMovingObjectPositionFromPlayer(worldIn, player, useLiquids);
        }
    }

    public static MovingObjectPosition collisionRayTrace(AxisAlignedBB bound, World worldIn, int x, int y, int z,
        Vec3 startVec, Vec3 endVec) {
        startVec = startVec.addVector((double) (-x), (double) (-y), (double) (-z));
        endVec = endVec.addVector((double) (-x), (double) (-y), (double) (-z));
        Vec3 vec32 = startVec.getIntermediateWithXValue(endVec, bound.minX);
        Vec3 vec33 = startVec.getIntermediateWithXValue(endVec, bound.maxX);
        Vec3 vec34 = startVec.getIntermediateWithYValue(endVec, bound.minY);
        Vec3 vec35 = startVec.getIntermediateWithYValue(endVec, bound.maxY);
        Vec3 vec36 = startVec.getIntermediateWithZValue(endVec, bound.minZ);
        Vec3 vec37 = startVec.getIntermediateWithZValue(endVec, bound.maxZ);

        if (!isVecInsideYZBounds(bound, vec32)) {
            vec32 = null;
        }

        if (!isVecInsideYZBounds(bound, vec33)) {
            vec33 = null;
        }

        if (!isVecInsideXZBounds(bound, vec34)) {
            vec34 = null;
        }

        if (!isVecInsideXZBounds(bound, vec35)) {
            vec35 = null;
        }

        if (!isVecInsideXYBounds(bound, vec36)) {
            vec36 = null;
        }

        if (!isVecInsideXYBounds(bound, vec37)) {
            vec37 = null;
        }

        Vec3 vec38 = null;

        if (vec32 != null && (vec38 == null || startVec.squareDistanceTo(vec32) < startVec.squareDistanceTo(vec38))) {
            vec38 = vec32;
        }

        if (vec33 != null && (vec38 == null || startVec.squareDistanceTo(vec33) < startVec.squareDistanceTo(vec38))) {
            vec38 = vec33;
        }

        if (vec34 != null && (vec38 == null || startVec.squareDistanceTo(vec34) < startVec.squareDistanceTo(vec38))) {
            vec38 = vec34;
        }

        if (vec35 != null && (vec38 == null || startVec.squareDistanceTo(vec35) < startVec.squareDistanceTo(vec38))) {
            vec38 = vec35;
        }

        if (vec36 != null && (vec38 == null || startVec.squareDistanceTo(vec36) < startVec.squareDistanceTo(vec38))) {
            vec38 = vec36;
        }

        if (vec37 != null && (vec38 == null || startVec.squareDistanceTo(vec37) < startVec.squareDistanceTo(vec38))) {
            vec38 = vec37;
        }

        if (vec38 == null) {
            return null;
        } else {
            byte b0 = -1;

            if (vec38 == vec32) {
                b0 = 4;
            }

            if (vec38 == vec33) {
                b0 = 5;
            }

            if (vec38 == vec34) {
                b0 = 0;
            }

            if (vec38 == vec35) {
                b0 = 1;
            }

            if (vec38 == vec36) {
                b0 = 2;
            }

            if (vec38 == vec37) {
                b0 = 3;
            }

            MovingObjectPosition mop = new MovingObjectPosition(
                x,
                y,
                z,
                b0,
                vec38.addVector((double) x, (double) y, (double) z));
            mop.hitInfo = bound;
            return mop;
        }
    }

    private static boolean isVecInsideYZBounds(AxisAlignedBB bound, Vec3 point) {
        return point == null ? false
            : point.yCoord >= bound.minY && point.yCoord <= bound.maxY
                && point.zCoord >= bound.minZ
                && point.zCoord <= bound.maxZ;
    }

    private static boolean isVecInsideXZBounds(AxisAlignedBB bound, Vec3 point) {
        return point == null ? false
            : point.xCoord >= bound.minX && point.xCoord <= bound.maxX
                && point.zCoord >= bound.minZ
                && point.zCoord <= bound.maxZ;
    }

    private static boolean isVecInsideXYBounds(AxisAlignedBB bound, Vec3 point) {
        return point == null ? false
            : point.xCoord >= bound.minX && point.xCoord <= bound.maxX
                && point.yCoord >= bound.minY
                && point.yCoord <= bound.maxY;
    }

    public static MovingObjectPosition customCollisionRayTrace(Block thiz, World worldIn, int x, int y, int z,
        Vec3 startVec, Vec3 endVec) {
        if (thiz instanceof IPrimalBlock iblock) {
            List<AxisAlignedBB> bounds;
            thiz.setBlockBoundsBasedOnState(worldIn, x, y, z);
            bounds = iblock.getBounds(worldIn, x, y, z, null, BoundsType.rayTraced);
            if (bounds != null && !bounds.isEmpty()) {
                for (AxisAlignedBB bb : bounds) {
                    MovingObjectPosition mop = BlockUtils.collisionRayTrace(bb, worldIn, x, y, z, startVec, endVec);
                    if (mop != null) {
                        return mop;
                    }
                }
            }
            return BlockUtils.collisionRayTrace(
                AxisAlignedBB.getBoundingBox(
                    thiz.getBlockBoundsMinX(),
                    thiz.getBlockBoundsMinY(),
                    thiz.getBlockBoundsMinZ(),
                    thiz.getBlockBoundsMaxX(),
                    thiz.getBlockBoundsMaxY(),
                    thiz.getBlockBoundsMaxZ()),
                worldIn,
                x,
                y,
                z,
                startVec,
                endVec);
        }
        return null;
    }

    public static void addCustomCollisionBoxesToList(Block thiz, World worldIn, int x, int y, int z, AxisAlignedBB mask,
        List<AxisAlignedBB> list, Entity collider) {
        if (thiz instanceof IPrimalBlock iblock) {
            thiz.setBlockBoundsBasedOnState(worldIn, x, y, z);
            List<AxisAlignedBB> bounds;
            bounds = iblock.getBounds(worldIn, x, y, z, null, BoundsType.collision);
            if (bounds != null && !bounds.isEmpty()) {
                for (AxisAlignedBB bb : bounds) {
                    bb = bb.copy()
                        .offset(x, y, z);
                    if (mask.intersectsWith(bb)) {
                        list.add(bb);
                    }
                }
            }
            if (iblock.collideDefaultBounds()) {
                AxisAlignedBB axisalignedbb1 = thiz.getCollisionBoundingBoxFromPool(worldIn, x, y, z);

                if (axisalignedbb1 != null && mask.intersectsWith(axisalignedbb1)) {
                    list.add(axisalignedbb1);
                }
            }
        }
    }

    public static int getDirectionXZYaw(int yaw) {
        if (yaw == 0) {
            return 1;
        } else if (yaw == 1) {
            return 4;
        } else if (yaw == 2) {
            return 3;
        } else if (yaw == 3) {
            return 2;
        }

        return 0;
    }

    public static int getMetaYaw(float rotationYaw) {
        int yaw = MathHelper.floor_double((double) (rotationYaw * 4.0F / 360.0F) + 0.5D) & 3;
        return getDirectionXZYaw(yaw);
    }

    public static int getMetaYawSide(float rotationYaw, int side) {
        if (side == 1 || side == 0) {
            return getMetaYaw(rotationYaw);
        } else {
            return getFacingFromDirection(side);
        }
    }

    public static float getFacingAngle(int meta) {
        float angle;
        switch (meta) {
            case 1:
                angle = (float) -Math.PI;
                break;
            case 2:
                angle = (float) (-Math.PI / 2);
                break;
            case 3:
                angle = 0.0F;
                break;
            case 4:
                angle = (float) (-3 * Math.PI / 2);
                break;
            default:
                angle = (float) (meta * Math.PI / 2);
                break;
        }
        return angle;
    }

    public static int getFacingAngleDegree(int meta) {
        int angle = 0;
        switch (meta) {
            case 1:
                angle = -180;
                break;
            case 2:
                angle = -90;
                break;
            case 3:
                angle = 0;
                break;
            case 4:
                angle = -270;
                break;
            default:
                angle = 90 * meta;
                break;
        }
        return angle;
    }

    public static void place(ItemStack stack, World world, int x, int y, int z, Block toPlace, int metadata,
        EntityPlayer player) {
        if (world.isAirBlock(x, y, z) && world.isSideSolid(x, y - 1, z, ForgeDirection.UP)) {
            if (world.checkNoEntityCollision(toPlace.getCollisionBoundingBoxFromPool(world, x, y, z))
                && world.setBlock(x, y, z, toPlace, metadata, 3)) {
                world.setBlock(x, y, z, toPlace, metadata, 2);
                toPlace.onBlockPlacedBy(world, x, y, z, player, stack);
                if (toPlace instanceof BlockContainerPrimal block2) {
                    block2.onBlockSidePlacedBy(world, x, y, z, player, stack, 0);
                }
                stack.stackSize -= 1;
                playSound(world, x, y, z, toPlace);
                player.swingItem();
            }
        }
    }

    public static void placeNoConsume(ItemStack stack, World world, int x, int y, int z, Block toPlace, int metadata,
        EntityPlayer player) {
        if (world.isAirBlock(x, y, z) && world.isSideSolid(x, y - 1, z, ForgeDirection.UP)) {
            if (world.checkNoEntityCollision(toPlace.getCollisionBoundingBoxFromPool(world, x, y, z))
                && world.setBlock(x, y, z, toPlace, metadata, 3)) {
                world.setBlock(x, y, z, toPlace, metadata, 2);
                toPlace.onBlockPlacedBy(world, x, y, z, player, stack);
                if (toPlace instanceof BlockContainerPrimal block2) {
                    block2.onBlockSidePlacedBy(world, x, y, z, player, stack, 0);
                }
                playSound(world, x, y, z, toPlace);
                player.swingItem();
            }
        }
    }

    public static void placeSilent(ItemStack stack, World world, int x, int y, int z, Block toPlace, int metadata,
        EntityPlayer player) {
        if (world.isAirBlock(x, y, z) && world.isSideSolid(x, y - 1, z, ForgeDirection.UP)) {
            if (world.checkNoEntityCollision(toPlace.getCollisionBoundingBoxFromPool(world, x, y, z))
                && world.setBlock(x, y, z, toPlace, metadata, 3)) {
                world.setBlock(x, y, z, toPlace, metadata, 2);
                toPlace.onBlockPlacedBy(world, x, y, z, player, stack);
                if (toPlace instanceof BlockContainerPrimal block2) {
                    block2.onBlockSidePlacedBy(world, x, y, z, player, stack, 0);
                }
                player.swingItem();
            }
        }
    }

    public static void playSound(World world, int x, int y, int z, Block block) {
        world.playSoundEffect(
            x + 0.5f,
            y + 0.5f,
            z + 0.5f,
            block.stepSound.func_150496_b(),
            (block.stepSound.getVolume() + 1.0F) / 2.0F,
            block.stepSound.getPitch() * 0.8F);
    }

    public static void playSound(World world, int x, int y, int z, SoundTypePrimal stepSound) {
        world.playSoundEffect(
            x + 0.5f,
            y + 0.5f,
            z + 0.5f,
            stepSound.getPath(),
            (stepSound.getVolume() + 1.0F) / 2.0F,
            stepSound.getPitch() * 0.8F);
    }

    public static int getHarvestLevel(Block block, int meta) {
        return block.getHarvestLevel(meta);
    }

    public static boolean isLogBlock(Block block) {
        if (block == null) return false;
        if (block instanceof BlockLog) return true;
        return Utils.containsOreDict(block, "logWood");
    }

    public static boolean isTerrainBlock(Block block) {
        return isNaturalStone(block) || isDirtBlock(block)
            || isGrassBlock(block)
            || isGravelBlock(block)
            || isSandBlock(block);
    }

    public static boolean isOreBlock(Block block) {
        if (block instanceof BlockStoneOre) return true;
        return false;
    }

    public static boolean isNaturalStone(Block block) {
        if (block == null) return false;
        if (block == Registry.stone) return true;
        if (block == Blocks.stone) return true;
        return false;
    }

    public static boolean isDirtBlock(Block block) {
        if (block == null) return false;
        if (block instanceof BlockMetaDirt) return true;
        if (block == Blocks.dirt) return true;
        return false;
    }

    public static boolean isFarmlandBlock(Block block) {
        if (block == null) return false;
        if (block instanceof BlockMetaFarmland) return true;
        if (block == Blocks.farmland) return true;
        return false;
    }

    public static boolean isGrassBlock(Block block) {
        if (block == null) return false;
        if (block instanceof BlockMetaGrass) return true;
        if (block == Blocks.grass) return true;
        return false;
    }

    public static boolean isBushBlock(Block block) {
        if (block instanceof BlockBush) return true;
        return false;
    }

    public static boolean isSandBlock(Block block) {
        if (block == null) return false;
        if (block instanceof BlockMetaSand) return true;
        if (block == Blocks.sand) return true;
        return false;
    }

    public static boolean isGravelBlock(Block block) {
        if (block == null) return false;
        if (block instanceof BlockMetaGravel) return true;
        if (block == Blocks.gravel) return true;
        return false;
    }

    public static boolean isWaterBlock(Block block) {
        if (block == null) return false;
        if (block == Blocks.flowing_water || block == Blocks.water) return true;
        return false;
    }

    public static boolean isSoilBlock(Block block, int meta) {
        if (block == null) return false;
        return block.getHarvestTool(meta) == "shovel";
    }
}
