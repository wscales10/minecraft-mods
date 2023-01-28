package com.woodyscales.contextmod;

import net.minecraft.client.gui.screens.Screen;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;

public class MinecraftContext 
{
	private ResourceKey<Biome> biome;
	
	private ResourceKey<Level> dimension;
	
	private GameType gameMode;
	
	private boolean isUnderwater;
	
	private MyScreen screen;
	
	public ResourceKey<Biome> getBiome()
	{
		return biome;
	}
	
	public MinecraftContext setBiome(ResourceKey<Biome> biome) {
		this.biome = biome;
		return this;
	}

	public ResourceKey<Level> getDimension() {
		return dimension;
	}

	public MinecraftContext setDimension(ResourceKey<Level> dimension) {
		this.dimension = dimension;
		return this;
	}

	public GameType getGameMode() {
		return gameMode;
	}

	public MinecraftContext setGameMode(GameType gameMode) {
		this.gameMode = gameMode;
		return this;
	}

	public boolean getIsUnderwater() {
		return isUnderwater;
	}

	public MinecraftContext setIsUnderwater(boolean isUnderwater) {
		this.isUnderwater = isUnderwater;
		return this;
	}

	public MyScreen getScreen() {
		return screen;
	}

	public MinecraftContext setScreen(Screen screen) {
		this.screen = screen == null ? null : new MyScreen(screen.getClass());
		return this;
	}
}
