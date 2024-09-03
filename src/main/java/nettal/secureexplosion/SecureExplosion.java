package nettal.secureexplosion;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Explosion;
import net.neoforged.neoforge.event.level.ExplosionEvent;
import org.slf4j.Logger;

import com.mojang.logging.LogUtils;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.common.NeoForge;


@Mod(SecureExplosion.MODID)
public class SecureExplosion {
    public static final String MODID = "secureexplosion";
    public static Logger LOGGER = LogUtils.getLogger();

    public SecureExplosion(IEventBus modEventBus, ModContainer modContainer) {
        modContainer.registerConfig(ModConfig.Type.COMMON, Config.COMMON_CONFIG);
        NeoForge.EVENT_BUS.register(SecureExplosion.class);
    }

    @SubscribeEvent
    public static void onExplosionStart(ExplosionEvent.Detonate event) {
        Explosion explosion = event.getExplosion();
        Entity exploder = explosion.getDirectSourceEntity();
        if (Config.PrintExploderInfo.get()) {
            if (exploder == null) {
                LOGGER.info("[SecureExplosion]:Can not get Exploder,Exploder is null");
            } else {
                LOGGER.info(String.format("[SecureExplosion]:Exploder:[%s] CanonicalName:[%s]",
                        exploder, exploder.getClass().getCanonicalName()));
            }
        }
        if (exploder != null && Config.EnableWhiteList.get()
                && Config.WhiteList.get().contains(exploder.getClass().getCanonicalName())) {
            return;
        }
        if (Config.InterceptALL.get() ||
                (Config.InterceptExploder.get() && exploder != null) ||
                (Config.InterceptDirectSourceEntity.get() && explosion.getDirectSourceEntity() != null)) {
            event.getExplosion().clearToBlow();
        }
    }
}
