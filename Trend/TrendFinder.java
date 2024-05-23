
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import org.json.simple.JSONArray; 
import org.json.simple.parser.*;

import pairPackage.PairArray;
import trendPackage.*;

import trendPackage.timeDisplayer.*;
/**
 * 3 phần:
 * 	1. Chart từ được nói đến trong các bài báo theo thời gian <Line Chart>
 * 	2. Phần trăm các theo web cào ( hoặc phần trăm type ) <Pie Chart>
 * 	3. Các tag trending (web không có tag sẽ lọc từ khóa từ title)
 */
public class TrendFinder{
	private String outputFile;
	private DataExtract extractor;
	private TimeDisplay timeDisplayer;
	private TagRecognition tagScraper;
	
	public TrendFinder(String outputFile) throws FileNotFoundException, IOException, ParseException {
		//Khởi tạo
		this.outputFile = outputFile;
		Object object = new JSONParser().parse(new FileReader(this.outputFile));
		JSONArray jsonArray = (JSONArray) object;  //Lấy dữ liệu từ file Json
		this.extractor = new DataExtract(jsonArray);
		this.tagScraper = new TagRecognition(jsonArray);
	}
	
	/**
	 * Xem qua thời gian, có bao nhiêu bài báo nói đến từ được tra, sẽ tìm từ ấy qua "title, description, tag"
	 * yearSpan(int): số năm được biểu diễn, tính từ ngày hiện tại. Tùy theo sô năm cách biểu diễn sẽ khác nhau:
	 * 		<=2: Theo nửa tháng một
	 * 		2->6: Theo từng tháng
	 * 		6->9: Theo 4 tháng một (1/3 năm)
	 * 		>=10: Theo từng năm 
	 * @param locateWord: Từ được tra.
	 * @return
	 */
	public PairArray trendOverTime(String locateWord, int yearSpan) {
		PairArray dateList = this.extractor.dateExtract(locateWord);
		switch (yearSpan/3) {
			case 0:
				timeDisplayer = new HalfMonthRange(dateList);
				break;
			case 1: 
				timeDisplayer = new MonthRange(dateList);
				break;
			case 2:
				timeDisplayer = new AThirdOfYearRange(dateList);
				break;
			default:
				timeDisplayer = new HalfYearRange(dateList);
				break;
		}
		dateList = timeDisplayer.timeRangeGrouping(yearSpan);
		for(int i = 0; i<dateList.size(); i++){
			//Chỉnh lại format từ năm-tháng-ngày sang năm/tháng/ngày
			String newDate =  dateList.getProperty(i).replace('-', '/');
			newDate = newDate.replace('_', '-');
			dateList.setProperty(i, newDate);
		}
		return dateList;
	}
	
	
	/**
	 * Tìm phần trăm các trang web chính được thực hiện cào
	 * @return 
	 * @throws FileNotFoundException
	 * @throws IOException
	 * @throws ParseException
	 */
	public PairArray extractedWeb() throws FileNotFoundException, IOException, ParseException {
		PairArray listOfWeb = this.extractor.mainWebListing();
		listOfWeb.sortValue();
		double total = 0;
		for(int i = 0; i< listOfWeb.size(); i++) {
			//lấy tổng số lượng web
			total += listOfWeb.getValue(i);
		}
		PairArray webPrecentList = new PairArray();
		for(int i = 0; i<listOfWeb.size(); i++) {
			//chia dần ra lấy phần trăm
			double precentValue = listOfWeb.getValue(i) / total * 100;
			precentValue = Math.round(precentValue*100) / 100.0;
			webPrecentList.add(listOfWeb.getProperty(i), precentValue);
		}
		return webPrecentList;
	}
	
	/**
	 * Tìm một số các (tag) xuất hiện nhiều nhất
	 * 
	 * @param findNumber: số tag ở trên đầu được tìm
	 * @param splitRegex: kí hiệu để phân tách các từ theo
	 * ("[,]" nếu là tag; "[ ]" nếu tra title, description, "[!]" nếu không muốn tách)
	 * @param ignoreDowncase: (true) Bỏ các từ không viết hoa, 
	 * 							(false) Lấy các từ viết thường sẽ viết lại từ ấy thành viết hoa.
	 * @param extractSubject: Các subject được phân tích, có thể 1 hoặc nhiều subject (Thường dùng "tag" or "title")
	 * @throws ParseException 
	 * @throws IOException 
	 * @throws FileNotFoundException 
	 * @throws Exception
	 */
	public PairArray findMostTrending(int findNumber) throws FileNotFoundException, IOException, ParseException {
		PairArray trendList = extractor.extractTagTrend();
		PairArray mostTrend = new PairArray();
		//cắt dãy con không cần thiết tùy vào findNumber
		//trường hợp là số lượng từ tìm bằng độ sâu của cây vung đống
		double maxIndex = Math.pow(2, findNumber);
		while(trendList.size() < maxIndex) {
			maxIndex = maxIndex/2;
		}
		mostTrend = trendList.subList(0, (int) maxIndex);
		mostTrend.sortValue();
		if(findNumber > mostTrend.size()) {
			findNumber = mostTrend.size();
		}
		mostTrend = mostTrend.subList(0, findNumber); //cắt tỉa nốt để lấy các từ cần thiết
		return mostTrend;
	}
	
	
	public static void main(String[] args) throws Exception {
		
		TrendFinder trendFinder = new TrendFinder("Data/Output.json");
				
		trendFinder.findMostTrending(14).printPair();
		trendFinder.trendOverTime("Ethereum", 7).printPair();
		//trendFinder.extractedWeb().printPair();
		// for(TrendData data : TagRecognition.webTagData){
		// 	System.out.println(data.tagList + " : " + data.publishedDate);
		// }


      
		
	}
		
}
