package trendPackage;

import java.time.LocalDate;
import java.util.Iterator;
import org.json.simple.JSONArray; 
import org.json.simple.JSONObject;

import pairPackage.Pair;
import pairPackage.PairArray; 

public class TimeDisplay {
	
	private JSONArray json_file;
	
	public TimeDisplay(JSONArray json_file){
		this.json_file = json_file;
	}
	
	/**
	 * Tìm list các năm/tháng/ngày 
	 * @param located word: từ đang được tìm kiếm
	 * @return
	 * 
	 */
	public PairArray dateExtract(String located_word){
		PairArray date_appearance = new PairArray();
		int counter = 0;
		for(TrendData data : TagRecognition.web_tag_data){
			if(data.published_date.isEmpty() || data.published_date == "") continue;
			if(data.tag_list.contains(located_word) == true) {
				String date = data.published_date.substring(0, 10);
				if(date == "") continue;
				int index = date_appearance.indexOfProperty(date);
				if(index == -1) {
					date_appearance.add(date, 1);
				}
				else {
					date_appearance.setValue(index, date_appearance.getValue(index) + 1);
				}
			}
		}
 		date_appearance.sortProperty();
		return date_appearance;
	}
	
	/**
	 * Gộp các thời gian theo từng nửa tháng
	 * @param date_list
	 * @return
	 */
	
	public static PairArray halfMonthGroup(PairArray date_list, int year_span) {
		PairArray half_month = new PairArray(); 
		// liệt kê thời gian trong khoảng year_span
		LocalDate current_date = LocalDate.now();
		LocalDate last_date = current_date.minusYears(year_span);
		while(current_date.compareTo(last_date) >= 0) {
			String checked_half = getHalfMonth(current_date.toString());
			half_month.add(checked_half, 0);
			if(current_date.getDayOfMonth() == 31){
				current_date = current_date.minusDays(11); //các tháng có độ dài khác nhau, tránh trùng lặp
			}
			current_date = current_date.minusDays(15);
		}
		String last_half = getHalfMonth(last_date.toString());
		//Vòng lặp trên hay thiếu phần tử cuối cùng nên kiêm tra nốt
		if(!half_month.getLast().getProperty().equals(last_half)) half_month.add(last_half, 0);
		for(Pair date: date_list) {
			String date_string = date.getProperty();
			date_string = getHalfMonth(date_string);
			int index = half_month.indexOfProperty(date_string);
			if(index == -1) break;
			half_month.setValue(index, half_month.getValue(index) + date.getValue());
		}
		return half_month;
	}
	
	/**
	 * Gộp các thời gian theo từng 1 tháng
	 * @param date_list
	 * @return
	 */
	public static PairArray monthGroup(PairArray date_list, int year_span) {
		PairArray month_list = new PairArray();
		LocalDate current_date = LocalDate.now();
		LocalDate last_date = current_date.minusYears(year_span);
		while(current_date.compareTo(last_date) >= 0) {
			month_list.add(current_date.toString().substring(0, 7), 0);
			current_date = current_date.minusMonths(1);
		}
		for(Pair date: date_list) {
			String date_string = date.getProperty().substring(0, 7); //chỉ lấy tháng
			date_string = date_string.substring(0, 7);
			int index = month_list.indexOfProperty(date_string);
			if(index == -1) {
				break;
			}
			else {
				month_list.setValue(index, month_list.getValue(index) + date.getValue());
			}
		}
		return month_list;
	}
	
	/**
	 * Gộp các thời gian theo từng 4 tháng
	 * @param date_list
	 * @return
	 */
	public static PairArray aThirdOfYearGroup(PairArray date_list, int year_span) {
		PairArray a_third_year = new PairArray();
		LocalDate current_date = LocalDate.now();
		LocalDate last_date = current_date.minusYears(year_span);
		while(current_date.compareTo(last_date) >= 0) {
			a_third_year.add(get4MonthRange(current_date), 0);
			current_date = current_date.minusMonths(4);
		}
		for(Pair date: date_list) {
			String date_string = get4MonthRange(LocalDate.parse(date.getProperty()));
			int index = a_third_year.indexOfProperty(date_string);
			if(index == -1) {
				break;
			}
			else {
				a_third_year.setValue(index, a_third_year.getValue(index) + date.getValue());
			}
		}
		return a_third_year;
	}

