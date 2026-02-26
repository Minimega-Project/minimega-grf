package dev.jab125.minimega.grf.actiondefinitions;

import dev.jab125.minimega.grf.actiondefinitions.element.ActionDefinitions;
import dev.jab125.minimega.grf.actiondefinitions.element.All;
import dev.jab125.minimega.grf.actiondefinitions.element.Always;
import dev.jab125.minimega.grf.actiondefinitions.element.CurrentNamedArea;
import dev.jab125.minimega.grf.actiondefinitions.element.ITrigger;
import dev.jab125.minimega.grf.actiondefinitions.element.InteractionBlockPosition;
import dev.jab125.minimega.grf.actiondefinitions.element.Not;
import dev.jab125.minimega.grf.actiondefinitions.element.NotRunningAlready;
import dev.jab125.minimega.grf.actiondefinitions.element.Any;
import dev.jab125.minimega.grf.actiondefinitions.element.PreviousNamedArea;
import dev.jab125.minimega.grf.element.Element;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.InputStream;

public class ActionDefinitionUtils {
	public static ActionDefinitions deserialize(InputStream stream) {
		try {
			return (ActionDefinitions) Element.fromXML(stream, ActionDefinitionsElementRegistry.REGISTRY);
		} catch (ParserConfigurationException | IOException | SAXException e) {
			throw new RuntimeException(e);
		}
	}

	public static boolean evaluateTrigger(ITrigger trigger, Context context) {
		return switch (trigger) {
			case CurrentNamedArea currentNamedArea -> context.currentNamedArea() != null && currentNamedArea.namedArea.equals(context.currentNamedArea().name);
			case Always ignored -> true;
			case Not n -> {
				ITrigger inner = n.stream()
						.filter(ITrigger.class::isInstance)
						.map(ITrigger.class::cast)
						.findFirst()
						.orElseThrow();

				yield !evaluateTrigger(inner, context);
			}
			case All all -> all.stream()
					.filter(ITrigger.class::isInstance)
					.map(ITrigger.class::cast).allMatch(a -> evaluateTrigger(a, context));
			case Any any -> any.stream()
					.filter(ITrigger.class::isInstance)
					.map(ITrigger.class::cast).anyMatch(a -> evaluateTrigger(a, context));
			case NotRunningAlready ignored -> context.notRunningAlready();
			case PreviousNamedArea previousNamedArea -> context.previousNamedArea() != null && previousNamedArea.namedArea.equals(context.previousNamedArea().name);
			case InteractionBlockPosition interactionBlockPosition -> context.isInteractionBlockPosition(interactionBlockPosition.x, interactionBlockPosition.y, interactionBlockPosition.z);
		};
	}

}
