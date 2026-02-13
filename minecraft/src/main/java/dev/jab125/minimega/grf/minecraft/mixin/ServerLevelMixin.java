package dev.jab125.minimega.grf.minecraft.mixin;

import dev.jab125.minimega.grf.GrfContainer;
import dev.jab125.minimega.grf.element.__ROOT__;
import dev.jab125.minimega.grf.minecraft.ModInit;
import dev.jab125.minimega.grf.minecraft.ServerLevelExtension;
import dev.jab125.minimega.grf.minecraft.util.RootHolder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
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
import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.Executor;

@Mixin(ServerLevel.class)
public class ServerLevelMixin implements GrfContainer, ServerLevelExtension, RootHolder {
	@Unique
	private @Final @Mutable __ROOT__ grf;
	@Unique
	private @Final @Mutable Path grfPath;
	@Inject(method = "<init>", at = @At("RETURN"))
	void init(MinecraftServer minecraftServer, Executor executor, LevelStorageSource.LevelStorageAccess levelStorageAccess, ServerLevelData serverLevelData, ResourceKey<Level> resourceKey, LevelStem levelStem, boolean bl, long l, List<CustomSpawner> list, boolean bl2, RandomSequences randomSequences, CallbackInfo ci) throws IOException, ParserConfigurationException, SAXException {
		grf = ModInit.getFrom(resourceKey, levelStorageAccess);
		grfPath = levelStorageAccess.getDimensionPath(resourceKey).resolve("gamerulefile.xml");
	}

	@Override
	public __ROOT__ getGrf() {
		return grf;
	}

	@Override
	public __ROOT__ getRoot() {
		return grf;
	}

	@Override
	public void setRoot(__ROOT__ root) {
		this.grf = root;
		for (ServerPlayer player : ((ServerLevel) (Object) this).players()) {
			ModInit.sendGrfToPlayer(player, (ServerLevel) (Object) this);
		}
	}

	@Override
	public Path getGrfPath() {
		return grfPath;
	}
}
