package dev.jab125.minimega.grf.minecraft.mixin;

import dev.jab125.minimega.grf.minecraft.event.Events;
import net.minecraft.core.Holder;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.gameevent.GameEventDispatcher;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameEventDispatcher.class)
public class GameEventDispatcherMixin {
	@Inject(method = "post", at = @At("HEAD"))
	void post(Holder<GameEvent> holder, Vec3 vec3, GameEvent.Context context, CallbackInfo ci) {
		Events.GAME_EVENT.invoker().call(holder, vec3, context);
	}
}
