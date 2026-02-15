package dev.jab125.minimega.grf.actiondefinitions.element;

public sealed interface Effect permits AppendToDialog, Delay, Dialog, DialogSequence, ExitDialogSequence, Log, PopulateAllContainersInsideOfNamedArea, ProceedCancel {

}
