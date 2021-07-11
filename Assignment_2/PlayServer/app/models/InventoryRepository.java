package models;

import org.jongo.MongoCollection;

import javax.inject.Inject;
import javax.inject.Singleton;

import uk.co.panaxiom.playjongo.PlayJongo;

@Singleton
public class InventoryRepository {
    @Inject
    private PuzzleRepository puzzles;
    @Inject
    private PlayJongo jongo;

    private static InventoryRepository instance = null;

    public InventoryRepository() {
        instance = this;
    }

    public static InventoryRepository getInstance() {
        return instance;
    }

    public MongoCollection inventories() {
        return jongo.getCollection("inventories");
    }

    public Inventory getInventory(String id) {
        Inventory inventory = inventories().findOne("{_id: #}", id).as(Inventory.class);
        if (inventory == null) {
            inventory = new Inventory();
            inventory.id = id;
            this.insert(inventory);
        }
        inventory.cleanUp();
        return inventory;
    }

    public void insert(Inventory inventory) {
        inventory.cleanUp();
        inventories().save(inventory);
    }

    public void update(Inventory inventory) {
        inventory.cleanUp();
        inventories().update("{_id: #}", inventory.id).with(this.copyInventory(inventory));
    }

    public Inventory copyInventory(Inventory inventory) {
        Inventory copy = new Inventory();
        copy.id = inventory.id;
        copy.sets = inventory.sets;
        copy.titles = inventory.titles;
        copy.cleanUp();
        inventory.cleanUp();
        return copy;
    }
}