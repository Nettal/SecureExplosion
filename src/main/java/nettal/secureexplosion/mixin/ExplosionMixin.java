package nettal.secureexplosion.mixin;

import net.minecraft.world.explosion.Explosion;
import org.spongepowered.asm.mixin.*;

@Mixin(Explosion.class)
public abstract class ExplosionMixin {

    @Overwrite
    public boolean shouldDestroy() {
        Explosion e = (Explosion) (Object) this;
        return e.getCausingEntity() == null;
    }
}
