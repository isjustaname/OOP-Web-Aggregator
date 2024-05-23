package trendPackage;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.json.simple.JSONArray; 
import org.json.simple.JSONObject; 

import pairPackage.PairArray; 

/**
 * Dùng để sử lí, lọc các dữ liệu của dữ liệu JSON gắn với object
 */
public class DataExtract {
	private JSONArray jsonFile;
	
	public DataExtract(JSONArray jsonFile){
		this.jsonFile = jsonFile;
	}
	
	/**
	 * Tìm list các năm/tháng/ngày 
	 * @param located word: từ đang được tìm kiếm
	 * @return
	 * 
	 */
	public PairArray dateExtract(String locatedWord){
		PairArray dateAppearance = new PairArray();
		for(TrendData data : TagRecognition.webTagData){
			if(data.publishedDate.isEmpty() || data.publishedDate == "") continue;
			if(data.publishedDate.contains("/")) continue;
			if(data.tagList.contains(locatedWord) == true) {
				String date = data.publishedDate.substring(0, 10);
				if(date == "") continue;
				int index = dateAppearance.indexOfProperty(date);
				if(index == -1) {
					dateAppearance.add(date, 1);
				}
				else {
					dateAppearance.setValue(index, dateAppearance.getValue(index) + 1);
				}
			}
		}
 		dateAppearance.sortProperty();
		return dateAppearance;
	}

	public PairArray extractTagTrend(){
		List<String> everyWebTag = new ArrayList<>();
		for(TrendData data : TagRecognition.webTagData){
			everyWebTag.addAll(data.tagList);
		}
		return heapCountingList(everyWebTag);
	}
	
	/**
	 * Tìm phần trăm các homepage được cào
	 * @return
	 */
	public PairArray mainWebListing(){
		Iterator<?> itr = this.jsonFile.iterator();
		List<String> webList = new ArrayList<>();
 		while (itr.hasNext()){
			JSONObject jsonObject = (JSONObject) itr.next();
			String webUrl = (String) jsonObject.get("webUrl");
			webList.add(webUrl);
		}
		return heapCountingList(webList);
	}
	
	/**
	 * Đếm các từ và vun đống các từ xuất hiện nhiều lên trên
	 * @param list
	 * @return
	 */
	private PairArray heapCountingList(List<String> list){
		PairArray wordAppearance = new PairArray();
		for (String word : list){
			if(word == "") continue; // Lọc cái dấu cách còn sót
			int index = wordAppearance.indexOfProperty(word);
			if(index == -1) {
				wordAppearance.add(word, 1);
			}
			else {
				wordAppearance.setValue(index, wordAppearance.getValue(index) + 1);
				// Vung đống đẻ tìm chữ xuất hiện nhiều nhất
				while(wordAppearance.getValue(index) > wordAppearance.getValue(index/2) && index != 0) {
					Collections.swap(wordAppearance, index, index/2);
					index = index/2;
				}
			}
		}
		return wordAppearance;
	}

	/**
	 * Viết hoa các từ đầu chữ
	 * @param word
	 * @return
	 */
	public static String upperCaseFirst(String word){
		String[] letterList = word.split("[ ]");
		StringBuilder newWord = new StringBuilder();
		for(String letter : letterList){
			//Đi từng chữ và viết hoa chữ đầu
			newWord.append(letter.substring(0, 1).toUpperCase()
			.concat(letter.substring(1)));
			newWord.append(" ");
		}
		newWord.setLength(newWord.length()-1);
		return newWord.toString();
	}

}
