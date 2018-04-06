package Inzynierka.Filters;

public interface Filter {

	public abstract double[] filter(double timeArray[], double valueArray[], int win);
	
}
