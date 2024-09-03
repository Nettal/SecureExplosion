package nettal.secureexplosion;

import net.minecraftforge.common.ForgeConfigSpec;

import java.util.Arrays;
import java.util.List;

public class Config {
    public static ForgeConfigSpec COMMON_CONFIG;
    public static ForgeConfigSpec.BooleanValue InterceptALL;
    public static ForgeConfigSpec.BooleanValue InterceptExploder;
    public static ForgeConfigSpec.BooleanValue InterceptDirectSourceEntity;
    public static ForgeConfigSpec.BooleanValue PrintExploderInfo;
    public static ForgeConfigSpec.BooleanValue EnableWhiteList;
    public static ForgeConfigSpec.ConfigValue<List<? extends String>> WhiteList;

    static {
        ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();
        builder.comment("Interception Settings").push("settings");
        InterceptALL = builder.comment("Whether if intercept all the explosions")
                .define("InterceptALL",false);
        InterceptExploder = builder.comment("If the Exploder can be found, intercept the explosion")
                .define("InterceptExploder",false);
        InterceptDirectSourceEntity = builder.comment("If the DirectSourceEntity can be found, intercept the explosion")
                .define("InterceptDirectSourceEntity",true);
        PrintExploderInfo = builder.comment("Print the Exploder info and CanonicalName")
                .define("PrintExploder",true);
        EnableWhiteList = builder.comment("Enable the WhiteList")
                .define("EnableWhiteList",true);
        WhiteList = builder.comment("Add the CanonicalName of Exploder's class, the added classes will not intercept\n"+
                        "Example:WhiteList = [\"mekanism.additions.common.entity.EntityObsidianTNT\"," +
                        " \"mekanism.additions.common.entity.baby.EntityBabyCreeper\" ," +
                        "\"net.minecraft.world.entity.monster.Creeper\"," +
                        "\"net.minecraft.world.entity.item.PrimedTnt\"]")
                .defineList("WhiteList", Arrays.asList("", ""), o -> {
                    //  System.out.println(o);
                    return true;
                });

        COMMON_CONFIG = builder.build();
    }
}

