package Inzynierka;

import javax.swing.*;
import javax.swing.border.EmptyBorder;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import Inzynierka.Filters.AverageFilter;
import Inzynierka.Filters.FFT;
import Inzynierka.Filters.MedianFilter;

public class EcgVisualizationSystem extends JFrame implements ActionListener, ItemListener {

	static JFrame frame = new myFrame("ECG signal");

	JScrollBar scroll = new JScrollBar(JScrollBar.HORIZONTAL, 0, 40, 0, 500);
	static ArrayList<EcgData> data = new ArrayList<EcgData>(); // include all existing datas without Ecg Input
	JComboBox<String> spectrumList;

	public static JComboBox<String> timeAxis;
	public static JComboBox<String> valueAxis;
	public static JComboBox<Integer> averageWindow;
	public static JComboBox<Integer> medianWindow;

	Reader rdr = new Reader();
	TestReader rdrTest = new TestReader();
	EcgData ecgProc = new EcgData();
	EcgData ecgAverage = new EcgData();
	EcgData ecgMedian = new EcgData();
	EcgData ecgFFT = new EcgData();
	EcgData ecgTest = new EcgData();
	Spectrum spectrum = new Spectrum();
	TestWindow testWindow = new TestWindow();

	XYSeries seriesProc = new XYSeries("Original");
	XYSeries seriesAverage = new XYSeries("Average Filter");
	XYSeries seriesMedian = new XYSeries("Median Filter");
	XYSeries seriesFFT = new XYSeries("FFT Filter");
	XYSeries seriesTest = new XYSeries("Test Series");

	public static int samples;

	public static int scrollPosition;
	public static int scrollMaxPosition;

	private XYPlot plot;
	private ValueAxis xAxis;
	private ValueAxis yAxis;

	public XYDataset dataset;

	public File fileData;
	public File fileTest;

	public static JTextField sChanger;

	public JFreeChart chart;

	Checkbox originalCheckbox;
	Checkbox averageCheckbox;
	Checkbox medianCheckbox;
	Checkbox fftCheckbox;
	Checkbox testCheckbox;

	int n;
	FFT fftObject;
	AverageFilter averageFilter = new AverageFilter();
	MedianFilter medianFilter = new MedianFilter();

	// variables for making a compare with test signal
	public static double originalCompare = 0;
	public static double averageCompare = 0;
	public static double medianCompare = 0;
	public static double FFTCompare = 0;

