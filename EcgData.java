package Inzynierka;

import org.jfree.data.xy.XYSeries;

public class EcgData {
	private double[] valueArray;
	private double[] timeArray;
	private Time time;
	private Value value;
	private XYSeries xySeries;

	EcgData() {
	}

	EcgData(double[] vA, double[] tA, Time t, Value v, XYSeries xys) {
	}

	void setVA(double[] vA) {
		this.valueArray = vA;
	}

	void setTA(double[] tA) {
		this.timeArray = tA;
	}

	void setT(Time t) {
		this.time = t;
	}

	void setV(Value v) {
		this.value = v;
	}

	void setXYSeries(XYSeries xys) {
		this.xySeries = xys;
	}

	Value getV() {
		return this.value;
	}

	Time getT() {
		return this.time;
	}

	double[] getVA() {
		return this.valueArray;
	}

	double getElementOfVA(int i) {
		return this.valueArray[i];
	}

	double[] getTA() {
		return this.timeArray;
	}

	XYSeries getXYSeries() {
		return this.xySeries;
	}

	double getElementOfTA(int i) {
		return this.timeArray[i];
	}

	double[] getValueAsV() {
		if (this.value == Value.MILIVOLTS) {
			for (int i = 0; i < this.getVA().length; i++) {
				this.valueArray[i] = this.valueArray[i] / 1000;
			}
			this.value = Value.VOLTS;
			return this.valueArray;
		} else if (this.value == Value.NANOVOLTS) {
			for (int i = 0; i < this.getVA().length; i++) {
				this.valueArray[i] = this.valueArray[i] / 1000000;
			}
			this.value = Value.VOLTS;
			return this.valueArray;
		} else {
			return this.valueArray;
		}
	}

	double[] getValueAsNV() {
		if (this.value == Value.MILIVOLTS) {
			for (int i = 0; i < this.getVA().length; i++) {
				this.valueArray[i] = this.valueArray[i] * 1000;
			}
			this.value = Value.NANOVOLTS;
			return this.valueArray;
		} else if (this.value == Value.VOLTS) {
			for (int i = 0; i < this.getVA().length; i++) {
				this.valueArray[i] = this.valueArray[i] * 1000000;
			}
			this.value = Value.NANOVOLTS;
			return this.valueArray;
		} else {
			return this.valueArray;
		}
	}

	double[] getValueAsMV() {
		if (this.value == Value.VOLTS) {
			for (int i = 0; i < this.getVA().length; i++) {
				this.valueArray[i] = this.valueArray[i] * 1000;
			}
			this.value = Value.MILIVOLTS;
			return this.valueArray;
		} else if (this.value == Value.NANOVOLTS) {
			for (int i = 0; i < this.getVA().length; i++) {
				this.valueArray[i] = this.valueArray[i] / 1000;
			}
			this.value = Value.MILIVOLTS;
			return this.valueArray;
		} else {
			return this.valueArray;
		}
	}

	double[] getTimeAsS() {
		if (this.time == Time.MILISECONDS) {
			for (int i = 0; i < this.getVA().length; i++) {
				this.timeArray[i] = this.timeArray[i] / 1000;
			}
			this.time = Time.SECONDS;
			return this.timeArray;
		} else if (this.time == Time.MINUTES) {
			for (int i = 0; i < this.getVA().length; i++) {
				this.timeArray[i] = this.timeArray[i] * 60;
			}
			this.time = Time.SECONDS;
			return this.timeArray;
		} else {
			return this.timeArray;
		}
	}

	double[] getTimeAsMS() {
		if (this.time == Time.SECONDS) {
			for (int i = 0; i < this.getVA().length; i++) {
				this.timeArray[i] = this.timeArray[i] * 1000;
			}
			this.time = Time.MILISECONDS;
			return this.timeArray;
		} else if (this.time == Time.MINUTES) {
			for (int i = 0; i < this.getVA().length; i++) {
				this.timeArray[i] = this.timeArray[i] * 60000;
			}
			this.time = Time.MILISECONDS;
			return this.timeArray;
		} else {
			return this.timeArray;
		}
	}

	double[] getTimeAsMIN() {
		if (this.time == Time.SECONDS) {
			for (int i = 0; i < this.getVA().length; i++) {
				this.timeArray[i] = this.timeArray[i] / 60;
			}
			this.time = Time.MINUTES;
			return this.timeArray;
		} else if (this.time == Time.MILISECONDS) {
			for (int i = 0; i < this.getVA().length; i++) {
				this.timeArray[i] = this.timeArray[i] / 60000;
			}
			this.time = Time.MINUTES;
			return this.timeArray;
		} else {
			return this.timeArray;
		}
	}

	boolean isV() {
		if (this.value == null) {
			return false;
		} else {
			return true;
		}
	}

	boolean isT() {
		if (this.time == null) {
			return false;
		} else {
			return true;
		}
	}

	enum Time {
		SECONDS, MILISECONDS, MINUTES
	}

	enum Value {
		MILIVOLTS, VOLTS, NANOVOLTS
	}

	void updateSeries(int samples, int scrollPosition, int scrollMaxPosition) {
		this.getXYSeries().clear();
		int length = this.getTA().length;
		int middlePoint = (scrollPosition * length) / scrollMaxPosition;
		if (middlePoint < samples) {
			middlePoint = middlePoint + (samples - middlePoint);
		}
		if (middlePoint > (length - samples)) {
			middlePoint = middlePoint - (middlePoint - (length - samples));
		}
		if (length < 1000) {
			for (int i = 0; i < this.getTA().length; i++) {
				xySeries.add(this.getElementOfTA(i), this.getElementOfVA(i));
			}
		} else {
			for (int k = (middlePoint - samples); k < (middlePoint + samples); k++) {
				xySeries.add(this.getElementOfTA(k), this.getElementOfVA(k));
			}
		}
	}

}