package com.tundrey.lightspeed.interfaces;

import net.minecraft.server.packs.PackResources;
import net.neoforged.neoforgespi.locating.IModFile; // 改为了 neoforgespi

public interface IPathResourcePack extends PackResources, IPackResources {
    void lightspeed$setModFile(IModFile modFile);
}