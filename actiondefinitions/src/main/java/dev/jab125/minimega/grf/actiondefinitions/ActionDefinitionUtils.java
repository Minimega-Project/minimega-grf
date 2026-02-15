package dev.jab125.minimega.grf.actiondefinitions;

import dev.jab125.minimega.grf.actiondefinitions.element.ACElement;
import dev.jab125.minimega.grf.actiondefinitions.element.ActionDefinitions;
import dev.jab125.minimega.grf.actiondefinitions.element.Always;
import dev.jab125.minimega.grf.actiondefinitions.element.Cancel;
import dev.jab125.minimega.grf.actiondefinitions.element.CurrentNamedArea;
import dev.jab125.minimega.grf.actiondefinitions.element.Effect;
import dev.jab125.minimega.grf.actiondefinitions.element.Effects;
import dev.jab125.minimega.grf.actiondefinitions.element.EnteredNamedArea;
import dev.jab125.minimega.grf.actiondefinitions.element.ITrigger;
import dev.jab125.minimega.grf.actiondefinitions.element.Not;
import dev.jab125.minimega.grf.actiondefinitions.element.OnAction;
import dev.jab125.minimega.grf.actiondefinitions.element.Proceed;
import dev.jab125.minimega.grf.actiondefinitions.element.Trigger;
import dev.jab125.minimega.grf.element.Element;
import dev.jab125.minimega.grf.element.NamedArea;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

public class ActionDefinitionUtils {
	public static ActionDefinitions deserialize(InputStream stream) {
		try {
			return (ActionDefinitions) Element.fromXML(stream, ActionDefinitionsElementRegistry.REGISTRY);
		} catch (ParserConfigurationException | IOException | SAXException e) {
			throw new RuntimeException(e);
		}
	}

	public static <T extends ACElement & OnAction, E extends ACElement & ITrigger> void main(String[] args) {
		ActionDefinitions deserialize = deserialize(ActionDefinitionUtils.class.getResourceAsStream("/testing.xml"));
		System.out.println(deserialize);

		T enteredNamedArea = (T) deserialize.getOnActions().getFirstOf(EnteredNamedArea.class).orElseThrow();

		List<ActionRuntime> actionRuntimes = new ArrayList<>();
		Context context = new Context() {
			@Override
			public void runAsync(ActionRuntime runtime) {
				actionRuntimes.add(runtime);
			}

			@Override
			public NamedArea currentNamedArea() {
				return new NamedArea("beaconArea", 0, 0, 0, 0, 0, 0, 0, List.of());
			}

			@Override
			public NamedArea previousNamedArea() {
				return null;
			}

			@Override
			public void log(String text) {
				System.out.println("[LOG] " + text);
			}

			@Override
			public void populateAllContainersInsideOfNamedArea() {
				System.out.println("[Populated all containers in " + currentNamedArea().name + "]");
			}

			private String activeDialog;
			@Override
			public void replaceDialogWith(String dialog) {
				activeDialog = dialog;
				System.out.println("NEW DIALOG: " + dialog);
			}

			@Override
			public void appendDialog(String text) {
				activeDialog += "\n" + text;
				System.out.println("APPENDED DIALOG: " + activeDialog);
			}

			@Override
			public void hardcodedStatusMessage() {

			}

//			@Override
//			public void userInput(Optional<Proceed> proceed, Optional<Cancel> cancel) {
//				Scanner scanner = new Scanner(System.in);
//				System.out.println("INPUT (y/n)");
//				String string = scanner.nextLine();
//				if ("y".equals(string)) {
//					proceed.ifPresent(obj -> obj.stream());
//					List<Effect> effectsAsList = proceed.orElseThrow().streamOf((Class) Effect.class).toList();
//					for (Effect effect1 : effectsAsList.reversed()) {
//						effects.add(cursor + 1, effect1);
//					}
//				}
//				if ("n".equals(string)) {
//					List<Effect> effectsAsList = (List<Effect>) proceedCancel.getFirstOf(Cancel.class).orElseThrow().streamOf((Class) Effect.class).toList();
//					for (Effect effect1 : effectsAsList.reversed()) {
//						effects.add(cursor + 1, effect1);
//					}
//				}
//			}
		};
		Trigger trigger = enteredNamedArea.getTrigger();
		boolean moveForward = false;
		for (Element element : trigger) {
			if (element instanceof ITrigger iTrigger) {
				E e = (E) iTrigger;
				moveForward = evaluateTrigger(e, null);
			}
			break;
		}
		if (!moveForward) return;


		Effects effects = enteredNamedArea.getEffects();
		//noinspection unchecked, rawtypes
		List<Effect> effectsAsList = (List<Effect>) effects.streamOf((Class) Effect.class).toList();
		ActionRuntime actionRuntime = new ActionRuntime(effectsAsList, context);

		actionRuntimes.add(actionRuntime);
		while (!actionRuntimes.isEmpty()) {
			for (Iterator<ActionRuntime> iterator = new ArrayList<>(actionRuntimes).iterator(); iterator.hasNext(); ) {
				ActionRuntime runtime = iterator.next();
				if (runtime.isAwaitingUserInput()) {
					Scanner scanner = new Scanner(System.in);
					System.out.println("INPUT (y/n)");
					String string = scanner.nextLine();
					Optional<Proceed> proceed = runtime.getProceedCancel().getFirstOf(Proceed.class);
					Optional<Cancel> cancel = runtime.getProceedCancel().getFirstOf(Cancel.class);
					if ("y".equals(string)) {
						proceed.ifPresent(obj -> runtime.addAfterCursor(obj.streamOf((Class) Effect.class).toList()));
					}
					if ("n".equals(string)) {
						cancel.ifPresent(obj -> runtime.addAfterCursor(obj.streamOf((Class) Effect.class).toList()));
					}
					runtime.resume();
				}
				if (!runtime.evaluate()) actionRuntimes.remove(runtime);
			}
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
		};
	}

}
