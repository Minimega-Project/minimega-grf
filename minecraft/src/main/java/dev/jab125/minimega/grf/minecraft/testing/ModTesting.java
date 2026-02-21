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
package dev.jab125.minimega.grf.minecraft.testing;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import dev.jab125.minimega.grf.GrfContainer;
import dev.jab125.minimega.grf.actiondefinitions.ActionDefinitionUtils;
import dev.jab125.minimega.grf.actiondefinitions.ActionDefinitionsElementRegistry;
import dev.jab125.minimega.grf.actiondefinitions.ActionRuntime;
import dev.jab125.minimega.grf.actiondefinitions.Context;
import dev.jab125.minimega.grf.actiondefinitions.element.ActionDefinitions;
import dev.jab125.minimega.grf.actiondefinitions.element.Cancel;
import dev.jab125.minimega.grf.actiondefinitions.element.Effect;
import dev.jab125.minimega.grf.actiondefinitions.element.Effects;
import dev.jab125.minimega.grf.actiondefinitions.element.EnteredNamedArea;
import dev.jab125.minimega.grf.actiondefinitions.element.ITrigger;
import dev.jab125.minimega.grf.actiondefinitions.element.Proceed;
import dev.jab125.minimega.grf.element.AddItem;
import dev.jab125.minimega.grf.element.Element;
import dev.jab125.minimega.grf.element.NamedArea;
import dev.jab125.minimega.grf.element.PopulateContainer;
import dev.jab125.minimega.grf.element.__ROOT__;
import dev.jab125.minimega.grf.minecraft.ModInit;
import dev.jab125.minimega.grf.minecraft.event.Events;
import dev.jab125.minimega.grf.minecraft.networking.DialogPayload;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.message.v1.ServerMessageEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.arguments.item.ItemInput;
import net.minecraft.commands.arguments.item.ItemParser;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.AABB;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static dev.jab125.minimega.grf.minecraft.ModInit.fromNamedArea;

public class ModTesting implements ModInitializer {
	//public static __ROOT__ root;

	public static ActionDefinitions actionDefinitions;
	public static Runtime runtime;
	@Override
	public void onInitialize() {
		runtime = new Runtime();
		//if (true) return;
//		try {
//			root = (__ROOT__) Element.fromXML(ModInit.class.getResourceAsStream("/tutorial.xml"));
//		} catch (ParserConfigurationException e) {
//			throw new RuntimeException(e);
//		} catch (IOException e) {
//			throw new RuntimeException(e);
//		} catch (SAXException e) {
//			throw new RuntimeException(e);
//		}

//		Events.ENTERED_NAMED_AREA.register((player, area) -> {
//			player.sendSystemMessage(Component.literal("Entered named area " + (area == null ? "null" : area.name)), true);
//
//			poplulateContainersInsideOfNamedArea(player, area);
//		});


		try {
			actionDefinitions = (ActionDefinitions) Element.fromXML(ModTesting.class.getResourceAsStream("/testing.xml"), ActionDefinitionsElementRegistry.REGISTRY);
		} catch (ParserConfigurationException e) {
			throw new RuntimeException(e);
		} catch (IOException e) {
			throw new RuntimeException(e);
		} catch (SAXException e) {
			throw new RuntimeException(e);
		}

		Events.ENTERED_NAMED_AREA.register((player, area) -> {
			List<EnteredNamedArea> list = actionDefinitions.getOnActions().streamOf(EnteredNamedArea.class).toList();
			for (EnteredNamedArea enteredNamedArea : list) {
				var context = new Context() {
					private final Object identity = enteredNamedArea;

					@Override
					public void runAsync(ActionRuntime runtime) {
						ModTesting.runtime.addRuntime(runtime);
					}

					@Override
					public NamedArea currentNamedArea() {
						return area;
					}

					@Override
					public NamedArea previousNamedArea() {
						return null;
					}

					@Override
					public void log(String text) {
						player.sendSystemMessage(Component.literal(text));
					}

					@Override
					public void populateAllContainersInsideOfNamedArea() {
						ModTesting.poplulateContainersInsideOfNamedArea(player, area);
					}

					@Override
					public void replaceDialogWith(String dialog) {
						if (ServerPlayNetworking.canSend(player, DialogPayload.TYPE)) {
							ServerPlayNetworking.send(player, new DialogPayload(dialog == null ? null : Component.literal(dialog), false));
						} else {
							current = current == ChatFormatting.DARK_GREEN ? ChatFormatting.BLUE : ChatFormatting.DARK_GREEN;
							player.sendSystemMessage(Component.literal(dialog).withStyle(current));
						}
					}

					@Override
					public void appendDialog(String text) {
						if (ServerPlayNetworking.canSend(player, DialogPayload.TYPE)) {
							ServerPlayNetworking.send(player, new DialogPayload(Component.literal(text), true));
						} else {
							player.sendSystemMessage(Component.literal(text).withStyle(current));
						}
					}

					//private ChatFormatting[] formattings = {ChatFormatting.DARK_GREEN, ChatFormatting.BLUE};
					private ChatFormatting current = ChatFormatting.DARK_GREEN;

					@Override
					public void hardcodedStatusMessage() {
						player.sendSystemMessage(Component.literal("Entered named area " + (area == null ? "null" : area.name)), true);
					}

					@Override
					public boolean notRunningAlready() {
						return ModTesting.runtime.runtimes.stream().noneMatch(a -> a.getIdentity() == identity);
					}

					@Override
					public Object getIdentity() {
						return identity;
					}

					@Override
					public Object getOwner() {
						return player;
					}
				};
				if (ActionDefinitionUtils.evaluateTrigger((ITrigger) enteredNamedArea.getTrigger().iterator().next(), context)) {
					Effects effects = enteredNamedArea.getEffects();
					List<Effect> effectsAsList = (List<Effect>) effects.streamOf((Class) Effect.class).toList();
					ActionRuntime actionRuntime = new ActionRuntime(effectsAsList, context);
					ModTesting.runtime.addRuntime(actionRuntime);
				}
			}
		});
		ServerTickEvents.START_SERVER_TICK.register(server -> {
			runtime.tick(server);
		});
		ServerMessageEvents.ALLOW_CHAT_MESSAGE.register((playerChatMessage, serverPlayer, bound) -> {
			String content = playerChatMessage.signedBody().content();
			return !runtime.userInput(content, serverPlayer);
		});
	}

