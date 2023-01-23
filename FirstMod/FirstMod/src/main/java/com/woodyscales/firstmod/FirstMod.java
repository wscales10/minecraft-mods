package com.woodyscales.firstmod;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;

@Mod("firstmod")
public class FirstMod {
	public FirstMod() {
		MinecraftForge.EVENT_BUS.register(this);
	}
}
