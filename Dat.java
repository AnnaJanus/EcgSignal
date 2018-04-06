/*

import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.IOException;

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
*/