package optimal.barrows.kills;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.util.*;
import java.util.stream.Collectors;
import javax.inject.Inject;
import net.runelite.api.Client;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.components.PanelComponent;
import net.runelite.client.ui.overlay.components.TitleComponent;
import net.runelite.client.ui.overlay.components.LineComponent;
import lombok.extern.slf4j.Slf4j;

import static net.runelite.api.Varbits.BARROWS_REWARD_POTENTIAL;

@Slf4j
class OptimalBarrowsKillsOverlay extends Overlay
{
    private final Client client;
    private final OptimalBarrowsKillsPlugin plugin;
    private final PanelComponent panelComponent = new PanelComponent();

    @Inject
    private OptimalBarrowsKillsOverlay(Client client, OptimalBarrowsKillsPlugin plugin)
    {
        setPosition(OverlayPosition.ABOVE_CHATBOX_RIGHT);
        this.client = client;
        this.plugin = plugin;
    }

    @Override
    public Dimension render(Graphics2D graphics)
    {
        // if not in crypt or haven't killed all brothers
        if (!plugin.isInCrypt() || plugin.numBrothersKilled() < 5) { return null; }

        List<String> content = getDisplayLists();

        int remainingPotential = plugin.OPTIMAL_COMBAT_SUM - client.getVarbitValue(BARROWS_REWARD_POTENTIAL);
        String overlayTitle = String.format("Optimal Barrows (%d Remaining)", remainingPotential);

        int overlayWidth = longestLineLength(graphics, overlayTitle, content) + 30;

        // render title
        panelComponent.getChildren().clear();

        // Build overlay title
        panelComponent.getChildren().add(TitleComponent.builder()
                .text(overlayTitle)
                .color(Color.GREEN)
                .build());

        // Set the size of the overlay (width)
        panelComponent.setPreferredSize(new Dimension(overlayWidth,0));

        // render the infobox's content
        for (String contentLine : content) {
            panelComponent.getChildren().add(LineComponent.builder()
                    .left(contentLine)
                    .build());
        }

        return panelComponent.render(graphics);
    }

    private List<String> getDisplayLists() {
        BarrowsBrother remainingBrother = plugin.getNextUnkilledBrother();
        int remainingBrotherCombatLevel = remainingBrother != null ? remainingBrother.getCombatLevel() : 0;

        int remainingPotential = plugin.OPTIMAL_COMBAT_SUM
                - client.getVarbitValue(BARROWS_REWARD_POTENTIAL)
                - remainingBrotherCombatLevel;

        // case 1: optimal kills achieved
        if (remainingPotential < plugin.LOWEST_COMBAT_LEVEL) {
            return Collections.singletonList("Optimal Kills Achieved");
        }

        List<List<BarrowsNPC>> viableLists = plugin.viableKillLists();
        List<String> nameLists = new ArrayList<>();

        for (List<BarrowsNPC> list : viableLists) {
            nameLists.add(list.stream().map(BarrowsNPC::getName).collect(Collectors.joining(", ")));
        }

        // case 2: no lists and optimal hasn't been met
        // case 3: optimal lists remain
        if (nameLists.isEmpty()) {
            return Collections.singletonList("No Remaining Default Lists");
        } else {
            return nameLists;
        }
    }

    private int longestLineLength(Graphics2D graphics, String title, List<String> content) {
        int titleLength = graphics.getFontMetrics().stringWidth(title);

        int longestContentLine = 0;

        for (String line : content) {
            longestContentLine = Integer.max(longestContentLine, graphics.getFontMetrics().stringWidth(line));
        }

        return Integer.max(titleLength, longestContentLine);
    }
}