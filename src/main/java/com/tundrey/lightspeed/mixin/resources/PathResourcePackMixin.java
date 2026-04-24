package com.tundrey.lightspeed.mixin.resources;

import com.tundrey.lightspeed.cache.GlobalCache;
import com.tundrey.lightspeed.interfaces.IPackResources;
import com.tundrey.lightspeed.interfaces.IPathResourcePack;
import com.google.common.collect.Maps;
import net.minecraft.server.packs.PackLocationInfo;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.PathPackResources;
import net.neoforged.neoforgespi.locating.IModFile;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.nio.file.Path;
import java.util.*;

@Mixin(PathPackResources.class)
public abstract class PathResourcePackMixin implements IPathResourcePack, IPackResources {

    @Shadow @Final private static Logger LOGGER;

    @Unique
    private IModFile lightspeed$modFile;
    @Unique
    private String lightspeed$id;

    @Unique
    private final Map<String, Path> lightspeed$resolvedPathByResource = Maps.newConcurrentMap();
    @Unique
    private final Map<PackType, Set<String>> lightspeed$namespacesByPackType = Maps.newConcurrentMap();

    @Inject(method = "<init>", at = @At("RETURN"))
    public void initReturnInjected(PackLocationInfo locationInfo, Path path, CallbackInfo ci) {
        if (GlobalCache.isEnabled)
            GlobalCache.add(this);
    }

    @Override
    public void lightspeed$setModFile(IModFile modFile) {
        this.lightspeed$modFile = modFile;
        this.lightspeed$id = modFile.getModFileInfo().moduleName() + modFile.getModFileInfo().versionString()
                + "-" + FilenameUtils.getBaseName(modFile.getFilePath().toString()).replaceAll("[^a-zA-Z0-9.-]", "");
        lightspeed$setExistenceByResource(GlobalCache.PERSISTED_EXISTENCES_BY_MOD.computeIfAbsent(
                lightspeed$id, i -> Maps.newConcurrentMap()));
    }

    @Override
    public void lightspeed$persistAndClearCache() {
        lightspeed$resolvedPathByResource.clear();
        lightspeed$namespacesByPackType.clear();
    }

    @Unique
    public Path lightspeed$getResolvedPath(String... paths) {
        return lightspeed$resolvedPathByResource.get(Arrays.toString(paths));
    }
}