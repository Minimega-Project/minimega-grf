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
package dev.jab125.minimega.grf.minecraft.client;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import dev.jab125.minimega.grf.GrfContainer;
import dev.jab125.minimega.grf.Json2XmlConverter;
import dev.jab125.minimega.grf.element.Element;
import dev.jab125.minimega.grf.element.ElementType;
import dev.jab125.minimega.grf.element.LevelRules;
import dev.jab125.minimega.grf.element.NamedArea;
import dev.jab125.minimega.grf.element.__ROOT__;
import dev.jab125.minimega.grf.minecraft.GRFStreamCodecs;
import dev.jab125.minimega.grf.minecraft.ServerLevelExtension;
import dev.jab125.minimega.grf.minecraft.networking.GameRuleFilePayload;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.rendering.v1.RenderStateDataKey;
import net.fabricmc.fabric.api.client.rendering.v1.world.WorldRenderEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.debug.DebugScreenEntries;
//import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.ShapeRenderer;
import net.minecraft.client.renderer.debug.DebugRenderer;
import net.minecraft.gizmos.GizmoStyle;
import net.minecraft.gizmos.Gizmos;
import net.minecraft.gizmos.TextGizmo;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.w3c.dom.Document;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamWriter;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static dev.jab125.minimega.grf.minecraft.ModInit.fromNamedArea;

public class ModInitClient implements ClientModInitializer {
	public static __ROOT__ currentDimensionGrf;
	@Override
	public void onInitializeClient() {
		RenderStateDataKey<List<NamedArea>> namedAreasDataKey = RenderStateDataKey.create(() -> "Named Areas Data Key");
		WorldRenderEvents.BEFORE_DEBUG_RENDER.register(worldRenderContext -> {

			//Gizmos.cuboid()
		});
		WorldRenderEvents.END_EXTRACTION.register(context -> {
			//context.worldState().setData(namedAreasDataKey, ().orElseGet(List::of));
			__ROOT__ grf = ((GrfContainer) context.world()).getGrf();
			if (grf == null) return;
			for (NamedArea namedArea : grf.getLevelRules().streamOf(NamedArea.class).toList()) {
				AABB aabb = fromNamedArea(namedArea);
				Gizmos.cuboid(aabb, GizmoStyle.stroke(0xffffffff));
				Gizmos.billboardText(namedArea.name, aabb.getCenter(), TextGizmo.Style.whiteAndCentered());
			}
		});

		ClientPlayNetworking.registerGlobalReceiver(GameRuleFilePayload.TYPE, (payload, context) -> {
			currentDimensionGrf = payload.root().orElse(null);
		});


//		WorldRenderEvents.BEFORE_DEBUG_RENDER.register(context -> {
//			if (!Minecraft.getInstance().debugEntries.isCurrentlyEnabled(DebugScreenEntries.ENTITY_HITBOXES)) return;
//			List<NamedArea> data = context.worldState().getData(namedAreasDataKey);
//			if (data == null) return;
//			for (NamedArea namedArea : data) {
//				AABB aabb = new AABB(namedArea.x0, namedArea.y0, namedArea.z0, namedArea.x1, namedArea.y1, namedArea.z1);
//				Vec3 center = aabb.getCenter();
//
//				DebugRenderer.renderFloatingText(context.matrices(), context.consumers(), namedArea.name, center.x, center.y, center.z, 0xffffffff);
//				ShapeRenderer.renderLineBox(context.matrices().last(), context.consumers().getBuffer(RenderType.lines()), aabb.move(context.gameRenderer().getMainCamera().position().multiply(-1, -1, -1)), 1, 1, 1, 1);
//			}
//		});
	}
}
