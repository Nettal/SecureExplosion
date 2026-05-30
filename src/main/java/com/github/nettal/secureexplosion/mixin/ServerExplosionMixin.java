package com.github.nettal.secureexplosion.mixin;

import com.github.nettal.secureexplosion.Config;
import com.mojang.logging.LogUtils;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.ServerExplosion;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;

@Mixin(ServerExplosion.class)
public abstract class ServerExplosionMixin {
    private static final Logger LOGGER = LogUtils.getLogger();

    private void info(boolean log, String format, Object... arguments) {
        if (log) {
            LOGGER.info(format, arguments);
        }
    }

    @Shadow
    @Nullable
    public abstract Entity getDirectSourceEntity();

    private Protections computeProtections() {
        if (!Config.ENABLE_MOD.get()) new Protections(false, false);

        Entity exploder = getDirectSourceEntity();
        boolean log = Config.LOG_ENABLED.get();

        // 记录爆炸源信息（仅当日志启用）
        if (exploder == null) {
            info(log, "Explosion detected - no direct source entity. explosion class: {}",
                    getClass().getCanonicalName());
        } else {
            info(log, "Explosion detected - explosion class: {}, exploder: {}, class: {}, descriptionId: {}",
                    getClass().getCanonicalName(),
                    exploder,
                    exploder.getClass().getCanonicalName(),
                    exploder.getType().getDescriptionId());
        }

        boolean shouldClearEntities = false;
        boolean shouldClearBlocks = false;

        if (exploder == null) {
            shouldClearEntities = Config.CLEAR_ENTITIES_WHEN_EXPLODER_NULL.get();
            shouldClearBlocks = Config.CLEAR_BLOCKS_WHEN_EXPLODER_NULL.get();
            if (shouldClearEntities || shouldClearBlocks) {
                info(log, "Exploder is null -> clear entities: {}, clear blocks: {}",
                        shouldClearEntities, shouldClearBlocks);
            }
        } else {
            String descriptionId = exploder.getType().getDescriptionId();
            String className = exploder.getClass().getCanonicalName();

            // 判断实体清除
            if (matchesAny(descriptionId, Config.CLEAR_ENTITIES_BY_DESCRIPTOR_ID.get()) ||
                    matchesAny(className, Config.CLEAR_ENTITIES_BY_CLASS_NAME.get())) {
                shouldClearEntities =
                        !matchesAny(descriptionId, Config.EXEMPT_ENTITIES_BY_DESCRIPTOR_ID.get()) &&
                                !matchesAny(className, Config.EXEMPT_ENTITIES_BY_CLASS_NAME.get());
            }
            // 判断方块清除
            if (matchesAny(descriptionId, Config.CLEAR_BLOCKS_BY_DESCRIPTOR_ID.get()) ||
                    matchesAny(className, Config.CLEAR_BLOCKS_BY_CLASS_NAME.get())) {
                shouldClearBlocks =
                        !matchesAny(descriptionId, Config.EXEMPT_BLOCKS_BY_DESCRIPTOR_ID.get()) &&
                                !matchesAny(className, Config.EXEMPT_BLOCKS_BY_CLASS_NAME.get());
            }
            if (shouldClearEntities) {
                info(log, "Entity clearing triggered for exploder {} (desc={}, class={})",
                        exploder, descriptionId, className);
            }
            if (shouldClearBlocks) {
                info(log, "Block clearing triggered for exploder {} (desc={}, class={})",
                        exploder, descriptionId, className);
            }
            if (!shouldClearEntities && !shouldClearBlocks) {
                info(log, "No clearing rule matched for exploder {} -> no action", exploder);
            }
        }
        return new Protections(shouldClearEntities, shouldClearBlocks);
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

    private record Protections(boolean entities, boolean blocks) {
    }

    private Protections protections = null;

    boolean shouldProtectEntities() {
        if (protections == null) {
            protections = computeProtections();
        }
        return protections.entities;
    }

    boolean shouldProtectBlocks() {
        if (protections == null) {
            protections = computeProtections();
        }
        return protections.blocks;
    }

    @Inject(at = @At("HEAD"), method = "hurtEntities", cancellable = true)
    private void before_hurtEntities(CallbackInfo info) {
        if (shouldProtectEntities()) {
            info.cancel();
        }
    }

    // slow way, calc blocks
//    @Inject(at = @At("HEAD"), method = "createFire", cancellable = true)
//    private void before_createFire(CallbackInfo info) {
//        if (shouldProtectBlocks()) {
//            info.cancel();
//        }
//    }
//
//    @Inject(at = @At("HEAD"), method = "interactWithBlocks", cancellable = true)
//    private void before_interactWithBlocks(CallbackInfo info) {
//        if (shouldProtectBlocks()) {
//            info.cancel();
//        }
//    }

    // fast way, not calc blocks
    @Inject(at = @At("HEAD"), method = "calculateExplodedPositions", cancellable = true)
    private void before_calculateExplodedPositions(CallbackInfoReturnable<List<BlockPos>> info) {
        if (shouldProtectBlocks()) {
            info.setReturnValue(new ObjectArrayList<>());
        }
    }
}