package controllers;

import com.google.gson.Gson;

import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import java.util.stream.Stream;

import javax.inject.Inject;

import models.Dealer;
import models.DealersRepository;
import models.Location;
import models.LocationsRepository;
import models.Shop;
import models.ShopsRepository;
import models.UsersRepository;
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
    @Inject
    private UsersRepository users;
    private static final Gson gson = new Gson();


    public Result getInBound(String firebaseId, Double latSW, Double lonSW, Double latNE, Double lonNE) {
        Double[] lastLocation = locations.getLocation(firebaseId).loc1;
        Double[][] lastLocations;
        LocalTime now = LocalTime.now();
        List<Double[]> listLastLocation = new ArrayList<>();
        String[] nearbyUsersId = locations.getNearbyUsers(firebaseId);
        Map<String, Double[]> nearbyUsers = new HashMap();
        Arrays.stream(nearbyUsersId)
                .filter(id -> {
                    Location loc = locations.getLocation(id);
                    return LocalTime.of(loc.hour, loc.minute, loc.second, loc.nano).until(now, ChronoUnit.SECONDS) <= 20;
                })
                .forEach(id -> {
                    Double[] loc = locations.getLocation(id).loc1;
                    if (Utilities.inBound(latSW, lonSW, latNE, lonNE, loc[0], loc[1])) {
                        nearbyUsers.put(id, loc);
                        listLastLocation.add(loc);
                    }
                });
        listLastLocation.add(lastLocation);
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
        markers.userLocations = nearbyUsers;
        markers.nearbyUsers = new HashMap();
        nearbyUsers.keySet().stream().forEach(id -> markers.nearbyUsers.put(id, users.getUser(id)));

        return ok(gson.toJson(markers));
    }

    private Dealer[] dealerFilterToArray(Stream<Dealer> stream, Double[] location, boolean invert, Supplier<Stream<Double[]>> locationSupplier) {
        return stream
                .filter(d -> locationSupplier.get().filter(l -> Utilities.inRange(l[0], l[1], d.lat, d.lon, d.range)).count() < d.despawnAt)
                .filter(d -> invert ^ Utilities.inRange(location[0], location[1], d.lat, d.lon, d.range)).toArray(Dealer[]::new);
    }

    private Shop[] shopFilterToArray(Stream<Shop> stream, Double[] location, boolean invert) {
        return stream.filter(d -> invert ^ Utilities.inRange(location[0], location[1], d.lat, d.lon, d.range)).toArray(Shop[]::new);
    }
}
