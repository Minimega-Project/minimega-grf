/*
 * Copyright (C) 2026 Jab125. All rights reserved unless explicitly stated.
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
 * ADDITIONAL PERMISSION — CONDITIONAL MINECRAFT LINKING
 *
 * As a special additional permission under section 7 of the GNU General
 * Public License, this program may be used, combined, or distributed as
 * part of a larger work that includes the "minimega-grf:minecraft"
 * subproject, provided that the larger work is distributed under the
 * terms of the GNU General Public License version 3 or later together
 * with the "Minecraft Linking Exception" granted by that subproject.
 *
 * This additional permission applies only when this program is used as
 * part of the Minecraft subproject described above. When used, combined,
 * or distributed independently of that subproject, this program is
 * licensed solely under the GNU General Public License, version 3 or
 * later, without any additional exceptions.
 */
package dev.jab125.minimega.grf.element;

import org.w3c.dom.Document;

import java.util.List;

public final class CustomBeacon extends Element {
	public final Direction beamDirection;
	public final int beamLength, spawnX, spawnY, spawnZ;

	public CustomBeacon(Direction beamDirection, int beamLength, int spawnX, int spawnY, int spawnZ, List<Element> children) {
		super(children);
		this.beamDirection = beamDirection;
		this.beamLength = beamLength;
		this.spawnX = spawnX;
		this.spawnY = spawnY;
		this.spawnZ = spawnZ;
	}

	public CustomBeacon(String beamDirection, String beamLength, String spawnX, String spawnY, String spawnZ, List<Element> children) {
		this(parseDirection(beamDirection), Integer.parseInt(beamLength), Integer.parseInt(spawnX), Integer.parseInt(spawnY), Integer.parseInt(spawnZ), children);
	}

	private static Direction parseDirection(String direction) {
		return switch (direction) {
			case "plus_y" -> Direction.UP;
			case "minus_y" -> Direction.DOWN;
			case "plus_x" -> Direction.EAST;
			case "minus_x" -> Direction.WEST;
			case "plus_z" -> Direction.SOUTH;
			case "minus_z" -> Direction.NORTH;
			default -> throw new IllegalStateException("Unexpected value: " + direction);
		};
	}

	private static String unparseDirection(Direction direction) {
		return switch (direction) {
			case Direction.UP -> "plus_y";
			case Direction.DOWN -> "minus_y";
			case Direction.EAST -> "plus_x";
			case Direction.WEST -> "minus_x";
			case Direction.SOUTH -> "plus_z";
			case Direction.NORTH -> "minus_z";
		};
	}

	@Override
	public String getId() {
		return "CustomBeacon";
	}

	static class Type implements ElementType<CustomBeacon> {
		@Override
		public CustomBeacon parseSelf(org.w3c.dom.Element element, List<Element> children) {
			return new CustomBeacon(element.getAttribute("beam_direction"), element.getAttribute("beam_length"), element.getAttribute("spawnX"), element.getAttribute("spawnY"), element.getAttribute("spawnZ"), children);
		}

		@Override
		public org.w3c.dom.Element serializeSelf(
				Document document,
				CustomBeacon self,
				List<org.w3c.dom.Element> children
		) {
			org.w3c.dom.Element element = document.createElement(self.getId());
			element.setAttribute("beam_direction", unparseDirection(self.beamDirection));
			element.setAttribute("beam_length", Integer.toString(self.beamLength));
			element.setAttribute("spawnX", Integer.toString(self.spawnX));
			element.setAttribute("spawnY", Integer.toString(self.spawnY));
			element.setAttribute("spawnZ", Integer.toString(self.spawnZ));
			children.forEach(element::appendChild);

			return element;
		}
	}
}
