package com.github.nettal.secureexplosion;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.boss.enderdragon.EndCrystal;
import net.minecraft.world.entity.monster.Creeper;
import net.neoforged.neoforge.common.ModConfigSpec;

import java.util.List;
import java.util.regex.Pattern;

// An example config class. This is not required, but it's a good idea to have one to keep your config organized.
// Demonstrates how to use Neo's config APIs
public class Config {
    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

    // 全局开关
    public static final ModConfigSpec.BooleanValue ENABLE_MOD = BUILDER
            .comment("Enable or disable the entire secure explosion feature.")
            .define("enableSecureExplosions", true);

    public static final ModConfigSpec.BooleanValue LOG_ENABLED = BUILDER
            .comment("Enable detailed logging for explosion events.")
            .define("enableLogging", true);

    // 当爆炸的直接来源实体为 null 时的行为
    public static final ModConfigSpec.BooleanValue CLEAR_ENTITIES_WHEN_EXPLODER_NULL = BUILDER
            .comment("If true, clear affected entities when the explosion has no direct source entity (e.g. bed in nether).",
                    "Default: false (entities not cleared)")
            .define("clearEntities.whenExploderNull", false);

    public static final ModConfigSpec.BooleanValue CLEAR_BLOCKS_WHEN_EXPLODER_NULL = BUILDER
            .comment("If true, clear affected blocks when the explosion has no direct source entity.",
                    "Default: true (blocks cleared)")
            .define("clearBlocks.whenExploderNull", true);

    // ========== 实体清除 ==========
    public static final ModConfigSpec.ConfigValue<List<? extends String>> CLEAR_ENTITIES_BY_DESCRIPTOR_ID = BUILDER
            .comment("Blacklist (regex): clear affected entities if the exploder's description ID matches any pattern.",
                    "Description ID examples: '" + quoteEntity(EntityType.CREEPER) + "', '" + quoteEntity(EntityType.TNT) + "'",
                    "Default: [] (empty, meaning no entity will be cleared)")
            .defineListAllowEmpty("clearEntities.entityDescriptionId.blacklist",
                    List.of(), () -> "", Config::validateRegex);

    public static final ModConfigSpec.ConfigValue<List<? extends String>> EXEMPT_ENTITIES_BY_DESCRIPTOR_ID = BUILDER
            .comment("Whitelist (regex): do NOT clear affected entities even if blacklist matches. Overrides blacklist.",
                    "Default: []")
            .defineListAllowEmpty("clearEntities.entityDescriptionId.whitelist",
                    List.of(), () -> "", Config::validateRegex);

    public static final ModConfigSpec.ConfigValue<List<? extends String>> CLEAR_ENTITIES_BY_CLASS_NAME = BUILDER
            .comment("Blacklist (regex): clear affected entities if the exploder's canonical class name matches any pattern.",
                    "Class name examples: '" + quoteClass(Creeper.class) + "','" + quoteClass(EndCrystal.class) + "'",
                    "Default: []")
            .defineListAllowEmpty("clearEntities.entityClassName.blacklist",
                    List.of(), () -> "", Config::validateRegex);

    public static final ModConfigSpec.ConfigValue<List<? extends String>> EXEMPT_ENTITIES_BY_CLASS_NAME = BUILDER
            .comment("Whitelist (regex): do NOT clear affected entities even if blacklist matches. Overrides blacklist.",
                    "Default: []")
            .defineListAllowEmpty("clearEntities.entityClassName.whitelist",
                    List.of(), () -> "", Config::validateRegex);

    // ========== 方块清除配置 ==========
    // 注意：默认清除所有爆炸影响的方块（黑名单包含 ".*"）
    public static final ModConfigSpec.ConfigValue<List<? extends String>> CLEAR_BLOCKS_BY_DESCRIPTOR_ID = BUILDER
            .comment("Blacklist (regex): clear affected blocks if the exploder's description ID matches any pattern.",
                    "Default: [\".*\"]  ← matches every description ID, so all explosions will clear blocks.",
                    "To change this behavior, remove \".*\" from the list or add specific patterns.",
                    "Example: to allow only TNT explosions to clear blocks, set blacklist = [\"" + quoteEntity(EntityType.TNT) + "\"]")
            .defineListAllowEmpty("clearBlocks.entityDescriptionId.blacklist",
                    List.of(".*"), () -> "", Config::validateRegex);

    public static final ModConfigSpec.ConfigValue<List<? extends String>> EXEMPT_BLOCKS_BY_DESCRIPTOR_ID = BUILDER
            .comment("Whitelist (regex): do NOT clear affected blocks even if blacklist matches. Overrides blacklist.",
                    "Example: to prevent creepers from destroying blocks, add \"" + quoteEntity(EntityType.CREEPER) + "\" to this whitelist.",
                    "Default: []")
            .defineListAllowEmpty("clearBlocks.entityDescriptionId.whitelist",
                    List.of(), () -> "", Config::validateRegex);

    public static final ModConfigSpec.ConfigValue<List<? extends String>> CLEAR_BLOCKS_BY_CLASS_NAME = BUILDER
            .comment("Blacklist (regex): clear affected blocks if the exploder's canonical class name matches any pattern.",
                    "Default: [] (since descriptionId blacklist already covers all by default)")
            .defineListAllowEmpty("clearBlocks.entityClassName.blacklist",
                    List.of(), () -> "", Config::validateRegex);

    public static final ModConfigSpec.ConfigValue<List<? extends String>> EXEMPT_BLOCKS_BY_CLASS_NAME = BUILDER
            .comment("Whitelist (regex): do NOT clear affected blocks even if blacklist matches. Overrides blacklist.",
                    "Default: []")
            .defineListAllowEmpty("clearBlocks.entityClassName.whitelist",
                    List.of(), () -> "", Config::validateRegex);

    static final ModConfigSpec SPEC = BUILDER.build();

    private static boolean validateRegex(final Object obj) {
        if (!(obj instanceof String regex)) {
            return false;
        }
        try {
            Pattern.compile(regex);
            return true;
        } catch (Throwable e) {
            return false;
        }
    }

    private static String quoteClass(Class<?> clazz) {
        return Pattern.quote(clazz.getCanonicalName());
    }

    private static String quoteEntity(EntityType<?> type) {
        return Pattern.quote(type.getDescriptionId());
    }
}
