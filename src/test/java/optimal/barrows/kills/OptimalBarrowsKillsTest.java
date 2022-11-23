package optimal.barrows.kills;

import net.runelite.client.RuneLite;
import net.runelite.client.externalplugins.ExternalPluginManager;

public class OptimalBarrowsKillsTest
{
	public static void main(String[] args) throws Exception
	{
		ExternalPluginManager.loadBuiltin(OptimalBarrowsKillsPlugin.class);
		RuneLite.main(args);
	}
}