package io.luna.analytics;

import io.luna.game.model.item.Item;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

class AnalyticsTest {

    @Test
    void testTrade() {
        String eventName = "trade";
        Map<String, Object> tradeData = new HashMap<>();
        tradeData.put("sender_id", "Zezima");
        tradeData.put("receiver_id", "Mod Ashe");

        Item[] items = new Item[]{
                new Item(995, 100_000),
                new Item(3195, 50)
        };
        tradeData.put("items", items);

        Analytics.instance().customData(eventName, tradeData);
        Analytics.instance().flush();
    }

    @Test
    void testReward() {
        // Create reward data
        String eventName = "reward";
        Map<String, Object> rewardData = new HashMap<>();
        Item[] rewardItems = new Item[]{
                new Item(995, 2_000_000),
        };
        rewardData.put("items", rewardItems);
        rewardData.put("pk_points", 100);
        rewardData.put("receiver_id", "Zezima");

        // Submit data to analytics
        Analytics.instance().customData(eventName, rewardData);
        Analytics.instance().flush();
    }

    @Test
        // TODO we don't want each item to trigger a pickup event.
        // TODO At what point do we flush?
    void testPickup() {
        String eventName = "pickup";
    }

    @Test
    void testDrop() {
        // This test simulates a player dropping an item.
    }

    @Test
    void testPurchase() {
        // This test simulates an external purchase for in-game items.
    }
}