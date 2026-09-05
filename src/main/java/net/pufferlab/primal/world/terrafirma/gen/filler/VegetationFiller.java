package net.pufferlab.primal.world.terrafirma.gen.filler;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import net.pufferlab.primal.Constants;
import net.pufferlab.primal.utils.BlockUtils;
import net.pufferlab.primal.utils.PlantType;
import net.pufferlab.primal.utils.Spline;
import net.pufferlab.primal.utils.WorldUtils;
import net.pufferlab.primal.world.structures.StructureFile;
import net.pufferlab.primal.world.terrafirma.ChunkBlockData;
import net.pufferlab.primal.world.terrafirma.ChunkNoiseData;

public class VegetationFiller implements IBlockLayer {

    public static final Spline forestSpline;

    static {
        forestSpline = new Spline();
        forestSpline.addPoint(0.0F, 0.0F);
        forestSpline.addPoint(0.5F, 0.0F);
        forestSpline.addPoint(0.6F, 0.8F);
        forestSpline.addPoint(1.0F, 1.0F);
    }

    public World world;

    public VegetationFiller(World world) {
        this.world = world;
    }

    @Override
    public void generate(ChunkBlockData data, ChunkNoiseData dataNoise, int chunkX, int chunkZ) {
        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                int worldX = (chunkX << 4) + x;
                int worldZ = (chunkZ << 4) + z;

                int y = dataNoise.getHeight(x, z);

                Block blockAbove = data.getBlock(x, y + 1, z);
                Block blockBelow = data.getBlock(x, y - 1, z);
                Block blockReplacing = data.getBlock(x, y, z);
                if (blockBelow != Blocks.air) {
                    float detail = dataNoise.getDetail(x, z);

                    float temperature = dataNoise.getTemperature(x, z) + (detail * 0.03F);
                    float vegetation = dataNoise.getVegetation(x, z) + (detail * 0.02F);
                    float forestness = dataNoise.getForestness(x, z) + (detail * 0.02F);

                    if (BlockUtils.isSandBlock(blockBelow)) {
                        if (vegetation > 0.5F) {
                            if (data.random.nextFloat() < 0.05F) {
                                placePlant(data, x, y, z, Constants.dry_grass);
                            }
                        }
                    }
                    if (BlockUtils.isWaterBlock(blockReplacing)) {
                        if (BlockUtils.isWaterBlock(blockAbove)) {
                            if (data.random.nextFloat() < 0.1F) {
                                placePlant(data, x, y, z, Constants.pondweed);
                            }
                            if (data.random.nextFloat() < 0.1F) {
                                placePlant(data, x, y, z, Constants.seaweed);
                            }
                        } else {
                            if (data.random.nextFloat() < 0.04F) {
                                placePlant(data, x, y, z, Constants.cattails);
                            }
                        }
                    }
                    if (BlockUtils.isGrassBlock(blockBelow)) {
                        if (blockReplacing != Blocks.snow_layer) {
                            if (vegetation > 0.3F) {
                                if (data.random.nextFloat() < 0.5F) {
                                    placePlant(data, x, y, z, Constants.grass);
                                }
                            }
                            if (vegetation > 0.5F) {
                                if (data.random.nextFloat() < 0.1F) {
                                    placePlant(data, x, y, z, Constants.tall_grass);
                                }
                            }
                        } else {
                            if (vegetation > 0.3F) {
                                if (data.random.nextFloat() < 0.5F) {
                                    placePlant(data, x, y, z, Constants.snowy_grass);
                                }
                            }
                        }

                        if (data.random.nextInt(50) == 0) {
                            if (data.random.nextFloat() < forestSpline.sample(forestness)) {
                                int num = data.random.nextInt(2) + 1;
                                int facing = data.random.nextInt(4);

                                if (temperature < 0.45F) {
                                    StructureFile.loadStructure(
                                        "spruce_tree_1",
                                        worldX,
                                        y,
                                        worldZ,
                                        world,
                                        facing,
                                        StructureFile.LoadingPosition.ground);
                                } else {
                                    StructureFile.loadStructure(
                                        "oak_tree_" + num,
                                        worldX,
                                        y,
                                        worldZ,
                                        world,
                                        facing,
                                        StructureFile.LoadingPosition.ground);
                                }
                            }
                        }
                    }
                }
            }
        }
        for (PlantType plant : Constants.plantTypeSpawnable) {
            if (plant.getChance(data.random)) {
                int count = data.random.nextInt(4) + 1;
                for (int i = 0; i < count; i++) {
                    int x = data.random.nextInt(16);
                    int z = data.random.nextInt(16);
                    float temperature = dataNoise.getTemperature(x, z);
                    float rainfall = dataNoise.getRainfall(x, z);
                    if (plant.canSpawn(rainfall, temperature)) {
                        int y = dataNoise.getHeight(x, z);
                        Block block = data.getBlock(x, y - 1, z);
                        if (block != Blocks.air) {
                            boolean shouldSpawn = false;
                            if (plant.isDesertic) {
                                shouldSpawn = BlockUtils.isSandBlock(block);
                            } else if (plant.isAquatic) {
                                shouldSpawn = BlockUtils.isWaterBlock(block);
                            } else {
                                shouldSpawn = BlockUtils.isGrassBlock(block);
                            }
                            if (shouldSpawn) {
                                placePlant(data, x, y, z, plant);
                            }
                        }
                    }
                }
            }
        }
    }

    public void placePlant(ChunkBlockData data, int x, int y, int z, PlantType plant) {
        Block block1 = plant.plantBlock;
        int meta1 = plant.plantMeta;
        data.setBlock(x, y, z, block1, meta1);
        if (plant.doublePlant) {
            Block block2 = plant.plantBlock2;
            int meta2 = plant.plantMeta2;
            data.setBlock(x, y + 1, z, block2, meta2);
        }
    }

    public void placePlantSpot(ChunkBlockData data, int x, int y, int z, PlantType plant) {
        int worldX = data.chunkX + x;
        int worldZ = data.chunkZ + z;
        Block block1 = plant.plantBlock;
        int meta1 = plant.plantMeta;
        WorldUtils.setBlockWorldgen(world, worldX, y, worldZ, block1, meta1);
        if (plant.doublePlant) {
            Block block2 = plant.plantBlock2;
            int meta2 = plant.plantMeta2;
            WorldUtils.setBlockWorldgen(world, worldX, y + 1, worldZ, block2, meta2);
        }
    }
}
