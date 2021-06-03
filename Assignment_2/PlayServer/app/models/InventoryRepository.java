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

    public MongoCollection inventory() {
        MongoCollection inventoryCollection = jongo.getCollection("inventory");
        return inventoryCollection;
    }
}