package dev.jab125.minimega.grf.element;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class ElementRegistry {
	private final Map<String, ElementType<?>> registry = new HashMap<>() {
		@Override
		public ElementType<?> get(Object key) {
			return Objects.requireNonNull(super.get(key), key.toString());
		}
	};

	public <T extends Element> ElementType<T> get(String type) {
		return (ElementType<T>) registry.get(type);
	}

	protected void register(String string, ElementType<?> elementType) {
		registry.put(string, elementType);
	}

	{
		if (this.getClass() == ElementRegistry.class) {
			// Not a perfect ordering, but good enough
			// Glide
			registry.put("__ROOT__", new __ROOT__.Type());
			registry.put("ForcedBiome", new ForcedBiome.Type());
			registry.put("Checkpoint", new Checkpoint.Type());
			registry.put("UpdatePlayer", new UpdatePlayer.Type());
			registry.put("MapOptions", new MapOptions.Type());
			registry.put("PlayerBoundsVolume", new Unfinished.Type("PlayerBoundsVolume"));
			registry.put("LevelRules", new LevelRules.Type());
			registry.put("ActiveChunkArea", new ActiveChunkArea.Type());
			registry.put("ActiveViewArea", new Unfinished.Type("ActiveViewArea"));
			registry.put("CustomBeacon", new CustomBeacon.Type());
			registry.put("ThermalArea", new ThermalArea.Type());
			registry.put("TargetArea", new TargetArea.Type());
			registry.put("ScoreRing", new ScoreRing.Type());
			registry.put("BlockCollisionException", new Unfinished.Type("BlockCollisionException"));
			registry.put("NamedArea", new NamedArea.Type());

			// Tumble
			registry.put("Killbox", new Unfinished.Type("Killbox"));
			registry.put("GrantPermissions", new Unfinished.Type("GrantPermissions"));
			registry.put("AllowIn", new Unfinished.Type("AllowIn"));
			registry.put("LayerGeneration", new Unfinished.Type("LayerGeneration"));
			registry.put("AnyCombinationOf", new Unfinished.Type("AnyCombinationOf"));
			registry.put("LayerSize", new Unfinished.Type("LayerSize"));
			registry.put("LinearBlendSize", new Unfinished.Type("LinearBlendSize"));
			registry.put("LayerShape", new Unfinished.Type("LayerShape"));
			registry.put("BasicShape", new Unfinished.Type("BasicShape"));
			registry.put("StarShape", new Unfinished.Type("StarShape"));
			registry.put("RingShape", new Unfinished.Type("RingShape"));
			registry.put("LayerFill", new Unfinished.Type("LayerFill"));
			registry.put("BasicLayerFill", new Unfinished.Type("BasicLayerFill"));
			registry.put("CurvedLayerFill", new Unfinished.Type("CurvedLayerFill"));
			registry.put("WarpedLayerFill", new Unfinished.Type("WarpedLayerFill"));
			registry.put("LayerTheme", new Unfinished.Type("LayerTheme"));
			registry.put("MushroomBlockTheme", new Unfinished.Type("MushroomBlockTheme"));
			registry.put("RainbowTheme", new Unfinished.Type("RainbowTheme"));
			registry.put("BlockDef", new Unfinished.Type("BlockDef"));
			registry.put("TerracottaTheme", new Unfinished.Type("TerracottaTheme"));
			registry.put("FunctionPatchesTheme", new Unfinished.Type("FunctionPatchesTheme"));
			registry.put("BasicPatchesTheme", new Unfinished.Type("BasicPatchesTheme"));
			registry.put("FilterTheme", new Unfinished.Type("FilterTheme"));
			registry.put("SimplePatchesTheme", new Unfinished.Type("SimplePatchesTheme"));
			registry.put("Variations", new Unfinished.Type("Variations"));
			registry.put("ShaftsTheme", new Unfinished.Type("ShaftsTheme"));
			registry.put("NullTheme", new Unfinished.Type("NullTheme"));

			// Fistfight
			registry.put("DropperY", new DropperY.Type());
			registry.put("FistfightFlag", new FistfightFlag.Type());

			// Lobby
			registry.put("OnGameStartSpawnPositions", new Unfinished.Type("OnGameStartSpawnPositions"));
			registry.put("SpawnPositionSet", new Unfinished.Type("SpawnPositionSet"));
			registry.put("OnInitialiseWorld", new Unfinished.Type("OnInitialiseWorld"));
			registry.put("PopulateContainer", new PopulateContainer.Type());
			registry.put("AddItem", new AddItem.Type());

			// Tutorial
			registry.put("StartFeature", new Unfinished.Type("StartFeature"));
			registry.put("AddEnchantment", new Unfinished.Type("AddEnchantment"));
			registry.put("CompleteAll", new Unfinished.Type("CompleteAll"));
			registry.put("CollectItem", new Unfinished.Type("CollectItem"));
		}
	}
}
