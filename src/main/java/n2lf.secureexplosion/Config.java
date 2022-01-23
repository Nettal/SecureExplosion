package n2lf.secureexplosion;

import net.minecraftforge.common.ForgeConfigSpec;

import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;

public class Config {
    public static ForgeConfigSpec COMMON_CONFIG;
    public static ForgeConfigSpec.BooleanValue InterceptALL;
    public static ForgeConfigSpec.BooleanValue InterceptExploder;
    public static ForgeConfigSpec.BooleanValue InterceptSourceMob;
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
        InterceptSourceMob = builder.comment("If the SourceMob can be found, intercept the explosion")
                .define("InterceptSourceMob",true);
        PrintExploderInfo = builder.comment("Print the Exploder info and CanonicalName")
                .define("PrintExploder",true);
        EnableWhiteList = builder.comment("Enable the WhiteList")
                .define("EnableWhiteList",true);
        WhiteList = builder.comment("Add the CanonicalName of Exploder's class, the added classes will not intercept\n"+
                        "Example:WhiteList = [\"mekanism.additions.common.entity.EntityObsidianTNT\"," +
                        " \"mekanism.additions.common.entity.baby.EntityBabyCreeper\" ," +
                        "\"net.minecraft.entity.monster.CreeperEntity\"," +
                        "\"net.minecraft.entity.item.TNTEntity\"]")
                .defineList("WhiteList", Arrays.asList("", ""), new Predicate<Object>() {
                    @Override
                    public boolean test(Object o) {
                      //  System.out.println(o);
                        return true;
                    }
                });

        COMMON_CONFIG = builder.build();
    }
}
