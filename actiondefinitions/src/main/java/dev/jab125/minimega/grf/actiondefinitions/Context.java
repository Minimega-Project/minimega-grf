package dev.jab125.minimega.grf.actiondefinitions;

import dev.jab125.minimega.grf.element.NamedArea;

public interface Context {
	default boolean completedTutorial(String tutorial) {
		return false;
	}

	void runAsync(ActionRuntime runtime);

	NamedArea currentNamedArea();

	NamedArea previousNamedArea();

	void log(String text);

	void populateAllContainersInsideOfNamedArea();

	void replaceDialogWith(String dialog);

	void appendDialog(String text);

	//void userInput(Optional<Proceed> proceed, Optional<Cancel> cancel);
}
