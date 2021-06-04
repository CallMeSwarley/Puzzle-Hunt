package models;

import org.jongo.MongoCollection;

import javax.inject.Inject;
import javax.inject.Singleton;

import uk.co.panaxiom.playjongo.PlayJongo;

@Singleton
public class InventoryRepository {

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
        MongoCollection inventoryCollection = jongo.getCollection("inventories");
        return inventoryCollection;
    }

    public Inventory getInventory(String id) {
        return inventories().findOne("{_id: #}", id).as(Inventory.class);
    }

    public void insert(Inventory inventory) {
        inventories().save(inventory);
    }

    public void update(Inventory inventory) {
        inventories().update("{_id: #}", inventory.id).with(this.copyInventory(inventory));
    }

    public Inventory copyInventory(Inventory inventory) {
        Inventory copy = new Inventory();
        copy.id = inventory.id;
        copy.sets = inventory.sets;
        copy.titles = inventory.titles;

        return copy;
    }
}