	/**
	 * Gộp các thời gian theo nửa năm
	 * @param date_list
	 * @return
	 */
	public static PairArray halfYearGroup(PairArray date_list, int year_span) {
		PairArray half_year = new PairArray();
		LocalDate current_date = LocalDate.now();
		LocalDate last_date = current_date.minusYears(year_span);
		if(getHalfYear(last_date).substring(5,7).compareTo("07") >= 0)
			last_date = last_date.minusMonths(6);
		while(current_date.compareTo(last_date) >= 0) {
			half_year.add(getHalfYear(current_date), 0);
			current_date = current_date.minusMonths(6);
		}
		String last_year = String.valueOf(last_date.getYear());
		half_year.add("<".concat(last_year), 0);
		for(Pair date: date_list) {
			String date_string = getHalfYear(LocalDate.parse(date.getProperty()));
			if(date_string.substring(0, 4).compareTo(last_year) < 0)
				date_string = "<".concat(last_year);
			int index = half_year.indexOfProperty(date_string);
			half_year.setValue(index, half_year.getValue(index) + date.getValue());
		}
		return half_year;
	}

	/**
	 * Gộp các thời gian theo từng năm
	 * @param date_list
	 * @return
	 */
	public static PairArray yearGroup(PairArray date_list, int year_span) {
		PairArray year_group = new PairArray();
		LocalDate current_date = LocalDate.now();
		int this_year = current_date.getYear();
		for(int i = this_year; i >= this_year-year_span; i--){
			year_group.add(String.valueOf(i), 0);
		}
		for(Pair date: date_list) {
			String date_string = date.getProperty().substring(0, 4);
			int index = year_group.indexOfProperty(date_string);
			if(index == -1) {
				break;
			}
			else {
				year_group.setValue(index, year_group.getValue(index) + date.getValue());
			}
		}
		return year_group;
	}
	
	/**
	 * Chuyển date về khoảng nửa tháng của nó
	 * @param date_string
	 * @return
	 */
	private static String getHalfMonth(String date_string) {
		int day = Integer.parseInt(date_string.substring(8, 10));
		String month_side = "";
		if(day < 15) {
			month_side = "01_15";
		}
		else {
			String end_of_month = String.valueOf(getEndOfMonth(date_string));
			month_side  = "16_".concat(end_of_month);
		}
		date_string = date_string.substring(0, 8).concat(month_side);
		return date_string;
	}

	/**
	 * Tìm khoảng 4 tháng trong năm (0-4; 5-8; 9-12);
	 * @param date_string
	 * @return
	 */
	private static String get4MonthRange(LocalDate date){
		String month_range;
		int month = date.getMonthValue();
		if(month <= 4) month_range = "01_04";
		else if(month <=8) month_range = "05_08";
		else month_range = "09_12";
		month_range = date.toString().substring(0, 5).concat(month_range);
		return month_range;
	}

	/**
	 * Tìm khoảng nửa năm (0-6; 7-12);
	 * @param date_string
	 * @return
	 */
	private static String getHalfYear(LocalDate date){
		String month_range;
		int month = date.getMonthValue();
		if(month <= 6) month_range = "01_06";
		else month_range = "07_12";
		month_range = date.toString().substring(0, 5).concat(month_range);
		return month_range;
	}

	/**
	 * Tìm ngày cuối cùng của tháng
	 * @param date_string
	 * @return
	 */
	private static int getEndOfMonth(String date_string) {
		LocalDate date = LocalDate.parse(date_string);
		return date.lengthOfMonth();
	}
}
