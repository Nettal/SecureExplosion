package com.github.nettal.secureexplosion;

import com.mojang.logging.LogUtils;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Explosion;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.level.ExplosionEvent;
import org.slf4j.Logger;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;

// The value here should match an entry in the META-INF/neoforge.mods.toml file
@Mod(SecureExplosion.MODID)
public class SecureExplosion {
    // Define mod id in a common place for everything to reference
    public static final String MODID = "secureexplosion";
    // Directly reference a slf4j logger
    public static final Logger LOGGER = LogUtils.getLogger();

    // The constructor for the mod class is the first code that is run when your mod is loaded.
    // FML will recognize some parameter types like IEventBus or ModContainer and pass them in automatically.
    public SecureExplosion(IEventBus modEventBus, ModContainer modContainer) {
        // Register ourselves for server and other game events we are interested in.
        // Note that this is necessary if and only if we want *this* class (SecureExplosion) to respond directly to events.
        // Do not add this line if there are no @SubscribeEvent-annotated functions in this class, like onServerStarting() below.
        NeoForge.EVENT_BUS.register(this);

        // Register our mod's ModConfigSpec so that FML can create and load the config file for us
        modContainer.registerConfig(ModConfig.Type.SERVER, Config.SPEC);
    }

    @SubscribeEvent
    public void onExplosionDetonate(ExplosionEvent.Detonate event) {
        if (!Config.ENABLE_MOD.get()) return;

        Explosion explosion = event.getExplosion();
        Entity exploder = explosion.getDirectSourceEntity();
        boolean log = Config.LOG_ENABLED.get();

        // 记录爆炸源信息（仅当日志启用）
        if (log) {
            if (exploder == null) {
                LOGGER.info("Explosion detected - no direct source entity. explosion class: {}",
                        explosion.getClass().getCanonicalName());
            } else {
                LOGGER.info("Explosion detected - explosion class: {}, exploder: {}, class: {}, descriptionId: {}",
                        explosion.getClass().getCanonicalName(),
                        exploder,
                        exploder.getClass().getCanonicalName(),
                        exploder.getType().getDescriptionId());
            }
        }

        boolean shouldClearEntities = false;
        boolean shouldClearBlocks = false;

        if (exploder == null) {
            shouldClearEntities = Config.CLEAR_ENTITIES_WHEN_EXPLODER_NULL.get();
            shouldClearBlocks = Config.CLEAR_BLOCKS_WHEN_EXPLODER_NULL.get();
            if (log && (shouldClearEntities || shouldClearBlocks)) {
                LOGGER.info("Exploder is null -> clear entities: {}, clear blocks: {}",
                        shouldClearEntities, shouldClearBlocks);
            }
        } else {
            String descriptionId = exploder.getType().getDescriptionId();
            String className = exploder.getClass().getCanonicalName();

            // 判断实体清除
            if (isMatching(descriptionId, Config.CLEAR_ENTITIES_BY_DESCRIPTOR_ID.get(),
                    Config.EXEMPT_ENTITIES_BY_DESCRIPTOR_ID.get()) ||
                isMatching(className, Config.CLEAR_ENTITIES_BY_CLASS_NAME.get(),
                        Config.EXEMPT_ENTITIES_BY_CLASS_NAME.get())) {
                shouldClearEntities = true;
                if (log) {
                    LOGGER.info("Entity clearing triggered for exploder {} (desc={}, class={})",
                            exploder, descriptionId, className);
                }
            }

            // 判断方块清除
            if (isMatching(descriptionId, Config.CLEAR_BLOCKS_BY_DESCRIPTOR_ID.get(),
                    Config.EXEMPT_BLOCKS_BY_DESCRIPTOR_ID.get()) ||
                isMatching(className, Config.CLEAR_BLOCKS_BY_CLASS_NAME.get(),
                        Config.EXEMPT_BLOCKS_BY_CLASS_NAME.get())) {
                shouldClearBlocks = true;
                if (log) {
                    LOGGER.info("Block clearing triggered for exploder {} (desc={}, class={})",
                            exploder, descriptionId, className);
                }
            }

            if (log && !shouldClearEntities && !shouldClearBlocks) {
                LOGGER.info("No clearing rule matched for exploder {} -> no action", exploder);
            }
        }

        // 执行清除并记录结果
        if (shouldClearBlocks) {
            try {
                int blockCount = event.getAffectedBlocks().size();
                event.getAffectedBlocks().clear();
                if (log) LOGGER.info("Cleared {} affected blocks", blockCount);
            } catch (Exception e) {
                LOGGER.error("Failed to clear affected blocks", e);
            }
        }
        if (shouldClearEntities) {
            try {
                int entityCount = event.getAffectedEntities().size();
                event.getAffectedEntities().clear();
                if (log) LOGGER.info("Cleared {} affected entities", entityCount);
            } catch (Exception e) {
                LOGGER.error("Failed to clear affected entities", e);
            }
        }
    }

    // 不可能测试的时候弄几千个正则然后不重启游戏吧
    private static final Map<String, Pattern> PATTERN_CACHE = new ConcurrentHashMap<>();

    private boolean matchesAny(String input, List<? extends String> patterns) {
        if (input == null || patterns == null) return false;
        for (String regex : patterns) {
            Pattern pattern = PATTERN_CACHE.computeIfAbsent(regex, Pattern::compile);
            if (pattern.matcher(input).matches()) return true;
        }
        return false;
    }

    private boolean isMatching(String input, List<? extends String> blacklist, List<? extends String> whitelist) {
        // 白名单优先
        if (matchesAny(input, whitelist)) return false;
        return matchesAny(input, blacklist);
    }
}
