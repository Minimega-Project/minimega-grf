package dev.jab125.minimega.grf.minecraft.mixin;

import dev.jab125.minimega.grf.GrfContainer;
import dev.jab125.minimega.grf.element.__ROOT__;
import dev.jab125.minimega.grf.minecraft.ModInit;
import dev.jab125.minimega.grf.minecraft.client.ModInitClient;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.RandomSequences;
import net.minecraft.world.level.CustomSpawner;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.dimension.LevelStem;
import net.minecraft.world.level.storage.LevelStorageSource;
import net.minecraft.world.level.storage.ServerLevelData;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.Executor;

@Mixin(ClientLevel.class)
public class ClientLevelMixin implements GrfContainer {
//	@Unique
//	private @Final @Mutable __ROOT__ grf;
//	@Inject(method = "<init>", at = @At("RETURN"))
//	void init(ClientPacketListener clientPacketListener, ClientLevel.ClientLevelData clientLevelData, ResourceKey resourceKey, Holder holder, int i, int j, LevelRenderer levelRenderer, boolean bl, long l, int k, CallbackInfo ci) throws IOException, ParserConfigurationException, SAXException {
//		grf = ModInit.getFrom(resourceKey, levelStorageAccess);
//	}

	@Override
	public __ROOT__ getGrf() {
		return ModInitClient.currentDimensionGrf;
	}
}
