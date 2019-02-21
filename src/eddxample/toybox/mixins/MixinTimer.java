package eddxample.toybox.mixins;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import eddxample.toybox.commands.TPS;
import net.minecraft.util.Timer;

@Mixin(Timer.class)
public class MixinTimer
{
	@Shadow
	public float tickLength;

	@Inject(at = @At("INVOKE"), method = "updateTimer")
	public void syncTimer(CallbackInfo ci)
	{
		tickLength = 1000.0F / TPS.clientTPS;
	}
}
