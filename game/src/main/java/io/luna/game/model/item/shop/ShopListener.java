package io.luna.game.model.item.shop;

import io.luna.game.model.item.IndexedItem;
import io.luna.game.model.item.ItemContainer;
import io.luna.game.model.item.RefreshListener;
import io.luna.net.msg.out.WidgetIndexedItemsMessageWriter;

import java.util.List;
import java.util.OptionalInt;

/**
 * A {@link RefreshListener} implementation that queues display messages and triggers restock task
 * scheduling.
 *
 * @author lare96 <http://github.com/lare96>
 */
public final class ShopListener extends RefreshListener {

    /**
     * The shop.
     */
    private final Shop shop;

    /**
     * Creates a new {@link ShopListener}.
     *
     * @param shop The shop.
     */
    public ShopListener(Shop shop) {
        this.shop = shop;
    }

    @Override
    public void onInit(ItemContainer items) {
        OptionalInt[] amountMap = shop.getAmountMap();
        for (int index = 0; index < items.capacity(); index++) {
            int amount = items.computeAmountForIndex(index);
            if (amount == 0) {
                amountMap[index] = OptionalInt.empty();
            } else {
                amountMap[index] = OptionalInt.of(amount);
            }
        }
    }

    @Override
    public void displayUpdate(ItemContainer items, List<IndexedItem> updateItems,
                              WidgetIndexedItemsMessageWriter msg) {

        // Determine if a restock is needed. Will usually only loop once.
        for (int index = 0; index < updateItems.size(); index++) {
            IndexedItem item = updateItems.get(index);
            if (item != null && item.getAmount() == 0) {
                shop.restockItems();
                break;
            }
        }

        // Queue message for whoever has shop open.
        shop.getViewing().forEach(player -> player.queue(msg));
    }

    @Override
    public void onCapacityExceeded(ItemContainer items) {
        throw new IllegalStateException("Shop capacity exceeded.");
    }
}