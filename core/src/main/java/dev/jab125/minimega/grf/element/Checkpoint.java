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

public final class Checkpoint extends Element {
	public final int id, x0, x1, y0, y1, z0, z1;

	public Checkpoint(int id, int x0, int x1, int y0, int y1, int z0, int z1, List<Element> children) {
		super(children);
		this.id = id;
		this.x0 = x0;
		this.x1 = x1;
		this.y0 = y0;
		this.y1 = y1;
		this.z0 = z0;
		this.z1 = z1;
	}

	public Checkpoint(String id, String x0, String x1, String y0, String y1, String z0, String z1, List<Element> children) {
		this(parseInt(id), parseInt(x0), parseInt(x1), parseInt(y0), parseInt(y1), parseInt(z0), parseInt(z1), children);
	}

	private static int parseInt(String str) {
		if (str.endsWith(".0")) str = str.substring(0, str.length() - 2);
		return Integer.parseInt(str);
	}

	@Override
	public String getId() {
		return "Checkpoint";
	}

	static class Type implements ElementType<Checkpoint> {
		@Override
		public Checkpoint parseSelf(org.w3c.dom.Element element, List<Element> children) {
			return new Checkpoint(element.getAttribute("id"), element.getAttribute("x0"), element.getAttribute("x1"), element.getAttribute("y0"), element.getAttribute("y1"), element.getAttribute("z0"), element.getAttribute("z1"), children);
		}

		@Override
		public org.w3c.dom.Element serializeSelf(
				Document document,
				Checkpoint self,
				List<org.w3c.dom.Element> children
		) {
			org.w3c.dom.Element element = document.createElement(self.getId());

			element.setAttribute("id", Integer.toString(self.id));
			element.setAttribute("x0", Integer.toString(self.x0));
			element.setAttribute("x1", Integer.toString(self.x1));
			element.setAttribute("y0", Integer.toString(self.y0));
			element.setAttribute("y1", Integer.toString(self.y1));
			element.setAttribute("z0", Integer.toString(self.z0));
			element.setAttribute("z1", Integer.toString(self.z1));

			children.forEach(element::appendChild);

			return element;
		}
	}
}
