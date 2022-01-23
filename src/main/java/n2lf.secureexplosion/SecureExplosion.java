package n2lf.secureexplosion;

import net.minecraft.entity.Entity;
import net.minecraft.world.Explosion;
import net.minecraftforge.event.world.ExplosionEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(SecureExplosion.MODID)
@Mod.EventBusSubscriber()
public class SecureExplosion {
    public static final String MODID = "secureexplosion";
    public static Logger LOGGER = LogManager.getLogger();

    public SecureExplosion() {
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, Config.COMMON_CONFIG);
    }

    @SubscribeEvent
    public static void onExplosionStart(ExplosionEvent event) {
        if (!(event instanceof ExplosionEvent.Detonate))
            return;
        Explosion explosion = event.getExplosion();
        Entity exploder = explosion.getExploder();
        if (Config.PrintExploderInfo.get()) {
            if (exploder == null) {
                LOGGER.info("[SecureExplosion]:Can not get Exploder,Exploder is null");
            } else {
                LOGGER.info(String.format("[SecureExplosion]:ExploderInfo:%s;CanonicalName:%s%n",exploder , exploder.getClass().getCanonicalName()));
            }
        }
        if(exploder != null && Config.EnableWhiteList.get()
                && Config.WhiteList.get().contains(exploder.getClass().getCanonicalName())){
            return;
        }
        if (Config.InterceptALL.get() ||
                (Config.InterceptExploder.get() && exploder != null) ||
                (Config.InterceptSourceMob.get() && explosion.getSourceMob() != null)) {
            event.getExplosion().clearToBlow();
        }
    }
}