package net.pufferlab.primal.blocks;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.BlockTorch;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.IIcon;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import net.pufferlab.primal.Primal;
import net.pufferlab.primal.Registry;
import net.pufferlab.primal.items.itemblocks.ItemBlockMeta;
import net.pufferlab.primal.utils.WoodType;

import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;

public class BlockLogThin extends Block implements IPrimalBlock, IMetaBlock {

    public String name;
    public WoodType wood;
    public String[] field_150168_M;
    public String[] names;
    protected IIcon[] logIcon;
    protected IIcon[] logTopIcon;

    public BlockLogThin(WoodType wood) {
        super(Material.wood);
        this.name = wood.getName();
        this.wood = wood;
        this.field_150168_M = wood.types;
        this.wood.setLogThinBlock(this, 0);
        this.wood.setStrippedLogThinBlock(this, 1);
        this.wood.setWoodThinBlock(this, 2);
        this.wood.setStrippedWoodThinBlock(this, 3);
        this.names = wood.thinTypes;
        this.setStepSound(Block.soundTypeWood);
        this.setHardness(2.0F);
        this.setBlockBounds(0.5f, 0.5f, 0.5f, 0.51f, 0.51f, 0.51f);
    }

    @Override
    public boolean renderDefaultBounds() {
        return false;
    }

    @Override
    public void getSubBlocks(Item item, CreativeTabs tab, List<ItemStack> list) {
        for (int i = 0; i < 16; i++) {
            list.add(new ItemStack(item, 1, i));
        }
    }

    public float getMargin(int meta) {
        int type = this.wood.getOffset(meta);
        if (type == 0) {
            return 0.125F;
        }
        if (type == 4) {
            return 0.1875F;
        }
        if (type == 8) {
            return 0.25F;
        }
        return 0.375F;
    }

    public int calcConnectionFlags(IBlockAccess world, int x, int y, int z) {
        int flagsY = calcConnectYFlags(world, x, y, z);
        int flagsZNeg = calcConnectYFlags(world, x, y, z - 1);
        int flagsZPos = calcConnectYFlags(world, x, y, z + 1);
        int flagsXNeg = calcConnectYFlags(world, x - 1, y, z);
        int flagsXPos = calcConnectYFlags(world, x + 1, y, z);

        int connectFlagsY = flagsY & 3;
        int connectFlagsZNeg = flagsZNeg & 3;
        int connectFlagsZPos = flagsZPos & 3;
        int connectFlagsXNeg = flagsXNeg & 3;
        int connectFlagsXPos = flagsXPos & 3;

        Block blockZNeg = world.getBlock(x, y, z - 1);
        Block blockZPos = world.getBlock(x, y, z + 1);
        Block blockXNeg = world.getBlock(x - 1, y, z);
        Block blockXPos = world.getBlock(x + 1, y, z);

        boolean hardZNeg = isNeighborHardConnection(world, x, y, z - 1, blockZNeg, ForgeDirection.NORTH)
            || blockZNeg instanceof BlockTorch;
        boolean hardZPos = isNeighborHardConnection(world, x, y, z + 1, blockZPos, ForgeDirection.SOUTH)
            || blockZPos instanceof BlockTorch;
        boolean hardXNeg = isNeighborHardConnection(world, x - 1, y, z, blockXNeg, ForgeDirection.WEST)
            || blockXNeg instanceof BlockTorch;
        boolean hardXPos = isNeighborHardConnection(world, x + 1, y, z, blockXPos, ForgeDirection.EAST)
            || blockXPos instanceof BlockTorch;

        boolean hardConnection = (flagsY & 4) != 0;
        boolean hardConnectionZNeg = hardConnection && (flagsZNeg & 4) != 0;
        boolean hardConnectionZPos = hardConnection && (flagsZPos & 4) != 0;
        boolean hardConnectionXNeg = hardConnection && (flagsXNeg & 4) != 0;
        boolean hardConnectionXPos = hardConnection && (flagsXPos & 4) != 0;

        boolean connectZNeg = (connectFlagsY == 0 && hardZNeg)
            || (blockZNeg instanceof BlockLogThin && !hardConnectionZNeg
                && (connectFlagsY != 3 || connectFlagsZNeg != 3));
        boolean connectZPos = (connectFlagsY == 0 && hardZPos)
            || (blockZPos instanceof BlockLogThin && !hardConnectionZPos
                && (connectFlagsY != 3 || connectFlagsZPos != 3));
        boolean connectXNeg = (connectFlagsY == 0 && hardXNeg)
            || (blockXNeg instanceof BlockLogThin && !hardConnectionXNeg
                && (connectFlagsY != 3 || connectFlagsXNeg != 3));
        boolean connectXPos = (connectFlagsY == 0 && hardXPos)
            || (blockXPos instanceof BlockLogThin && !hardConnectionXPos
                && (connectFlagsY != 3 || connectFlagsXPos != 3));

        boolean connectSide = connectZNeg | connectZPos | connectXNeg | connectXPos;
        if (!connectSide && (connectFlagsY & 1) == 0) {
            if (hardZNeg) connectZNeg = true;
            if (hardZPos) connectZPos = true;
            if (hardXNeg) connectXNeg = true;
            if (hardXPos) connectXPos = true;
        }

        if (!(connectZNeg | connectZPos | connectXNeg | connectXPos)) connectFlagsY = 3;

        if (connectFlagsY == 2 && hardZNeg) connectZNeg = true;
        if (connectFlagsY == 2 && hardZPos) connectZPos = true;
        if (connectFlagsY == 2 && hardXNeg) connectXNeg = true;
        if (connectFlagsY == 2 && hardXPos) connectXPos = true;

        return connectFlagsY | (connectZNeg ? 4 : 0)
            | (connectZPos ? 8 : 0)
            | (connectXNeg ? 16 : 0)
            | (connectXPos ? 32 : 0);
    }

