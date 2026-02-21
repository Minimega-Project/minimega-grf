/*
 * Copyright (C) 2026 Jab125, All rights reserved unless explicitly stated.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 *
 * --------------------------------------------------------------------
 *
 * "MINECRAFT" LINKING EXCEPTION TO THE GPL
 *
 * Linking this mod statically or dynamically with other modules is making a
 * combined work based on this mod. Thus, the terms and conditions of the
 * GNU General Public License cover the whole combination.
 *
 * In addition, as a special exception, the copyright holders of this mod
 * give you permission to combine this mod with free software programs or
 * libraries that are released under the GNU LGPL and with code included
 * in the standard release of Minecraft under All Rights Reserved (or
 * modified versions of such code, with unchanged license).
 *
 * You may copy and distribute such a system following the terms of the
 * GNU General Public License for this mod and the licenses of the other
 * code concerned.
 *
 * Note that people who make modified versions of this mod are not obligated
 * to grant this special exception for their modified versions; it is their
 * choice whether to do so. The GNU General Public License gives permission
 * to release a modified version without this exception; this exception also
 * makes it possible to release a modified version which carries forward
 * this exception.
 */
package dev.jab125.minimega.grf.minecraft;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import dev.jab125.minimega.grf.GrfContainer;
import dev.jab125.minimega.grf.Json2XmlConverter;
import dev.jab125.minimega.grf.element.Element;
import dev.jab125.minimega.grf.element.ElementType;
import dev.jab125.minimega.grf.element.LevelRules;
import dev.jab125.minimega.grf.element.NamedArea;
import dev.jab125.minimega.grf.element.__ROOT__;
import dev.jab125.minimega.grf.minecraft.event.Events;
import dev.jab125.minimega.grf.minecraft.networking.DialogPayload;
import dev.jab125.minimega.grf.minecraft.networking.GameRuleFilePayload;
import dev.jab125.minimega.grf.minecraft.util.NamedAreaUtils;
import dev.jab125.minimega.grf.minecraft.util.RootHolder;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.attachment.v1.AttachmentRegistry;
import net.fabricmc.fabric.api.attachment.v1.AttachmentType;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.entity.event.v1.ServerEntityWorldChangeEvents;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.arguments.coordinates.BlockPosArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.commands.FillCommand;
import net.minecraft.server.commands.TeleportCommand;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Relative;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.LevelStorageSource;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NonNull;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.EnumSet;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;

