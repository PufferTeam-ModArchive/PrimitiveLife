package net.pufferlab.primal.mixins.early.minecraft;

import net.minecraft.block.Block;
import net.minecraft.world.World;
import net.pufferlab.primal.network.NetworkTree;
import net.pufferlab.primal.utils.BlockUtils;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Block.class)
public class MixinBlockBreak {

    @Shadow
    public float blockHardness;

    @Inject(method = "getBlockHardness", at = @At("HEAD"), cancellable = true)
    private void getBlockHardness$primal(World worldIn, int x, int y, int z, CallbackInfoReturnable<Float> cir) {
        if (BlockUtils.isLogBlock((Block) (Object) this)) {
            NetworkTree network = NetworkTree.generateTree(worldIn, x, y, z);
            float multiplier = 1.0F;
            if (network.isValidTree) {
                multiplier = (float) network.logAmount;
            }
            cir.setReturnValue(blockHardness * multiplier);
        }
    }
}
