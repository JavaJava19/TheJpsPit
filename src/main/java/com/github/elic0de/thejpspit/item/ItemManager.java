package com.github.elic0de.thejpspit.item;

import java.util.HashMap;

public class ItemManager {

    public static HashMap<String, PitItem> pitItemMap = new HashMap<>();

    public static PitItem getPitItem(String id) {
        return pitItemMap.get(id);
    }

    public static void register(PitItem pitItem) {
        pitItemMap.put(pitItem.getId(), pitItem);
    }
}
