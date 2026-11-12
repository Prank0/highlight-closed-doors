package com.highlightcloseddoors;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Shape;
import javax.inject.Inject;
import net.runelite.api.DecorativeObject;
import net.runelite.api.GameObject;
import net.runelite.api.GroundObject;
import net.runelite.api.TileObject;
import net.runelite.api.WallObject;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.OverlayPriority;

final class HighlightClosedDoorsOverlay extends Overlay
{
	private static final Color CYAN = new Color(0, 255, 255, 235);
	private static final BasicStroke OUTLINE = new BasicStroke(2.0f);

	private final HighlightClosedDoorsPlugin plugin;

	@Inject
	private HighlightClosedDoorsOverlay(HighlightClosedDoorsPlugin plugin)
	{
		this.plugin = plugin;
		setPosition(OverlayPosition.DYNAMIC);
		setLayer(OverlayLayer.ABOVE_SCENE);
		setPriority(OverlayPriority.MED);
	}

	@Override
	public Dimension render(Graphics2D graphics)
	{
		graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		graphics.setColor(CYAN);
		graphics.setStroke(OUTLINE);

		for (TileObject door : plugin.getClosedDoors())
		{
			if (!plugin.isClosedDoor(door))
			{
				continue;
			}

			Shape clickbox = door.getClickbox();
			if (clickbox == null)
			{
				clickbox = getObjectHull(door);
			}
			if (clickbox != null)
			{
				graphics.draw(clickbox);
			}
		}
		return null;
	}

	private static Shape getObjectHull(TileObject object)
	{
		if (object instanceof WallObject)
		{
			WallObject wall = (WallObject) object;
			Shape hull = wall.getConvexHull();
			return hull != null ? hull : wall.getConvexHull2();
		}
		if (object instanceof GameObject)
		{
			return ((GameObject) object).getConvexHull();
		}
		if (object instanceof DecorativeObject)
		{
			DecorativeObject decoration = (DecorativeObject) object;
			Shape hull = decoration.getConvexHull();
			return hull != null ? hull : decoration.getConvexHull2();
		}
		if (object instanceof GroundObject)
		{
			return ((GroundObject) object).getConvexHull();
		}
		return null;
	}
}
