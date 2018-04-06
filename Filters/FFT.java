package Inzynierka.Filters;

public class FFT {

	public double x[];
	public double y[];
	double[] f;
	double[] spectrum1;

	int n, m;

	// Lookup tables. Only need to recompute when size of FFT changes.
	double[] cos;
	double[] sin;
	double[] window;

	public double[] getTime() {
		return spectrum1;
	}

	public double[] getValue() {
		return f;
	}

	public FFT(int n) {
		this.n = n;
		this.m = (int) (Math.log(n) / Math.log(2));

		// Make sure n is a power of 2
		if (n != (1 << m))
			throw new RuntimeException("FFT length must be power of 2");

		// precompute tables
		cos = new double[n / 2];
		sin = new double[n / 2];

		// for(int i=0; i<n/4; i++) {
		// cos[i] = Math.cos(-2*Math.PI*i/n);
		// sin[n/4-i] = cos[i];
		// cos[n/2-i] = -cos[i];
		// sin[n/4+i] = cos[i];
		// cos[n/2+i] = -cos[i];
		// sin[n*3/4-i] = -cos[i];
		// cos[n-i] = cos[i];
		// sin[n*3/4+i] = -cos[i];
		// }

		for (int i = 0; i < n / 2; i++) {
			cos[i] = Math.cos(-2 * Math.PI * i / n);
			sin[i] = Math.sin(-2 * Math.PI * i / n);
		}

	}

	/***************************************************************
	 * fft.c Douglas L. Jones University of Illinois at Urbana-Champaign January 19,
	 * 1992 http://cnx.rice.edu/content/m12016/latest/
	 * 
	 * fft: in-place radix-2 DIT DFT of a complex input
	 * 
	 * input: n: length of FFT: must be a power of two m: n = 2**m input/output x:
	 * double array of length n with real part of data y: double array of length n
	 * with imag part of data
	 * 
	 * Permission to copy and use this program is granted as long as this header is
	 * included.
	 ****************************************************************/
	public void fft(double[] re) { // podaj wartoœci sygna³u

		y = new double[re.length];

		for (int l = 0; l == y.length; l++) {
			y[l] = 0;
		}
		int i, j, k, n1, n2, a;
		double c, s, t1, t2;
		x = re;
		// Bit-reverse
		j = 0;
		n2 = n / 2;
		for (i = 1; i < n - 1; i++) {
			n1 = n2;
			while (j >= n1) {
				j = j - n1;
				n1 = n1 / 2;
			}
			j = j + n1;

			if (i < j) {
				t1 = x[i];
				x[i] = x[j];
				x[j] = t1;
				t1 = y[i];
				y[i] = y[j];
				y[j] = t1;
			}
		}

		// FFT
		n1 = 0;
		n2 = 1;

		for (i = 0; i < m; i++) {
			n1 = n2;
			n2 = n2 + n2;
			a = 0;

			for (j = 0; j < n1; j++) {
				c = cos[a];
				s = sin[a];
				a += 1 << (m - i - 1);

				for (k = j; k < n; k = k + n2) {
					t1 = c * x[k + n1] - s * y[k + n1];
					t2 = s * x[k + n1] + c * y[k + n1];
					x[k + n1] = x[k] - t1;
					y[k + n1] = y[k] - t2;
					x[k] = x[k] + t1;
					y[k] = y[k] + t2;
				}
			}
		}
	}
	// -------- mój kod: -----------------------------

	public void FFT2(double[] time) { // podaj czas
		System.out.print(x.length);
		for (int i = 0; i < x.length; i++) {
			System.out.println("x[i] " + x[i]);
			i = i+1;
		}
		
		System.out.println("...........");
		for (int i = 0; i < time.length; i++) {
			System.out.println("time[i] " + time[i]);
		}
		System.out.println("...........");

		int length = x.length;
		System.out.println("length " + length);
		System.out.println("...........");

		double[] spectrum2 = new double[length];

		for (int i = 0; i < length; i++) {
		spectrum2[i] = x[i] / Math.abs(length);
		System.out.println("spectrum2 " + spectrum2[i]);
		}
		System.out.println("...........");
		spectrum1 = new double[length / 2];
		for (int k = 0; k < length / 2; k++) {
			spectrum1[k] = spectrum2[k];
		}
		for (int i = 2; i < (spectrum1.length - i); i++) {
			spectrum1[i] = 2 * spectrum1[i];
			System.out.println("spectrum1 " + spectrum1[i]);
		}
		System.out.println("...........");

		double fs = 1 * 10 / time[10]; // czêstotliwoœæ
		System.out.println("fs " + fs);
		System.out.println("...........");
		
		f = new double[length / 2];
		for (int m = 0; m < length / 2; m++) {
			f[m] = fs * m / length;
			System.out.println("f " + f[m]);
		}
	}

	public void ifft(double[] time, double[] value) {
		double[] time2 = time;
		double[] value2 = value;
		time = value2;
		value = time2;
		fft(value);
		//im = time;
		//re = value;
		//time = re;
		//value = im;
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