    private int calcConnectYFlags(IBlockAccess world, int x, int y, int z) {
        Block block = world.getBlock(x, y, z);
        if (!(block instanceof BlockLogThin)) return 0;

        Block blockYNeg = world.getBlock(x, y - 1, z);
        boolean hardYNeg = isNeighborHardConnectionY(world, x, y - 1, z, blockYNeg, ForgeDirection.DOWN);
        boolean connectYNeg = hardYNeg || blockYNeg instanceof BlockLogThin;

        Block blockYPos = world.getBlock(x, y + 1, z);
        boolean hardYPos = isNeighborHardConnectionY(world, x, y + 1, z, blockYPos, ForgeDirection.UP);
        boolean connectYPos = hardYPos || blockYPos instanceof BlockLogThin || blockYPos instanceof BlockTorch;

        return (connectYNeg ? 1 : 0) | (connectYPos ? 2 : 0) | (hardYNeg ? 4 : 0) | (hardYPos ? 8 : 0);
    }

    private boolean isNeighborHardConnection(IBlockAccess world, int x, int y, int z, Block block,
        ForgeDirection side) {
        if (block.getMaterial()
            .isOpaque() && block.renderAsNormalBlock()) return true;

        if (block.isSideSolid(world, x, y, z, side.getOpposite())) return true;
        // if (block == ModBlocks.largePot)
        // return true;
        return false;
    }

    private boolean isNeighborHardConnectionY(IBlockAccess world, int x, int y, int z, Block block,
        ForgeDirection side) {
        if (isNeighborHardConnection(world, x, y, z, block, side)) return true;

        return block instanceof BlockLogThin;
    }

    @Override
    public CreativeTabs getCreativeTab() {
        return Registry.creativeTabWorld;
    }

    @Override
    public void registerBlockIcons(IIconRegister reg) {
        this.logIcon = new IIcon[field_150168_M.length];
        this.logTopIcon = new IIcon[field_150168_M.length];

        for (int i = 0; i < this.logIcon.length; ++i) {
            if (i == 0 || i == 1) {
                this.logIcon[i] = reg.registerIcon(wood.getSideTexture(i == 1));
                this.logTopIcon[i] = reg.registerIcon(wood.getTopTexture(i == 1));
            } else {
                this.logIcon[i] = this.logIcon[i - 2];
                this.logTopIcon[i] = this.logIcon[i - 2];
            }
        }
    }

    @Override
    public IIcon getIcon(int side, int meta) {
        int orient = getPass();

        int ometa = 0;
        if (orient == 1) ometa |= 8;
        else if (orient == 2) ometa |= 4;
        else if (orient == 3) ometa |= 12;

        int meta2 = meta % 4 | ometa;

        int k = meta2 & 12;
        int i = meta2 & 3;
        int l = i;
        return k == 0 && (side == 1 || side == 0) ? logTopIcon[l]
            : (k == 4 && (side == 5 || side == 4) ? logTopIcon[l]
                : (k == 8 && (side == 2 || side == 3) ? logTopIcon[l] : logIcon[l]));
    }

    @Override
    public int getStateID() {
        return 3;
    }

