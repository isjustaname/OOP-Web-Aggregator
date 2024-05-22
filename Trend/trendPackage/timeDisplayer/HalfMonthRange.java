package trendPackage.timeDisplayer;

import java.time.LocalDate;

import pairPackage.Pair;
import pairPackage.PairArray;


/**
 * Liệt kê thời gian theo nửa tháng
 */
public class HalfMonthRange implements TimeDisplay{
    public PairArray date_list; 

    public HalfMonthRange(PairArray date_list){
		this.date_list = new PairArray();
		this.date_list.addAll(date_list);
	}

    @Override
    public PairArray timeRangeGrouping(int year_span) {
        PairArray new_date_list = new PairArray(); 
		// liệt kê thời gian trong khoảng year_span
		LocalDate current_date = LocalDate.now();
		LocalDate last_date = current_date.minusYears(year_span);
        last_date = last_date.minusDays(current_date.getDayOfMonth()-1);
		while(current_date.compareTo(last_date) >= 0) {
			new_date_list.add(getTimeRange(current_date), 0);
			if(current_date.getDayOfMonth() == 31){
				current_date = current_date.minusDays(11); //các tháng có độ dài khác nhau, tránh trùng lặp
			}
			current_date = current_date.minusDays(15);
		}

        String others_date = last_date.toString().substring(0, 7);
        others_date = "<".concat(others_date);
        new_date_list.add(others_date, 0);

		for(Pair date: date_list) {
			String date_string = date.getProperty();
            if(LocalDate.parse(date_string).compareTo(last_date) <= 0){
                date_string = others_date;
            }
            else{
			    date_string = getTimeRange(LocalDate.parse(date_string));
            }
			int index = new_date_list.indexOfProperty(date_string);
            System.out.println(date_string);
			new_date_list.setValue(index, new_date_list.getValue(index) + date.getValue());
		}
		return new_date_list;
    }

    @Override
    public String getTimeRange(LocalDate date) {
		String month_side = "";
		if(date.getDayOfMonth() <= 15) {
			month_side = "01_15";
		}
		else {
			String end_of_month = String.valueOf(date.lengthOfMonth());
			month_side  = "16_".concat(end_of_month);
		}
		String date_string = date.toString().substring(0, 8).concat(month_side);
		return date_string;
    }

}
