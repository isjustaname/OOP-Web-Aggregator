package pairPackage;
import java.util.ArrayList;
import java.util.Collections;

import pairPackage.pairComparator.CompareProperty;
import pairPackage.pairComparator.CompareValue;
/** 
*1 dãy các cặp Pair thêm một số hàm hơi khác với List
*@param first là dãy thứ 1
*@param second là dãy thứ 2
*/
public class PairArray extends ArrayList<Pair>{
	
	/**
	 * Sắp xếp dãy theo thuộc tính (proprety)
	 */
	public void sortProperty() {
		Collections.sort(this, Collections.reverseOrder(new CompareProperty())); // Xếp theo giảm dần
	}
	
	/**
	 * Sắp xếp dãy theo giá trị (value)
	 */
	public void sortValue() {
		Collections.sort(this, Collections.reverseOrder(new CompareValue()));
	}
	
	public void add(String first, double second){
		this.add(new Pair(first, second));
	}
	
	public String getProperty(int index) {
		return this.get(index).getProperty();
	}

	public double getValue(int index) {
		return this.get(index).getValue();
	}
	
	public void setProperty(int index, String s) {
		this.set(index, new Pair(s, this.getValue(index)));
	}
	
	public void setValue(int index, double d) {
		this.set(index, new Pair(this.getProperty(index), d));
	}
	
	public int indexOfProperty(String s) {
		for(int i = 0; i< this.size(); i++) {
			if(this.get(i).getProperty().equals(s)) {
				return i;
			}
		}
		return -1;
	}
	
	public int indexOfValue(double d) {
		for(int i = 0; i< this.size(); i++) {
			if(this.get(i).getValue() == d) {
				return i;
			}
		}
		return -1;
	
	}
	
	public PairArray subList(int begin, int end) {
		PairArray new_list = new PairArray();
		for(int i = begin; i<end; i++) {
			new_list.add(this.get(i));
		}
		return new_list;
	}
	
	public void printPair() {
		for(Pair p : this) {
			System.out.println(p.getProperty() + " : " + p.getValue());
		}
	}

}
