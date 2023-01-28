package com.woodyscales.contextmod;

import java.util.EventObject;

public abstract class ContextEvent extends EventObject {

	protected ContextEvent(Object source) {
		super(source);
	}
	
	public static class Update extends ContextEvent {

		private MinecraftContext context;
		
		protected Update(Object source, MinecraftContext context) {
			super(source);
			this.context = context;
		}

		public MinecraftContext getContext() {
			return context;
		}
	}
}
