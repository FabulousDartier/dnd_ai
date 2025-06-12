package com.id013754;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import org.junit.jupiter.api.Test;

class ItemTest {

    @Test
    void constructor_shouldSetPropertiesCorrectly() {
        String name = "Health Potion";
        String description = "Restores a small amount of HP.";
        Item item = new Item(name, description);

        assertEquals(name, item.getName(), "Item name should be set correctly.");
        assertEquals(description, item.getDescription(), "Item description should be set correctly.");
    }

    @Test
    void getName_shouldReturnName() {
        Item item = new Item("Sword", "A sharp blade.");
        assertEquals("Sword", item.getName());
    }

    @Test
    void getDescription_shouldReturnDescription() {
        Item item = new Item("Shield", "Offers protection.");
        assertEquals("Offers protection.", item.getDescription());
    }

    @Test
    void toString_shouldReturnName() {
        Item item = new Item("Gold Coin", "Shiny currency.");
        assertEquals("Gold Coin", item.toString(), "toString should return the item name.");
    }

    @Test
    void constructor_withNullName_shouldStillCreateObject_PendsFurtherDiscussionOnNullHandling() {
        // Current implementation allows null, though this might be undesirable.
        // If Item enforced non-null names, this test would change to assertThrows.
        Item item = new Item(null, "Description");
        assertNull(item.getName(), "Name should be null if passed as null.");
    }

    @Test
    void constructor_withNullDescription_shouldStillCreateObject_PendsFurtherDiscussionOnNullHandling() {
        // Current implementation allows null.
        Item item = new Item("ItemName", null);
        assertNull(item.getDescription(), "Description should be null if passed as null.");
    }

    @Test
    void constructor_withEmptyName_shouldSetEmptyName() {
        Item item = new Item("", "Description");
        assertEquals("", item.getName(), "Name should be empty if passed as empty.");
    }

    @Test
    void constructor_withEmptyDescription_shouldSetEmptyDescription() {
        Item item = new Item("ItemName", "");
        assertEquals("", item.getDescription(), "Description should be empty if passed as empty.");
    }
}