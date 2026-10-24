package com.shuddie.overhead;

import net.runelite.client.RuneLite;
import net.runelite.client.externalplugins.ExternalPluginManager;

public class OverheadNotificationsPluginTest
{
	public static void main(String[] args) throws Exception
	{
		ExternalPluginManager.loadBuiltin(OverheadNotificationsPlugin.class);
		RuneLite.main(args);
	}
}