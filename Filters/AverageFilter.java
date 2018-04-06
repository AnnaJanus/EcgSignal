package Inzynierka.Filters;

import Inzynierka.Filters.Filter;

public class AverageFilter implements Filter{

	public double[] filter(double timeArray[], double valueArray[], int win) {
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
