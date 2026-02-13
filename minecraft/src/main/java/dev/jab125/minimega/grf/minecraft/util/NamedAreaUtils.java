package dev.jab125.minimega.grf.minecraft.util;

import dev.jab125.minimega.grf.element.Element;
import dev.jab125.minimega.grf.element.LevelRules;
import dev.jab125.minimega.grf.element.NamedArea;
import dev.jab125.minimega.grf.element.__ROOT__;

import java.util.ArrayList;
import java.util.List;

public class NamedAreaUtils {
	public static Result addNamedArea(RootHolder rootHolder, NamedArea area) {
		__ROOT__ root = rootHolder.getRoot();
		LevelRules levelRules = root.getLevelRules();
		if (levelRules.streamOf(NamedArea.class).anyMatch(a -> area.name.equals(a.name))) {
			return Result.ERROR_DUPLICATE;
		}
		ArrayList<Element> children = new ArrayList<>(levelRules.getChildren());
		children.add(area);
		LevelRules elements = new LevelRules(levelRules.ruleType, children);
		List<Element> children1 = new ArrayList<>(root.getChildren());
		children1.removeIf(a -> a instanceof LevelRules);
		children1.add(elements);
		rootHolder.setRoot(new __ROOT__(children1));
		return Result.SUCCESS;
	}

	public static Result removeNamedArea(RootHolder rootHolder, String name) {
		__ROOT__ root = rootHolder.getRoot();
		LevelRules levelRules = root.getLevelRules();
		if (levelRules.streamOf(NamedArea.class).noneMatch(a -> name.equals(a.name))) {
			return Result.ERROR_NONE_FOUND;
		}
		ArrayList<Element> children = new ArrayList<>(levelRules.getChildren());
		children.removeIf(a -> a instanceof NamedArea area && name.equals(area.name));
		LevelRules elements = new LevelRules(levelRules.ruleType, children);
		List<Element> children1 = new ArrayList<>(root.getChildren());
		children1.removeIf(a -> a instanceof LevelRules);
		children1.add(elements);
		rootHolder.setRoot(new __ROOT__(children1));
		return Result.SUCCESS;
	}

	public enum Result {
		SUCCESS,
		ERROR_DUPLICATE,
		ERROR_NONE_FOUND
	}
}
