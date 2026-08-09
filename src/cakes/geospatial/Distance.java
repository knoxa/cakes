package cakes.geospatial;

import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.RealVector;

import cakes.geometry.Matrix;
import cakes.geometry.Tuple;

public class Distance {
	
	private static final double EARTH_RADIUS_IN_KM = 6371.0;

	public static double distance(RealVector a, RealVector b) {
		
		// great circle distance in km between locations
		// Assume Earth is a sphere of radius 6371 km;
		
		// Take a pair of points at longitude, latitude (0,0) on the surface of a unit sphere.		
		RealVector ptA, ptB;
		ptA = ptB = Tuple.point(new double[] {0, 0, 1});
		
		double lonA = a.getEntry(0) / 360 * Math.PI;
		double latA = a.getEntry(1) / 180 * Math.PI;
		double lonB = b.getEntry(0) / 360 * Math.PI;
		double latB = b.getEntry(1) / 180 * Math.PI;
		
		// Rotate by relevant latitudes and longitudes.
		RealMatrix transformA = Matrix.rotationY(lonA).multiply(Matrix.rotationX(latA));
		RealVector locA = transformA.preMultiply(ptA);
	
		RealMatrix transformB = Matrix.rotationY(lonB).multiply(Matrix.rotationX(latB));
		RealVector locB = transformB.preMultiply(ptB);
		
		double distance = Math.acos(locA.cosine(locB)) * Math.PI * EARTH_RADIUS_IN_KM / 2;
		return distance;
	}

	public static double distanceFCC(RealVector a, RealVector b) {
		
		// https://en.wikipedia.org/wiki/Geographical_distance#FCC's_formula
		// FCC formula for approx distance in km (not exceeding 475)
		
		double dlat = a.getEntry(1) - b.getEntry(1);
		double dlon = a.getEntry(0) - b.getEntry(0);
		double midlat = (a.getEntry(1) + b.getEntry(1)) / 2;		
		double k1 = 111.13209 - 0.56605 * Math.cos(2 * midlat) + 0.00120 * Math.cos(4 * midlat);
		double k2 = 111.41513 * Math.cos(midlat) - 0.09455 * Math.cos(3 * midlat) + 0.00012 * Math.cos(5 * midlat);
		double distance = Math.sqrt(Math.pow(dlon * k1, 2) + Math.pow(dlat * k2, 2));
	
		return distance;
	}

}
