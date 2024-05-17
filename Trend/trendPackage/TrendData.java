package trendPackage;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import pairPackage.PairArray;

public class TrendData {
	public Set<String> tag_list = new HashSet<>();
	public String published_date;
	
	public void filterKeyWord(){
		Set<String> copy_tag_list = new HashSet<>(tag_list);
		for(String tag : copy_tag_list){
			if(TagRecognition.isBlockchainKeyword(tag) == false){
				tag_list.remove(tag);
			}
		}
	}
}
