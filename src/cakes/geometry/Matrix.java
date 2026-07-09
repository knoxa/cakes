package cakes.geometry;

import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealMatrix;

public class Matrix {

	
	public static RealMatrix rotationX(double angle) {
		
		RealMatrix r = MatrixUtils.createRealIdentityMatrix(4);
		
		double s = Math.sin(angle);
		double c = Math.cos(angle);
		
		r.setEntry(1, 1, c);
		r.setEntry(1, 2, -s);
		r.setEntry(2, 1, s);
		r.setEntry(2, 2, c);
		
		return r;
	}
	
	public static RealMatrix rotationY(double angle) {
		
		RealMatrix r = MatrixUtils.createRealIdentityMatrix(4);
		
		double s = Math.sin(angle);
		double c = Math.cos(angle);
		
		r.setEntry(0, 0, c);
		r.setEntry(0, 2, s);
		r.setEntry(2, 0, -s);
		r.setEntry(2, 2, c);
		
		return r;
	}
	
	public static RealMatrix rotationZ(double angle) {
		
		RealMatrix r = MatrixUtils.createRealIdentityMatrix(4);
		
		double s = Math.sin(angle);
		double c = Math.cos(angle);
		
		r.setEntry(0, 0, c);
		r.setEntry(0, 1, -s);
		r.setEntry(1, 0, s);
		r.setEntry(1, 1, c);
		
		return r;
	}

}
