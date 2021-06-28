package utils;

import models.Location;

public class Utilities {
    public static double POIVisibilityRange = 100;

    public static boolean inBound(Double latSW, Double lonSW, Double latNE, Double lonNE, Double lat, Double lon) {
        return latSW <= lat && lat <= latNE && lonSW <= lon && lon <= lonNE;
    }

    public static boolean inRange(Double latUser, Double lonUser, Double lat, Double lon, Long range) {
        return Utilities.distance(latUser, lonUser, lat, lon) <= range;
    }

    public static final double DISTANCE_THRESHOLD = 50;
    public static final double maxVisibilityRange = 1400;

    //Source: https://www.geeksforgeeks.org/program-distance-two-points-earth/
    public static double distance(Location loc1, Location loc2) {
        return Utilities.distance(loc1.loc1[0], loc1.loc1[1], loc2.loc1[0], loc2.loc1[1]);
    }//Source: https://www.geeksforgeeks.org/program-distance-two-points-earth/

    public static double distance(Double lat1, Double lon1, Double lat2, Double lon2) {
        // The math module contains a function
        // named toRadians which converts from
        // degrees to radians.
        double radLon1 = Math.toRadians(lon1);
        double radLon2 = Math.toRadians(lon2);
        double radLat1 = Math.toRadians(lat1);
        double radLat2 = Math.toRadians(lat2);
        // Haversine formula
        double dlon = radLon2 - radLon1;
        double dlat = radLat2 - radLat1;
        double a = Math.pow(Math.sin(dlat / 2), 2)
                + Math.cos(radLat1) * Math.cos(radLat2)
                * Math.pow(Math.sin(dlon / 2), 2);

        double c = 2 * Math.asin(Math.sqrt(a));
        // Radius of earth in kilometers. Use 3956
        // for miles
        double r = 6371e3;
        // calculate the result
        return (c * r);
    }
}
