package dev.jab125.minimega.grf.minecraft.util;

import dev.jab125.minimega.grf.element.__ROOT__;

import java.util.function.Consumer;
import java.util.function.Supplier;

public interface RootHolder {
	__ROOT__ getRoot();

	void setRoot(__ROOT__ root);

	static RootHolder of(Supplier<__ROOT__> rootGetter, Consumer<__ROOT__> rootSetter) {
		return new RootHolder() {
			@Override
			public __ROOT__ getRoot() {
				return rootGetter.get();
			}

			@Override
			public void setRoot(__ROOT__ root) {
				rootSetter.accept(root);
			}
		};
	}
}
