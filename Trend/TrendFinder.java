
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import org.json.simple.JSONArray; 
import org.json.simple.parser.*;

import pairPackage.PairArray;
import trendPackage.*;
import trendPackage.timeDisplayer.AThirdOfYear;
import trendPackage.timeDisplayer.HalfMonth;
import trendPackage.timeDisplayer.HalfYear;
import trendPackage.timeDisplayer.MonthGroup;
import trendPackage.timeDisplayer.TimeDisplay;



/**
 * 3 phần:
 * 	1. Chart từ được nói đến trong các bài báo theo thời gian <Line Chart>
 * 	2. Phần trăm các theo web cào ( hoặc phần trăm type ) <Pie Chart>
 * 	3. Các tag trending (web không có tag sẽ lọc từ khóa từ title)
 */
public class TrendFinder{
	private String output_file;
	private DataExtract extractor;
	private TimeDisplay time_displayer;
	private TagRecognition tag_scraper;
	
	public TrendFinder(String output_file) throws FileNotFoundException, IOException, ParseException {
		//Khởi tạo
		this.output_file = output_file;
		Object object = new JSONParser().parse(new FileReader(this.output_file));
		JSONArray json_array = (JSONArray) object;  //Lấy dữ liệu từ file Json
		this.extractor = new DataExtract(json_array);
		this.tag_scraper = new TagRecognition(json_array);
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
		PairArray date_list = this.extractor.dateExtract(locate_word);
		time_displayer = new HalfYear(date_list);
		//date_list = time_displayer.timeRangeGrouping(year_span);
		switch (year_span/3) {
			case 0:
				time_displayer = new HalfMonth(date_list);
				break;
			case 1: 
				time_displayer = new MonthGroup(date_list);
				break;
			case 2:
				time_displayer = new AThirdOfYear(date_list);
				break;
			default:
				time_displayer = new HalfYear(date_list);
				break;
		}
		date_list = time_displayer.timeRangeGrouping(year_span);
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
		PairArray list_of_web = this.extractor.mainWebListing();
		list_of_web.sortValue();
		double total = 0;
		for(int i = 0; i< list_of_web.size(); i++) {
			//lấy tổng số lượng web
			total += list_of_web.getValue(i);
		}
		PairArray web_precent_list = new PairArray();
		for(int i = 0; i<list_of_web.size(); i++) {
			//chia dần ra lấy phần trăm
			double precent_value = list_of_web.getValue(i) / total * 100;
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
		PairArray trend_list = extractor.extractTagTrend();
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
				
		trend_finder.findMostTrending(14).printPair();
		trend_finder.trendOverTime("Ethereum", 7).printPair();
		//trend_finder.extractedWeb().printPair();
		// for(TrendData data : TagRecognition.web_tag_data){
		// 	System.out.println(data.tag_list + " : " + data.published_date);
		// }


      
		
	}
		
}
