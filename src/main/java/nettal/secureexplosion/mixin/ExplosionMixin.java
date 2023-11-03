package nettal.secureexplosion.mixin;

import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import net.minecraft.world.explosion.Explosion;
import net.minecraft.world.explosion.ExplosionBehavior;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Explosion.class)
public abstract class ExplosionMixin {
    @Shadow
    @Nullable
    public abstract LivingEntity getCausingEntity();

    @Shadow
    @Final
    private Explosion.DestructionType destructionType;

    @Mutable
    @Shadow
    @Final
    private ExplosionBehavior behavior;

    @Inject(method = "collectBlocksAndDamageEntities",
            at = @At("HEAD"))
    private void before_collectBlocksAndDamageEntities(CallbackInfo ci) {
        if (getCausingEntity() != null && destructionType != Explosion.DestructionType.KEEP) {
            behavior = new ExplosionBehavior() {
                @Override
                public boolean canDestroyBlock(Explosion explosion, BlockView world, BlockPos pos, BlockState state, float power) {
                    return false;
                }
            };
        }
    }
}
