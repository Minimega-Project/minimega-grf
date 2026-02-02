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
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.ArrayList;
import java.util.List;

public interface ElementType<T extends Element> {
	default T parse(org.w3c.dom.Element element) {
		return parseSelf(element, parseChildren(element));
	}

	T parseSelf(org.w3c.dom.Element element, List<Element> children);

	default List<Element> parseChildren(org.w3c.dom.Element element) {
		NodeList childNodes = element.getChildNodes();
		ArrayList<Element> objects = new ArrayList<>();
		for (int i = 0; i < childNodes.getLength(); i++) {
			Node item = childNodes.item(i);
			if (item.getNodeType() == Node.ELEMENT_NODE) {
				org.w3c.dom.Element item1 = (org.w3c.dom.Element) item;
				String nodeName = item1.getNodeName();
				objects.add(Element.REGISTRY.get(nodeName).parse(item1));
			}
		}
		return objects;
	}

	default org.w3c.dom.Element serialize(Document document, T self) {
		return serializeSelf(document, self, serializeChildren(document, self));
	}

	org.w3c.dom.Element serializeSelf(Document document, T self, List<org.w3c.dom.Element> children);

	default List<org.w3c.dom.Element> serializeChildren(Document document, T self) {
		ArrayList<org.w3c.dom.Element> objects = new ArrayList<>();
		self.stream().forEachOrdered(element -> {
			objects.add(((ElementType<Element>) Element.REGISTRY.get(element.getId())).serialize(document, element));
		});
		return objects;
	}
}
