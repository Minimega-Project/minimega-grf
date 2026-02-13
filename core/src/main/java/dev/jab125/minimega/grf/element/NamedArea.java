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

public final class NamedArea extends Element {
	public final int dataTag, x0, x1, y0, y1, z0, z1;
	public String name;

	public NamedArea(String name, int dataTag, int x0, int x1, int y0, int y1, int z0, int z1, List<Element> children) {
		super(children);
		this.name = name;
		this.dataTag = dataTag;
		this.x0 = x0;
		this.x1 = x1;
		this.y0 = y0;
		this.y1 = y1;
		this.z0 = z0;
		this.z1 = z1;
	}

	public NamedArea(String name, String dataTag, String x0, String x1, String y0, String y1, String z0, String z1, List<Element> children) {
		this(name, parseIntIfPresent(dataTag), parseInt(x0), parseInt(x1), parseIntOr0(y0), parseIntOr255(y1), parseInt(z0), parseInt(z1), children);
	}

	private static int parseIntOr255(String str) {
		if (str.isBlank()) return 255;
		return parseInt(str);
	}

	private static int parseIntOr0(String str) {
		if (str.isBlank()) return 0;
		return parseInt(str);
	}

	private static int parseIntIfPresent(String str) {
		if (str.isBlank()) return -1;
		return parseInt(str);
	}

	private static int parseInt(String str) {
		if (str.endsWith(".0")) str = str.substring(0, str.length() - 2);
		return Integer.parseInt(str);
	}

	@Override
	public String getId() {
		return "NamedArea";
	}

	static class Type implements ElementType<NamedArea> {
		@Override
		public NamedArea parseSelf(org.w3c.dom.Element element, List<Element> children) {
			return new NamedArea(element.getAttribute("name"), element.getAttribute("dataTag"), element.getAttribute("x0"), element.getAttribute("x1"), element.getAttribute("y0"), element.getAttribute("y1"), element.getAttribute("z0"), element.getAttribute("z1"), children);
		}

		@Override
		public org.w3c.dom.Element serializeSelf(
				Document document,
				NamedArea self,
				List<org.w3c.dom.Element> children
		) {
			org.w3c.dom.Element element = document.createElement(self.getId());

			element.setAttribute("name", self.name);
			if (self.dataTag != -1) element.setAttribute("dataTag", Integer.toString(self.dataTag));
			element.setAttribute("x0", Integer.toString(self.x0));
			element.setAttribute("x1", Integer.toString(self.x1));
			if (self.y0 != 0) element.setAttribute("y0", Integer.toString(self.y0));
			if (self.y0 != 255) element.setAttribute("y1", Integer.toString(self.y1));
			element.setAttribute("z0", Integer.toString(self.z0));
			element.setAttribute("z1", Integer.toString(self.z1));

			children.forEach(element::appendChild);

			return element;
		}
	}
}
