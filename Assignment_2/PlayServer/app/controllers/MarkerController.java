package controllers;

import com.google.gson.Gson;

import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.inject.Inject;

import models.Dealer;
import models.DealersRepository;
import models.LocationsRepository;
import models.Shop;
import models.ShopsRepository;
import models.markers.Markers;
import play.mvc.Controller;
import play.mvc.Result;
import utils.Utilities;

public class MarkerController extends Controller {
    @Inject
    private LocationsRepository locations;
    @Inject
    private ShopsRepository shops;
    @Inject
    private DealersRepository dealers;
    private static final Gson gson = new Gson();

    public Result getInBound(String firebaseId, Double latSW, Double lonSW, Double latNE, Double lonNE) {
        Double[] lastLocation = locations.getLocation(firebaseId).loc1;
        Double[][] lastLocations;
        List<Double[]> listLastLocation = locations.getAllLastLocations().stream().filter(l -> Utilities.inBound(latSW, lonSW, latNE, lonNE, l[0], l[1])).collect(Collectors.toList());
        lastLocations = new Double[listLastLocation.size()][2];
        for (int i = 0; i < listLastLocation.size(); ++i) {
            lastLocations[i] = listLastLocation.get(i);
        }
        Markers markers = new Markers();
        Supplier<Stream<Shop>> shopSupplier = () -> Stream.of(shops.getVisible(latSW, lonSW, latNE, lonNE));
        Supplier<Stream<Dealer>> dealerSupplier = () -> Stream.of(dealers.getVisible(latSW, lonSW, latNE, lonNE));
        Supplier<Stream<Double[]>> locationSupplier = () -> Stream.of(lastLocations);
        Shop[] visibleShops;
        Shop[] activeShops;
        Dealer[] visibleDealers;
        Dealer[] activeDealers;
        activeShops = shopFilterToArray(shopSupplier.get(), lastLocation, false);
        activeDealers = dealerFilterToArray(dealerSupplier.get(), lastLocation, false, locationSupplier);
        visibleShops = shopFilterToArray(shopSupplier.get(), lastLocation, true);
        visibleDealers = dealerFilterToArray(dealerSupplier.get(), lastLocation, true, locationSupplier);

        markers.activeDealers = activeDealers;
        markers.activeShops = activeShops;
        markers.visibleDealers = visibleDealers;
        markers.visibleShops = visibleShops;
        return ok(gson.toJson(markers));
    }

    private Dealer[] dealerFilterToArray(Stream<Dealer> stream, Double[] location, boolean invert, Supplier<Stream<Double[]>> locationSupplier) {
        return stream
                .filter(d -> locationSupplier.get().filter(l -> Utilities.inRange(l[0], l[1], d.lat, d.lon, d.range)).count() >= d.despawnAt)
                .filter(d -> invert ^ Utilities.inRange(location[0], location[1], d.lat, d.lon, d.range)).toArray(Dealer[]::new);
    }

    private Shop[] shopFilterToArray(Stream<Shop> stream, Double[] location, boolean invert) {
        return stream.filter(d -> invert ^ Utilities.inRange(location[0], location[1], d.lat, d.lon, d.range)).toArray(Shop[]::new);
    }
}
