package com.woodyscales.contextmod;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;

@Mod("contextmod")
public class ContextMod {
	
	public ContextMod() {
		MinecraftForge.EVENT_BUS.register(this);
	}
}
