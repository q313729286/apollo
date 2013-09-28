package seker.common.utils;

public final class AccuracyUtils {

	public static final float ACCURACY_FLOAT = 0.01f;

	public static final double ACCURACY_DOUBLE = 0.01;

	private AccuracyUtils() {
	}
	
	public static boolean equals(int i, float f) {
		return Math.abs(i - f) < ACCURACY_FLOAT;
	}

	public static boolean equals(float f, int i) {
		return Math.abs(i - f) < ACCURACY_FLOAT;
	}

	public static boolean equals(int i, double d) {
		return Math.abs(i - d) < ACCURACY_DOUBLE;
	}

	public static boolean equals(double d, int i) {
		return Math.abs(i - d) < ACCURACY_DOUBLE;
	}

	public static boolean equals(float f, double d) {
		return Math.abs(f - d) < ACCURACY_DOUBLE;
	}

	public static boolean equals(double d, float f) {
		return Math.abs(d - f) < ACCURACY_DOUBLE;
	}
}
