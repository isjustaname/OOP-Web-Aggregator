package trendPackage;
import java.io.FileNotFoundException;
import java.io.IOException;
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
	
	
	/**
	 * Phân tách các nội dung trong từng subject trong JSON thành từng từ riêng
	 * @param split_regex: kí hiệu dùng làm điểm phân tách
	 * @param ignore_downcase: (True)Bỏ qua các từ không viết hoa
	 */
	public List<String> ExtractWord(String split_regex, boolean ignore_downcase, String... subjects) {
		Iterator<?> itr = this.json_file.iterator();
		List<String> list_of_word = new ArrayList<>();
 		while (itr.hasNext()) 
		{
			JSONObject json_object = (JSONObject) itr.next();//Đọc các dữ liệu của từng web một
			for(String sub : subjects) {
				if(json_object.get(sub) instanceof JSONArray) {
					JSONArray json_array = (JSONArray) json_object.get(sub);
					Iterator<?> itr_child = json_array.iterator() ;
					while(itr_child.hasNext()) {
						String paragraph = (String) itr.next();
						String[] words = paragraph.split(split_regex);
						for(int i = 0; i<words.length; i++){
							//Lọc các dấu thừa ra trong từ
							words[i] = polishWord(words[i], ignore_downcase);
						}
						Collections.addAll(list_of_word, words);
					}
				}
				else {
					String paragraph = (String)json_object.get(sub);
					String[] words = paragraph.split(split_regex);
					for(int i = 0; i<words.length; i++){
						//Lọc các dấu thừa ra trong từ
						words[i] = polishWord(words[i], ignore_downcase);
					}
					Collections.addAll(list_of_word, words);
				}
			}
		}
		return list_of_word;
	}


	
	/**
	 * Liệt kê các từ xuất hiện trong dãy với các chữ xuất hiện nhiều được xếp ở trên (Chưa sắp xếp hoàn chỉnh)
	 * @param args
	 * @throws ParseException 
	 * @throws IOException 
	 * @throws FileNotFoundException 
	 * @throws Exception
	 */
	public PairArray Frequency(String split_regex ,boolean ignore_downcase, String... extract_subjects) {
		List<String> list_of_words = ExtractWord(split_regex, ignore_downcase, extract_subjects);
		return heapCountingList(list_of_words);
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
		String[] subjects = {"title", "description", "tag"};
		for(String sub : subjects) {
			String paragraph = (String)json_object.get(sub);
			String[] words = paragraph.split("[ ,]");
			for(String w: words) {
				w = polishWord(w, false);
				if(w.contains(locating_word)) {
					return true;
				}
			}
		}
		return false;
	}
	
	/**
	 * Lọc và chỉnh sửa các từ sau khi split
	 * @param word
	 * @param ignore_downcase: bỏ các từ viết thường không, nếu không bỏ thì viết hoa các từ đấy
	 */
	private static String polishWord(String word, boolean ignore_downcase) {
		if(word.isEmpty()) return "";
		if(ignore_downcase) { 
			String upper_case = "[A-Z]\\w*";
			if(word.matches(upper_case) == false) return "";
		}
		else {
			word = word.substring(0, 1).toUpperCase() + word.substring(1); //Viết hoa chữ đầu
		}
		while(word.substring(0, 1).matches("[\\w]") == false){
			word = word.substring(1);
			if(word == "") return "";
		}
		int len = word.length();
		while(word.substring(len-1, len).matches("\\w") == false) {
			word = word.substring(0, len-1);
			len -= 1;
			if(word == "") return "";
		}
		return word;
		
	}
	
}
