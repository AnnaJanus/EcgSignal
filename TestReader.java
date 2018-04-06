package Inzynierka;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.StringTokenizer;

public class TestReader {

	public static EcgData ecgInpTest = new EcgData();
	EcgData.Time sec = EcgData.Time.SECONDS;
	EcgData.Value valMV = EcgData.Value.MILIVOLTS;
	static public int change1 = 0;
	static double[] timeArrayTest;
	static double[] valueArrayTest;

	public double[] getX() {
		return timeArrayTest;
	}

	public double[] getY() {
		return valueArrayTest;
	}

	// ---------- CZYTANIE_PLIKU_TEKSTOWEGO ----------
	public void makeXY(String fileName) throws IOException {
		
		ArrayList<Double> timeEcg = new ArrayList<Double>();
		ArrayList<Double> valueEcg = new ArrayList<Double>();

		FileReader fr = new FileReader(fileName);
		BufferedReader br = new BufferedReader(fr);
		String s;
		while ((s = br.readLine()) != null) {
			StringTokenizer st = new StringTokenizer(s, " ");
			while (st.hasMoreTokens()) {
				{
					String time = st.nextToken();
					String value = st.nextToken();
					if (time.equals("Elapsed") || value.equals("Elapsed")) {

					} else if (time.equals("time") || value.equals("time")) {
					} else if (time.equals("EKG") || value.equals("EKG")) {
					} else if (time.equals("I") || value.equals("I")) {
					} else if (time.charAt(0) == 40 || value.charAt(0) == 40) {
						if (time.charAt(0) == 40) {
							int num1 = time.indexOf(')');
							String a1 = time.substring(1, num1);
							if (a1.equals("seconds")) {
								ecgInpTest.setT(sec);
							} else if ((a1.equals("mV"))) {
								ecgInpTest.setV(valMV);
							}
						}
						if (value.charAt(0) == 40) {
							int num1 = value.indexOf(')');
							String a1 = value.substring(1, num1);
							if (a1.equals("seconds")) {
								ecgInpTest.setT(sec);
							} else if ((a1.equals("mV")))
								ecgInpTest.setV(valMV);
						}

					} else {
						// change to double, write to arrays
						timeEcg.add(Double.parseDouble(time));
						valueEcg.add(Double.parseDouble(value));
					}
				}
			}
		}
		EcgVisualizationSystem.unitFlag(change1);
		fr.close();
		timeArrayTest = new double[timeEcg.size()];
		for (int k = 0; k < timeArrayTest.length; k++) {
			timeArrayTest[k] = timeEcg.get(k);
		}
		valueArrayTest = new double[valueEcg.size()];
		for (int n = 0; n < valueArrayTest.length; n++) {
			valueArrayTest[n] = valueEcg.get(n);
		}
		ecgInpTest.setTA(timeArrayTest);
		ecgInpTest.setVA(valueArrayTest);
		if (ecgInpTest.isT() == false) {
			ecgInpTest.setT(sec);
		}
		if (ecgInpTest.isV() == false) {
			ecgInpTest.setV(valMV);
		}
	}

}
