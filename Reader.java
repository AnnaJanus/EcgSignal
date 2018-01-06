package Inzynierka;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.StringTokenizer;

public class Reader {

	public static EcgData ecgInp;
	EcgData.Time sec = EcgData.Time.SECONDS;
	EcgData.Value val = EcgData.Value.MV;
	static public int change1 = 0;
	static double[] timeArray;
	static double[] valueArray;

	public double[] getX() {
		return timeArray;
	}

	public double[] getY() {
		return valueArray;
	}

	// ---------- WYŒWIETLANIE_PLIKU_TEKSTOWEGO_KONSOLA ----------
	public void read(String fileName) throws IOException {
		int i;
		FileInputStream fin = null;

		{
			try {
				fin = new FileInputStream(fileName);
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			do {
				i = fin.read();
				if (i != -1)
					System.out.print((char) i);
			} while (i != -1);
			fin.close();
		}
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
								ecgInp.setT(sec);
							} else if ((a1.equals("mV"))) {
								ecgInp.setV(val);
							}
						}
						if (value.charAt(0) == 40) {
							int num1 = value.indexOf(')');
							String a1 = value.substring(1, num1);
							if (a1.equals("seconds")) {
								ecgInp.setT(sec);
							} else if ((a1.equals("mV")))
								ecgInp.setV(val);
						}

					} else {
						timeEcg.add(Double.parseDouble(time));
						valueEcg.add(Double.parseDouble(value));
					}
				}
			}
		}
		if(ecgInp.isT()==false){
			ecgInp.setT(sec);
		}
		if(ecgInp.isV()==false){
			ecgInp.setV(val);
		}
		System.out.println(timeArray);
		Face.ch(change1);
		fr.close();
		timeArray = new double[timeEcg.size()];
		for (int k = 0; k < timeArray.length; k++) {
			timeArray[k] = timeEcg.get(k);
		}
		ecgInp.setTA(timeArray);
		ecgInp.setVA(valueArray);
		valueArray = new double[valueEcg.size()];
		for (int n = 0; n < valueArray.length; n++) {
			valueArray[n] = valueEcg.get(n);
		}
	}

}
