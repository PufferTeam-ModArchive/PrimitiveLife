package net.pufferlab.primal.mixinplugin;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.pufferlab.primal.Config;
import net.pufferlab.primal.Primal;

import com.gtnewhorizon.gtnhmixins.IEarlyMixinLoader;

import cpw.mods.fml.relauncher.FMLLaunchHandler;
import cpw.mods.fml.relauncher.IFMLLoadingPlugin;

@IFMLLoadingPlugin.Name("PrimalEarlyMixins")
@IFMLLoadingPlugin.MCVersion("1.7.10")
public class PrimalEarlyMixins implements IFMLLoadingPlugin, IEarlyMixinLoader {

    @Override
    public String getMixinConfig() {
        return Primal.earlyMixins;
    }

    List<String> mixins = new ArrayList<>();

    @Override
    public List<String> getMixins(Set<String> loadedCoreMods) {
        boolean isClient = FMLLaunchHandler.side()
            .isClient();
        Config.setupEarlyConfig();

        if (isClient) {
            if (Config.particleFix.getBoolean()) {
                mixins.add("minecraft.client.MixinEntityDiggingFX");
            }
            mixins.add("minecraft.client.MixinRenderGlobal");
            mixins.add("minecraft.client.MixinResourcePackRepository");
            mixins.add("minecraft.client.MixinBlockClient");
            mixins.add("minecraft.client.MixinMinecraft");
        }
        if (Config.harderTrees.getBoolean()) {
            mixins.add("minecraft.MixinBlockBreak");
        }
        if (Config.waterloggingFix.getBoolean()) {
            if (isClient) {
                mixins.add("minecraft.client.MixinRenderFluidBlocks");
                mixins.add("minecraft.client.MixinBlockLiquidClient");
            }
            mixins.add("minecraft.MixinBlockDynamicLiquid");
            mixins.add("minecraft.MixinBlockLiquid");
            mixins.add("minecraft.MixinWorldLiquid");
        }
        if (Config.plantFix.getBoolean()) {
            mixins.add("minecraft.MixinEntityAIEatGrass");
            mixins.add("minecraft.MixinBlockBush");
            mixins.add("minecraft.MixinBlockCrops");
            if (isClient) {
                mixins.add("minecraft.client.MixinBlockDoublePlant");
            }
        }
        if (Config.destructiveFallingBlocks.getBoolean()) {
            mixins.add("minecraft.MixinBlockFalling");
            mixins.add("minecraft.MixinEntityFallingBlock");
        }
        if (Config.sidewayFallingBlocks.getBoolean()) {
            mixins.add("minecraft.MixinBlockFalling_SideFall");
        }
        if (Config.worldLayerExtending.getBoolean()) {
            mixins.add("minecraft.world.MixinChunkProviderGenerate");
        }
        return mixins;
    }

    @Override
    public String[] getASMTransformerClass() {
        return null;
    }

    @Override
    public String getModContainerClass() {
        return null;
    }

    @Override
    public String getSetupClass() {
        return null;
    }

    @Override
    public void injectData(Map<String, Object> data) {

    }

    @Override
    public String getAccessTransformerClass() {
        return null;
    }
}
