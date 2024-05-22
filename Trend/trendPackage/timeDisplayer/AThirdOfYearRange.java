package trendPackage.timeDisplayer;

import java.time.LocalDate;

import pairPackage.Pair;
import pairPackage.PairArray;

/**
 * Liệt kê thời gian theo 4 tháng (1/3 năm)
 */
public class AThirdOfYearRange implements TimeDisplay{
    public PairArray date_list; 

    public AThirdOfYearRange(PairArray date_list){
		this.date_list = new PairArray();
		this.date_list.addAll(date_list);
	}

    @Override
    public PairArray timeRangeGrouping(int year_span) {
        PairArray new_date_list = new PairArray();
		LocalDate current_date = LocalDate.now();
		LocalDate last_date = current_date.minusYears(year_span);
        last_date = last_date.minusMonths(last_date.getMonthValue());
        
		while(current_date.compareTo(last_date) >= 0) {
			new_date_list.add(getTimeRange(current_date), 0);
			current_date = current_date.minusMonths(4);
		}

        String others_date = last_date.toString().substring(0, 4);
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
			new_date_list.setValue(index, new_date_list.getValue(index) + date.getValue());
		}
		return new_date_list;
    }

    @Override
    public String getTimeRange(LocalDate date) {
        String month_range;
		int month = date.getMonthValue();
		if(month <= 4) month_range = "01_04";
		else if(month <=8) month_range = "05_08";
		else month_range = "09_12";
		month_range = date.toString().substring(0, 5).concat(month_range);
		return month_range;
    }

}
