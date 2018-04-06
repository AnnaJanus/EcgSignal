package Inzynierka;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.JFrame;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import Inzynierka.Filters.FFT;

public class Spectrum extends JFrame {

	static JFrame frame;
	FFT fftObject2;
	static EcgData ecgSpectrum = new EcgData();
	static XYSeries series;

	public void spectrum(String name, EcgData ecg) {
		frame = new JFrame("Spectrum of " + name + " signal");
		frame.setMinimumSize(new Dimension(800, 500));
		frame.setLocation(150, 100);
		frame.setVisible(true);

		// ---------- CHART ----------
		series = new XYSeries(name);
		findSpectrum(ecg);
		makeSeries();
		XYDataset dataset = createDataset();
		JFreeChart chart = ChartFactory.createXYLineChart(name + "Spectrum", "Frequency", "Amplitude", dataset,
				PlotOrientation.VERTICAL, true, true, false);
		ChartPanel CP = new ChartPanel(chart);
		CP.setMinimumSize(new Dimension(300, 300));
		CP.setMouseWheelEnabled(true);
		frame.add(CP);
	}

	private XYDataset createDataset() {
		XYSeriesCollection dataset = new XYSeriesCollection();
		dataset.addSeries(series);
		return dataset;
	}

	private void findSpectrum(EcgData ecg) {
		int n = findN(ecg.getVA());
		ecgSpectrum.setTA(ecg.getTA());
		ecgSpectrum.setVA(ecg.getVA());
		ecgSpectrum.setTA(makeArrayNlength(ecgSpectrum.getTA(), n));
		ecgSpectrum.setVA(makeArrayNlength(ecgSpectrum.getVA(), n));
		fftObject2 = new FFT(n);
		fftObject2.fft( ecgSpectrum.getVA());
		fftObject2.FFT2(ecgSpectrum.getTA());
		ecgSpectrum.setTA(fftObject2.getTime());
		ecgSpectrum.setVA(fftObject2.getValue());
	}

	private void makeSeries() {
		for (int i = 0; i < ecgSpectrum.getTA().length; i++) {
			series.add(ecgSpectrum.getElementOfTA(i), ecgSpectrum.getElementOfVA(i));
		}
	}

	int findN(double[] value) {
		int k;
		int i = 1;
		while (Math.pow(2, i) < value.length) {
			i++;
		}
		k = (int) Math.pow(2, i);
		return k;
	}

	double[] makeArrayNlength(double[] array, int n) {
		if (array.length < n) {
			double[] newArray = new double[n];
			for (int i = 0; i < array.length; i++) {
				newArray[i] = array[i];
			}
			for (int i = array.length; i < n; i++) {
				newArray[i] = 0;
			}
			return newArray;
		} else {
			return array;
		}
	}
	
	public static void clearSpectrum() {
		ecgSpectrum.setT(null);
		ecgSpectrum.setTA(null);
		ecgSpectrum.setV(null);
		ecgSpectrum.setVA(null);
		series.clear();
	}
	
	public boolean doesExist() {
		if(ecgSpectrum.getTA()==null) {
			return false;
		}
		else {
			return true;
		}
	}
}
