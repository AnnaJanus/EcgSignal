package Inzynierka;

import javax.swing.*;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.StringTokenizer;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

public class Face extends JFrame implements ActionListener, ItemListener {

	static JFrame frame = new myFrame("ECG signal");

	JScrollBar scroll = new JScrollBar(JScrollBar.HORIZONTAL, 30, 40, 0, 500);

	static ArrayList<Double> timeEcg = new ArrayList<Double>();
	static ArrayList<Double> valueEcg = new ArrayList<Double>();

	public static JComboBox<String> timeAxis;
	public static JComboBox<String> valueAxis;

	static double timeArray[];
	static double valueArray[];
	double filteredValueArray[];
	double mfilteredValueArray[];
	double fftfilteredValueArray[];
	double fftfilteredTimeArray[];
	double prefftTime[];
	double prefftValue[];

	Filter flt = new Filter();
	Reader rdr = new Reader();
	EcgData ecgProc;

	EcgData.Time sec = EcgData.Time.S;
	EcgData.Time msec = EcgData.Time.MS;
	EcgData.Time min = EcgData.Time.MIN;
	EcgData.Value mvolt = EcgData.Value.MV;
	EcgData.Value nvolt = EcgData.Value.NV;
	EcgData.Value volt = EcgData.Value.V;

	public static int samples;
	public static int aWindow;
	public static int mWindow;

	public static int scrollPosition;
	public static int scrollMaxPosition;

	private XYPlot plot;
	private ValueAxis xAxis;
	private ValueAxis yAxis;

	public XYSeries series;
	public XYSeries filteredSeries;
	public XYSeries mfilteredSeries;
	public XYSeries fftfilteredSeries;
	public XYDataset dataset;

	public File file;

	public String filtered = "no";
	public String mfiltered = "no";
	public String fftfiltered = "no";

	public static JTextField sChanger;
	public static JTextField aChanger;
	public static JTextField mChanger;

	public JFreeChart chart;

	Checkbox origline;
	Checkbox fline1;
	Checkbox fline2;

	int n;
	FFT fftObject;

