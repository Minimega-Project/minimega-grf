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

import dev.jab125.minimega.grf.GrfContainer;
import dev.jab125.minimega.grf.element.LevelRules;
import dev.jab125.minimega.grf.element.NamedArea;
import dev.jab125.minimega.grf.minecraft.event.Events;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.attachment.v1.AttachmentRegistry;
import net.fabricmc.fabric.api.attachment.v1.AttachmentType;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.phys.AABB;

import java.util.List;

public class ModInit implements ModInitializer {
	public static final AttachmentType<NamedArea> CURRENT_NAMED_AREA = AttachmentRegistry.create(ResourceLocation.parse("minimega_grf:cna"));
	@Override
	public void onInitialize() {

		ServerTickEvents.START_WORLD_TICK.register(serverLevel -> {
			if (((GrfContainer) serverLevel).getGrf().getFirstOf(LevelRules.class).isEmpty()) return;
			List<NamedArea> list = ((GrfContainer) serverLevel).getGrf().getLevelRules().streamOf(NamedArea.class).toList();
			for (ServerPlayer player : serverLevel.players()) {
				l:
				{
					NamedArea attached = player.getAttached(CURRENT_NAMED_AREA);
					for (NamedArea namedArea : list) {
						AABB aabb = new AABB(namedArea.x0, namedArea.y0, namedArea.z0, namedArea.x1, namedArea.y1, namedArea.z1);
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
	}
}
