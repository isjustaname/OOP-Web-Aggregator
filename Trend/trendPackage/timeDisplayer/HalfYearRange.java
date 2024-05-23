package trendPackage.timeDisplayer;

import java.time.LocalDate;

import trendPackage.pairPackage.Pair;
import trendPackage.pairPackage.PairArray;


/**
 * Liệt kê thời gian theo nửa năm
 */
public class HalfYearRange implements TimeDisplay{
    public PairArray dateList; 

    public HalfYearRange(PairArray dateList){
		this.dateList = new PairArray();
		this.dateList.addAll(dateList);
	}

    @Override
    public PairArray timeRangeGrouping(int yearSpan) {
        PairArray newDateList = new PairArray();
		LocalDate currentDate = LocalDate.now();
		LocalDate lastDate = currentDate.minusYears(yearSpan);
		lastDate = lastDate.minusMonths(lastDate.getMonthValue());

		while(currentDate.compareTo(lastDate) >= 0) {
			newDateList.add(getTimeRange(currentDate), 0);
			currentDate = currentDate.minusMonths(6);
		}

		String othersDate = String.valueOf(lastDate.getYear());
		newDateList.add("<".concat(othersDate), 0); //Để biểu diễn các năm nằm ngoài yearSpan

		for(Pair date: this.dateList) {
			String dateString = getTimeRange(LocalDate.parse(date.getProperty()));
			if(dateString.substring(0, 4).compareTo(othersDate) < 0)
				dateString = "<".concat(othersDate);
			int index = newDateList.indexOfProperty(dateString);
			newDateList.setValue(index, newDateList.getValue(index) + date.getValue());
		}
		return newDateList;
    }

    @Override
    public String getTimeRange(LocalDate date) {
        String monthRange;
		int month = date.getMonthValue();
		if(month <= 6) monthRange = "01_06";
		else monthRange = "07_12";
		monthRange = date.toString().substring(0, 5).concat(monthRange);
		return monthRange;
    }

}
