package n2lf.secureexplosion;

import net.minecraftforge.event.world.ExplosionEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod(modid = SecureExplosion.MODID, name = SecureExplosion.NAME, version = SecureExplosion.VERSION)
@Mod.EventBusSubscriber
public class SecureExplosion {
    public static final String MODID = "secureexplosion";
    public static final String NAME = "SecureExplosion";
    public static final String VERSION = "1.0";

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        event.getModLog().info("Author: n2lf");
    }

    @SubscribeEvent
    public static void onExplosionStart(ExplosionEvent event){
        event.getExplosion().clearAffectedBlockPositions();
    }
}
