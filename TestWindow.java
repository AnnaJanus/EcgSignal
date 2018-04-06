package Inzynierka;

import java.awt.Dimension;

import javax.swing.Box;
import javax.swing.JFrame;
import javax.swing.JLabel;

public class TestWindow extends JFrame {

	static JFrame frame;

	public void testWindow() {
		frame = new JFrame("Test window");
		frame.setMinimumSize(new Dimension(800, 500));
		frame.setLocation(150, 100);
		frame.setVisible(true);

		Box verticalBox = Box.createVerticalBox();

		JLabel main = new JLabel("Mean Squared Error:");
		JLabel original;
		JLabel average;
		JLabel median;
		JLabel FFT;

		if (EcgVisualizationSystem.originalCompare != 0) {
			original = new JLabel("original: " + EcgVisualizationSystem.originalCompare);
		} else {
			original = new JLabel("original: " + "no data");
		}

		if (EcgVisualizationSystem.averageCompare != 0) {
			average = new JLabel("average: " + EcgVisualizationSystem.averageCompare);
		} else {
			average = new JLabel("average: " + "no data");
		}

		if (EcgVisualizationSystem.medianCompare != 0) {
			median = new JLabel("median: " + EcgVisualizationSystem.medianCompare);
		} else {
			median = new JLabel("median: " + "no data");
		}

		if (EcgVisualizationSystem.FFTCompare != 0) {
			FFT = new JLabel("FFT: "+ EcgVisualizationSystem.FFTCompare);
		} else {
			FFT = new JLabel("FFT: " + "no data");
		}
		
		verticalBox.add(main);
		verticalBox.add(Box.createRigidArea(new Dimension(0, 10))); // blank space
		verticalBox.add(original);
		verticalBox.add(Box.createRigidArea(new Dimension(0, 10))); // blank space
		verticalBox.add(average);
		verticalBox.add(Box.createRigidArea(new Dimension(0, 10))); // blank space
		verticalBox.add(median);
		verticalBox.add(Box.createRigidArea(new Dimension(0, 10))); // blank space
		verticalBox.add(FFT);

		frame.add(verticalBox);
	}
}
