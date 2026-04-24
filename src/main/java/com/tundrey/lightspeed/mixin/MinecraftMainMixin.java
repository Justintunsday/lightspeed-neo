package com.tundrey.lightspeed.mixin;

import com.tundrey.lightspeed.cache.GlobalCache;
import com.tundrey.lightspeed.util.CacheUtil;
import net.minecraft.client.main.Main;
import org.apache.commons.io.FilenameUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.io.File;
import java.util.Map;

import static com.tundrey.lightspeed.util.CacheUtil.HAS_RESOURCE_CACHE_DIR;
import static com.tundrey.lightspeed.util.CacheUtil.NAMESPACE_CACHE_DIR;

@Mixin(Main.class)
public class MinecraftMainMixin {

    @Inject(method = "main", at = @At(value = "INVOKE", target = "Lnet/minecraft/SharedConstants;tryDetectVersion()V", shift = At.Shift.AFTER))
    private static void mainTryDetecVersionInjected(String[] args, CallbackInfo ci) {
        GlobalCache.EXECUTOR.execute(() -> {
            lightspeed$loadPersistedCaches(HAS_RESOURCE_CACHE_DIR, GlobalCache.PERSISTED_EXISTENCES_BY_MOD);
            lightspeed$loadPersistedCaches(NAMESPACE_CACHE_DIR, GlobalCache.PERSISTED_NAMESPACES_BY_MOD);
        });
    }

    @Unique
    private static <K, V> void lightspeed$loadPersistedCaches(File dir, Map<String, Map<K, V>> targetMap) {
        CacheUtil.getCacheFiles(dir).forEach(file -> {
            String name = FilenameUtils.getBaseName(file.getName());
            targetMap.put(name, CacheUtil.load(file));
        });
    }
}