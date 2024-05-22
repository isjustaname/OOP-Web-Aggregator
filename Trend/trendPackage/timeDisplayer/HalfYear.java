package trendPackage.timeDisplayer;

import java.time.LocalDate;

import pairPackage.Pair;
import pairPackage.PairArray;

public class HalfYear implements TimeDisplay{
    public PairArray date_list; 

    public HalfYear(PairArray date_list){
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
			current_date = current_date.minusMonths(6);
		}

		String others_date = String.valueOf(last_date.getYear());
		new_date_list.add("<".concat(others_date), 0); //Để biểu diễn các năm nằm ngoài year_span

		for(Pair date: this.date_list) {
			String date_string = getTimeRange(LocalDate.parse(date.getProperty()));
			if(date_string.substring(0, 4).compareTo(others_date) < 0)
				date_string = "<".concat(others_date);
			int index = new_date_list.indexOfProperty(date_string);
			new_date_list.setValue(index, new_date_list.getValue(index) + date.getValue());
		}
		return new_date_list;
    }

    @Override
    public String getTimeRange(LocalDate date) {
        String month_range;
		int month = date.getMonthValue();
		if(month <= 6) month_range = "01_06";
		else month_range = "07_12";
		month_range = date.toString().substring(0, 5).concat(month_range);
		return month_range;
    }

}
