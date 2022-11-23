package optimal.barrows.kills;

import com.google.inject.Provides;
import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.*;
import net.runelite.api.events.VarbitChanged;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Slf4j
@PluginDescriptor(
	name = "Optimal Barrows Kills"
)
public class OptimalBarrowsKillsPlugin extends Plugin
{
	@Inject
	private Client client;

	@Inject
	private OverlayManager overlayManager;

	@Inject
	private OptimalBarrowsKillsConfig config;

	@Inject
	private OptimalBarrowsKillsOverlay overlay;

	private static final int CRYPT_REGION_ID = 14231;
	final int LOWEST_COMBAT_LEVEL = 43;

	final int OPTIMAL_COMBAT_SUM = 880;

	final ArrayList<BarrowsNPC> kills = new ArrayList<>();
	private int previousPotential = 0;

	private static final BarrowsNPC[][] OPTIMAL_KILLS_LIST = {
			{ BarrowsNPC.GIANT_CRYPT_SPIDER, BarrowsNPC.SKELETON, BarrowsNPC.CRYPT_SPIDER },
			{ BarrowsNPC.GIANT_CRYPT_SPIDER, BarrowsNPC.SKELETON, BarrowsNPC.BLOODWORM },
			{ BarrowsNPC.SKELETON, BarrowsNPC.SKELETON, BarrowsNPC.CRYPT_SPIDER },
			{ BarrowsNPC.SKELETON, BarrowsNPC.SKELETON, BarrowsNPC.BLOODWORM},
			{ BarrowsNPC.BLOODWORM, BarrowsNPC.BLOODWORM, BarrowsNPC.BLOODWORM, BarrowsNPC.CRYPT_SPIDER },
			{ BarrowsNPC.BLOODWORM, BarrowsNPC.BLOODWORM, BarrowsNPC.BLOODWORM, BarrowsNPC.BLOODWORM }
	};

	@Provides
	OptimalBarrowsKillsConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(OptimalBarrowsKillsConfig.class);
	}

	@Override
	protected void startUp() throws Exception
	{
		overlayManager.add(overlay);
	}

	@Override
	protected void shutDown() throws Exception
	{
		overlayManager.remove(overlay);
	}

	@Subscribe
	public void onVarbitChanged(VarbitChanged event)
	{
		if (event.getVarbitId() == Varbits.BARROWS_REWARD_POTENTIAL) {
			int potential = event.getValue();

			if (potential == 0) {
				previousPotential = 0;
				kills.clear();
			} else {
				int difference = potential - previousPotential;
				BarrowsNPC npcKilled = BarrowsNPC.findByCombatLevel(difference);

				if (npcKilled != null) {
					kills.add(npcKilled);
				}

				previousPotential = potential;
			}
		}
	}

	List<List<BarrowsNPC>> viableKillLists() {
		List<List<BarrowsNPC>> viableLists = new ArrayList<>();

		for (BarrowsNPC[] list : OPTIMAL_KILLS_LIST) {
			List<BarrowsNPC> asList = Arrays.asList(list);

			if (Collections.indexOfSubList(asList, kills) != -1) {
				viableLists.add(BarrowsNPC.removeSublist(asList, kills));
			}
		}

		return viableLists;
	}

	int numBrothersKilled() {
		return client.getVarbitValue(Varbits.BARROWS_KILLED_AHRIM) +
			client.getVarbitValue(Varbits.BARROWS_KILLED_DHAROK) +
			client.getVarbitValue(Varbits.BARROWS_KILLED_GUTHAN) +
			client.getVarbitValue(Varbits.BARROWS_KILLED_KARIL) +
			client.getVarbitValue(Varbits.BARROWS_KILLED_TORAG) +
			client.getVarbitValue(Varbits.BARROWS_KILLED_VERAC);
	}

	boolean isInCrypt()
	{
		Player localPlayer = client.getLocalPlayer();
		return localPlayer != null && localPlayer.getWorldLocation().getRegionID() == CRYPT_REGION_ID;
	}

	BarrowsBrother getNextUnkilledBrother() {
		for (BarrowsBrother brother : BarrowsBrother.values()) {
			if (client.getVarbitValue(brother.getKilledVarbit()) == 1) {
				return brother;
			}
		}

		return null;
	}
}
