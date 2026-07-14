package cakes.geospatial;

import java.util.List;

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
}
