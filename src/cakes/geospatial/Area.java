package cakes.geospatial;

import java.util.List;

import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.RealVector;

public class Area {

	public static double shoelace(List<RealVector> points) {
		
	// See - https://en.wikipedia.org/wiki/Shoelace_formula
	// "points" are the vertices of an ordered polygon (clockwise or anticlockwise). The last point is the same as the first.
		
		double area = 0.0;
		
		for ( int i = 0, j = 1; j < points.size(); i++, j++ ) {
			
			RealVector current = points.get(i);
			RealVector next    = points.get(j);
			area += (next.getEntry(0) + current.getEntry(0)) * (next.getEntry(1) - current.getEntry(1));
					
		}
		
		return Math.abs(area/2.0);
	}
	
	public static RealVector centroid(List<RealVector> points) {
		
		// See - https://en.wikipedia.org/wiki/Centroid#Of_a_polygon
		
		double xsum = 0.0, ysum = 0.0;
		
		for ( int i = 0, j = 1; j < points.size(); i++, j++ ) {
			
			RealVector current = points.get(i);
			RealVector next    = points.get(j);
			
			xsum += (current.getEntry(0) + next.getEntry(0)) * (current.getEntry(0) * next.getEntry(1) - next.getEntry(0) * current.getEntry(1));
			ysum += (current.getEntry(1) + next.getEntry(1)) * (current.getEntry(0) * next.getEntry(1) - next.getEntry(0) * current.getEntry(1));
					
		}
		
		double area = shoelace(points);
		RealVector point = new ArrayRealVector(new double[] {xsum / (6 * area), ysum / (6 * area)});
		return point;
	}
}
