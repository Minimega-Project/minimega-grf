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

public final class UpdatePlayer extends Element {
	public final double spawnX, spawnY, spawnZ, xRot, yRot;

	public UpdatePlayer(double spawnX, double spawnY, double spawnZ, double xRot, double yRot, List<Element> children) {
		super(children);
		this.spawnX = spawnX;
		this.spawnY = spawnY;
		this.spawnZ = spawnZ;
		this.xRot = xRot;
		this.yRot = yRot;
	}

	public UpdatePlayer(String spawnX, String spawnY, String spawnZ, String xRot, String yRot, List<Element> children) {
		this(parseInt(spawnX), parseInt(spawnY), parseInt(spawnZ), parseIntOrZero(xRot), parseInt(yRot), children);
	}

	private static double parseInt(String str) {
		return Double.parseDouble(str);
	}

	private static double parseIntOrZero(String str) {
		if (str.isBlank()) return 0;
		return parseInt(str);
	}

	@Override
	public String getId() {
		return "UpdatePlayer";
	}

	static class Type implements ElementType<UpdatePlayer> {
		@Override
		public UpdatePlayer parseSelf(org.w3c.dom.Element element, List<Element> children) {
			return new UpdatePlayer(element.getAttribute("spawnX"), element.getAttribute("spawnY"), element.getAttribute("spawnZ"), element.getAttribute("xRot"), element.getAttribute("yRot"), children);
		}

		@Override
		public org.w3c.dom.Element serializeSelf(
				Document document,
				UpdatePlayer self,
				List<org.w3c.dom.Element> children
		) {
			org.w3c.dom.Element element = document.createElement(self.getId());

			element.setAttribute("spawnX", Double.toString(self.spawnX));
			element.setAttribute("spawnY", Double.toString(self.spawnY));
			element.setAttribute("spawnZ", Double.toString(self.spawnZ));
			element.setAttribute("xRot", Double.toString(self.xRot));
			element.setAttribute("yRot", Double.toString(self.yRot));

			children.forEach(element::appendChild);

			return element;
		}
	}
}
