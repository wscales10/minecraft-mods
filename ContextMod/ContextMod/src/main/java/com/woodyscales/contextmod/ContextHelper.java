package com.woodyscales.contextmod;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.woodyscales.contextmod.events.EventServer2;

import net.minecraft.client.gui.screens.LevelLoadingScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.client.gui.screens.WinScreen;
import net.minecraft.client.gui.screens.worldselection.SelectWorldScreen;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.biome.Biome;
import net.minecraftforge.client.event.ClientPlayerNetworkEvent;
import net.minecraftforge.client.event.ComputeFovModifierEvent;
import net.minecraftforge.client.event.CustomizeGuiOverlayEvent;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.client.event.RenderArmEvent;
import net.minecraftforge.client.event.RenderGuiEvent;
import net.minecraftforge.client.event.RenderGuiOverlayEvent;
import net.minecraftforge.client.event.RenderHandEvent;
import net.minecraftforge.client.event.RenderHighlightEvent;
import net.minecraftforge.client.event.RenderLevelLastEvent;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import net.minecraftforge.client.event.RenderLivingEvent;
import net.minecraftforge.client.event.ScreenEvent;
import net.minecraftforge.client.event.ViewportEvent;
import net.minecraftforge.client.event.sound.PlaySoundEvent;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.ItemAttributeModifierEvent;
import net.minecraftforge.event.PlayLevelSoundEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.VanillaGameEvent;
import net.minecraftforge.event.entity.EntityEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.event.level.ChunkTicketLevelUpdatedEvent;
import net.minecraftforge.event.level.ChunkWatchEvent;
import net.minecraftforge.event.level.LevelEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.SubscribeEvent;

@SuppressWarnings("removal")
public class ContextHelper extends EventServer2<ContextListener> {
	private Screen relevantScreen;
	private Screen screen;
	private boolean listenForAll;
	private ArrayList<LevelAccessor> levels = new ArrayList<LevelAccessor>();
	
	private GameType gameMode;
	private ResourceKey<Level> dimension;
	private LocalPlayer player;
	@SuppressWarnings("unused")
	private Event previousEvent;
	private Level level;
	private boolean isUnderwater;
	private Holder<Biome> biome;

	private static final List<Type> relevantScreens = Collections
			.unmodifiableList(List.of(TitleScreen.class, WinScreen.class, SelectWorldScreen.class));

	public ContextHelper() {
		UpdateContext();
	}

	public void UpdateContext() {
		ContextEvent.Update event = new ContextEvent.Update(this, GetContext());
		getListeners().forEach(l -> l.contextUpdated(event));
	}

	public MinecraftContext GetContext() {
		return new MinecraftContext()
				.setScreen(relevantScreen)
				.setDimension(dimension)
				.setGameMode(gameMode)
				.setBiome(biome == null ? null : biome.unwrapKey().get())
				.setIsUnderwater(isUnderwater);
	}
	
	@SubscribeEvent
	public void OnScreenOpenEvent(ScreenEvent.Opening event) {
		Screen newScreen = event.getNewScreen();
		Type newScreenType = newScreen.getClass();
		System.out.println("opened " + newScreenType);

		screen = newScreen;
		if (relevantScreens.contains(newScreenType)) {
			relevantScreen = newScreen;
			UpdateContext();
		}

		if (newScreenType == LevelLoadingScreen.class) {
			listenForAll = true;
		}
	}

	@SubscribeEvent
	public void OnScreenCloseEvent(ScreenEvent.Closing event) {
		System.out.println("closed " + event.getScreen().getClass());

		if (event.getScreen() == screen && relevantScreen != null) {
			relevantScreen = null;
			UpdateContext();
		}
	}

	@SubscribeEvent
	public void OnLevelLoadEvent(LevelEvent.Load event) {
		LevelAccessor level = event.getLevel();
		System.out.println(levels.contains(level));
		levels.add(level);
		System.out.println("loaded " + level.getClass());
	}

	@SubscribeEvent
	public void OnLevelUnloadEvent(LevelEvent.Unload event) {
		levels.remove(event.getLevel());
	}

	@SubscribeEvent
	public void OnPlayerChangeDimensionEvent(PlayerEvent.PlayerChangedDimensionEvent event) {
		// TODO: test this on multiplayer and make sure it only applies to the correct
		// player
		dimension = event.getTo();
		UpdateContext();
	}
	
	@SubscribeEvent
	public void OnInputEvent(InputEvent.Key event) {
		if(event.getKey() == 342) {
			System.out.print("");
		}
	}

	@SubscribeEvent
	public void OnPlayerChangeGameModeEvent(PlayerEvent.PlayerChangeGameModeEvent event) {
		// TODO: test this on multiplayer and make sure it only applies to the correct
		// player
		gameMode = event.getNewGameMode();
		UpdateContext();
	}

	@SubscribeEvent
	public void OnClientLoginEvent(ClientPlayerNetworkEvent.LoggingIn event) {
		// TODO: test this on multiplayer and make sure it only applies to the correct
		// player
		player = event.getPlayer();
		level = player.getLevel();
		dimension = level.dimension();
		PlayerInfo playerInfo = player.connection.getPlayerInfo(player.getGameProfile().getId());
		gameMode = playerInfo == null ? null : playerInfo.getGameMode();
		
		UpdateContext();
	}
	
	@SubscribeEvent
	public void OnClientLogoutEvent(ClientPlayerNetworkEvent.LoggingOut event) {
		// TODO: test this on multiplayer and make sure it only applies to the correct
		// player
		player = null;
		level = null;
		dimension = null;
		gameMode = null;
		biome = null;
		isUnderwater = false;
		
		UpdateContext();
	}
	
	@SubscribeEvent
	public void OnLivingTickEvent(LivingEvent.LivingTickEvent event) {
		boolean hasChanged = false;
		if (event.getEntity() == player) {
			Holder<Biome> newBiome = player == null ? null : level.getBiome(player.blockPosition());

			if (newBiome != biome) {
				hasChanged = true;
				biome = newBiome;
			}

			if ((player == null ? false : player.isUnderWater()) != isUnderwater) {
				isUnderwater = !isUnderwater;
				hasChanged = true;
			}

			if (hasChanged) {
				UpdateContext();
			}
		}

	}
	
	@SubscribeEvent
	public void OnAnyEvent(Event event) {
		if (listenForAll) {
			var eventType = event.getClass();

			if (!(event instanceof ScreenEvent || event instanceof TickEvent || event instanceof LevelEvent
					|| event instanceof ChunkTicketLevelUpdatedEvent || event instanceof AttachCapabilitiesEvent<?>
					|| event instanceof EntityEvent || event instanceof ItemAttributeModifierEvent
					|| event instanceof VanillaGameEvent || event instanceof ChunkWatchEvent
					|| event instanceof PlaySoundEvent
					|| event instanceof RenderGuiOverlayEvent
					|| event instanceof BlockEvent
					|| event instanceof RenderLivingEvent
					|| event instanceof RenderArmEvent
					|| event instanceof PlayLevelSoundEvent
					|| event instanceof RenderLevelStageEvent
					|| event instanceof ViewportEvent
					|| event instanceof RenderHandEvent
					|| event instanceof RenderGuiEvent
					|| event instanceof CustomizeGuiOverlayEvent
					|| event instanceof RenderHighlightEvent
					|| event instanceof RenderLevelLastEvent
					|| event instanceof ComputeFovModifierEvent)) {
				System.out.println("event: " + eventType);
			}
		}
		previousEvent = event;
	}
}
