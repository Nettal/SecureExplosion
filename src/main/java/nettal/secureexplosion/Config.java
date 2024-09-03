package nettal.secureexplosion;

import net.neoforged.neoforge.common.ModConfigSpec;

import java.util.Arrays;
import java.util.List;

public class Config {
    public static ModConfigSpec COMMON_CONFIG;
    public static ModConfigSpec.BooleanValue InterceptALL;
    public static ModConfigSpec.BooleanValue InterceptExploder;
    public static ModConfigSpec.BooleanValue InterceptDirectSourceEntity;
    public static ModConfigSpec.BooleanValue PrintExploderInfo;
    public static ModConfigSpec.BooleanValue EnableWhiteList;
    public static ModConfigSpec.ConfigValue<List<? extends String>> WhiteList;

    static {
        ModConfigSpec.Builder builder = new ModConfigSpec.Builder();
        builder.comment("Interception Settings").push("settings");
        InterceptALL = builder.comment("Whether if intercept all the explosions")
                .define("InterceptALL", false);
        InterceptExploder = builder.comment("If the Exploder can be found, intercept the explosion")
                .define("InterceptExploder", false);
        InterceptDirectSourceEntity = builder.comment("If the DirectSourceEntity can be found, intercept the explosion")
                .define("InterceptDirectSourceEntity", true);
        PrintExploderInfo = builder.comment("Print the Exploder info and CanonicalName")
                .define("PrintExploder", true);
        EnableWhiteList = builder.comment("Enable the WhiteList")
                .define("EnableWhiteList", true);
        WhiteList = builder.comment("Add the CanonicalName of Exploder's class, the added classes will not intercept\n" +
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

