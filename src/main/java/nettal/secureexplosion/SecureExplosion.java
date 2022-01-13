package nettal.secureexplosion;

import net.minecraft.world.level.Explosion;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.ExplosionEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;

// The value here should match an entry in the META-INF/mods.toml file
@Mod("secureexplosion")
public class SecureExplosion
{
    public SecureExplosion() {
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, Config.COMMON_CONFIG);
        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void onExplosionStart(ExplosionEvent event) {
        Explosion explosion = event.getExplosion();
        if (Config.InterceptALL.get() ||
                (Config.InterceptExploder.get() && explosion.getExploder() != null) ||
                (Config.InterceptSourceMob.get() && explosion.getSourceMob() != null)) {
            event.getExplosion().clearToBlow();
        }
    }
}
