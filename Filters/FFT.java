package Inzynierka.Filters;

public class FFT {

	public double[] x;
	public double[] y;
	double[] f;
	double[] spectrum1;

	int n, m;

	// Lookup tables. Only need to recompute when size of FFT changes.
	double[] cos;

	double[] window;

	public FFT(int nInp, double[] xInp, double[] yInp) {
		n = nInp;
		x = new double[xInp.length];
		y = new double[xInp.length];
		for (int i = 0; i < xInp.length; i++) {
			x[i] = xInp[i];
			y[i] = 0; // x and y arrays are always the same length
		}
		m = (int) (Math.log(xInp.length) / Math.log(2));
		FFT2(y);

	}

	public double[] getTime() {
		return spectrum1;
	}

	public double[] getValue() {
		return f;
	}

	// ***************************************************************/
	// fft.c
	// Douglas L. Jones
	// University of Illinois at Urbana-Champaign
	// January 19, 1992
	//
	// fft: in-place radix-2 DIT DFT of a complex input
	//
	// input:
	// n: length of FFT: must be a power of two
	// m: n = 2**m
	// input/output
	// x: double array of length n with real part of data
	// y: double array of length n with imag part of data
	//
	// Permission to copy and use this program is granted
	// as long as this header is included.
	// ***************************************************************/

	private double[] fft() {
		int j = 0; // bit-reverse
		int n2 = n / 2;
		int n1;
		for (int i = 0; i < n - 2; i++) {
			n1 = n2;
			while (j >= n1) {
				j = j - n1;
				n1 = n1 / 2;
			}
			j = j + n1;

			if (i < j) {
				double t1;
				t1 = x[i+1];
				x[i+1] = x[j];
				x[j] = t1;
				t1 = y[i];
				y[i] = y[j];
				y[j] = t1;
			}
		}

		n1 = 0;
		n2 = 1;
		for (int i = 0; i <= (m - 1); i++) {
			n1 = n2;
			n2 = n2 + n2;
			double e = -2 * Math.PI / n2;
			double a = 0;

			for (j = 0; j <= (n1 - 1); j++) {
				double c = Math.cos(a);
				double s = Math.sin(a);
				a = a + e;

				for (int k = j; k <= (n - 1); k = k + n2) {
					double t1 = c * x[k + n1] - s * y[k + n1];
					double t2 = s * x[k + n1] + c * y[k + n1];
					x[k + n1] = x[k] - t1;
					y[k + n1] = y[k] - t2;
					x[k] = x[k] + t1;
					y[k] = y[k] + t2;
				}
			}
		}
		return x;
	}

	// -------- mój kod: -----------------------------

	public void FFT2(double[] time) { // podaj czas

		double fs = n; // sampling frequency
		//System.out.println("fs" + fs);
		double T = 1 / fs;
		//System.out.println("T" + T);
		int l = x.length;
		//System.out.println("L" + l);
		double[] t = new double[l];

		for (int i = 0; i < t.length; i++) {
			t[i]=i*T;
		//	System.out.println("t" + t[i]);
		}
		
		for (int i = 0; i < x.length; i++) {
			i = i + 1;
		}

		double[] spectrum2 = new double[l];

		double[] Y = fft();
		for (int i = 0; i < l; i++) {
			spectrum2[i] = Math.abs(Y[i] / l);
		}

		spectrum1 = new double[l / 2 + 1];
		for (int k = 0; k < spectrum1.length; k++) {
			spectrum1[k] = spectrum2[k];
		}
		for (int i = 2; i < (spectrum1.length - i); i++) {
			spectrum1[i] = 2 * spectrum1[i];
		}

		f = new double[l / 2 + 1];
		for (int m = 0; m < (l / 2) + 1; m++) {
			f[m] = fs * m / l;
		}
		
		for (int i = 0; i < f.length; i++) {
		//	System.out.println("f" + f[i]);
		}
		
		for (int i = 0; i < Y.length; i++) {
				System.out.println("Y" + Y[i]);
			}
		
		for (int i = 0; i < spectrum1.length; i++) {
			System.out.println("sp1 " + spectrum1[i]);
		}
		
		for (int i = 0; i < spectrum2.length; i++) {
			System.out.println("sp2 " + spectrum2[i]);
		}
	}

	public void ifft(double[] time, double[] value) {
		double[] time2 = time;
		double[] value2 = value;
		time = value2;
		value = time2;
		// fft(value);
		// im = time;
		// re = value;
		// time = re;
		// value = im;
		for (int i = 0; i < time.length; i++) {
			time[i] = time[i] / (double) n;
			value[i] = value[i] / (double) n;
		}
	}

	public void deleteFrequency(int frequencyA, int frequencyB, double[] value) {
		if (frequencyB > frequencyA) {
			for (int i = 0; i < value.length; i++) {
				if (value[i] >= frequencyA) {
					if (value[i] <= frequencyB) {
						value[i] = 0;
					}
				}
			}
		} else {
			System.out.print("Second frequency must be bigger than the first!");
		}
	}

}