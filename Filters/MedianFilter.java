package Inzynierka.Filters;

import java.util.Arrays;

public class MedianFilter implements Filter {

	public double[] filter(double timeArray[], double valueArray[], int win) {
		double[] newValueArray = new double[valueArray.length]; // create array for filtered values

		// ---------- FILTROWANIE ----------
		int length = timeArray.length;
		if (win == 3) {
			findValueForThree(length, valueArray, newValueArray);
		} else if (win == 5) {
			findValueForFive(length, valueArray, newValueArray);
		} else if (win == 7) {
			findValueForSeven(length, valueArray, newValueArray);
		}
		return newValueArray;
	}

	private void findValueForSeven(int length, double[] inputArray, double[] outputArray) { // Find Median if window of
																							// the filter is 7
		int i = 0;
		double median;
		while (i < length) {
			if (i == 0 || i == 1 || i == 2) { // If it's the first, second or third value, take the first seven values
				median = findMedOfSeven(inputArray[0], inputArray[1], inputArray[2], inputArray[3], inputArray[4],
						inputArray[5], inputArray[6]);
				outputArray[i] = median; // insert median value to your value
				i = i + 1; // take the next value
			} else if (i > 2 && i < (length - 3)) { // When it's possible - take three values before your value, your
													// value, and three after your value
				median = findMedOfSeven(inputArray[i - 3], inputArray[i - 2], inputArray[i - 1], inputArray[i],
						inputArray[i + 1], inputArray[i + 2], inputArray[i + 3]);
				outputArray[i] = median; // insert median value to your value
				i = i + 1; // take the next value
			} else if (i == length - 3 || i == length - 2 || i == length - 1) { // for the last three values - take
																				// seven last values
				median = findMedOfSeven(inputArray[length - 8], inputArray[length - 7], inputArray[length - 6],
						inputArray[length - 5], inputArray[length - 3], inputArray[length - 2], inputArray[length - 1]);
				outputArray[i] = median; // insert median value to your value
				i = i + 1; // take the next value
			}
		}
	}

	private void findValueForFive(int length, double[] inputArray, double[] outputArray) { // Find Median if window of
																							// the filter is 5
		int i = 0;
		double median;
		while (i < length) {
			if (i == 0) { // If it's the first value, take the first five values
				median = findMedOfFive(inputArray[i], inputArray[i + 1], inputArray[i + 2], inputArray[i + 3],
						inputArray[i + 4]);
				outputArray[i] = median; // insert median value to your value
				i = i + 1; // take the next value
			} else if (i == 1) { // If it's the second value take the first five values
				median = findMedOfFive(inputArray[i - 1], inputArray[i], inputArray[i + 1], inputArray[i + 2],
						inputArray[i + 3]);
				outputArray[i] = median; // insert median value to your value
				i = i + 1; // take the next value
			} else if (i > 1 && i < (length - 2)) { // When it's possible - take two values before your value, your
													// value, and two after your value
				median = findMedOfFive(inputArray[i - 2], inputArray[i - 1], inputArray[i], inputArray[i + 1],
						inputArray[i + 2]);
				outputArray[i] = median; // insert median value to your value
				i = i + 1; // take the next value
			} else if (i == length - 2) { // If it's the second value before the last one - take five last values
				median = findMedOfFive(inputArray[i - 3], inputArray[i - 2], inputArray[i - 1], inputArray[i],
						inputArray[i + 1]);
				outputArray[i] = median; // insert median value to your value
				i = i + 1; // take the next value
			} else if (i == length - 1) { // If it's the last one - take five last values
				median = findMedOfFive(inputArray[i - 4], inputArray[i - 3], inputArray[i - 2], inputArray[i - 1],
						inputArray[i]);
				outputArray[i] = median; // insert median value to your value
				i = i + 1; // take the next value

			}
		}
	}

	private void findValueForThree(int length, double[] inputArray, double[] outputArray) { // Find Median if window of
		// the filter is 3
		int i = 0;
		double median;
		while (i < length) {
			if (i == 0) { // If it's the first value, take the first three values
				median = findMedOfThree(inputArray[0], inputArray[1], inputArray[2]);
				outputArray[i] = median; // insert median value to your value
				i = i + 1; // take the next value
			} else if (i > 0 && i < (length - 1)) { // When it's possible - take one value before your value, your
				// value, and one after your value
				median = findMedOfThree(inputArray[i - 1], inputArray[i], inputArray[i + 1]);
				outputArray[i] = median; // insert median value to your value
				i = i + 1; // take the next value
			} else if (i == length - 1) { // if it's the last value - take last three values
				median = findMedOfThree(inputArray[length - 3], inputArray[length - 2], inputArray[length - 1]);
				outputArray[i] = median; // insert median value to your value
				i = i + 1; // take the next value
			}
		}
	}

	private double findMedOfSeven(double a, double b, double c, double d, double e, double f, double g) { // find median
																											// of seven
																											// values
		double[] abcde = new double[7];
		// put values to new array
		abcde[0] = a;
		abcde[1] = b;
		abcde[2] = c;
		abcde[3] = d;
		abcde[4] = e;
		abcde[5] = f;
		abcde[6] = g;
		Arrays.sort(abcde); // sort values
		return abcde[3]; // take median
	}

	private double findMedOfFive(double a, double b, double c, double d, double e) { // find median of five values
		double[] abcde = new double[5];
		// put values to new array
		abcde[0] = a;
		abcde[1] = b;
		abcde[2] = c;
		abcde[3] = d;
		abcde[4] = e;
		Arrays.sort(abcde); // sort values
		return abcde[2]; // take median
	}

	private double findMedOfThree(double a, double b, double c) { // find median of three values
		double[] abcde = new double[3];
		// put values to new array
		abcde[0] = a;
		abcde[1] = b;
		abcde[2] = c;
		Arrays.sort(abcde); // sort values
		return abcde[1]; // take median
	}
}
