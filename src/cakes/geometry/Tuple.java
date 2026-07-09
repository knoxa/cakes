package cakes.geometry;

import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.RealVector;

public class Tuple {
	
	public static double POINT = 1.0, VECTOR = 0.0;
	public static int X = 0, Y = 1, Z = 2, W = 3;
	private static double EPSILON = 1e-4;

	private Tuple() {
	}

	public static RealVector point(double[] values) {
		
		ArrayRealVector v = new ArrayRealVector(4);
		v.setSubVector(0, tuple(values));
		v.setEntry(3, POINT);
		return v;
	}
	
	public static RealVector vector(double[] values) {
		
		ArrayRealVector v = new ArrayRealVector(4);
		v.setSubVector(0, tuple(values));
		v.setEntry(3, VECTOR);
		return v;
	}

	public static RealVector tuple(double[] values) {
		
		ArrayRealVector v = new ArrayRealVector(values);
		return v;
	}

	public static RealVector negate(RealVector tuple) {
		
		return tuple.mapMultiply(-1.0);
	}

	public static RealVector crossProduct(RealVector a, RealVector b) {
		
		ArrayRealVector v = new ArrayRealVector(4);
		v.setEntry(0, a.getEntry(Y) * b.getEntry(Z) - a.getEntry(Z) * b.getEntry(Y));
		v.setEntry(1, a.getEntry(Z) * b.getEntry(X) - a.getEntry(X) * b.getEntry(Z));
		v.setEntry(2, a.getEntry(X) * b.getEntry(Y) - a.getEntry(Y) * b.getEntry(X));
		v.setEntry(3, VECTOR);
		return v;
	}
	
	public static boolean approxEquals(RealVector tuple1, RealVector tuple2) {
		
		return tuple1.subtract(tuple2).getNorm() < EPSILON;
	}

}
