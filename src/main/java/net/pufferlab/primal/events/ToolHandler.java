package net.pufferlab.primal.events;

import net.minecraft.block.Block;
import net.minecraft.block.BlockLeavesBase;
import net.minecraft.block.material.Material;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.event.entity.player.*;
import net.minecraftforge.event.world.BlockEvent;
import net.pufferlab.primal.*;
import net.pufferlab.primal.blocks.BlockMetaDirt;
import net.pufferlab.primal.blocks.BlockMetaGrass;
import net.pufferlab.primal.network.NetworkTree;
import net.pufferlab.primal.utils.BlockUtils;
import net.pufferlab.primal.utils.ItemUtils;

import cpw.mods.fml.common.eventhandler.Event;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;

public class ToolHandler implements IEventHandler {

    @SubscribeEvent
    public void setBreakSpeed(PlayerEvent.BreakSpeed event) {
        ItemStack heldItem = event.entityPlayer.inventory.getCurrentItem();

        if (Config.noTreeFistPunching.getBoolean()) {
            if (BlockUtils.isLogBlock(event.block)) {
                if (heldItem != null) {
                    if (!ItemUtils.isAxeTool(heldItem)) {
                        event.setCanceled(true);
                    }
                } else {
                    event.setCanceled(true);
                }
            }
        }

        if (Config.harderSoil.getBoolean()) {
            if (BlockUtils.isSoilBlock(event.block, event.metadata)) {
                if (heldItem != null) {
                    if (!ItemUtils.isShovelTool(heldItem)) {
                        event.newSpeed = event.originalSpeed / 2;
                    }
                } else {
                    event.newSpeed = event.originalSpeed / 2;
                }
            }
        }

    }

    @SubscribeEvent
    public void tooltipEvent(ItemTooltipEvent event) {
        if (Config.vanillaToolsRemovalMode.getInt() == 2) {
            if (ItemUtils.isBrokenTool(event.itemStack)) {
                event.toolTip.add("§cThis tool is too weak to be used!");
            }
        }
        if (Config.vanillaToolsRemovalMode.getInt() == 1) {
            if (ItemUtils.isBrokenTool(event.itemStack)) {
                event.toolTip.add("§cThis tool cannot be crafted!");
            }
        }
    }

    @SubscribeEvent
    public void attackEntityEvent(AttackEntityEvent event) {
        if (Config.vanillaToolsRemovalMode.getInt() == 2) {
            ItemStack heldItem = event.entityPlayer.getHeldItem();
            if (ItemUtils.isBrokenTool(heldItem)) {
                event.entityPlayer.destroyCurrentEquippedItem();
            }
        }

    }

    @SubscribeEvent
    public void useHoeEvent(UseHoeEvent event) {
        Block block = event.world.getBlock(event.x, event.y, event.z);
        Block blockAbove = event.world.getBlock(event.x, event.y + 1, event.z);
        int meta = event.world.getBlockMetadata(event.x, event.y, event.z);
        if (blockAbove.getMaterial() == Material.air) {
            Block farmland = null;
            if (block instanceof BlockMetaGrass block2) {
                farmland = block2.getFarmlandBlock();
            }
            if (block instanceof BlockMetaDirt block2) {
                farmland = block2.getFarmlandBlock();
            }
            if (farmland != null) {
                BlockUtils.playSound(event.world, event.x, event.y, event.z, Blocks.farmland);
                event.world.setBlock(event.x, event.y, event.z, farmland, meta, 2);
                Block block2 = event.world.getBlock(event.x, event.y, event.z);
                block2.onBlockPlacedBy(event.world, event.x, event.y, event.z, event.entityPlayer, event.current);

                event.setResult(Event.Result.ALLOW);
            }
        }
    }

