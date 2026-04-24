package com.tundrey.lightspeed.mixin.misc;

import com.tundrey.lightspeed.cache.GlobalCache;
import com.tundrey.lightspeed.interfaces.ICache;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.Property;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;
import java.util.function.Function;

@SuppressWarnings({"rawtypes", "unchecked"})
@Mixin(StateDefinition.class)
public class StateDefinitonMixin implements ICache {

    @Unique
    private Map<String, Property<?>> lightspeed$propsByName;

    @Inject(method = "<init>", at = @At("RETURN"))
    public void initReturnInjected(Function p_61052_, Object p_61053_, StateDefinition.Factory p_61054_, Map p_61055_, CallbackInfo ci) {
        if (GlobalCache.isEnabled)
            GlobalCache.add(this);
        lightspeed$propsByName = p_61055_;
    }

    @Override
    public void lightspeed$persistAndClearCache() {
        if (lightspeed$propsByName != null) {
            lightspeed$propsByName = null;
        }
    }
}