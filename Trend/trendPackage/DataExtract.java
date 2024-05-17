package trendPackage;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.Buffer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.json.simple.JSONArray; 
import org.json.simple.JSONObject; 
import org.json.simple.parser.*;

import pairPackage.PairArray; 

/**
 * Dùng để sử lí, lọc các dữ liệu của dữ liệu JSON gắn với object
 */
public class DataExtract {
	private JSONArray json_file;
	
	public DataExtract(JSONArray json_file){
		this.json_file = json_file;
	}
	
	public PairArray extractTagTrend(){
		List<String> every_web_tag = new ArrayList<>();
		for(TrendData data : TagRecognition.web_tag_data){
			every_web_tag.addAll(data.tag_list);
		}
		return heapCountingList(every_web_tag);
	}
	
	/**
	 * Tìm phần trăm các homepage được cào
	 * @return
	 */
	public PairArray mainWebListing(){
		Iterator<?> itr = this.json_file.iterator();
		List<String> web_list = new ArrayList<>();
 		while (itr.hasNext()){
			JSONObject json_object = (JSONObject) itr.next();
			String web_url = (String) json_object.get("web_url");
			web_list.add(web_url);
		}
		return heapCountingList(web_list);
	}
	
	/**
	 * Đếm các từ và vun đống các từ xuất hiện nhiều lên trên
	 * @param list
	 * @return
	 */
	private PairArray heapCountingList(List<String> list){
		PairArray word_appearance = new PairArray();
		for (String word : list){
			if(word == "") continue; // Lọc cái dấu cách còn sót
			int index = word_appearance.indexOfProperty(word);
			if(index == -1) {
				word_appearance.add(word, 1);
			}
			else {
				word_appearance.setValue(index, word_appearance.getValue(index) + 1);
				// Vung đống đẻ tìm chữ xuất hiện nhiều nhất
				while(word_appearance.getValue(index) > word_appearance.getValue(index/2) && index != 0) {
					Collections.swap(word_appearance, index, index/2);
					index = index/2;
				}
			}
		}
		return word_appearance;
	}
	
	/**
	 * Tìm xem trong web có xuất hiện ( nói đến ) từ cần tìm không
	 * @param json_object
	 * @param locating_word
	 * @return
	 */
	public static boolean locateWord(JSONObject json_object, String locating_word) {
		for(TrendData data : TagRecognition.web_tag_data) {
			if(data.tag_list.contains(locating_word) || data.tag_list.contains(upperCaseFirst(locating_word))){
				return true;
			}
		}
		return false;
	}

	public static String upperCaseFirst(String word){
		String[] letter_list = word.split("[ ]");
		StringBuilder new_word = new StringBuilder();
		for(String letter : letter_list){
			//Đi từng chữ và viết hoa chữ đầu
			new_word.append(letter.substring(0, 1).toUpperCase()
			.concat(letter.substring(1)));
			new_word.append(" ");
		}
		new_word.setLength(new_word.length()-1);
		return new_word.toString();
	}

}
