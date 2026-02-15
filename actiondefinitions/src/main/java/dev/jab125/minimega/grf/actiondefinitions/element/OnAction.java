package dev.jab125.minimega.grf.actiondefinitions.element;

import dev.jab125.minimega.grf.element.Element;

public interface OnAction {
	default Trigger getTrigger() {
		return ((Element)this).getFirstOf(Trigger.class).orElseThrow();
	}

	default Effects getEffects() {
		return ((Element)this).getFirstOf(Effects.class).orElseThrow();
	}
}
