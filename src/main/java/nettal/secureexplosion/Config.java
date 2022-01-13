package nettal.secureexplosion;

import net.minecraftforge.common.ForgeConfigSpec;

public class Config {
    public static ForgeConfigSpec COMMON_CONFIG;
    public static ForgeConfigSpec.BooleanValue InterceptALL;
    public static ForgeConfigSpec.BooleanValue InterceptExploder;
    public static ForgeConfigSpec.BooleanValue InterceptSourceMob;

    static {
        ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();
        builder.comment("Interception Settings").push("settings");
        InterceptALL = builder.comment("Whether if intercept all the explosions")
                .define("InterceptALL",false);
        InterceptExploder = builder.comment("If the Exploder can be found, intercept the explosion")
                .define("InterceptExploder",false);
        InterceptSourceMob = builder.comment("If the SourceMob can be found, intercept the explosion")
                .define("InterceptSourceMob",true);
        COMMON_CONFIG = builder.build();
    }
}
