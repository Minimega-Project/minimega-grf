package dev.jab125.minimega.grf.actiondefinitions.element;

import dev.jab125.minimega.grf.actiondefinitions.ActionDefinitionsElementRegistry;
import dev.jab125.minimega.grf.element.Element;
import dev.jab125.minimega.grf.element.ElementRegistry;

import java.util.List;

public abstract class ACElement extends Element {
	public ACElement(List<Element> children) {
		super(children);
	}

	@Override
	public ElementRegistry getRegistry() {
		return ActionDefinitionsElementRegistry.REGISTRY;
	}
}