	public Face() {

		// ---------- WINDOW ----------

		frame.setMinimumSize(new Dimension(800, 500));
		frame.setLocation(150, 100);

		Container container = getContentPane();
		container.setLayout(new BorderLayout());

		JPanel south = new JPanel();
		JPanel east = new JPanel();
		JPanel west = new JPanel();
		container.add(south, BorderLayout.SOUTH);
		south.setLayout(new GridLayout(3, 2, 0, 10));
		container.add(east, BorderLayout.EAST);
		east.setLayout(new GridLayout(12, 1, 0, 10));
		container.add(west, BorderLayout.WEST);
		west.setLayout(new GridLayout(12, 1, 0, 10));

		JLabel samplesChanger = new JLabel("Number of Samples:");
		sChanger = new JTextField();

		JLabel filterLabel = new JLabel("Average Filter: ");
		filterLabel.setForeground(Color.blue);
		JButton filterButton = new JButton("Average Filter");
		filterButton.setForeground(Color.blue);
		filterButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (file.exists()) {
					filtered = "yes";
					fline1.setState(true);
					filteredValueArray = flt.vfilter(timeArray, valueArray);
					fillChart(filteredSeries, timeArray, filteredValueArray);

				}
			}
		});
		JLabel mfilterLabel = new JLabel("Median Filter: ");

		mfilterLabel.setForeground(Color.gray);
		JButton mfilterButton = new JButton("Median Filter");
		mfilterButton.setForeground(Color.gray);
		mfilterButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (file.exists()) {
					mfiltered = "yes";
					fline2.setState(true);
					mfilteredValueArray = flt.mfilter(timeArray, valueArray);
					fillChart(mfilteredSeries, timeArray, mfilteredValueArray);

				}
			}
		});

		JLabel fftfilterLabel = new JLabel("FFT Filter: ");
		fftfilterLabel.setForeground(Color.BLACK);
		JButton fftfilterButton = new JButton("FFT Filter");
		fftfilterButton.setForeground(Color.BLACK);
		fftfilterButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (file.exists()) {
					fftfiltered = "yes";
					// fline2.setState(true);

					if (valueArray.length < n) {
						prefftTime = new double[n];
						prefftValue = new double[n];
						for (int i = 0; i < valueArray.length; i++) {
							prefftTime[i] = timeArray[i];
							prefftValue[i] = valueArray[i];
						}
						for (int i = valueArray.length; i < n; i++) {
							prefftTime[i] = 0;
							prefftValue[i] = 0;
						}
					} else {
						prefftTime = timeArray;
						prefftValue = valueArray;
					}
					fftObject = new FFT(n);
					fftObject.fft(prefftTime, prefftValue);
					fftfilteredTimeArray = fftObject.getTime();
					fftfilteredValueArray = fftObject.getValue();
					fillChart(fftfilteredSeries, fftfilteredTimeArray, fftfilteredValueArray);
					for (int i = 0; i < fftfilteredTimeArray.length; i++) {
						System.out.println(fftfilteredTimeArray[i]);
						System.out.println(fftfilteredValueArray[i]);
					}
				}
			}
		});

		JPanel averageF = new JPanel();
		JPanel medianF = new JPanel();
		averageF.setLayout(new GridLayout(1, 2));
		medianF.setLayout(new GridLayout(1, 2));
		JLabel averageL = new JLabel("Window(2-20)");
		aChanger = new JTextField();
		averageF.add(averageL);
		averageF.add(aChanger);
		JLabel medianL = new JLabel("Window(2-20)");
		mChanger = new JTextField();
		medianF.add(medianL);
		medianF.add(mChanger);
		averageL.setForeground(Color.blue);
		medianL.setForeground(Color.gray);

		aChanger.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					aWindow = Integer.parseInt(aChanger.getText());
					if (aWindow > 20) {
						JOptionPane.showMessageDialog(null, "Too much!");
					} else if (aWindow < 2) {
						JOptionPane.showMessageDialog(null, "Not enough!");
					} else {
						try {
							if (filtered == "yes") {
								fline1.setState(true);
								filteredValueArray = flt.v2filter(timeArray, valueArray, aWindow);
								fillChart(filteredSeries, timeArray, filteredValueArray);
							} else {
								JOptionPane.showMessageDialog(null, "Filter the signal!");
							}
						} catch (NullPointerException npe) {
							JOptionPane.showMessageDialog(null, "No file!");
						}
					}
				} catch (NumberFormatException ne) {
					JOptionPane.showMessageDialog(null, "Write integer");
				}

			}
		});

		mChanger.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					mWindow = Integer.parseInt(mChanger.getText());
					if (mWindow > 20) {
						JOptionPane.showMessageDialog(null, "Too much!");
					} else if (mWindow < 2) {
						JOptionPane.showMessageDialog(null, "Not enough!");
					}
				} catch (NumberFormatException ne) {
					JOptionPane.showMessageDialog(null, "Write integer");
				}
			}
		});

		east.add(filterLabel);
		east.add(filterButton);
		east.add(averageF);
		east.add(mfilterLabel);
		east.add(mfilterButton);
		east.add(medianF);
		east.add(fftfilterLabel);
		east.add(fftfilterButton);

		east.add(samplesChanger);
		east.add(sChanger);
		sChanger.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					samples = Integer.parseInt(sChanger.getText()) / 2;
					if (samples > (timeArray.length / 2)) {
						JOptionPane.showMessageDialog(null, "Too many samples!");
					} else {
						if (file.exists() && origline.getState() == true) {
							fillChart(series, timeArray, valueArray);
						}
						if (filtered == "yes" && fline1.getState() == true) {
							fillChart(filteredSeries, timeArray, filteredValueArray);
						}
						if (mfiltered == "yes" && fline2.getState() == true) {
							fillChart(mfilteredSeries, timeArray, mfilteredValueArray);
						}

					}
				} catch (NumberFormatException ne) {
					JOptionPane.showMessageDialog(null, "Write integer");
				}
			}
		});
		;

		JLabel signal = new JLabel("Signal:");
		south.add(signal);

		// ---------- AXIS OPTIONS ----------

		timeAxis = new JComboBox<String>();
		JLabel timeLabel = new JLabel("Time unit:");
		timeAxis.addItem("s");
		timeAxis.addItem("ms");
		timeAxis.addItem("min");
		timeAxis.setMinimumSize(new Dimension(50, 20));
		timeAxis.setSize(new Dimension(50, 20));
		timeAxis.setMaximumSize(new Dimension(50, 20));
		timeAxis.addItemListener(itemListener);

		valueAxis = new JComboBox<String>();
		JLabel valueLabel = new JLabel("Voltage unit:");
		valueAxis.setSize(100, 10);
		valueAxis.addItem("mV");
		valueAxis.addItem("V");
		valueAxis.addItem("nV");
		valueAxis.addItemListener(this);
		valueAxis.setMinimumSize(new Dimension(50, 20));
		valueAxis.setSize(new Dimension(50, 20));
		valueAxis.setMaximumSize(new Dimension(50, 20));
		valueAxis.addItemListener(itemListener);

		south.add(scroll);
		south.add(timeLabel);
		south.add(timeAxis);
		south.add(valueLabel);
		south.add(valueAxis);

		scroll.addAdjustmentListener(new MyAdjustmentListener());
		scroll.setMinimumSize(new Dimension(200, 20));
		scroll.setSize(new Dimension(200, 20));
		scroll.setMaximumSize(new Dimension(200, 20));
		scrollMaxPosition = scroll.getMaximum();
		scroll.setVisible(false);

		// ---------- CHECKBOX ----------

		origline = new Checkbox("Original");
		fline1 = new Checkbox("Average filter");
		fline1.setForeground(Color.blue);
		fline2 = new Checkbox("Median filter");
		fline2.setForeground(Color.gray);
		west.add(origline);
		west.add(fline1);
		west.add(fline2);
		origline.addItemListener(itemListener);
		fline1.addItemListener(itemListener);
		fline2.addItemListener(itemListener);

		// ---------- CHART ----------
		series = new XYSeries("Original");
		filteredSeries = new XYSeries("Average Filter");
		mfilteredSeries = new XYSeries("Median Filter");
		fftfilteredSeries = new XYSeries("FFT Filter");
		dataset = createDataset(series, filteredSeries, mfilteredSeries, fftfilteredSeries);
		chart = ChartFactory.createXYLineChart("ECG signal", "Time [s]", "Amplitude [mV]", dataset,
				PlotOrientation.VERTICAL, true, true, false);

		ChartPanel CP = new ChartPanel(chart);
		CP.setMinimumSize(new Dimension(300, 300));
		CP.setMouseWheelEnabled(true);
		container.add(CP, BorderLayout.CENTER);

		setMenu();
		frame.add(container);

	}

	// ---------- MENU ----------
	private void setMenu() {
		JMenuBar menuBar = new JMenuBar();
		frame.setJMenuBar(menuBar);
		menuBar.setVisible(true);

		JMenu program = new JMenu("Program");
		menuBar.add(program);
		JMenu load = new JMenu("Load");
		menuBar.add(load);
		JMenu analyze = new JMenu("Analyze");
		menuBar.add(analyze);

		JMenuItem close = new JMenuItem("Close");
		program.add(close);
		close.addActionListener(this);
		JMenuItem txt = new JMenuItem(".txt");
		JMenuItem dat = new JMenuItem(".dat");
		JMenuItem filterItem = new JMenuItem("Average Filter");
		JMenuItem mfilterItem = new JMenuItem("Median Filter");
		load.add(txt);
		load.add(dat);
		analyze.add(filterItem);
		analyze.add(mfilterItem);
		txt.addActionListener(this);
		dat.addActionListener(this);
		filterItem.addActionListener(this);
		mfilterItem.addActionListener(this);

	}

	// ---------- MAIN ----------
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			@Override
			public void run() {
				new Face();
			}
		});
	}

	// ---------- ITEM_LISTENER ----------
	ItemListener itemListener = new ItemListener() {
		public void itemStateChanged(ItemEvent ie) {
			if (ie.getStateChange() == ItemEvent.SELECTED) {
				String click = (String) ie.getItem();
				if (click == "mV") {
					ecgProc.setV(mvolt);
					plot = chart.getXYPlot();
					yAxis = plot.getRangeAxis();
					yAxis.setLabel("Amplitude [mV]");
					if (timeArray != null) {
						toMV(valueArray);
						fillChart(series, timeArray, valueArray);
						if (filtered == "yes") {
							toMV(filteredValueArray);
							fillChart(filteredSeries, timeArray, filteredValueArray);
						}
						if (mfiltered == "yes") {
							toMV(mfilteredValueArray);
							fillChart(mfilteredSeries, timeArray, mfilteredValueArray);
						}
						if (fftfiltered == "yes") {
							toMV(fftfilteredValueArray);
							fillChart(fftfilteredSeries, fftfilteredTimeArray, fftfilteredValueArray);
						}
					}
				} else if (click == "V") {
					ecgProc.setV(volt);
					plot = chart.getXYPlot();
					yAxis = plot.getRangeAxis();
					yAxis.setLabel("Amplitude [V]");
					if (timeArray != null) {
						toV(valueArray);
						fillChart(series, timeArray, valueArray);
						if (filtered == "yes") {
							toV(filteredValueArray);
							fillChart(filteredSeries, timeArray, filteredValueArray);
						}
						if (mfiltered == "yes") {
							toV(mfilteredValueArray);
							fillChart(mfilteredSeries, timeArray, mfilteredValueArray);
						}
						if (fftfiltered == "yes") {
							toV(fftfilteredValueArray);
							fillChart(fftfilteredSeries, fftfilteredTimeArray, fftfilteredValueArray);
						}
					}
				} else if (click == "nV") {
					ecgProc.setV(nvolt);
					plot = chart.getXYPlot();
					yAxis = plot.getRangeAxis();
					yAxis.setLabel("Amplitude [nV]");
					if (timeArray != null) {
						toNV(valueArray);
						fillChart(series, timeArray, valueArray);
						if (filtered == "yes") {
							toNV(filteredValueArray);
							fillChart(filteredSeries, timeArray, filteredValueArray);
						}
						if (mfiltered == "yes") {
							toNV(mfilteredValueArray);
							fillChart(mfilteredSeries, timeArray, mfilteredValueArray);
						}
						if (fftfiltered == "yes") {
							toNV(fftfilteredValueArray);
							fillChart(fftfilteredSeries, fftfilteredTimeArray, fftfilteredValueArray);
						}
					}
				} else if (click == "ms") {
					ecgProc.setT(msec);
					plot = chart.getXYPlot();
					xAxis = plot.getDomainAxis();
					xAxis.setLabel("Time [ms]");
					if (timeArray != null) {
						toMS(timeArray);
						if (fftfiltered == "yes") {
							toMS(fftfilteredTimeArray);
							fillChart(fftfilteredSeries, fftfilteredTimeArray, fftfilteredValueArray);
						}
						fillChart(series, timeArray, valueArray);
						if (filtered == "yes") {
							fillChart(filteredSeries, timeArray, filteredValueArray);
						}
						if (mfiltered == "yes") {
							fillChart(mfilteredSeries, timeArray, mfilteredValueArray);
						}
					}
				} else if (click == "s") {
					ecgProc.setT(sec);
					plot = chart.getXYPlot();
					xAxis = plot.getDomainAxis();
					xAxis.setLabel("Time [s]");
					if (timeArray != null) {
						toS(timeArray);
						fillChart(series, timeArray, valueArray);
						if (fftfiltered == "yes") {
							toS(fftfilteredTimeArray);
							fillChart(fftfilteredSeries, fftfilteredTimeArray, fftfilteredValueArray);
						}
						if (filtered == "yes") {
							fillChart(filteredSeries, timeArray, filteredValueArray);
						}
						if (mfiltered == "yes") {
							fillChart(mfilteredSeries, timeArray, mfilteredValueArray);
						}
					}
				} else if (click == "min") {
					ecgProc.setT(min);
					plot = chart.getXYPlot();
					xAxis = plot.getDomainAxis();
					xAxis.setLabel("Time [min]");
					if (timeArray != null) {
						toMIN(timeArray);
						fillChart(series, timeArray, valueArray);
						if (fftfiltered == "yes") {
							toMIN(fftfilteredTimeArray);
							fillChart(fftfilteredSeries, fftfilteredTimeArray, fftfilteredValueArray);
						}
						if (filtered == "yes") {
							fillChart(filteredSeries, timeArray, filteredValueArray);
						}
						if (mfiltered == "yes") {
							fillChart(mfilteredSeries, timeArray, mfilteredValueArray);
						}
					}
				} else if (click == "Original") {
					fillChart(series, timeArray, valueArray);
				} else if (click == "Average filter") {
					if (fline1.getState() == true) {
						if (filtered == "yes") {
							fillChart(filteredSeries, timeArray, filteredValueArray);
						} else {
							JOptionPane.showMessageDialog(null, "Filter the signal!");
							fline1.setState(false);
						}
					}
				} else if (click == "Median filter") {
					if (fline2.getState() == true) {
						if (mfiltered == "yes") {
							fillChart(mfilteredSeries, timeArray, mfilteredValueArray);
						} else {
							JOptionPane.showMessageDialog(null, "Filter the signal!");
							fline2.setState(false);
						}
					}
				}
			} else if (ie.getStateChange() == ItemEvent.DESELECTED) {
				String click = (String) ie.getItem();
				if (click == "Original") {
					series.clear();
				} else if (click == "Average filter") {
					filteredSeries.clear();
				} else if (click == "Median filter")
					mfilteredSeries.clear();
			}
		}
	};

	// ---------- SCROLL_LISTENER ----------
	// (http://www.zentut.com/java-swing/jscrollbar/)----------
	class MyAdjustmentListener implements AdjustmentListener {
		public void adjustmentValueChanged(AdjustmentEvent e) {
			scrollPosition = e.getValue();
			if (origline.getState() == true) {
				fillChart(series, timeArray, valueArray);
			}
			if (filtered == "yes" && fline1.getState() == true) {
				fillChart(filteredSeries, timeArray, filteredValueArray);
			}
			if (mfiltered == "yes" && fline2.getState() == true) {
				fillChart(mfilteredSeries, timeArray, mfilteredValueArray);
			}
		}
	}

	// ---------- ACTION_LISTENER ----------
	public void actionPerformed(ActionEvent arg) {
		String click = arg.getActionCommand();
		{
			if (click.equals(".txt")) {
				if (sChanger.getText().equals("")) {
					samples = 200;
					sChanger.setText("400");
				}
				JFileChooser fc = new JFileChooser();
				if (fc.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
					file = fc.getSelectedFile();
					try {
						scroll.setVisible(true);
						timeEcg.clear();
						valueEcg.clear();
						rdr.makeXY(file.getAbsolutePath());
						timeArray = rdr.getX();
						valueArray = rdr.getY();
						ecgProc = Reader.ecgInp;
						n = findN(timeArray, valueArray);
						fftObject = new FFT(n);
						fillChart(series, timeArray, valueArray);
						origline.setState(true);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			} else if (click.equals("Close")) {
				frame.dispose();
			} else if (click.equals(".dat")) {
				JFileChooser fc = new JFileChooser();
				if (fc.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
					File file = fc.getSelectedFile();
					try {
						scroll.setVisible(true);
						makedatXY(file.getAbsolutePath());
						fillChart(series, timeArray, valueArray);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
			if (click.equals("Average Filter")) {
				filtered = "yes";
				filteredValueArray = flt.vfilter(timeArray, valueArray);
				fillChart(filteredSeries, timeArray, filteredValueArray);
				fline1.setState(true);
			}
			if (click.equals("Median Filter")) {
				mfiltered = "yes";
				mfilteredValueArray = flt.mfilter(timeArray, valueArray);
				fillChart(mfilteredSeries, timeArray, mfilteredValueArray);
				fline2.setState(true);
			}
		}
	}

	// ---------- CZYTANIE_PLIKU_DAT ----------
	public static void makedatXY(String fileName) throws IOException {
		FileInputStream fis = new FileInputStream(fileName);
		DataInputStream dis = new DataInputStream(fis);
		timeEcg.clear();
		valueEcg.clear();
		int length = dis.available();
		byte[] buf = new byte[length];
		dis.readFully(buf, 0, 12);
		for (byte b : buf) {
			double c = '0';
			if (b != 0)
				c = (double) b;
			System.out.print(c);

		}
		timeArray = new double[timeEcg.size()];
		for (int k = 0; k < timeArray.length; k++) {
			timeArray[k] = timeEcg.get(k);
		}
		valueArray = new double[valueEcg.size()];
		for (int n = 0; n < valueArray.length; n++) {
			valueArray[n] = valueEcg.get(n);
		}

		dis.close();
	}

	private XYDataset createDataset(XYSeries series1, XYSeries series2, XYSeries series3, XYSeries series4) {
		XYSeriesCollection dataset = new XYSeriesCollection();
		dataset.addSeries(series1);
		dataset.addSeries(series2);
		dataset.addSeries(series3);
		dataset.addSeries(series4);
		return dataset;
	}

	public XYSeries fillChart(XYSeries series, double[] TableX, double[] TableY) {
		series.clear();
		int length = TableX.length;
		int length2 = (scrollPosition * TableX.length) / scrollMaxPosition;
		if (length2 < Face.samples) {
			length2 = length2 + (Face.samples - length2);
		}
		if (length2 > (TableX.length - Face.samples)) {
			length2 = length2 - (length2 - (TableX.length - Face.samples));
		}
		if (TableX.length < 1000) {
			for (int i = 0; i < length; i++) {
				series.add(TableX[i], TableY[i]);
			}
		} else {
			for (int k = (length2 - Face.samples); k < (length2 + Face.samples); k++) {
				series.add(TableX[k], TableY[k]);
			}
		}
		return series;
	}

	public void scaleUnit(EcgData inp, EcgData proc) {
		EcgData.Time tUnit = inp.getT();
		EcgData.Time tUnit2 = proc.getT();
		if (tUnit != tUnit2) {

		}
	}

	public static void toS(double[] time) {

		int length1 = time.length;
		if (tunit == 1) {
			for (int i = 0; i < length1; i++) {
				time[i] = time[i] / 1000;
			}
			samples = 100;
			sChanger.setText("200");

		} else if (tunit == 3) {
			for (int i = 0; i < length1; i++) {
				time[i] = time[i] * 60;
			}
			samples = 100;
			sChanger.setText("200");
		}
	}

	public static void toMS(double[] time) {
		int length1 = time.length;
		if (tunit == 2) {
			for (int i = 0; i < length1; i++) {
				time[i] = time[i] * 1000;
			}
			samples = 10;
			sChanger.setText("20");
		} else if (tunit == 3) {
			for (int i = 0; i < length1; i++) {
				time[i] = time[i] * 60000;
			}
			samples = 10;
			sChanger.setText("20");
		}
	}

	public static void toMIN(double[] time) {
		int length1 = time.length;
		if (tunit == 1) {
			for (int i = 0; i < length1; i++) {
				time[i] = time[i] / 60000;
			}
			samples = length1 / 2;
			sChanger.setText(Integer.toString(length1));
		} else if (tunit == 2) {
			for (int i = 0; i < length1; i++) {
				time[i] = time[i] / 60;
			}
			samples = length1 / 2;
			sChanger.setText(Integer.toString(length1));
		}
	}

	public static void toMV(double[] value) {
		int length1 = value.length;
		if (vunit == 2) {
			for (int i = 0; i < length1; i++) {
				value[i] = value[i] * 1000;
			}
		} else if (vunit == 3) {
			for (int i = 0; i < length1; i++) {
				value[i] = value[i] / 1000;
			}
		}
	}

	public static void toNV(double[] value) {
		int length1 = value.length;
		if (vunit == 1) {
			for (int i = 0; i < length1; i++) {
				value[i] = value[i] * 1000;
			}
		} else if (vunit == 2) {
			for (int i = 0; i < length1; i++) {
				value[i] = value[i] * 1000000;
			}
		}
	}

	public static void toV(double[] value) {
		int length1 = value.length;
		if (vunit == 1) {
			for (int i = 0; i < length1; i++) {
				value[i] = value[i] / 1000;
			}
		} else if (vunit == 3) {
			for (int i = 0; i < length1; i++) {
				value[i] = value[i] / 1000000;
			}
		}
	}

	public int findN(double[] time, double[] value) {
		int k;
		int i = 1;
		while (Math.pow(2, i) < value.length) {
			i++;
		}
		k = (int) Math.pow(2, i);
		return k;
	}

	public static void ch(int change) {
		if (change == 1) {
			timeAxis.setSelectedItem("s");
		}
		if (change == 0) {
			timeAxis.setSelectedItem("s");
			valueAxis.setSelectedItem("mV");
		}
	}

	@Override
	public void itemStateChanged(ItemEvent e) {
		// TODO Auto-generated method stub

	}
}
