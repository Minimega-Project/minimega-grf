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
package dev.jab125.minimega.grf;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

public class Json2XmlConverter {
	public static void main(String[] args) throws XMLStreamException, IOException, ParserConfigurationException, SAXException {
		toXML();
	}

//	public static void readOut(String minigame) throws XMLStreamException, IOException, ParserConfigurationException, SAXException {
//
//		Path path = Path.of("src/main/resources/data/minimega/minimega_minigames/%s/gamerules".formatted(minigame));
//		for (File file : path.toFile().listFiles()) {
//			Path path1 = file.toPath();
//			System.out.println(path1);
//			if (!path1.toString().endsWith(".xml")) {
//				System.out.println(path1 + " does not end with .xml");
//				continue;
//			}
//
//			System.out.println(Element.fromXML(Files.newInputStream(path1)));
//			//Files.writeString(path2, output);
//		}
//
//	}

	public static void toXML() throws XMLStreamException, IOException, ParserConfigurationException, SAXException {

		Path path1 = Path.of("tutorial.json");
		System.out.println(path1);
		if (!path1.toString().endsWith(".json")) {
			System.out.println(path1 + " does not end with .json");
			return;
		}
		XMLOutputFactory factory = XMLOutputFactory.newInstance();
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

		XMLStreamWriter xmlStreamWriter = factory.createXMLStreamWriter(byteArrayOutputStream);
		toXML(new Gson().fromJson(Files.readString(path1), JsonObject.class), xmlStreamWriter);
		xmlStreamWriter.close();
		String output = toPrettyString(byteArrayOutputStream.toString(), 2);
		Path path2 = path1.resolveSibling(path1.getFileName().toString().replace(".json", ".xml"));
		//System.out.println(path2.toAbsolutePath());
		//System.out.println(Element.fromXML(new ByteArrayInputStream(output.getBytes(StandardCharsets.UTF_8))));
		Files.write(path2, output.getBytes(StandardCharsets.UTF_8));

	}

	public static void toXML(JsonObject object, XMLStreamWriter writer) throws XMLStreamException {
		JsonPrimitive name = object.getAsJsonPrimitive("name");
		writer.writeStartElement(name.getAsString());
		JsonObject parameters = object.getAsJsonObject("parameters");
		for (Map.Entry<String, JsonElement> stringJsonElementEntry : parameters.entrySet()) {
			writer.writeAttribute(stringJsonElementEntry.getKey(), stringJsonElementEntry.getValue().getAsString());
		}
		if (object.has("childRules")) for (JsonElement stringJsonElementEntry : object.getAsJsonArray("childRules")) {
			toXML(stringJsonElementEntry.getAsJsonObject(), writer);
		}
		writer.writeEndElement();
	}

	public static String toPrettyString(String xml, int indent) {
		try {
			// Turn xml string into a document
			Document document = DocumentBuilderFactory.newInstance()
					.newDocumentBuilder()
					.parse(new InputSource(new ByteArrayInputStream(xml.getBytes(StandardCharsets.UTF_8))));

			// Remove whitespaces outside tags
			document.normalize();
			XPath xPath = XPathFactory.newInstance().newXPath();
			NodeList nodeList = (NodeList) xPath.evaluate("//text()[normalize-space()='']",
					document,
					XPathConstants.NODESET);

			for (int i = 0; i < nodeList.getLength(); ++i) {
				Node node = nodeList.item(i);
				node.getParentNode().removeChild(node);
			}

			// Setup pretty print options
			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			transformerFactory.setAttribute("indent-number", indent);
			Transformer transformer = transformerFactory.newTransformer();
			transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
			transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");

			// Return pretty print xml string
			StringWriter stringWriter = new StringWriter();
			transformer.transform(new DOMSource(document), new StreamResult(stringWriter));
			return stringWriter.toString();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}
