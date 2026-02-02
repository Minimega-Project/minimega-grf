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
import dev.jab125.minimega.grf.element.AddItem;
import dev.jab125.minimega.grf.element.Element;
import dev.jab125.minimega.grf.element.PopulateContainer;
import dev.jab125.minimega.grf.element.__ROOT__;
import dev.jab125.minimega.grf.minecraft.ModInit;
import dev.jab125.minimega.grf.minecraft.event.Events;
import net.fabricmc.api.ModInitializer;
import net.minecraft.commands.arguments.item.ItemInput;
import net.minecraft.commands.arguments.item.ItemParser;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.AABB;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;

public class ModTesting implements ModInitializer {
	public static __ROOT__ root;

	@Override
	public void onInitialize() {
		try {
			root = (__ROOT__) Element.fromXML(ModInit.class.getResourceAsStream("/tutorial.xml"));
		} catch (ParserConfigurationException e) {
			throw new RuntimeException(e);
		} catch (IOException e) {
			throw new RuntimeException(e);
		} catch (SAXException e) {
			throw new RuntimeException(e);
		}

		Events.ENTERED_NAMED_AREA.register((player, area) -> {
			//System.out.println("EVENT CALLED");
			player.sendSystemMessage(Component.literal("Entered named area " + (area == null ? "null" : area.name) + "."));

			if (area == null) return;
			AABB aabb = new AABB(area.x0, area.y0, area.z0, area.x1, area.y1, area.z1);
			for (PopulateContainer populateContainer : ((GrfContainer) player.level()).getGrf().getLevelRules().flatStreamOf(PopulateContainer.class).toList()) {
				if (aabb.contains(populateContainer.x + 0.5, populateContainer.y + 0.5, populateContainer.z + 0.5)) {
					//	System.out.println("ITS INSIDE");
					BlockEntity blockEntity = player.level().getBlockEntity(new BlockPos(populateContainer.x, populateContainer.y, populateContainer.z));
					if (blockEntity instanceof Container container) {
						//		System.out.println("IT IS A CONTAINER");
						ItemParser itemParser = new ItemParser(player.registryAccess());
						for (AddItem addItem : populateContainer.getAddItems()) {
							//System.out.println(addItem);
							ItemParser.ItemResult parse = null;
							try {
								parse = itemParser.parse(new StringReader(addItem.itemId + "[" + addItem.dataTag + "]"));
							} catch (CommandSyntaxException e) {
								throw new RuntimeException(e);
							}
							try {
								//	System.out.println("Setting " + addItem.slot);
								ItemStack itemStack = new ItemInput(parse.item(), parse.components()).createItemStack(addItem.quantity, false);
								if (container.getContainerSize() > addItem.slot /*container.canPlaceItem(addItem.slot, itemStack)*/) {

									container.setItem(addItem.slot, itemStack);
								}
							} catch (CommandSyntaxException e) {
								throw new RuntimeException(e);
							}
						}
					}

				}
			}
		});

	}
}
