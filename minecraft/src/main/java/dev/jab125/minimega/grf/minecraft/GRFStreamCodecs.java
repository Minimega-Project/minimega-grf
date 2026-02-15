package dev.jab125.minimega.grf.minecraft;

import dev.jab125.minimega.grf.element.Element;
import dev.jab125.minimega.grf.element.ElementType;
import dev.jab125.minimega.grf.element.__ROOT__;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;

public class GRFStreamCodecs {
	public static final StreamCodec<ByteBuf, ? extends Element> ELEMENT_STREAM_CODEC = new StreamCodec<>() {

		@Override
		public void encode(ByteBuf object, Element object2) {
			String id = object2.getId();
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = null;
			try {
				builder = factory.newDocumentBuilder();
			} catch (ParserConfigurationException e) {
				throw new RuntimeException(e);
			}


			// Create a new, empty Document object
			Document doc = builder.newDocument();
			ByteBufCodecs.STRING_UTF8.encode(object, GRFStreamCodecs.toString(Element.GAME_RULE_FILE_REGISTRY.get(id).serialize(doc, object2)));
		}

		@Override
		public Element decode(ByteBuf object) {
			try {
				return Element.fromXML(new ByteArrayInputStream(ByteBufCodecs.STRING_UTF8.decode(object).getBytes(StandardCharsets.UTF_8)), Element.GAME_RULE_FILE_REGISTRY);
			} catch (ParserConfigurationException | IOException | SAXException e) {
				throw new RuntimeException(e);
			}
		}
	};

	/**
	 * Converts a Document object to a String representation.
	 * @param doc The XML Document to convert.
	 * @return The String representation of the XML Document.
	 */
	public static String toString(org.w3c.dom.Node doc) {
		try {
			// Create a StringWriter to hold the XML output
			StringWriter sw = new StringWriter();

			// Configure the Transformer
			TransformerFactory tf = TransformerFactory.newInstance();
			Transformer transformer = tf.newTransformer();

			// Optional: Format the output nicely
			transformer.setOutputProperty(javax.xml.transform.OutputKeys.OMIT_XML_DECLARATION, "no");
			transformer.setOutputProperty(javax.xml.transform.OutputKeys.METHOD, "xml");
			transformer.setOutputProperty(javax.xml.transform.OutputKeys.INDENT, "yes");
			transformer.setOutputProperty(javax.xml.transform.OutputKeys.ENCODING, "UTF-8");

			// Perform the transformation
			transformer.transform(new DOMSource(doc), new StreamResult(sw));

			// Return the resulting String
			return sw.toString();
		} catch (Exception ex) {
			// Handle exceptions appropriately
			throw new RuntimeException("Error converting Document to String", ex);
		}
	}
}
