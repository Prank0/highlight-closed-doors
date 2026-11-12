package com.highlightcloseddoors;

import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.Locale;
import java.util.Set;
import javax.inject.Inject;
import net.runelite.api.Client;
import net.runelite.api.GameObject;
import net.runelite.api.GameState;
import net.runelite.api.ObjectComposition;
import net.runelite.api.Scene;
import net.runelite.api.Tile;
import net.runelite.api.TileObject;
import net.runelite.api.WallObject;
import net.runelite.api.events.GameTick;
import net.runelite.api.events.GameStateChanged;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;

@PluginDescriptor(
	name = "Highlight Closed Doors",
	description = "Outlines visible closed door clickboxes in cyan",
	tags = {"door", "gate", "quest", "highlight", "clickbox"}
)
public class HighlightClosedDoorsPlugin extends Plugin
{
	@Inject private Client client;
	@Inject private ClientThread clientThread;
	@Inject private OverlayManager overlayManager;
	@Inject private HighlightClosedDoorsOverlay overlay;

	private volatile Set<TileObject> closedDoors = Collections.emptySet();

	@Override
	protected void startUp()
	{
		overlayManager.add(overlay);
		clientThread.invoke(this::rebuildDoorSet);
	}

	@Override
	protected void shutDown()
	{
		overlayManager.remove(overlay);
		closedDoors = Collections.emptySet();
	}

	Set<TileObject> getClosedDoors()
	{
		return closedDoors;
	}

	@Subscribe
	public void onGameStateChanged(GameStateChanged event)
	{
		if (event.getGameState() == GameState.LOGGED_IN)
		{
			rebuildDoorSet();
		}
		else if (event.getGameState() == GameState.LOADING || event.getGameState() == GameState.LOGIN_SCREEN)
		{
			closedDoors = Collections.emptySet();
		}
	}

	@Subscribe
	public void onGameTick(GameTick event)
	{
		// Quest and instanced doors can change their live operations without a
		// distinct object ID change. Rebuilding once per tick keeps those exact.
		rebuildDoorSet();
	}

	private void rebuildDoorSet()
	{
		if (client.getGameState() != GameState.LOGGED_IN)
		{
			closedDoors = Collections.emptySet();
			return;
		}

		Scene scene = client.getScene();
		if (scene == null)
		{
			closedDoors = Collections.emptySet();
			return;
		}

		Tile[][] currentPlane = getPlaneTiles(scene.getTiles(), client.getPlane());
		if (currentPlane == null)
		{
			closedDoors = Collections.emptySet();
			return;
		}

		Set<TileObject> found = Collections.newSetFromMap(new IdentityHashMap<>());
		for (Tile[] row : currentPlane)
		{
			for (Tile tile : row)
			{
				if (tile == null)
				{
					continue;
				}
				addIfClosedDoor(found, tile.getWallObject());
				addIfClosedDoor(found, tile.getDecorativeObject());
				addIfClosedDoor(found, tile.getGroundObject());
				for (GameObject gameObject : tile.getGameObjects())
				{
					addIfClosedDoor(found, gameObject);
				}
			}
		}
		closedDoors = Collections.unmodifiableSet(found);
	}

	static Tile[][] getPlaneTiles(Tile[][][] tiles, int plane)
	{
		return tiles != null && plane >= 0 && plane < tiles.length ? tiles[plane] : null;
	}

	private void addIfClosedDoor(Set<TileObject> destination, TileObject object)
	{
		if (object == null)
		{
			return;
		}

		// A malformed/deliberately unusual object transform must not abort the
		// entire scene scan. Some quest-area objects currently throw from
		// ObjectComposition#getImpostor(), which previously meant no doors later
		// in the scene were ever collected.
		try
		{
			if (isClosedDoor(object))
			{
				destination.add(object);
			}
		}
		catch (RuntimeException ignored)
		{
			// Continue scanning the remaining tile objects.
		}
	}

	boolean isClosedDoor(TileObject object)
	{
		ObjectComposition composition = client.getObjectDefinition(object.getId());
		if (composition == null)
		{
			return false;
		}

		ObjectComposition baseComposition = composition;
		try
		{
			ObjectComposition impostor = composition.getImpostor();
			if (impostor != null)
			{
				composition = impostor;
			}
		}
		catch (RuntimeException ignored)
		{
			// Fall back to the base definition. The object's operation overrides
			// below still reflect live "Open"/"Close" changes.
		}

		String name = composition.getName();
		if (name == null || name.equals("null"))
		{
			name = baseComposition.getName();
		}
		String[] definitionActions = composition.getActions();
		String[] liveActions = new String[5];
		if (definitionActions != null)
		{
			System.arraycopy(definitionActions, 0, liveActions, 0,
				Math.min(definitionActions.length, liveActions.length));
		}
		for (int index = 0; index < liveActions.length; index++)
		{
			String override = object.getOpOverride(index);
			if (override != null)
			{
				liveActions[index] = override;
			}
		}
		return isClosedDoorDefinition(name, liveActions);
	}

	static boolean isClosedDoorDefinition(String name, String[] actions)
	{
		if (name == null || actions == null)
		{
			return false;
		}
		String lowerName = name.toLowerCase(Locale.ENGLISH);
		if (!lowerName.contains("door") && !lowerName.contains("gate"))
		{
			return false;
		}
		for (String action : actions)
		{
			if (action != null && action.equalsIgnoreCase("Open"))
			{
				return true;
			}
		}
		return false;
	}
}
