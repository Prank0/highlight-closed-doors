package com.highlightcloseddoors;

import net.runelite.client.RuneLite;
import net.runelite.client.externalplugins.ExternalPluginManager;

public class HighlightClosedDoorsPluginTest
{
	public static void main(String[] args) throws Exception
	{
		ExternalPluginManager.loadBuiltin(HighlightClosedDoorsPlugin.class);
		RuneLite.main(args);
	}
}
