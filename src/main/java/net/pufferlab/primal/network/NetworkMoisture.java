package net.pufferlab.primal.network;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;
import net.pufferlab.primal.tileentities.ITile;
import net.pufferlab.primal.tileentities.TileEntityFarmland;
import net.pufferlab.primal.utils.BlockUtils;
import net.pufferlab.primal.utils.PosList;

public class NetworkMoisture {

    List<ITile> tiles = new ArrayList<>();
    PosList waterBlocks = new PosList();

    public static void generateNetwork(ITile te) {
        if (te == null) return;
        NetworkMoisture network = new NetworkMoisture();
        network.recurseTile(te);

        for (ITile tile : network.tiles) {
            if (tile instanceof TileEntityFarmland tef) {
                float best = 0.0F;

                for (int i = 0; i < network.waterBlocks.size(); i++) {
                    int x = network.waterBlocks.getX(i);
                    int y = network.waterBlocks.getY(i);
                    int z = network.waterBlocks.getZ(i);

                    float moisture = getMoisture(tef, x, y, z);
                    best = Math.max(moisture, best);
                }

                tef.setMoisture(best);
            }
        }

    }

    private static float getMoisture(ITile tef, int waterX, int y, int waterZ) {
        int dx = Math.abs(tef.getX() - waterX);
        int dy = Math.abs(tef.getY() - y);
        if (dy > 1) return 0.0F;
        int dz = Math.abs(tef.getZ() - waterZ);

        float dist = Math.max(dx, dz);

        float moisture = Math.max(0.0F, 1.25F - (dist / 4.0F));
        return moisture;
    }

    public void recurseTile(ITile currentTe) {
        for (ITile te : getConnectedTiles(currentTe)) {
            if (!this.tiles.contains(te)) {
                this.tiles.add(te);
                recurseTile(te);
            }
        }
    }

    public List<ITile> getConnectedTiles(ITile te) {
        List<ITile> connected = new ArrayList<>();
        connected.add(te);
        if (te instanceof TileEntityFarmland) {
            for (ForgeDirection direction : BlockUtils.sideXZDirections) {
                int offsetX = te.getX() + direction.offsetX;
                int offsetY = te.getY() + direction.offsetY;
                int offsetZ = te.getZ() + direction.offsetZ;
                Block block = te.getWorld()
                    .getBlock(offsetX, offsetY, offsetZ);
                if (BlockUtils.isWaterBlock(block)) {
                    if (!this.waterBlocks.contains(offsetX, offsetY, offsetZ)) {
                        this.waterBlocks.add(offsetX, offsetY, offsetZ);
                    }
                }
                TileEntity neighbourTE = te.getWorld()
                    .getTileEntity(offsetX, offsetY, offsetZ);
                if (neighbourTE instanceof TileEntityFarmland farmland) {
                    connected.add(farmland);
                }
            }
        }

        return connected;
    }
}
