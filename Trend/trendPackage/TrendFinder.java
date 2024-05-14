package trendPackage;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import org.json.simple.JSONArray; 
import org.json.simple.parser.*;

import com.google.gson.Gson;

import pairPackage.PairArray; 


/**
 * 3 phần:
 * 	1. Chart từ được nói đến trong các bài báo theo thời gian <Line Chart>
 * 	2. Phần trăm các theo web cào ( hoặc phần trăm type ) <Pie Chart>
 * 	3. Các tag trending (web không có tag sẽ lọc từ khóa từ title)
 */
public class TrendFinder{
	private String output_file;
	private DataExtract extractor;
	private TimeDisplay time_display;
	
	public TrendFinder(String output_file) throws FileNotFoundException, IOException, ParseException {
		//Khởi tạo
		this.output_file = output_file;
		Object obj = new JSONParser().parse(new FileReader(this.output_file));
		JSONArray ja = (JSONArray) obj;  //Lấy dữ liệu từ file Json
		this.extractor = new DataExtract(ja);
		this.time_display = new TimeDisplay(ja);
	}
	
	/**
	 * Xem qua thời gian, có bao nhiêu bài báo nói đến từ được tra, sẽ tìm từ ấy qua "title, description, tag"
	 * year_span(int): số năm được biểu diễn, tính từ ngày hiện tại. Tùy theo sô năm cách biểu diễn sẽ khác nhau:
	 * 		<=2: Theo nửa tháng một
	 * 		2->6: Theo từng tháng
	 * 		6->9: Theo 4 tháng một (1/3 năm)
	 * 		>=10: Theo từng năm 
	 * @param locate_word: Từ được tra.
	 * @return
	 */
	public PairArray trendOverTime(String locate_word, int year_span) {
		PairArray date_list = this.time_display.dateExtract(locate_word);
		switch (year_span/3) {
			case 0:
				date_list = TimeDisplay.halfMonthGroup(date_list, year_span);
				break;
			case 1: 
				date_list = TimeDisplay.monthGroup(date_list, year_span);
				break;
			case 2:
				date_list = TimeDisplay.aThirdOfYearGroup(date_list, year_span);
				break;
			default:
				date_list = TimeDisplay.yearGroup(date_list, year_span);
				break;
		}
		for(int i = 0; i<date_list.size(); i++){
			//Chỉnh lại format từ năm-tháng-ngày sang năm/tháng/ngày
			String new_date =  date_list.getProperty(i).replace('-', '/');
			new_date = new_date.replace('_', '-');
			date_list.setProperty(i, new_date);
		}
		return date_list;
	}
	
	
	/**
	 * Tìm phần trăm các trang web chính được thực hiện cào
	 * @return 
	 * @throws FileNotFoundException
	 * @throws IOException
	 * @throws ParseException
	 */
	public PairArray extractedWeb() throws FileNotFoundException, IOException, ParseException {
		PairArray list_of_web = this.extractor.Frequency("[,]", false, "web_url");
		list_of_web.add("http://www.blockchain.new", 20); //Đây để test vì còn ít web quá
		double total = 0;
		for(int i = 0; i< list_of_web.size(); i++) {
			//lấy tổng số lượng web
			total += list_of_web.getValue(i);
		}
		PairArray web_precent_list = new PairArray();
		for(int i = 0; i<list_of_web.size(); i++) {
			//chia dần ra lấy phần trăm
			double precent_value = (double)list_of_web.getValue(i) / total * 100;
			precent_value = Math.round(precent_value*100) / 100.0;
			web_precent_list.add(list_of_web.getProperty(i), precent_value);
		}
		return web_precent_list;
	}
	
	/**
	 * Tìm một số các (tag) xuất hiện nhiều nhất
	 * 
	 * @param find_number: số tag ở trên đầu được tìm
	 * @param split_regex: kí hiệu để phân tách các từ theo
	 * ("[,]" nếu là tag; "[ ]" nếu tra title, description, "[!]" nếu không muốn tách)
	 * @param ignore_downcase: (true) Bỏ các từ không viết hoa, 
	 * 							(false) Lấy các từ viết thường sẽ viết lại từ ấy thành viết hoa.
	 * @param extract_subject: Các subject được phân tích, có thể 1 hoặc nhiều subject (Thường dùng "tag" or "title")
	 * @throws ParseException 
	 * @throws IOException 
	 * @throws FileNotFoundException 
	 * @throws Exception
	 */
	public PairArray findMostTrending(int find_number) throws FileNotFoundException, IOException, ParseException {
		PairArray trend_list = this.extractor.Frequency("[,]", false, "tag");
		PairArray most_trend = new PairArray();
		//cắt dãy con không cần thiết tùy vào find_number
		//trường hợp là số lượng từ tìm bằng độ sâu của cây vung đống
		double max_index = Math.pow(2, find_number);
		while(trend_list.size() < max_index) {
			max_index = max_index/2;
		}
		most_trend = trend_list.subList(0, (int) max_index);
		most_trend.sortValue();
		if(find_number > most_trend.size()) {
			find_number = most_trend.size();
		}
		most_trend = most_trend.subList(0, find_number); //cắt tỉa nốt để lấy các từ cần thiết
		return most_trend;
	}
	
	
	public static void main(String[] args) throws Exception {
		TrendFinder trend_finder = new TrendFinder("Data/Output.json");
		TrendData data_list = new TrendData();
		
		data_list.search_word = "Bitcoin";
		data_list.most_trending = trend_finder.findMostTrending(8);
		data_list.web_display = trend_finder.extractedWeb();
		trend_finder.findMostTrending(9).printPair();
		//trend_finder.trendOverTime("Bitcoin", 2).printPair();
		//trend_finder.trendOverTime("Bitcoin", 4).printPair();
		//trend_finder.trendOverTime("Bitcoin", 8).printPair();
		//trend_finder.trendOverTime("Bitcoin", 15).printPair();
		
		
		
        Gson gson = new Gson();
        String jsonData = gson.toJson(data_list);

        // Để viết ra Json
        BufferedWriter writer = new BufferedWriter(new FileWriter("Data/Trend.json"));
        writer.write(jsonData);
        writer.close();
		
	}
		
}
