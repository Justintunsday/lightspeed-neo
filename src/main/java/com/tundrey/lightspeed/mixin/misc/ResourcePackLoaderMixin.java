package com.tundrey.lightspeed.mixin.misc;

import com.tundrey.lightspeed.cache.GlobalCache;
import com.tundrey.lightspeed.interfaces.IPathResourcePack;
import net.minecraft.server.packs.PathPackResources;
import net.minecraft.server.packs.PackLocationInfo;
import net.minecraft.server.packs.PackSelectionConfig;
import net.minecraft.server.packs.repository.PackSource;
import net.minecraft.network.chat.Component;
import net.neoforged.neoforge.resource.ResourcePackLoader;
import net.neoforged.neoforgespi.language.IModFileInfo;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;

@Mixin(ResourcePackLoader.class)
public abstract class ResourcePackLoaderMixin {

    @Inject(method = "createPackForMod", at = @At("HEAD"), remap = false, cancellable = true)
    private static void createPackForModHeadInjected(IModFileInfo mf, CallbackInfoReturnable<PathPackResources> cir) {
        if (!GlobalCache.isEnabled)
            return;

        PackLocationInfo locationInfo = new PackLocationInfo(
                mf.getFile().getFileName(),
                Component.literal(mf.getFile().getFileName()),
                PackSource.BUILT_IN,
                Optional.empty()
        );

        PathPackResources resourcePack = new PathPackResources(locationInfo, mf.getFile().getFilePath());
        ((IPathResourcePack) resourcePack).lightspeed$setModFile(mf.getFile());
        cir.setReturnValue(resourcePack);
    }
}