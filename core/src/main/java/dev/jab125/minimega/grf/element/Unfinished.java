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

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;

import java.util.List;

public final class Unfinished extends Element {

	private final String id;
	private final NamedNodeMap attributes;

	public Unfinished(List<Element> children, String id, NamedNodeMap attributes) {
		super(children);
		this.id = id;
		this.attributes = attributes;
	}

	@Override
	public String getId() {
		return id;
	}

	static class Type implements ElementType<Unfinished> {
		private final String id;

		public Type(String id) {
			this.id = id;
		}

		@Override
		public Unfinished parseSelf(org.w3c.dom.Element element, List<Element> children) {
			return new Unfinished(children, id, element.getAttributes());
		}

		@Override
		public org.w3c.dom.Element serializeSelf(
				Document document,
				Unfinished self,
				List<org.w3c.dom.Element> children
		) {
			org.w3c.dom.Element element = document.createElement(self.getId());

			NamedNodeMap attributes1 = self.attributes;
			if (attributes1 != null) {
				for (int i = 0; i < attributes1.getLength(); i++) {
					Attr item = (Attr) attributes1.item(i);
					element.setAttribute(item.getName(), item.getValue());
				}
			}

			children.forEach(element::appendChild);

			return element;
		}
	}
}
