package dev.jab125.minimega.grf.actiondefinitions;

import dev.jab125.minimega.grf.actiondefinitions.element.*;
import dev.jab125.minimega.grf.element.ElementRegistry;

public class ActionDefinitionsElementRegistry extends ElementRegistry {
	public static final ActionDefinitionsElementRegistry REGISTRY = new ActionDefinitionsElementRegistry();
	{
		register("ActionDefinitions", new ActionDefinitions.Type());
		register("Always", new Always.Type());
		register("AppendToDialog", new AppendToDialog.Type());
		register("Cancel", new Cancel.Type());
		register("ContinueText", new ContinueText.Type());
		register("CurrentNamedArea", new CurrentNamedArea.Type());
		register("Delay", new Delay.Type());
		register("Dialog", new Dialog.Type());
		register("DialogSequence", new DialogSequence.Type());
		register("Effects", new Effects.Type());
		register("EnteredNamedArea", new EnteredNamedArea.Type());
		register("ExitDialogSequence", new ExitDialogSequence.Type());
		register("HardcodedStatusMessage", new HardcodedStatusMessage.Type());
		register("Log", new Log.Type());
		register("Not", new Not.Type());
		register("OnActions", new OnActions.Type());
		register("PopulateAllContainersInsideOfNamedArea", new PopulateAllContainersInsideOfNamedArea.Type());
		register("Proceed", new Proceed.Type());
		register("ProceedCancel", new ProceedCancel.Type());
		register("Trigger", new Trigger.Type());
	}
}