	private static void poplulateContainersInsideOfNamedArea(ServerPlayer player, NamedArea area) {
		if (area == null) return;
		AABB aabb = fromNamedArea(area);
		ServerLevel level = player.level();
		((GrfContainer) level).getGrf().getLevelRules().flatStreamOf(PopulateContainer.class).filter(populateContainer -> aabb.contains(populateContainer.x + 0.5, populateContainer.y + 0.5, populateContainer.z + 0.5)).forEach(populateContainer -> {
					BlockEntity blockEntity = level.getBlockEntity(new BlockPos(populateContainer.x, populateContainer.y, populateContainer.z));
					if (blockEntity instanceof Container container) {
						container.clearContent();
						ItemParser itemParser = new ItemParser(player.registryAccess());
						for (AddItem addItem : populateContainer.getAddItems()) {
							ItemParser.ItemResult parse;
							try {
								parse = itemParser.parse(new StringReader(addItem.itemId + "[" + addItem.dataTag + "]"));
							} catch (CommandSyntaxException e) {
								throw new RuntimeException(e);
							}
							ItemStack itemStack;
							try {
								itemStack = new ItemInput(parse.item(), parse.components()).createItemStack(addItem.quantity, false);
							} catch (CommandSyntaxException e) {
								throw new RuntimeException(e);
							}
							if (container.getContainerSize() > addItem.slot && addItem.slot >= 0) {
								container.setItem(addItem.slot, itemStack);
							}
						}
					}
				});
	}

	public static class Runtime {
		List<ActionRuntime> runtimes = new ArrayList<>();

		public void addRuntime(ActionRuntime runtime) {
			this.runtimes.add(runtime);
		}

		public void tick(MinecraftServer server) {
			List<ActionRuntime> toRemove = new ArrayList<>();
			for (ActionRuntime actionRuntime : List.copyOf(runtimes)) {
				boolean stillMore = actionRuntime.evaluate();
				if (!stillMore) toRemove.add(actionRuntime);
			}
			runtimes.removeAll(toRemove);
		}

		public boolean userInput(String content, ServerPlayer player) {
			boolean absorb = false;
			for (ActionRuntime runtime : List.copyOf(runtimes)) {
				if (runtime.getOwner() == player && runtime.isAwaitingUserInput()) {
					Optional<Proceed> proceed = runtime.getProceedCancel().getFirstOf(Proceed.class);
					Optional<Cancel> cancel = runtime.getProceedCancel().getFirstOf(Cancel.class);
					labe:
					{
						if ("y".equals(content)) {
							proceed.ifPresent(obj -> runtime.addAfterCursor(obj.streamOf((Class) Effect.class).toList()));
							absorb = true;
							break labe;
						}
						if ("n".equals(content)) {
							cancel.ifPresent(obj -> runtime.addAfterCursor(obj.streamOf((Class) Effect.class).toList()));
							absorb = true;
							break labe;
						}
						continue;
					}
					runtime.resume();
				}
			}
			return absorb;
		}
	}
}
