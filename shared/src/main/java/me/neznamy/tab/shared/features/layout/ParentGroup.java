package me.neznamy.tab.shared.features.layout;

import java.util.*;

import lombok.Getter;
import lombok.NonNull;
import me.neznamy.tab.shared.TabConstants;
import me.neznamy.tab.shared.platform.TabList;
import me.neznamy.tab.shared.platform.TabPlayer;
import me.neznamy.tab.shared.placeholders.conditions.Condition;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ParentGroup {

    @NonNull private final Layout layout;
    @Nullable private final Condition condition;
    @Getter private final int[] slots;
    @Getter private final Map<Integer, PlayerSlot> playerSlots = new HashMap<>();
    @Getter final Map<TabPlayer, PlayerSlot> players = new HashMap<>();

    public ParentGroup(@NonNull Layout layout, @Nullable Condition condition, int[] slots) {
        this.layout = layout;
        this.condition = condition;
        this.slots = slots;
        for (int slot : slots) {
            playerSlots.put(slot, new PlayerSlot(layout, layout.getManager().getUUID(slot)));
        }
    }

    public void tick(@NonNull List<TabPlayer> remainingPlayers) {
        players.clear();
        List<TabPlayer> meetingCondition = new ArrayList<>();
        for (TabPlayer p : remainingPlayers) {
            if (condition == null || condition.isMet(p)) meetingCondition.add(p);
        }
        remainingPlayers.removeAll(meetingCondition);
        for (int index = 0; index < slots.length; index++) {
            int slot = slots[index];
            if (layout.getManager().isRemainingPlayersTextEnabled() && index == slots.length - 1 && playerSlots.size() < meetingCondition.size()) {
                playerSlots.get(slot).setText(String.format(layout.getManager().getRemainingPlayersText(), meetingCondition.size() - playerSlots.size() + 1));
                break;
            }
            if (meetingCondition.size() > index) {
                TabPlayer p = meetingCondition.get(index);
                playerSlots.get(slot).setPlayer(p);
                players.put(p, playerSlots.get(slot));
            } else {
                playerSlots.get(slot).setText("");
            }
        }
    }
    
    public @NotNull List<TabList.Entry> getSlots(@NonNull TabPlayer p) {
        List<TabList.Entry> data = new ArrayList<>();
        playerSlots.values().forEach(s -> data.add(s.getSlot(p)));
        return data;
    }

    @SuppressWarnings("unchecked")
    public static @NotNull ParentGroup fromMap(@NonNull Layout layout, @NonNull Map<String, Object> map) {
        Condition condition = Condition.getCondition((String) map.get("condition"));
        List<Integer> positions = new ArrayList<>();
        for (String line : (List<String>) map.get("slots")) {
            String[] arr = line.split("-");
            int from = Integer.parseInt(arr[0]);
            int to = arr.length == 1 ? from : Integer.parseInt(arr[1]);
            for (int i = from; i<= to; i++) {
                positions.add(i);
            }
        }
        positions.removeAll(layout.getFixedSlots().keySet());
        layout.getEmptySlots().removeAll(positions);
        if (condition != null) {
            layout.addUsedPlaceholders(Collections.singletonList(TabConstants.Placeholder.condition(condition.getName())));
        }
        return new ParentGroup(layout, condition, positions.stream().mapToInt(i->i).toArray());
    }
}