	public EcgVisualizationSystem() {

		// ---------- WINDOW ----------

		frame.setMinimumSize(new Dimension(1000, 700));
		frame.setLocation(150, 100);

		Container container = getContentPane();
		container.setLayout(new BorderLayout());

		JPanel south = new JPanel();
		JPanel east = new JPanel();
		JPanel west = new JPanel();
		container.add(south, BorderLayout.SOUTH);
		GridLayout gd = new GridLayout(3, 2, 10, 10);
		south.setLayout(gd);
		south.setAlignmentX(RIGHT_ALIGNMENT);
		south.setBorder(new EmptyBorder(8, 8, 8, 8));
		container.add(east, BorderLayout.EAST);
		container.add(west, BorderLayout.WEST);

		JLabel samplesChanger = new JLabel("Number of Samples:");
		sChanger = new JTextField();

		JLabel afilterLabel = new JLabel("Average Filter: ");
		afilterLabel.setForeground(Color.blue);

		JButton afilterButton = new JButton("Average Filter");
		afilterButton.setForeground(Color.blue);
		afilterButton.addActionListener(this);
		JLabel mfilterLabel = new JLabel("Median Filter: ");

		mfilterLabel.setForeground(Color.gray);
		JButton mfilterButton = new JButton("Median Filter");
		mfilterButton.setForeground(Color.gray);
		mfilterButton.addActionListener(this);

		JLabel fftfilterLabel = new JLabel("FFT Filter: ");
		fftfilterLabel.setForeground(Color.BLACK);
		JButton fftfilterButton = new JButton("FFT Filter");
		fftfilterButton.setForeground(Color.BLACK);
		fftfilterButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (fileData != null) {
					if (ecgFFT.getVA() == null) {
						fftCheckbox.setState(true);
						ecgFFT.setTA(ecgProc.getTA());
						ecgFFT.setVA(ecgProc.getVA());
						n = findN(ecgFFT.getVA());
						ecgFFT.setTA(makeArrayNlength(ecgFFT.getTA(), n));
						ecgFFT.setVA(makeArrayNlength(ecgFFT.getVA(), n));
						fftObject = new FFT(n,ecgFFT.getVA(),ecgFFT.getTA());
						ecgFFT.setTA(fftObject.getTime());
						ecgFFT.setVA(fftObject.getValue());
						// fftObject.deleteFrequency(50, 53, ecgFFT.getVA());
						// fftObject.ifft(ecgFFT.getTA(), ecgFFT.getVA());
						data.add(ecgFFT);
						ecgFFT.updateSeries(samples, scrollPosition, scrollMaxPosition);
					} else {
						JOptionPane.showMessageDialog(null, "Signal has already been filtered");
					}
				} else {
					JOptionPane.showMessageDialog(null, "Load the file.");
				}
			}
		});

		JPanel averageF = new JPanel();
		JPanel medianF = new JPanel();
		averageF.setLayout(new GridLayout(1, 2));
		medianF.setLayout(new GridLayout(1, 2));
		JLabel averageL = new JLabel("Window");
		averageWindow = new JComboBox<Integer>();
		averageWindow.addItem(3);
		averageWindow.addItem(5);
		averageWindow.addItem(7);
		averageWindow.setVisible(false);
		averageF.add(averageL);
		averageF.add(averageWindow);
		JLabel medianL = new JLabel("Window");
		medianWindow = new JComboBox<Integer>();
		medianWindow.addItem(3);
		medianWindow.addItem(5);
		medianWindow.addItem(7);
		medianWindow.setVisible(false);
		medianF.add(medianL);
		medianF.add(medianWindow);
		averageL.setForeground(Color.blue);
		medianL.setForeground(Color.gray);

		// ---- change_average_window ---
		averageWindow.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int aWindow = (int) averageWindow.getSelectedItem();
				try {
					double[] filteredValueArray = averageFilter.filter(ecgProc.getTA(), ecgProc.getVA(), aWindow);
					ecgAverage.setVA(filteredValueArray);
					ecgAverage.updateSeries(samples, scrollPosition, scrollMaxPosition);
				} catch (NullPointerException npe) {
					JOptionPane.showMessageDialog(null, "Load the file and filter the signal.");
					ecgAverage.setVA(null);
				}

			}
		});

		// ---- change_median_window ---
		medianWindow.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int mWindow = (int) medianWindow.getSelectedItem();
				try {
					double[] filteredValueArray = medianFilter.filter(ecgProc.getTA(), ecgProc.getVA(), mWindow);
					ecgMedian.setVA(filteredValueArray);
					ecgMedian.updateSeries(samples, scrollPosition, scrollMaxPosition);
				} catch (NullPointerException npe) {
					JOptionPane.showMessageDialog(null, "Load the file and filter the signal.");
					ecgMedian.setVA(null);
				}
			}
		});

		// east_layout
		Box verticalBox = Box.createVerticalBox(); // include all the panels
		Box verticalBox1 = Box.createVerticalBox(); // include items from first panel
		Box verticalBox2 = Box.createVerticalBox(); // include items from second panel
		Box verticalBox3 = Box.createVerticalBox(); // include items from third panel
		Box verticalBox4 = Box.createVerticalBox(); // include items from fourth panel

		Box horizontalBox1 = Box.createHorizontalBox();
		Box horizontalBox2 = Box.createHorizontalBox();
		Box horizontalBox3 = Box.createHorizontalBox();
		Box horizontalBox4 = Box.createHorizontalBox();

		JPanel panel1 = new JPanel();
		panel1.setBorder(BorderFactory.createTitledBorder("Average Filter"));
		JPanel panel2 = new JPanel();
		panel2.setBorder(BorderFactory.createTitledBorder("Median Filter"));
		JPanel panel3 = new JPanel();
		panel3.setBorder(BorderFactory.createTitledBorder("FFT Filter"));
		JPanel panel4 = new JPanel();
		panel4.setBorder(BorderFactory.createTitledBorder("Samples"));

		horizontalBox1.add(afilterLabel);
		horizontalBox1.add(Box.createGlue()); // attach a label to the left
		verticalBox1.add(horizontalBox1);
		verticalBox1.add(Box.createRigidArea(new Dimension(0, 10))); // blank space
		verticalBox1.add(afilterButton);
		verticalBox1.add(Box.createRigidArea(new Dimension(0, 10))); // blank space
		verticalBox1.add(averageF);
		verticalBox1.add(Box.createRigidArea(new Dimension(0, 40))); // blank space
		panel1.add(verticalBox1);
		verticalBox.add(panel1);

		horizontalBox2.add(mfilterLabel);
		horizontalBox2.add(Box.createGlue()); // attach a label to the left
		verticalBox2.add(horizontalBox2);
		verticalBox2.add(Box.createRigidArea(new Dimension(0, 10))); // blank space
		verticalBox2.add(mfilterButton);
		verticalBox2.add(Box.createRigidArea(new Dimension(0, 10))); // blank space
		verticalBox2.add(medianF);
		verticalBox2.add(Box.createRigidArea(new Dimension(0, 40))); // blank space
		panel2.add(verticalBox2);
		verticalBox.add(panel2);

		horizontalBox3.add(fftfilterLabel);
		horizontalBox3.add(Box.createGlue()); // attach a label to the left
		verticalBox3.add(horizontalBox3);
		verticalBox3.add(Box.createRigidArea(new Dimension(0, 10))); // blank space
		verticalBox3.add(fftfilterButton);
		verticalBox3.add(Box.createRigidArea(new Dimension(0, 40))); // blank space
		panel3.add(verticalBox3);
		verticalBox.add(panel3);

		horizontalBox4.add(samplesChanger);
		horizontalBox4.add(Box.createGlue()); // attach a label to the left
		verticalBox4.add(horizontalBox4);
		verticalBox4.add(Box.createRigidArea(new Dimension(0, 10))); // blank space\
		verticalBox4.add(sChanger);
		panel4.add(verticalBox4);
		verticalBox.add(panel4);

		east.add(verticalBox);

		// samples_changer_listener
		sChanger.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				try {
					samples = Integer.parseInt(sChanger.getText()) / 2;
					if (samples > (findTheLongestArray() / 2)) { // check if number of samples isn't too big
						JOptionPane.showMessageDialog(null, "Too many samples!");
					} else {
						for (EcgData d : data) {
							d.updateSeries(samples, scrollPosition, scrollMaxPosition);
						}
					}

				} catch (NumberFormatException ne) {
					JOptionPane.showMessageDialog(null, "Write integer");
				}
			}
		});
		;

		JLabel signal = new JLabel("Signal:");
		signal.setHorizontalAlignment(JLabel.RIGHT);
		south.add(signal);

		// ---------- SET SERIES ----------
		ecgProc.setXYSeries(seriesProc);
		ecgAverage.setXYSeries(seriesAverage);
		ecgMedian.setXYSeries(seriesMedian);
		ecgFFT.setXYSeries(seriesFFT);
		ecgTest.setXYSeries(seriesTest);

		// ---------- AXIS OPTIONS ----------

		timeAxis = new JComboBox<String>();
		JLabel timeLabel = new JLabel("Time unit:");
		timeLabel.setHorizontalAlignment(JLabel.RIGHT);
		//Box timeBox = Box.createHorizontalBox();
		timeLabel.setAlignmentX(JLabel.RIGHT_ALIGNMENT);
		//timeBox.add(timeLabel);
		timeAxis.addItem("s");
		timeAxis.addItem("ms");
		timeAxis.addItem("min");
		timeAxis.setMinimumSize(new Dimension(50, 20));
		timeAxis.setSize(new Dimension(50, 20));
		timeAxis.setMaximumSize(new Dimension(50, 20));
		timeAxis.addItemListener(itemListener);
		timeAxis.setSize(100, timeAxis.getPreferredSize().height);
		
		valueAxis = new JComboBox<String>();
		JLabel valueLabel = new JLabel("Voltage unit:");
		valueLabel.setHorizontalAlignment(JLabel.RIGHT);
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

		originalCheckbox = new Checkbox("Original");
		originalCheckbox.setForeground(Color.red);
		averageCheckbox = new Checkbox("Average filter");
		averageCheckbox.setForeground(Color.blue);
		medianCheckbox = new Checkbox("Median filter");
		medianCheckbox.setForeground(Color.gray);
		fftCheckbox = new Checkbox("FFT filter");
		fftCheckbox.setForeground(Color.black);
		testCheckbox = new Checkbox("Test data");
		testCheckbox.setForeground(Color.pink);

		JPanel panelCheckbox = new JPanel();
		panelCheckbox.setBorder(BorderFactory.createTitledBorder("Show"));
		Box verticalCheckboxBox = Box.createVerticalBox();
		verticalCheckboxBox.add(originalCheckbox);
		verticalCheckboxBox.add(Box.createRigidArea(new Dimension(0, 10))); // blank space
		verticalCheckboxBox.add(averageCheckbox);
		verticalCheckboxBox.add(Box.createRigidArea(new Dimension(0, 10))); // blank space
		verticalCheckboxBox.add(medianCheckbox);
		verticalCheckboxBox.add(Box.createRigidArea(new Dimension(0, 10))); // blank space
		verticalCheckboxBox.add(fftCheckbox);
		verticalCheckboxBox.add(Box.createRigidArea(new Dimension(0, 10))); // blank space
		verticalCheckboxBox.add(testCheckbox);
		verticalCheckboxBox.add(Box.createRigidArea(new Dimension(0, 20))); // blank space
		panelCheckbox.add(verticalCheckboxBox);

		originalCheckbox.addItemListener(itemListener);
		averageCheckbox.addItemListener(itemListener);
		medianCheckbox.addItemListener(itemListener);
		fftCheckbox.addItemListener(itemListener);
		testCheckbox.addItemListener(itemListener);

		// ---------- SPECTRUM WINDOW --------

		JPanel panelSpectrum = new JPanel();
		panelSpectrum.setBorder(BorderFactory.createTitledBorder("Spectrum"));
		Box verticalSpectrumBox = Box.createVerticalBox();
		JLabel showSpectrum = new JLabel("Show spectrum");

		spectrumList = new JComboBox<String>();
		spectrumList.addItem("---");
		spectrumList.addItem("Original");
		spectrumList.addItem("Average");
		spectrumList.addItem("Median");
		spectrumList.addItem("FFT");
		verticalSpectrumBox.add(showSpectrum);
		verticalSpectrumBox.add(Box.createRigidArea(new Dimension(0, 10))); // blank space
		verticalSpectrumBox.add(spectrumList);
		panelSpectrum.add(verticalSpectrumBox);
		Box verticalWestBox = Box.createVerticalBox();
		verticalWestBox.add(panelCheckbox);
		verticalWestBox.add(panelSpectrum);
		west.add(verticalWestBox);
		spectrumList.addItemListener(itemSpectrumListener);

		// ---------- CHART ----------

		dataset = createDataset();
		chart = ChartFactory.createXYLineChart("ECG signal", "Time [s]", "Amplitude [mV]", dataset,
				PlotOrientation.VERTICAL, true, true, false);
		XYPlot plot = (XYPlot) chart.getPlot();

		plot.getRenderer().setSeriesPaint(0, Color.red);
		plot.getRenderer().setSeriesPaint(1, Color.blue);
		plot.getRenderer().setSeriesPaint(2, Color.gray);
		plot.getRenderer().setSeriesPaint(3, Color.black);
		plot.getRenderer().setSeriesPaint(4, Color.pink);

		ChartPanel CP = new ChartPanel(chart);
		CP.setMinimumSize(new Dimension(300, 300));
		CP.setMouseWheelEnabled(true);
		container.add(CP, BorderLayout.CENTER);
		
		//CP.setDomainZoomable(false);
		//CP.setRangeZoomable(false);
		
		/*
		chart.addChangeListener(new ChartChangeListener() {

			@Override
			public void chartChanged(ChartChangeEvent event) {
				// TODO Auto-generated method stub
				try {
					double maxInZoom = chart.getXYPlot().getDomainAxis().getRange().getUpperBound(); // maximum showed time
					double minInZoom = chart.getXYPlot().getDomainAxis().getRange().getLowerBound(); // minimum showed time
					double chartWindowWidth = maxInZoom - minInZoom;
					
					int middlePoint = (scrollPosition * ecgProc.getTA().length) / scrollMaxPosition;
					
					
					if(maxInZoom>ecgProc.getElementOfTA(middlePoint + samples)) {
					//ecgProc.getElementOfTA((ecgProc.getTA().length-1))) {
						scroll.setVisibleAmount(500);
					} else {
						double visibleAmount = chartWindowWidth * 500 / ecgProc.getElementOfTA((ecgProc.getTA().length-1));
						scroll.setVisibleAmount((int) visibleAmount);
					}
					
										
					System.out.println("max" + maxInZoom );
					System.out.println(ecgProc.getElementOfTA(middlePoint + samples));
				} catch (IndexOutOfBoundsException iofbe) {
					System.out.println("index out of bounds");
				} catch(NullPointerException npe) {
					System.out.println("null pointer exception");
				}
			}

		});*/

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
		JMenu datamenu = new JMenu("Data");
		menuBar.add(datamenu);
		JMenu test = new JMenu("Test");
		menuBar.add(test);

		JMenuItem close = new JMenuItem("Close");
		program.add(close);
		close.addActionListener(this);
		JMenuItem txt = new JMenuItem(".txt");
		JMenuItem filterItem = new JMenuItem("Average Filter");
		JMenuItem mfilterItem = new JMenuItem("Median Filter");
		JMenuItem fftfilterItem = new JMenuItem("FFT Filter");
		JMenuItem clear = new JMenuItem("Clear");
		JMenuItem loadTest = new JMenuItem("Load test data");

		load.add(txt);
		analyze.add(filterItem);
		analyze.add(mfilterItem);
		analyze.add(fftfilterItem);
		datamenu.add(clear);
		test.add(loadTest);

		loadTest.addActionListener(this);
		clear.addActionListener(this);
		txt.addActionListener(this);
		filterItem.addActionListener(this);
		mfilterItem.addActionListener(this);
		fftfilterItem.addActionListener(this);

	}

	// ---------- MAIN ----------
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			@Override
			public void run() {
				new EcgVisualizationSystem();
			}
		});
	}

	// ---------- ITEM_LISTENER ----------
	ItemListener itemListener = new ItemListener() {
		public void itemStateChanged(ItemEvent ie) {
			if (ie.getStateChange() == ItemEvent.SELECTED) {
				String click = (String) ie.getItem();
				if (click.equals("mV")) {
					plot = chart.getXYPlot();
					yAxis = plot.getRangeAxis();
					yAxis.setLabel("Amplitude [mV]");
					for (EcgData d : data) {
						d.setVA(d.getValueAsMV());
						d.updateSeries(samples, scrollPosition, scrollMaxPosition);
					}

				} else if (click.equals("V")) {
					plot = chart.getXYPlot();
					yAxis = plot.getRangeAxis();
					yAxis.setLabel("Amplitude [V]");
					for (EcgData d : data) {
						d.setVA(d.getValueAsV());
						d.updateSeries(samples, scrollPosition, scrollMaxPosition);
					}
				} else if (click.equals("nV")) {
					plot = chart.getXYPlot();
					yAxis = plot.getRangeAxis();
					yAxis.setLabel("Amplitude [nV]");
					for (EcgData d : data) {
						d.setVA(d.getValueAsNV());
						d.updateSeries(samples, scrollPosition, scrollMaxPosition);
					}
				} else if (click.equals("ms")) {
					plot = chart.getXYPlot();
					xAxis = plot.getDomainAxis();
					xAxis.setLabel("Time [ms]");
					for (EcgData d : data) {
						d.setTA(d.getTimeAsMS());
						d.updateSeries(samples, scrollPosition, scrollMaxPosition);
					}
				} else if (click.equals("s")) {
					plot = chart.getXYPlot();
					xAxis = plot.getDomainAxis();
					xAxis.setLabel("Time [s]");
					for (EcgData d : data) {
						d.setTA(d.getTimeAsS());
						d.updateSeries(samples, scrollPosition, scrollMaxPosition);
					}
				} else if (click.equals("min")) {
					plot = chart.getXYPlot();
					xAxis = plot.getDomainAxis();
					xAxis.setLabel("Time [min]");
					for (EcgData d : data) {
						d.setTA(d.getTimeAsMIN());
						d.updateSeries(samples, scrollPosition, scrollMaxPosition);
					}
				} else if (click == "Original") {
					if (fileData != null) {
						if (ecgProc.getXYSeries().isEmpty() == false) {

						} else {
							data.add(ecgProc);
							ecgProc.updateSeries(samples, scrollPosition, scrollMaxPosition);
						}
					} else {
						JOptionPane.showMessageDialog(null, "Load the file!");
						originalCheckbox.setState(false);
					}
				} else if (click == "Average filter") {
					if (ecgAverage.getVA()!=null) {
						if (ecgAverage.getXYSeries().isEmpty() == false) {
						} else {
							data.add(ecgAverage);
							ecgAverage.updateSeries(samples, scrollPosition, scrollMaxPosition);
						}
					} else {
						JOptionPane.showMessageDialog(null, "Filter the signal!");
						averageCheckbox.setState(false);
					}
				} else if (click == "Median filter") {
					if (ecgMedian.getVA()!=null) {
						if (ecgMedian.getXYSeries().isEmpty() == false) {
						} else {
							data.add(ecgMedian);
							ecgMedian.updateSeries(samples, scrollPosition, scrollMaxPosition);
						}
					} else {
						JOptionPane.showMessageDialog(null, "Filter the signal!");
						medianCheckbox.setState(false);
					}
				} else if (click == "FFT filter") {
					if (ecgFFT.getVA()!=null) {
						if (ecgFFT.getXYSeries().isEmpty() == false) {
						} else {
							data.add(ecgFFT);
							ecgFFT.updateSeries(samples, scrollPosition, scrollMaxPosition);
						}
					} else {
						JOptionPane.showMessageDialog(null, "Filter the signal!");
						fftCheckbox.setState(false);
					}
				} else if (click == "Test data") {
					if (data.contains(ecgTest)) {
						if (ecgTest.getXYSeries().isEmpty() == false) {
						} else {
							ecgTest.updateSeries(samples, scrollPosition, scrollMaxPosition);
						}
					} else {
						JOptionPane.showMessageDialog(null, "Filter the signal!");
						testCheckbox.setState(false);
					}
				}

			} else if (ie.getStateChange() == ItemEvent.DESELECTED) {
				String click = (String) ie.getItem();
				if (click == "Original") {
					seriesProc.clear();
					data.remove(ecgProc);
				} else if (click == "Average filter") {
					seriesAverage.clear();
					data.remove(ecgAverage);
				} else if (click == "Median filter") {
					seriesMedian.clear();
					data.remove(ecgMedian);
				} else if (click == "FFT filter") {
					seriesFFT.clear();
					data.remove(ecgFFT);
				} else if (click == "Test data") {
					seriesTest.clear();
				}

			}
		}
	};
	// ---------- ITEM_SPECTRUM_LISTENER ----------

	ItemListener itemSpectrumListener = new ItemListener() {
		public void itemStateChanged(ItemEvent ie) {
			if (ie.getStateChange() == ItemEvent.SELECTED) {
				String click = (String) ie.getItem();
				if (click == "Original") {
					if (ecgProc.getTA() != null) {
						spectrum.spectrum("original", ecgProc);
					} else {
						JOptionPane.showMessageDialog(null, "Load the signal!");
					}
				}
				if (click == "Average") {
					if (ecgAverage.getTA() != null) {
						spectrum.spectrum("average", ecgAverage);
					} else {
						JOptionPane.showMessageDialog(null, "Filter the signal!");
					}
				}
				if (click == "Median") {
					if (ecgMedian.getTA() != null) {
						spectrum.spectrum("median", ecgMedian);
					} else {
						JOptionPane.showMessageDialog(null, "Filter the signal!");
					}
				}
				if (click == "FFT") {
					if (ecgFFT.getTA() != null) {
						spectrum.spectrum("FFT", ecgFFT);
					} else {
						JOptionPane.showMessageDialog(null, "Filter the signal!");
					}
				}
			}
		}
	};

	// ---------- SCROLL_LISTENER ----------
	class MyAdjustmentListener implements AdjustmentListener {
		public void adjustmentValueChanged(AdjustmentEvent e) {
			scrollPosition = e.getValue();
			for (EcgData d : data) {
				if (d.getXYSeries().isEmpty() == false) {
					d.updateSeries(samples, scrollPosition, scrollMaxPosition);
				}
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
				if (fileData == null) {
					readFile();
				} else {
					int dialogButton = JOptionPane.YES_NO_OPTION;
					int dialogResult = JOptionPane.showConfirmDialog(this, "Do you want to clear data?", "Warning",
							dialogButton); // https://stackoverflow.com/questions/8689122/joptionpane-yes-no-options-confirm-dialog-box-issue-java/14784981
					if (dialogResult == 0) {
						clearall();
						readFile();
					}
				}
			} else if (click.equals("Clear")) {
				clearall();
			} else if (click.equals("Close")) {
				frame.dispose();
			} else if (click.equals("Average Filter")) {
				if (fileData != null) {
					if (ecgAverage.getVA() == null) {
						averageCheckbox.setState(true);
						averageWindow.setVisible(true);
						double[] filteredValueArray = averageFilter.filter(ecgProc.getTA(), ecgProc.getVA(), 3);
						ecgAverage.setTA(ecgProc.getTA());
						ecgAverage.setVA(filteredValueArray);
						ecgAverage.setT(ecgProc.getT());
						ecgAverage.setV(ecgProc.getV());
						data.add(ecgAverage); // add filtered data to data array
						averageWindow.setVisible(true);
						averageWindow.setSelectedItem(3);
						ecgAverage.updateSeries(samples, scrollPosition, scrollMaxPosition);
					} else {
						JOptionPane.showMessageDialog(null, "Signal has already been filtered");
					}
				} else {
					JOptionPane.showMessageDialog(null, "Load the file.");
				}
			} else if (click.equals("Median Filter")) {
				if (fileData != null) {
					if (ecgMedian.getVA() == null) {
						medianCheckbox.setState(true);
						ecgMedian.setTA(ecgProc.getTA());
						ecgMedian.setVA(ecgProc.getVA());
						double[] filteredValueArray = medianFilter.filter(ecgMedian.getTA(), ecgMedian.getVA(), 3);
						ecgMedian.setVA(filteredValueArray);
						ecgMedian.setT(ecgProc.getT());
						ecgMedian.setV(ecgProc.getV());
						data.add(ecgMedian);
						medianWindow.setVisible(true);
						medianWindow.setSelectedItem(3);
						ecgMedian.updateSeries(samples, scrollPosition, scrollMaxPosition);
					} else {
						JOptionPane.showMessageDialog(null, "Signal has already been filtered");
					}
				} else {
					JOptionPane.showMessageDialog(null, "Load the file.");
				}
			} else if (click.equals("Load test data")) {
				if (fileTest == null) {
					readTestFile();
				} else {
					int dialogButton = JOptionPane.YES_NO_OPTION;
					int dialogResult = JOptionPane.showConfirmDialog(this, "Do you want to clear data?", "Warning",
							dialogButton); // https://stackoverflow.com/questions/8689122/joptionpane-yes-no-options-confirm-dialog-box-issue-java/14784981
					if (dialogResult == 0) {
						clearall();
						readTestFile();
					}
				}
				if (fileTest != null) {
					compareToTest();
					testWindow.testWindow();
				}

			}
		}

	}

	private XYDataset createDataset() {
		XYSeriesCollection dataset = new XYSeriesCollection();
		dataset.addSeries(seriesProc);
		dataset.addSeries(seriesAverage);
		dataset.addSeries(seriesMedian);
		dataset.addSeries(seriesFFT);
		dataset.addSeries(seriesTest);
		return dataset;
	}

	private void clearall() {

		// clear input data
		Reader.ecgInp.setT(null);
		Reader.ecgInp.setTA(null);
		Reader.ecgInp.setV(null);
		Reader.ecgInp.setVA(null);

		// clear file
		fileData = null;
		// clear all datas

		for (EcgData d : data) {
			d.setT(null);
			d.setTA(null);
			d.setV(null);
			d.setVA(null);
		}
		data.clear();

		// clear series
		seriesProc.clear();
		seriesAverage.clear();
		seriesMedian.clear();
		seriesFFT.clear();
		seriesTest.clear();

		// delete scroll and uncheck checkboxes
		scroll.setVisible(false);
		averageWindow.setVisible(false);
		medianWindow.setVisible(false);
		originalCheckbox.setState(false);
		averageCheckbox.setState(false);
		medianCheckbox.setState(false);
		if (spectrum.doesExist() == true) {
			Spectrum.clearSpectrum();
			spectrumList.setSelectedItem("---");
		}

	}

	public static void unitFlag(int change) {
		if (change == 1) {
			timeAxis.setSelectedItem("s");
		}
		if (change == 0) {
			timeAxis.setSelectedItem("s");
			valueAxis.setSelectedItem("mV");
		}
	}

	static int findTheLongestArray() {
		int length = data.get(0).getTA().length; // take the first EcgData and write its length to our length variable
		for (EcgData d : data) {
			if (d.getTA().length > length) // for all EcgData check if data length is not longer than length variable
				length = d.getTA().length; // if its longer write this data length in our length variable
		}
		return length;
	}

	public int findN(double[] value) {
		int k;
		int i = 1;
		while (Math.pow(2, i) < value.length) {
			i++;
		}
		k = (int) Math.pow(2, i);
		return k;
	}

	public double[] makeArrayNlength(double[] array, int n) {
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

	private void readFile() {
		JFileChooser fc = new JFileChooser();
		if (fc.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
			fileData = fc.getSelectedFile(); // load the file from user
			String fileName = fileData.getName(); // load the name of file
			if (fileName.contains("txt")) { // check if file is txt type
				try {
					try {
						scroll.setVisible(true);
						rdr.makeXY(fileData.getAbsolutePath());
						ecgProc.setTA(Reader.ecgInp.getTA());
						ecgProc.setVA(Reader.ecgInp.getVA());
						data.clear();
						ecgProc.setV(Reader.ecgInp.getV());
						ecgProc.setT(Reader.ecgInp.getT());
						ecgProc.updateSeries(samples, scrollPosition, scrollMaxPosition);
						data.add(ecgProc);
						originalCheckbox.setState(true);
					} catch (NumberFormatException nfe) {
						clearall();
						scroll.setVisible(false);
						originalCheckbox.setState(false);
						JOptionPane.showMessageDialog(null, "Data is in wrong format.  Load another file.");
					}

				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} else {
				JOptionPane.showMessageDialog(null, "Your file is not a 'txt' type.");
			}
		}
	}

	private void readTestFile() {
		JFileChooser fc = new JFileChooser();
		if (fc.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
			fileTest = fc.getSelectedFile(); // load the file from user
			String fileName = fileTest.getName(); // load the name of file
			if (fileName.contains("txt")) { // check if file is txt type
				try {
					try {
						rdrTest.makeXY(fileTest.getAbsolutePath());
						ecgTest.setTA(TestReader.ecgInpTest.getTA());
						ecgTest.setVA(TestReader.ecgInpTest.getVA());
						ecgTest.setV(TestReader.ecgInpTest.getV());
						ecgTest.setT(TestReader.ecgInpTest.getT());
						ecgTest.updateSeries(samples, scrollPosition, scrollMaxPosition);
						data.add(ecgTest);
						testCheckbox.setState(true);
					} catch (NumberFormatException nfe) {
						scroll.setVisible(false);
						ecgTest.setTA(null);
						ecgTest.setVA(null);
						fileTest = null;
						testCheckbox.setState(false);
						JOptionPane.showMessageDialog(null, "Data is in wrong format.  Load another file.");
					}

				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} else {
				JOptionPane.showMessageDialog(null, "Your file is not a 'txt' type.");
			}
		}
	}

	private void compareToTest() {
		if (ecgProc.getTA() != null) { // check if file is loaded
			if (ecgProc.getTA().length != ecgTest.getTA().length) { // Does test ecg has the same length as original
																	// ecg?
				JOptionPane.showMessageDialog(null, "Length of original ecg and length of test ecg are not the same");
			} else {
				originalCompare = comparisionAlgoritm(ecgProc);
				if (ecgAverage.getVA() != null) {
					averageCompare = comparisionAlgoritm(ecgAverage);
				}
				if (ecgMedian.getVA() != null) {
					medianCompare = comparisionAlgoritm(ecgMedian);
				}
				if (ecgFFT.getVA() != null) {
					FFTCompare = comparisionAlgoritm(ecgFFT);
				}

			}
		} else {
			JOptionPane.showMessageDialog(null, "Load the file!");
		}
	}

	private double comparisionAlgoritm(EcgData e) {
		double sum = 0;
		for (int k = 0; k < e.getVA().length; k++) {
			double diff = e.getElementOfVA(k) - ecgTest.getElementOfVA(k);
			sum = sum + Math.pow(diff, 2);
		}
		double error = sum / e.getVA().length;
		return error;
	}
	

	@Override
	public void itemStateChanged(ItemEvent arg0) {
		// TODO Auto-generated method stub

	}
}
