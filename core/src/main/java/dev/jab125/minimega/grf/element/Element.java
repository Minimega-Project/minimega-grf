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
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

public abstract class Element implements Iterable<Element> {
	public static final ElementRegistry GAME_RULE_FILE_REGISTRY = new ElementRegistry();
	private final List<Element> elementList;

	public Element(List<Element> children) {
		this.elementList = children;
	}

	public static Element fromXML(InputStream stream, ElementRegistry registry) throws ParserConfigurationException, IOException, SAXException {
		// 1. Create a DocumentBuilderFactory
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

		// Optional: configure the factory
		factory.setIgnoringElementContentWhitespace(true);

		// 2. Create a DocumentBuilder
		DocumentBuilder builder = factory.newDocumentBuilder();

		// 3. Parse the XML file into a Document object (DOM tree)

		Document doc = builder.parse(new ByteArrayInputStream(stream.readAllBytes()));
		org.w3c.dom.Element documentElement = doc.getDocumentElement();
		String nodeName = documentElement.getNodeName();
		return registry.get(nodeName).parse(documentElement);
	}

	@Override
	public Iterator<Element> iterator() {
		return elementList.iterator();
	}

	public List<Element> getChildren() {
		return Collections.unmodifiableList(elementList);
	}

	public Stream<Element> stream() {
		return elementList.stream();
	}

	public Stream<Element> flatten(Element root) {
		return Stream.concat(
				Stream.of(root),
				root.elementList == null
						? Stream.empty()
						: root.elementList.stream().flatMap(this::flatten)
		);
	}

	public <T extends Element> Stream<T> streamOf(Class<T> clazz) {
		return stream().filter(clazz::isInstance).map(clazz::cast);
	}

	public <T extends Element> Stream<T> flatStreamOf(Class<T> clazz) {
		return stream().flatMap(this::flatten).filter(clazz::isInstance).map(clazz::cast);
	}

	public <T extends Element> Optional<T> getFirstOf(Class<T> clazz) {
		return streamOf(clazz).findFirst();
	}

	public <T extends Element> Optional<T> getFirstOfFlat(Class<T> clazz) {
		return flatStreamOf(clazz).findFirst();
	}

	public abstract String getId();

	@Override
	public String toString() {
		return getId() + paramsDisp() + (elementList.isEmpty() ? "" : elementList);
	}

	private String paramsDisp() {
		String string = "{";
		NamedNodeMap attributes = null;
		try {
			attributes = getRegistry().get(getId()).serialize(DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument(), this).getAttributes();
		} catch (ParserConfigurationException e) {
			throw new RuntimeException(e);
		}
		if (attributes.getLength() == 0) return "";
		for (int i = 0; i < attributes.getLength(); i++) {
			Attr item = (Attr) attributes.item(i);
			string += item.getName() + "=" + item.getValue();
			if (i != attributes.getLength() - 1) string += ",";
		}
		string += "}";
		return string;
	}

	public ElementRegistry getRegistry() {
		return GAME_RULE_FILE_REGISTRY;
	}
}


