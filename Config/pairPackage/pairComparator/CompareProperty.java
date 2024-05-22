package pairPackage.pairComparator;

import java.util.Comparator;

import pairPackage.Pair;

public class CompareProperty implements Comparator<Pair> {

	@Override
	public int compare(Pair p1, Pair p2) {
		return p1.getProperty().compareTo(p2.getProperty()); //Thêm - để sắp 
	}
	
}
