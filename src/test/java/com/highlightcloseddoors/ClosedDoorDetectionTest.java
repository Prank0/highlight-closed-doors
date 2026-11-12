package com.highlightcloseddoors;

import net.runelite.api.Tile;
import org.junit.Test;

import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class ClosedDoorDetectionTest
{
	@Test
	public void findsClosedDoorsAndGates()
	{
		assertTrue(HighlightClosedDoorsPlugin.isClosedDoorDefinition("Door", new String[]{"Open", null, "Pick-lock"}));
		assertTrue(HighlightClosedDoorsPlugin.isClosedDoorDefinition("Large gate", new String[]{"Open"}));
		assertTrue(HighlightClosedDoorsPlugin.isClosedDoorDefinition("Cell DOOR", new String[]{null, "open"}));
	}

	@Test
	public void ignoresOpenAndUnrelatedObjects()
	{
		assertFalse(HighlightClosedDoorsPlugin.isClosedDoorDefinition("Door", new String[]{"Close"}));
		assertFalse(HighlightClosedDoorsPlugin.isClosedDoorDefinition("Chest", new String[]{"Open"}));
		assertFalse(HighlightClosedDoorsPlugin.isClosedDoorDefinition("Trapdoor", null));
		assertFalse(HighlightClosedDoorsPlugin.isClosedDoorDefinition(null, new String[]{"Open"}));
	}

	@Test
	public void selectsOnlyThePlayersCurrentPlane()
	{
		Tile[][] groundFloor = new Tile[1][1];
		Tile[][] firstFloor = new Tile[1][1];
		Tile[][][] sceneTiles = {groundFloor, firstFloor};

		assertSame(groundFloor, HighlightClosedDoorsPlugin.getPlaneTiles(sceneTiles, 0));
		assertSame(firstFloor, HighlightClosedDoorsPlugin.getPlaneTiles(sceneTiles, 1));
		assertNull(HighlightClosedDoorsPlugin.getPlaneTiles(sceneTiles, -1));
		assertNull(HighlightClosedDoorsPlugin.getPlaneTiles(sceneTiles, 2));
	}
}