    @Override
    public List<AxisAlignedBB> getBounds(World world, int x, int y, int z, EntityPlayer player, BoundsType bounds) {
        int connectFlags = this.calcConnectionFlags(world, x, y, z);
        float margin = this.getMargin(world.getBlockMetadata(x, y, z));
        List<AxisAlignedBB> list = new ArrayList<>();

        boolean connectYNeg = (connectFlags & 1) != 0;
        boolean connectYPos = (connectFlags & 2) != 0;
        boolean connectZNeg = (connectFlags & 4) != 0;
        boolean connectZPos = (connectFlags & 8) != 0;
        boolean connectXNeg = (connectFlags & 16) != 0;
        boolean connectXPos = (connectFlags & 32) != 0;

        boolean connectY = connectYNeg | connectYPos;
        boolean connectZ = connectZNeg | connectZPos;
        boolean connectX = connectXNeg | connectXPos;

        if (!(connectYNeg && connectYPos) && !(connectZNeg && connectZPos) && !(connectXNeg && connectXPos)) {
            list.add(AxisAlignedBB.getBoundingBox(margin, margin, margin, 1 - margin, 1 - margin, 1 - margin));
        }

        if (connectY) {
            if (connectYNeg && connectYPos && !(connectXNeg && connectXPos)) {
                list.add(AxisAlignedBB.getBoundingBox(margin, 0, margin, 1 - margin, 1, 1 - margin));
            } else if (connectYNeg && connectYPos && (connectXNeg && connectXPos)) {
                list.add(AxisAlignedBB.getBoundingBox(margin, 0, margin, 1 - margin, margin, 1 - margin));
                list.add(AxisAlignedBB.getBoundingBox(margin, 1 - margin, margin, 1 - margin, 1, 1 - margin));
            } else if (connectYNeg) {
                list.add(AxisAlignedBB.getBoundingBox(margin, 0, margin, 1 - margin, margin, 1 - margin));
            } else if (connectYPos) {
                list.add(AxisAlignedBB.getBoundingBox(margin, 1 - margin, margin, 1 - margin, 1, 1 - margin));
            }
        }

        if (connectZ) {
            if (connectZNeg && connectZPos && !(connectYNeg && connectYPos)) {
                list.add(AxisAlignedBB.getBoundingBox(margin, margin, 0, 1 - margin, 1 - margin, 1));
            } else if (connectZNeg && connectZPos && (connectYNeg && connectYPos)) {
                list.add(AxisAlignedBB.getBoundingBox(margin, margin, 0, 1 - margin, 1 - margin, margin));
                list.add(AxisAlignedBB.getBoundingBox(margin, margin, 1 - margin, 1 - margin, 1 - margin, 1));
            } else if (connectZNeg) {
                list.add(AxisAlignedBB.getBoundingBox(margin, margin, 0, 1 - margin, 1 - margin, margin));
            } else if (connectZPos) {
                list.add(AxisAlignedBB.getBoundingBox(margin, margin, 1 - margin, 1 - margin, 1 - margin, 1));
            }
        }

        if (connectX) {
            if (connectXNeg && connectXPos && !(connectZNeg && connectZPos)) {
                list.add(AxisAlignedBB.getBoundingBox(0, margin, margin, 1, 1 - margin, 1 - margin));
            } else if (connectXNeg && connectXPos && (connectZNeg && connectZPos)) {
                list.add(AxisAlignedBB.getBoundingBox(0, margin, margin, margin, 1 - margin, 1 - margin));
                list.add(AxisAlignedBB.getBoundingBox(1 - margin, margin, margin, 1, 1 - margin, 1 - margin));
            } else if (connectXNeg) {
                list.add(AxisAlignedBB.getBoundingBox(0, margin, margin, margin, 1 - margin, 1 - margin));
            } else if (connectXPos) {
                list.add(AxisAlignedBB.getBoundingBox(1 - margin, margin, margin, 1, 1 - margin, 1 - margin));
            }
        }
        return list;
    }

    @Override
    public int damageDropped(int meta) {
        return meta;
    }

    @Override
    public MovingObjectPosition collisionRayTrace(World worldIn, int x, int y, int z, Vec3 startVec, Vec3 endVec) {
        return customCollisionRayTrace(worldIn, x, y, z, startVec, endVec);
    }

    @Override
    public void addCollisionBoxesToList(World worldIn, int x, int y, int z, AxisAlignedBB mask,
        List<AxisAlignedBB> list, Entity collider) {
        addCustomCollisionBoxesToList(worldIn, x, y, z, mask, list, collider);
    }

    @Override
    public boolean isOpaqueCube() {
        return false;
    }

    @Override
    public boolean renderAsNormalBlock() {
        return false;
    }

    @Override
    public boolean canPlaceTorchOnTop(World world, int x, int y, int z) {
        return true;
    }

    @Override
    public boolean shouldSideBeRendered(IBlockAccess blockAccess, int x, int y, int z, int side) {
        return true;
    }

    @Override
    public int getRenderType() {
        return getRenderId();
    }

    @Override
    public ISimpleBlockRenderingHandler getRenderer() {
        return Primal.proxy.getThinLogRenderer();
    }

    @Override
    public String[] getElements() {
        return names;
    }

    @Override
    public String getElementName() {
        return "thin_log";
    }

    @Override
    public boolean wrapElements() {
        return true;
    }

    @Override
    public boolean hasSuffix() {
        return false;
    }

    @Override
    public Class<? extends ItemBlock> getItemBlockClass() {
        return ItemBlockMeta.class;
    }
}
