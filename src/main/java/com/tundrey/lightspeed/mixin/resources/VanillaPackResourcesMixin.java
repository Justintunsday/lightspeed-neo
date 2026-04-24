package com.tundrey.lightspeed.mixin.resources;

import com.tundrey.lightspeed.cache.GlobalCache;
import com.tundrey.lightspeed.interfaces.IPackResources;
import com.tundrey.lightspeed.util.CacheUtil;
import com.google.common.collect.Maps;
import net.minecraft.SharedConstants;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.VanillaPackResources;
import net.minecraft.server.packs.resources.IoSupplier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.io.File;
import java.io.InputStream;
import java.util.Map;

import static com.tundrey.lightspeed.util.CacheUtil.HAS_RESOURCE_CACHE_DIR;

@Mixin(VanillaPackResources.class)
public abstract class VanillaPackResourcesMixin implements IPackResources {

    @Unique
    private Map<String, Boolean> lightspeed$existencePerClientResource = Maps.newConcurrentMap();
    @Unique
    private Map<String, Boolean> lightspeed$existencePerServerResource = Maps.newConcurrentMap();
    @Unique
    private String lightspeed$versionId;

    @Inject(method = "getResource", at = @At("RETURN"))
    public void getResourceReturnInjected(PackType packType, ResourceLocation location, CallbackInfoReturnable<IoSupplier<InputStream>> cir) {
        if (!GlobalCache.isEnabled)
            return;

        boolean actuallyExists = cir.getReturnValue() != null;
        lightspeed$cacheExists(packType, location.toString(), actuallyExists);
    }

    @Unique
    public void lightspeed$cacheExists(PackType packType, String resourceName, boolean exists) {
        if (packType == PackType.CLIENT_RESOURCES)
            lightspeed$existencePerClientResource.put(resourceName, exists);
        else
            lightspeed$existencePerServerResource.put(resourceName, exists);
    }

    @Override
    public void lightspeed$persistAndClearCache() {
        if (lightspeed$versionId == null) {
            lightspeed$versionId = SharedConstants.getCurrentVersion().getId();
        }
        if (lightspeed$versionId != null) {
            CacheUtil.persist(lightspeed$existencePerClientResource, new File(HAS_RESOURCE_CACHE_DIR.getPath(), lightspeed$versionId + "-client.ser"));
            CacheUtil.persist(lightspeed$existencePerServerResource, new File(HAS_RESOURCE_CACHE_DIR.getPath(), lightspeed$versionId + "-server.ser"));
        }
        lightspeed$existencePerClientResource.clear();
        lightspeed$existencePerServerResource.clear();
    }

    @Override
    public Map<String, Boolean> lightspeed$getExistenceByResource() { return Maps.newConcurrentMap(); }
    @Override
    public void lightspeed$setExistenceByResource(Map<String, Boolean> existenceByResource) {}
}