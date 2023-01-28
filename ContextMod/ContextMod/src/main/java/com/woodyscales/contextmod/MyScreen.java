package com.woodyscales.contextmod;

public class MyScreen {
	private final String fullName;
	private final String simpleName;

	public MyScreen(Class<?> c) {
		if (net.minecraft.client.gui.screens.Screen.class.isAssignableFrom(c)) {
			fullName = c.getName();
			simpleName = c.getSimpleName();
		} else {
			throw new IllegalArgumentException();
		}
	}

	public String getFullName() {
		return fullName;
	}

	public String getSimpleName() {
		return simpleName;
	}

	public boolean equals(MyScreen other) {
		if (other == null) {
			return false;
		}
		return fullName == null ? other.fullName == null : fullName.equals(other.fullName);
	}

	@Override
	public boolean equals(Object other) {
		return other instanceof MyScreen ? equals((MyScreen) other) : false;
	}
}
