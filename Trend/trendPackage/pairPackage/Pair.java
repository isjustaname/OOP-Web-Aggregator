package trendPackage.pairPackage;

/**
 * Một cặp biểu diễn thuộc tính và giá trị của nó
 * @author admin
 *
 */
public class Pair {
	private String property;
	private double value;

	public Pair(String property, double value) {
		super();
		this.property = property;
		this.value = value;
	}

	public String getProperty() {
		return property;
	}

	public void setProperty(String property) {
		this.property = property;
	}

	public double getValue() {
		return value;
	}

	public void setValue(double value) {
		this.value = value;
	}
	
	
}
