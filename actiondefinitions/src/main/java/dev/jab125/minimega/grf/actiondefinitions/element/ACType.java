package dev.jab125.minimega.grf.actiondefinitions.element;

import dev.jab125.minimega.grf.actiondefinitions.ActionDefinitionsElementRegistry;
import dev.jab125.minimega.grf.element.ElementRegistry;
import dev.jab125.minimega.grf.element.ElementType;

public interface ACType<T extends ACElement> extends ElementType<T> {
	@Override
	default ElementRegistry getRegistry() {
		return ActionDefinitionsElementRegistry.REGISTRY;
	}
}
