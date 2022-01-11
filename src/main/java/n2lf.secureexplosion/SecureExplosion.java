package n2lf.secureexplosion;

import net.minecraft.world.Explosion;
import net.minecraftforge.event.world.ExplosionEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import org.apache.logging.log4j.LogManager;

@Mod(SecureExplosion.MODID)
@Mod.EventBusSubscriber()
public class SecureExplosion {
    public static final String MODID = "secureexplosion";

    public SecureExplosion() {
        //       LogManager.getLogger().info("Author:n2lf");
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, Config.COMMON_CONFIG);
    }

    @SubscribeEvent
    public static void onExplosionStart(ExplosionEvent event) {
        Explosion explosion = event.getExplosion();
        //System.out.println("getExploder:"+event.getExplosion().getExploder()+":getSourceMob:"+event.getExplosion().getSourceMob());
        if (Config.InterceptALL.get() ||
                (Config.InterceptExploder.get() && explosion.getExploder() != null) ||
                (Config.InterceptSourceMob.get() && explosion.getSourceMob() != null)) {
            event.getExplosion().clearToBlow();
        }
    }
}
