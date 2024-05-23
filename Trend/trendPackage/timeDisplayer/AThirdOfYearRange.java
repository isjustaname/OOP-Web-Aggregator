package trendPackage.timeDisplayer;

import java.time.LocalDate;

import pairPackage.Pair;
import pairPackage.PairArray;

/**
 * Liệt kê thời gian theo 4 tháng (1/3 năm)
 */
public class AThirdOfYearRange implements TimeDisplay{
    public PairArray dateList; 

    public AThirdOfYearRange(PairArray dateList){
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
			currentDate = currentDate.minusMonths(4);
		}

        String othersDate = lastDate.toString().substring(0, 4);
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
        String monthRange;
		int month = date.getMonthValue();
		if(month <= 4) monthRange = "01_04";
		else if(month <=8) monthRange = "05_08";
		else monthRange = "09_12";
		monthRange = date.toString().substring(0, 5).concat(monthRange);
		return monthRange;
    }

}
