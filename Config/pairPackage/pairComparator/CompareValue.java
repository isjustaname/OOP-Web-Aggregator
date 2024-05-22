package pairPackage.pairComparator;

import java.util.Comparator;

import pairPackage.Pair;

public class CompareValue implements Comparator<Pair> {
	
	@Override
	public int compare(Pair p1, Pair p2) {
		return (int) (p1.getValue() - p2.getValue());
	}
}
