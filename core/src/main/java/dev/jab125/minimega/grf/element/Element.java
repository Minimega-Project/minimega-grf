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
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

public abstract sealed class Element implements Iterable<Element> permits ActiveChunkArea, AddItem, Checkpoint, CustomBeacon, DropperY, FistfightFlag, ForcedBiome, LevelRules, MapOptions, NamedArea, PopulateContainer, ScoreRing, TargetArea, ThermalArea, Unfinished, UpdatePlayer, __ROOT__ {
	public static final Map<String, ElementType<?>> REGISTRY = new HashMap<>() {
		@Override
		public ElementType<?> get(Object key) {
			return Objects.requireNonNull(super.get(key), key.toString());
		}
	};

	static {
		// Not a perfect ordering, but good enough
		// Glide
		REGISTRY.put("__ROOT__", new __ROOT__.Type());
		REGISTRY.put("ForcedBiome", new ForcedBiome.Type());
		REGISTRY.put("Checkpoint", new Checkpoint.Type());
		REGISTRY.put("UpdatePlayer", new UpdatePlayer.Type());
		REGISTRY.put("MapOptions", new MapOptions.Type());
		REGISTRY.put("PlayerBoundsVolume", new Unfinished.Type("PlayerBoundsVolume"));
		REGISTRY.put("LevelRules", new LevelRules.Type());
		REGISTRY.put("ActiveChunkArea", new ActiveChunkArea.Type());
		REGISTRY.put("ActiveViewArea", new Unfinished.Type("ActiveViewArea"));
		REGISTRY.put("CustomBeacon", new CustomBeacon.Type());
		REGISTRY.put("ThermalArea", new ThermalArea.Type());
		REGISTRY.put("TargetArea", new TargetArea.Type());
		REGISTRY.put("ScoreRing", new ScoreRing.Type());
		REGISTRY.put("BlockCollisionException", new Unfinished.Type("BlockCollisionException"));
		REGISTRY.put("NamedArea", new NamedArea.Type());

		// Tumble
		REGISTRY.put("Killbox", new Unfinished.Type("Killbox"));
		REGISTRY.put("GrantPermissions", new Unfinished.Type("GrantPermissions"));
		REGISTRY.put("AllowIn", new Unfinished.Type("AllowIn"));
		REGISTRY.put("LayerGeneration", new Unfinished.Type("LayerGeneration"));
		REGISTRY.put("AnyCombinationOf", new Unfinished.Type("AnyCombinationOf"));
		REGISTRY.put("LayerSize", new Unfinished.Type("LayerSize"));
		REGISTRY.put("LinearBlendSize", new Unfinished.Type("LinearBlendSize"));
		REGISTRY.put("LayerShape", new Unfinished.Type("LayerShape"));
		REGISTRY.put("BasicShape", new Unfinished.Type("BasicShape"));
		REGISTRY.put("StarShape", new Unfinished.Type("StarShape"));
		REGISTRY.put("RingShape", new Unfinished.Type("RingShape"));
		REGISTRY.put("LayerFill", new Unfinished.Type("LayerFill"));
		REGISTRY.put("BasicLayerFill", new Unfinished.Type("BasicLayerFill"));
		REGISTRY.put("CurvedLayerFill", new Unfinished.Type("CurvedLayerFill"));
		REGISTRY.put("WarpedLayerFill", new Unfinished.Type("WarpedLayerFill"));
		REGISTRY.put("LayerTheme", new Unfinished.Type("LayerTheme"));
		REGISTRY.put("MushroomBlockTheme", new Unfinished.Type("MushroomBlockTheme"));
		REGISTRY.put("RainbowTheme", new Unfinished.Type("RainbowTheme"));
		REGISTRY.put("BlockDef", new Unfinished.Type("BlockDef"));
		REGISTRY.put("TerracottaTheme", new Unfinished.Type("TerracottaTheme"));
		REGISTRY.put("FunctionPatchesTheme", new Unfinished.Type("FunctionPatchesTheme"));
		REGISTRY.put("BasicPatchesTheme", new Unfinished.Type("BasicPatchesTheme"));
		REGISTRY.put("FilterTheme", new Unfinished.Type("FilterTheme"));
		REGISTRY.put("SimplePatchesTheme", new Unfinished.Type("SimplePatchesTheme"));
		REGISTRY.put("Variations", new Unfinished.Type("Variations"));
		REGISTRY.put("ShaftsTheme", new Unfinished.Type("ShaftsTheme"));
		REGISTRY.put("NullTheme", new Unfinished.Type("NullTheme"));

		// Fistfight
		REGISTRY.put("DropperY", new DropperY.Type());
		REGISTRY.put("FistfightFlag", new FistfightFlag.Type());

		// Lobby
		REGISTRY.put("OnGameStartSpawnPositions", new Unfinished.Type("OnGameStartSpawnPositions"));
		REGISTRY.put("SpawnPositionSet", new Unfinished.Type("SpawnPositionSet"));
		REGISTRY.put("OnInitialiseWorld", new Unfinished.Type("OnInitialiseWorld"));
		REGISTRY.put("PopulateContainer", new PopulateContainer.Type());
		REGISTRY.put("AddItem", new AddItem.Type());

		// Tutorial
		REGISTRY.put("StartFeature", new Unfinished.Type("StartFeature"));
		REGISTRY.put("AddEnchantment", new Unfinished.Type("AddEnchantment"));
		REGISTRY.put("CompleteAll", new Unfinished.Type("CompleteAll"));
		REGISTRY.put("CollectItem", new Unfinished.Type("CollectItem"));
	}

	private final List<Element> elementList;

	public Element(List<Element> children) {
		this.elementList = children;
	}

	public static Element fromXML(InputStream stream) throws ParserConfigurationException, IOException, SAXException {
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
		return REGISTRY.get(nodeName).parse(documentElement);
	}

	@Override
	public Iterator<Element> iterator() {
		return elementList.iterator();
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
			attributes = ((ElementType<Element>) REGISTRY.get(getId())).serialize(DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument(), this).getAttributes();
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
}


