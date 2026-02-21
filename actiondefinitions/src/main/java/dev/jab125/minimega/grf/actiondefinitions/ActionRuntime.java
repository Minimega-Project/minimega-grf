package dev.jab125.minimega.grf.actiondefinitions;

import dev.jab125.minimega.grf.actiondefinitions.element.AppendToDialog;
import dev.jab125.minimega.grf.actiondefinitions.element.ContinueText;
import dev.jab125.minimega.grf.actiondefinitions.element.Delay;
import dev.jab125.minimega.grf.actiondefinitions.element.Dialog;
import dev.jab125.minimega.grf.actiondefinitions.element.DialogSequence;
import dev.jab125.minimega.grf.actiondefinitions.element.Effect;
import dev.jab125.minimega.grf.actiondefinitions.element.ExitDialogSequence;
import dev.jab125.minimega.grf.actiondefinitions.element.HardcodedStatusMessage;
import dev.jab125.minimega.grf.actiondefinitions.element.Log;
import dev.jab125.minimega.grf.actiondefinitions.element.PopulateAllContainersInsideOfNamedArea;
import dev.jab125.minimega.grf.actiondefinitions.element.ProceedCancel;

import java.util.ArrayList;
import java.util.List;

// multiple can be active at the same time
public class ActionRuntime {
	private final List<Effect> effects;
	private int cursor;
	private boolean exited;
	private boolean suspended = false;
	private boolean awaitingUserInput;
	private ProceedCancel proceedCancel;
	private final Context context;
	private int stop;

	public Object getIdentity() {
		return context.getIdentity();
	}

	public ActionRuntime(List<Effect> effects, Context context) {
		this.effects = new ArrayList<>(effects);
		this.context = context;
	}

	// returns true if there is still more to evaluate
	public boolean evaluate() {
		if (exited) return false;
		if (suspended) return true;
		if (stop > 0) {
			stop--;
			return true;
		}
		while (cursor < effects.size()) {
			try {
//					try {
//						Thread.sleep(1000);
//					} catch (InterruptedException e) {
//						throw new RuntimeException(e);
//					}
				Effect effect = effects.get(cursor);
				switch (effect) {
					case AppendToDialog appendToDialog -> {
						context.appendDialog(appendToDialog.text);
					}
					case ContinueText continueText -> {
						context.appendDialog("Press {y} to continue.");
					}
					case Delay ignored -> {
						stop = 40;
						return true;
					}
					case Dialog dialog -> {
						context.replaceDialogWith(dialog.text);
					}
					case DialogSequence sequence -> {
						List<Effect> effectsAsList = (List<Effect>) sequence.streamOf((Class) Effect.class).toList();
						ActionRuntime actionRuntime = new ActionRuntime(effectsAsList, context);
						context.runAsync(actionRuntime);
					}
					case ExitDialogSequence ignored -> {
						exited = true;
						return false;
					}
					case HardcodedStatusMessage ignored -> context.hardcodedStatusMessage();
					case Log log -> context.log(log.text);
					case PopulateAllContainersInsideOfNamedArea ignored ->
							context.populateAllContainersInsideOfNamedArea();
					case ProceedCancel proceedCancel -> {
						this.suspended = true;
						this.awaitingUserInput = true;
						this.proceedCancel = proceedCancel;
						return true;
					}
				}
			} finally {
				cursor++;
			}
		}
		return false;
	}

	public void addAfterCursor(List<Effect> effects) {
		for (Effect effect : effects.reversed()) {
			this.effects.add(cursor, effect);
		}
	}

	public void resume() {
		this.suspended = false;
		this.awaitingUserInput = false;
	}

	public boolean isAwaitingUserInput() {
		return this.awaitingUserInput;
	}

	public ProceedCancel getProceedCancel() {
		return proceedCancel;
	}
}
