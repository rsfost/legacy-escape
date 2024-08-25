package rsfost.legacy_escape;

import net.runelite.client.RuneLite;
import net.runelite.client.externalplugins.ExternalPluginManager;

public class LegacyEscapePluginTest
{
	public static void main(String[] args) throws Exception
	{
		ExternalPluginManager.loadBuiltin(LegacyEscapePlugin.class);
		RuneLite.main(args);
	}
}