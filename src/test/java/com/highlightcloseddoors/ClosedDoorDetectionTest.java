package com.highlightcloseddoors;

import org.junit.Test;

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
}
