package Inzynierka;

import java.util.Arrays;

public class Filter {
	double[] vfilter(double timeArray[], double valueArray[]) {
		double[] newValueArray = new double[valueArray.length];

		// ---------- FILTROWANIE ----------
		int i = 0;
		int length = timeArray.length;
		double srednia;
		while (i < length) {
			if (i == 0) {
				srednia = (valueArray[i] + valueArray[i + 1] + valueArray[i + 2] + valueArray[i + 3]
						+ valueArray[i + 4]) / 5;
				newValueArray[i] = srednia;
				i = i + 1;
			} else if (i == 1) {
				srednia = (valueArray[i - 1] + valueArray[i] + valueArray[i + 1] + valueArray[i + 2]
						+ valueArray[i + 3]) / 5;
				newValueArray[i] = srednia;
				i = i + 1;
			} else if (i > 1 && i < (length - 2)) {
				srednia = (valueArray[i - 2] + valueArray[i - 1] + valueArray[i] + valueArray[i + 1]
						+ valueArray[i + 2]) / 5;
				newValueArray[i] = srednia;
				i = i + 1;
			} else if (i == length - 2) {
				srednia = (valueArray[i - 3] + valueArray[i - 2] + valueArray[i - 1] + valueArray[i]
						+ valueArray[i + 1]) / 5;
				newValueArray[i] = srednia;
				i = i + 1;
			} else if (i == length - 1) {
				srednia = (valueArray[i - 4] + valueArray[i - 3] + valueArray[i - 2] + valueArray[i - 1]
						+ valueArray[i]) / 5;
				newValueArray[i] = srednia;
				i = i + 1;

			}
		}
		return newValueArray;

	}

	double[] mfilter(double timeArray[], double valueArray[]) {
		double[] newValueArray = new double[valueArray.length];

		// ---------- FILTROWANIE ----------
		int i = 0;
		int length = timeArray.length;
		double mediana;
		while (i < length) {
			if (i == 0) {
				mediana = findMed(valueArray[i], valueArray[i + 1], valueArray[i + 2], valueArray[i + 3],
						valueArray[i + 4]);
				newValueArray[i] = mediana;
				i = i + 1;
			} else if (i == 1) {
				mediana = findMed(valueArray[i - 1], valueArray[i], valueArray[i + 1], valueArray[i + 2],
						valueArray[i + 3]);
				newValueArray[i] = mediana;
				i = i + 1;
			} else if (i > 1 && i < (length - 2)) {
				mediana = findMed(valueArray[i - 2], valueArray[i - 1], valueArray[i], valueArray[i + 1],
						valueArray[i + 2]);
				newValueArray[i] = mediana;
				i = i + 1;
			} else if (i == length - 2) {
				mediana = findMed(valueArray[i - 3], valueArray[i - 2], valueArray[i - 1], valueArray[i],
						valueArray[i + 1]);
				newValueArray[i] = mediana;
				i = i + 1;
			} else if (i == length - 1) {
				mediana = findMed(valueArray[i - 4], valueArray[i - 3], valueArray[i - 2], valueArray[i - 1],
						valueArray[i]);
				newValueArray[i] = mediana;
				i = i + 1;

			}
		}
		return newValueArray;

	}

	private double findMed(double a, double b, double c, double d, double e) {
		double[] abcde = new double[5];
		abcde[0] = a;
		abcde[1] = b;
		abcde[2] = c;
		abcde[3] = d;
		abcde[4] = e;
		Arrays.sort(abcde);
		return abcde[2];
	}

	double[] v2filter(double timeArray[], double valueArray[], int win) {
		double[] newValueArray = new double[valueArray.length];

		// ---------- FILTROWANIE ----------
		int i = 0;
		int length = timeArray.length;
		double srednia = 0;
		int k;
		while (i < length) {
			if (win % 2 == 0) {

				if (i < (win + 2) / 2) {
					for (k = 0; k < win; k++) {
						srednia = srednia + valueArray[k];
					}
				} else if (i > length - (win + 2) / 2) {
					for (k = length - win; k < length; k++) {
						srednia = srednia + valueArray[k];
					}
				} else {
					for (k = i - win / 2; k < i + win / 2; k++) {
						srednia = srednia + valueArray[k];
					}
				}
			} else {
				if (i < (win + 1) / 2) {
					for (k = 0; k < win; k++) {
						srednia = srednia + valueArray[k];
					}
				} else if (i > length - (win + 1) / 2) {
					for (k = length - win; k < length; k++) {
						srednia = srednia + valueArray[k];
					}
				} else {
					for (k = i - (win - 1) / 2; k < i + (win - 1) / 2 + 1; k++) {
						srednia = srednia + valueArray[k];
					}
				}

			}
			newValueArray[i] = srednia/win;
			srednia=0;
			i = i + 1;
		}
		return newValueArray;

	}
}
