package cakes.geospatial;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.RealVector;

public class Inclusion {

	// https://web.archive.org/web/20130126163405/http://geomalgorithms.com/a03-_inclusion.html
	
	public static void main(String[] args) {
		
		RealVector a = new ArrayRealVector(new double[] {20.0 ,30.0});
		RealVector b = new ArrayRealVector(new double[] {30.0 ,40.0});
		RealVector pt = new ArrayRealVector(new double[] {15.0 ,18.0});
		
		System.out.println(isLeft(a,b,pt));
		
		List<RealVector> polygon = new ArrayList<RealVector>();
		polygon.add(new ArrayRealVector(new double[] {10.0 ,10.0}));
		polygon.add(new ArrayRealVector(new double[] {10.0 ,20.0}));
		polygon.add(new ArrayRealVector(new double[] {20.0 ,20.0}));
		polygon.add(new ArrayRealVector(new double[] {20.0 ,10.0}));
		polygon.add(new ArrayRealVector(new double[] {10.0 ,10.0}));
		
		System.out.println("POLY " + pointInPolygon(pt, polygon));
	}
	
	public static boolean pointInPolygon(RealVector point, List<RealVector> polygon) {
		
		int windingNumber = 0;
		
		for ( int i = 0; i < polygon.size()-1; i++ ) {
			
			RealVector v1 = polygon.get(i);
			RealVector v2 = polygon.get(i+1);
			
			if ( v1.getEntry(1) <= point.getEntry(1) ) { // start y < P.y
				
				if ( v2.getEntry(1) > point.getEntry(1) ) { // an upward crossing
					
					if ( isLeft(v1, v2, point) > 0 )  ++windingNumber; // point left of edge, have valid up intersect
				}
			}
			else { // y > P.y (no test needed)
				
				if ( v2.getEntry(1) <= point.getEntry(1) ) { // a downward crossing
					
					if ( isLeft(v1, v2, point) < 0 )  --windingNumber; // point right of edge, have valid down intersect
				}
				
			}
		}
		
		return windingNumber != 0;
	}

	public static int getWindingNumber(RealVector point, List<RealVector> polygon) {
		
		int windingNumber = 0;
		
		for ( int i = 0; i < polygon.size(); i++ ) {
			
			//point.
		}
		
		return windingNumber;
	}

	protected static double isLeft(RealVector vertexA, RealVector vertexB, RealVector point) {
		
		RealVector atob = vertexB.subtract(vertexA);
		RealVector atop = point.subtract(vertexA);

		return atob.getEntry(0) * atop.getEntry(1) - atop.getEntry(0) * atob.getEntry(1);
		
	}
}
