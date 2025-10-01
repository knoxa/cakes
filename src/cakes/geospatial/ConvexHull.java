package cakes.geospatial;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.RealVector;

public class ConvexHull {

	public static void main(String[] args) {
		
		// See also: https://paulbourke.net/geometry/pointlineplane/
		
		List<RealVector> points = new ArrayList<RealVector>();
		points.add(new ArrayRealVector(new double[] {10.0 ,10.0}));
		points.add(new ArrayRealVector(new double[] {10.0 ,20.0}));
		points.add(new ArrayRealVector(new double[] {20.0 ,20.0}));
		points.add(new ArrayRealVector(new double[] {20.0 ,10.0}));
		points.add(new ArrayRealVector(new double[] {11.0 ,15.0}));
		points.add(new ArrayRealVector(new double[] {12.0 ,15.0}));
		points.add(new ArrayRealVector(new double[] {13.0 ,15.0}));
		points.add(new ArrayRealVector(new double[] {14.0 ,15.0}));
		points.add(new ArrayRealVector(new double[] {15.0 ,15.0}));
		points.add(new ArrayRealVector(new double[] {25.0 ,15.0}));
		points.add(new ArrayRealVector(new double[] {11.0 ,1.0}));
	//	points.add(new ArrayRealVector(new double[] {10.0 ,10.0}));
		
		List<RealVector> hull = monotoneChain(points);
		System.out.println(hull);
		
		System.out.println("finished");
	}
	
	
	public static List<RealVector> monotoneChain(List<RealVector> points) {
			
		// Andrew's monotone chain convex hull algorithm
		// A. M. Andrew, "Another Efficient Algorithm for Convex Hulls in Two Dimensions", Info. Proc. Letters 9, 216-219 (1979).
		
		// https://en.wikibooks.org/wiki/Algorithm_Implementation/Geometry/Convex_hull/Monotone_chain
			
		List<RealVector> results = null;
		
		Collections.sort(points, new Comparator<RealVector>() {

			@Override
			public int compare(RealVector a, RealVector b) {

				int xcompare = Double.compare(a.getEntry(0), b.getEntry(0));
				return ( xcompare != 0 ) ? xcompare : Double.compare(a.getEntry(1), b.getEntry(1));
			}
		});
		
		int numPoints = points.size();		
		RealVector[] hull = new RealVector[numPoints * 2];
		int k = 0;
		
		// build lower hull
		
		for ( int i = 0; i < numPoints; i++ ) {
			
			while (k >= 2 && Inclusion.isLeft(hull[k - 1], hull[k - 2], points.get(i)) <= 0 )  k--;
			hull[k++] = points.get(i);
		}
		
		// build upper hull
		
		for ( int i = numPoints -2, t = k + 1; i >= 0; i-- ) {
			
			while (k >= t && Inclusion.isLeft(hull[k - 1], hull[k - 2], points.get(i)) <= 0 )  k--;
			hull[k++] = points.get(i);
		}
		
		results = new ArrayList<RealVector>();
		
		//results.addAll(Arrays.asList(Arrays.copyOfRange(hull, 0, k - 1)));

		// last point is the same as the first, but I want a closed polygon anyway
		results.addAll(Arrays.asList(Arrays.copyOfRange(hull, 0, k)));
		
		return results;
	}

}