    @SubscribeEvent
    public void playerInteractEventHandler(PlayerInteractEvent event) {
        if (Config.vanillaToolsRemovalMode.getInt() == 2) {
            if (event.action != PlayerInteractEvent.Action.RIGHT_CLICK_AIR) {
                ItemStack heldItem = event.entityPlayer.getHeldItem();
                if (ItemUtils.isBrokenTool(heldItem)) {
                    event.entityPlayer.destroyCurrentEquippedItem();
                }
            }
        }

        if (event.action == PlayerInteractEvent.Action.RIGHT_CLICK_BLOCK) {
            ItemStack heldItem = event.entityPlayer.getHeldItem();
            if (ItemUtils.isShovelTool(heldItem)) {
                World world = event.world;
                Block block = world.getBlock(event.x, event.y, event.z);
                Block blockAbove = event.world.getBlock(event.x, event.y + 1, event.z);
                int meta = world.getBlockMetadata(event.x, event.y, event.z);
                if (blockAbove.getMaterial() == Material.air) {
                    Block path = null;
                    if (block instanceof BlockMetaGrass block2) {
                        path = block2.getPathBlock();
                    }
                    if (block instanceof BlockMetaDirt block2) {
                        path = block2.getPathBlock();
                    }
                    if (path != null) {
                        BlockUtils.playSound(world, event.x, event.y, event.z, Blocks.dirt);
                        world.setBlock(event.x, event.y, event.z, path, meta, 2);
                        heldItem.damageItem(1, event.entityPlayer);
                        event.entityPlayer.swingItem();
                    }
                }
            }
            if (ItemUtils.isKnifeTool(heldItem) && Mods.efr.isLoaded()) {
                World world = event.world;
                Block block = world.getBlock(event.x, event.y, event.z);
                int meta = world.getBlockMetadata(event.x, event.y, event.z);
                Block target = null;

                ForgeDirection side = ForgeDirection.getOrientation(event.face);

                int offsetX = side.offsetX;
                int offsetY = side.offsetY;
                int offsetZ = side.offsetZ;
                if (block == Blocks.log) {
                    target = Mods.efr.getModBlock("log_stripped");
                } else if (block == Blocks.log2) {
                    target = Mods.efr.getModBlock("log2_stripped");
                }

                if (target != null) {
                    int woodType = meta & 3;
                    ItemStack droppedStack = null;
                    world.setBlock(event.x, event.y, event.z, target, meta, 2);
                    heldItem.damageItem(1, event.entityPlayer);
                    BlockUtils.playSound(event.world, event.x, event.y, event.z, Blocks.log);

                    if (block == Blocks.log) {
                        droppedStack = new ItemStack(Registry.bark, 1, woodType);
                    } else {
                        droppedStack = new ItemStack(Registry.bark, 1, woodType + 4);
                    }

                    event.entityPlayer.swingItem();
                    if (!world.isRemote) {
                        EntityItem entityItem = new EntityItem(
                            world,
                            event.x + 0.5 + offsetX,
                            event.y + 0.5 + offsetY,
                            event.z + 0.5 + offsetZ,
                            droppedStack);

                        entityItem.motionX = world.rand.nextGaussian() * 0.005D;
                        entityItem.motionY = 0.02D;
                        entityItem.motionZ = world.rand.nextGaussian() * 0.005D;
                        world.spawnEntityInWorld(entityItem);
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public void harvestDropsEvent(BlockEvent.HarvestDropsEvent event) {

        if (event.block instanceof BlockLeavesBase) {
            if (Config.stickDropChance.roll(event.world.rand)) {
                event.drops.add(new ItemStack(Items.stick, 1, 0));
            }
        }

        if (event.harvester != null) {
            ItemStack heldItem = event.harvester.getHeldItem();
            if (Config.vanillaToolsRemovalMode.getInt() == 2) {
                if (ItemUtils.isBrokenTool(heldItem)) {
                    event.drops.clear();
                    event.harvester.destroyCurrentEquippedItem();
                }
            }

            if (Config.fallingTree.getBoolean()) {
                if (BlockUtils.isLogBlock(event.block)) {
                    NetworkTree.generateAndDestroyTree(event.world, event.x, event.y, event.z, event.harvester);
                }
            }

            if (BlockUtils.isBushBlock(event.block)) {
                if (ItemUtils.isKnifeTool(heldItem)) {
                    heldItem.damageItem(1, event.harvester);
                    event.drops.clear();
                    event.drops.add(
                        ItemUtils.getModItem("straw", 1)
                            .copy());
                }
            }
        }

    }
}
