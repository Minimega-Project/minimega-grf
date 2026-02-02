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

public final class AddItem extends Element {
	public final String dataTag;
	public final String itemId;
	public final int quantity, slot;

	public AddItem(String dataTag, String itemId, int quantity, int slot, List<Element> children) {
		super(children);
		this.dataTag = dataTag;
		this.itemId = itemId;
		this.quantity = quantity;
		this.slot = slot;
	}

	@Override
	public String getId() {
		return "AddItem";
	}

	static class Type implements ElementType<AddItem> {
		@Override
		public AddItem parseSelf(org.w3c.dom.Element element, List<Element> children) {
			return new AddItem(element.getAttribute("dataTag"), element.getAttribute("itemId"), Integer.parseInt(element.getAttribute("quantity")), Integer.parseInt(element.getAttribute("slot")), children);
		}

		@Override
		public org.w3c.dom.Element serializeSelf(
				Document document,
				AddItem self,
				List<org.w3c.dom.Element> children
		) {
			org.w3c.dom.Element element = document.createElement(self.getId());
			if (!self.dataTag.isBlank()) element.setAttribute("dataTag", self.dataTag);
			element.setAttribute("itemId", self.itemId);
			element.setAttribute("quantity", Integer.toString(self.quantity));
			element.setAttribute("slot", Integer.toString(self.slot));

			children.forEach(element::appendChild);

			return element;
		}
	}
}
