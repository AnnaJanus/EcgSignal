package Inzynierka;

public class EcgData {
	private double[] valueArray;
	private double[] timeArray;
	private Time time;
	private Value value;

	EcgData(double[] vA, double[] tA, Time t, Value v) {

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
	
	Value getV(){
		return this.value;
	}
	
	Time getT(){
		return this.time;
	}
	
	boolean isV(){
		if(this.value==null){
			return false;
		}else{
			return true;
		}
	}
	
	boolean isT(){
		if(this.time==null){
			return false;
		}else{
			return true;
		}
	}

	enum Time {
		SECONDS, MILISECONDS, MINUTES
	}

	enum Value {
		MV, V, NV
	}
}