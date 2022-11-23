package optimal.barrows.kills;

import lombok.RequiredArgsConstructor;
import net.runelite.api.Varbits;

@RequiredArgsConstructor
enum BarrowsBrother
{
    AHRIM("Ahrim", 98, Varbits.BARROWS_KILLED_AHRIM),
    DHAROK("Dharok", 115, Varbits.BARROWS_KILLED_DHAROK),
    GUTHAN("Guthan", 115, Varbits.BARROWS_KILLED_GUTHAN),
    KARIL("Karil", 98, Varbits.BARROWS_KILLED_KARIL),
    TORAG("Torag",115, Varbits.BARROWS_KILLED_TORAG),
    VERAC("Verac", 155, Varbits.BARROWS_KILLED_VERAC);

    private final String name;
    private final int combatLevel;
    private final int killedVarbit;

    public int getCombatLevel() {
        return this.combatLevel;
    }

    public int getKilledVarbit() {
        return this.killedVarbit;
    }
}