public class ModInit implements ModInitializer {
	public static final AttachmentType<NamedArea> CURRENT_NAMED_AREA = AttachmentRegistry.create(Identifier.parse("minimega_grf:cna"));
	@Override
	public void onInitialize() {
		PayloadTypeRegistry.playS2C().register(GameRuleFilePayload.TYPE, GameRuleFilePayload.STREAM_CODEC);
		PayloadTypeRegistry.playS2C().register(DialogPayload.TYPE, DialogPayload.STREAM_CODEC);
		ServerTickEvents.START_WORLD_TICK.register(serverLevel -> {
			__ROOT__ grf = ((GrfContainer) serverLevel).getGrf();
			if (grf == null) return;
			if (grf.getFirstOf(LevelRules.class).isEmpty()) return;
			List<NamedArea> list = ((GrfContainer) serverLevel).getGrf().getLevelRules().streamOf(NamedArea.class).toList();
			for (ServerPlayer player : serverLevel.players()) {
				l:
				{
					NamedArea attached = player.getAttached(CURRENT_NAMED_AREA);
					for (NamedArea namedArea : list) {
						AABB aabb = fromNamedArea(namedArea);
						if (aabb.intersects(player.getBoundingBox())) {
							//System.out.println("INTERSECTION");
							if (attached == null || !attached.name.equals(namedArea.name)) {
								//System.out.println("PLAYER ENTERED A ZONE");
								player.setAttached(CURRENT_NAMED_AREA, namedArea);
								Events.ENTERED_NAMED_AREA.invoker().call(player, namedArea);
							}
							break l;
						} else {
							//System.out.println(player.getBoundingBox() + "NO" + aabb);
						}
					}
					//System.out.println("PLAYER NOT IN A NAMED AREA");
					player.setAttached(CURRENT_NAMED_AREA, null);
					if (attached != null) Events.ENTERED_NAMED_AREA.invoker().call(player, null);
				}
			}

		});
		ServerEntityWorldChangeEvents.AFTER_PLAYER_CHANGE_WORLD.register((player, origin, destination) -> {
			sendGrfToPlayer(player, destination);
		});
		ServerPlayerEvents.JOIN.register(player -> {
			sendGrfToPlayer(player);
		});

		ServerLifecycleEvents.SERVER_STOPPED.register(server -> {
			for (ServerLevel level : server.getAllLevels()) {
				__ROOT__ grf = ((GrfContainer) level).getGrf();
				if (grf != null) {
					Path grfPath = ((ServerLevelExtension) level).getGrfPath();

					String id = grf.getId();
					DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
					DocumentBuilder builder = null;
					try {
						builder = factory.newDocumentBuilder();
					} catch (ParserConfigurationException e) {
						throw new RuntimeException(e);
					}


					// Create a new, empty Document object
					Document doc = builder.newDocument();
					String str = GRFStreamCodecs.toString(Element.GAME_RULE_FILE_REGISTRY.get(id).serialize(doc, grf));
					try {
						Files.writeString(grfPath, Json2XmlConverter.toPrettyString(str, 2));
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		});

		CommandRegistrationCallback.EVENT.register((dispatcher, context, selection) -> {
			LiteralArgumentBuilder<CommandSourceStack> grf = Commands.literal("namedarea").requires(Commands.hasPermission(Commands.LEVEL_GAMEMASTERS))
					.then(Commands.literal("add")
							.then(Commands.argument("from", BlockPosArgument.blockPos())
									.then(Commands.argument("to", BlockPosArgument.blockPos())
											.then(Commands.argument("name", StringArgumentType.word())
													.executes(namedAreaAddCommand(false))
													.then(Commands.argument("dataTag", IntegerArgumentType.integer())
															.executes(namedAreaAddCommand(true)))))))
					.then(Commands.literal("remove")
							.then(Commands.argument("name", StringArgumentType.word())
									.suggests((context2, builder) -> {
										__ROOT__ root = ((RootHolder) context2.getSource().getLevel()).getRoot();
										if (root == null) return Suggestions.empty();
										return SharedSuggestionProvider.suggest(root.getLevelRules().streamOf(NamedArea.class).map(a -> a.name), builder);
									})
									.executes(
											context1 -> {
												String string = StringArgumentType.getString(context1, "name");
												var result = switch (NamedAreaUtils.removeNamedArea((RootHolder) context1.getSource().getLevel(), string)) {
													case SUCCESS -> 0;
													case ERROR_DUPLICATE -> 1;
													case ERROR_NONE_FOUND -> 2;
												};
												if (result == 0) {
													context1.getSource().sendSuccess(() -> Component.literal("Removed named area \"%s\"".formatted(string)), true);
												} else if (result == 1) {
													context1.getSource().sendFailure(Component.literal("A named area with the name \"%s\" already exists!".formatted(string)));
												} else {
													context1.getSource().sendFailure(Component.literal("No named area with the name \"%s\" exists!".formatted(string)));
												}
												return result;
											})
							))
					.then(Commands.literal("list").executes(context1 -> {
						__ROOT__ root = ((RootHolder) context1.getSource().getLevel()).getRoot();
						int count = (int) root.getLevelRules().streamOf(NamedArea.class).count();
						String join = String.join(", ", root.getLevelRules().streamOf(NamedArea.class).map(a -> a.name).toList());
						context1.getSource().sendSuccess(() -> Component.literal("Found %s named areas:".formatted(count)), false);
						context1.getSource().sendSuccess(() -> Component.literal(join), false);
						return count;
					}))
					.then(Commands.literal("teleport")
							.then(Commands.argument("name", StringArgumentType.word())
									.suggests((context2, builder) -> {
										__ROOT__ root = ((RootHolder) context2.getSource().getLevel()).getRoot();
										if (root == null) return Suggestions.empty();
										return SharedSuggestionProvider.suggest(root.getLevelRules().streamOf(NamedArea.class).map(a -> a.name), builder);
									})
									.executes(context1 -> {
										String string = StringArgumentType.getString(context1, "name");
										__ROOT__ root = ((RootHolder) context1.getSource().getLevel()).getRoot();
										Optional<NamedArea> first = root.getLevelRules().streamOf(NamedArea.class).filter(a -> string.equals(a.name)).findFirst();
										if (first.isPresent()) {
											Vec3 center = fromNamedArea(first.get()).getCenter();
											Entity entityOrException = context1.getSource().getEntityOrException();
											entityOrException.teleportTo(context1.getSource().getLevel(), center.x, center.y, center.z, EnumSet.noneOf(Relative.class), entityOrException.getXRot(), entityOrException.getYRot(), true);
											context1.getSource().sendSuccess(() -> Component.translatable("commands.teleport.success.entity.single", entityOrException.getDisplayName(), "named area \"" + string + "\""), true);
											return 0;
										} else {
											context1.getSource().sendFailure(Component.literal("No named area with that name!"));
											return 1;
										}
									})));
			dispatcher.register(grf);
		});
	}

	private @NonNull Command<CommandSourceStack> namedAreaAddCommand(boolean hasDataTag) {
		return context1 -> {
			String string = StringArgumentType.getString(context1, "name");
			var result = switch (NamedAreaUtils.addNamedArea((RootHolder) context1.getSource().getLevel(), construct(context1, hasDataTag))) {
				case SUCCESS -> 0;
				case ERROR_DUPLICATE -> 1;
				case ERROR_NONE_FOUND -> 2;
			};
			if (result == 0) {
				context1.getSource().sendSuccess(() -> Component.literal("Created named area \"%s\"".formatted(string)), true);
			} else if (result == 1) {
				context1.getSource().sendFailure(Component.literal("A named area with the name \"%s\" already exists!".formatted(string)));
			} else {
				context1.getSource().sendFailure(Component.literal("No named area with the name \"%s\" exists!".formatted(string)));
			}
			return result;
		};
	}

	private NamedArea construct(CommandContext<CommandSourceStack> context, boolean hasDataTag) {
		BlockPos from = BlockPosArgument.getBlockPos(context, "from");
		BlockPos to = BlockPosArgument.getBlockPos(context, "to");
		String name = StringArgumentType.getString(context, "name");
		int dataTag = hasDataTag ? IntegerArgumentType.getInteger(context, "dataTag") : -1;
		return new NamedArea(name, dataTag, from.getX(), to.getX(), from.getY(), to.getY(), from.getZ(), to.getZ(), List.of());
	}

	public static void sendGrfToPlayer(ServerPlayer serverPlayer) {
		sendGrfToPlayer(serverPlayer, serverPlayer.level());
	}
	public static void sendGrfToPlayer(ServerPlayer serverPlayer, ServerLevel level) {
		if (ServerPlayNetworking.canSend(serverPlayer, GameRuleFilePayload.TYPE)) {
			serverPlayer.connection.send(ServerPlayNetworking.createS2CPacket(new GameRuleFilePayload(Optional.ofNullable((((GrfContainer) level).getGrf())))));
		}
	}

	public static @Nullable __ROOT__ getFrom(ResourceKey<Level> resourceKey, LevelStorageSource.LevelStorageAccess access) throws IOException, ParserConfigurationException, SAXException {
		Path dimensionPath = access.getDimensionPath(resourceKey);
		Path resolve = dimensionPath.resolve("gamerulefile.xml");
		if (Files.isRegularFile(resolve)) {
			InputStream inputStream = Files.newInputStream(resolve);
			return (__ROOT__) Element.fromXML(inputStream, Element.GAME_RULE_FILE_REGISTRY);
		}
		return null;
	}

	public static AABB fromNamedArea(NamedArea area) {
		int minX = Math.min(area.x0, area.x1);
		int maxX = Math.max(area.x0, area.x1);

		int minY = Math.min(area.y0, area.y1);
		int maxY = Math.max(area.y0, area.y1);

		int minZ = Math.min(area.z0, area.z1);
		int maxZ = Math.max(area.z0, area.z1);

		return new AABB(
				(double) minX,
				(double) minY,
				(double) minZ,
				(double) maxX + 1.0,
				(double) maxY + 1.0,
				(double) maxZ + 1.0
		);
	}
}
