package optimal.barrows.kills;

import lombok.RequiredArgsConstructor;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public enum BarrowsNPC {
    CRYPT_RAT("Crypt rat", 43),
    BLOODWORM("Bloodworm", 52),
    CRYPT_SPIDER("Crypt spider", 56),
    GIANT_CRYPT_RAT("Giant crypt rat", 76),
    SKELETON("Skeleton", 77),
    GIANT_CRYPT_SPIDER("Giant crypt spider", 79);


    private final String name;
    private final int combatLevel;

    public static BarrowsNPC findByCombatLevel(final int combatLevel) {
        return Arrays.stream(values()).filter(value -> value.combatLevel == combatLevel).findFirst().orElse(null);
    }

    public static BarrowsNPC findByName(final String name) {
        return Arrays.stream(values()).filter(value -> Objects.equals(value.name, name)).findFirst().orElse(null);
    }

    public String getName() {
        return this.name;
    }

    public int getCombatLevel() {
        return this.combatLevel;
    }

    public static List<BarrowsNPC> removeSublist(List<BarrowsNPC> superList, List<BarrowsNPC> sublist) {
        List<String> superListNames = superList.stream().map(BarrowsNPC::getName).collect(Collectors.toList());
        List<String> subListNames = sublist.stream().map(BarrowsNPC::getName).collect(Collectors.toList());

        for (String name : subListNames) {
            superListNames.remove(name);
        }

        return superListNames.stream().map(BarrowsNPC::findByName).collect(Collectors.toList());
    }
}
