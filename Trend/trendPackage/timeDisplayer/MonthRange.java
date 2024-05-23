package trendPackage.timeDisplayer;

import java.time.LocalDate;

import trendPackage.pairPackage.Pair;
import trendPackage.pairPackage.PairArray;

/**
 * Liệt kê thời gian theo từng tháng
 */
public class MonthRange implements TimeDisplay{
    public PairArray dateList; 

    public MonthRange(PairArray dateList){
		this.dateList = new PairArray();
		this.dateList.addAll(dateList);
	}

    @Override
    public PairArray timeRangeGrouping(int yearSpan) {
        PairArray newDateList = new PairArray();
		LocalDate currentDate = LocalDate.now();
		LocalDate lastDate = currentDate.minusYears(yearSpan);
        lastDate = lastDate.minusDays(currentDate.getDayOfMonth()-1);

		while(currentDate.compareTo(lastDate) >= 0) {
			newDateList.add(currentDate.toString().substring(0, 7), 0);
			currentDate = currentDate.minusMonths(1);
		}

        String othersDate = lastDate.toString().substring(0, 7);
        othersDate = "<".concat(othersDate);
        newDateList.add(othersDate, 0);

		for(Pair date: dateList) {
			String dateString = date.getProperty();
            if(LocalDate.parse(dateString).compareTo(lastDate) <= 0){
                dateString = othersDate;
            }
            else{
			    dateString = getTimeRange(LocalDate.parse(dateString));
            }
			int index = newDateList.indexOfProperty(dateString);
			newDateList.setValue(index, newDateList.getValue(index) + date.getValue());
		}
		return newDateList;
    }

    @Override
    public String getTimeRange(LocalDate date) {
        return date.toString().substring(0, 7);
    }
    

}
