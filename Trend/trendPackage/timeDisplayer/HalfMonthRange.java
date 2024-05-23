package trendPackage.timeDisplayer;

import java.time.LocalDate;

import pairPackage.Pair;
import pairPackage.PairArray;


/**
 * Liệt kê thời gian theo nửa tháng
 */
public class HalfMonthRange implements TimeDisplay{
    public PairArray dateList; 

    public HalfMonthRange(PairArray dateList){
		this.dateList = new PairArray();
		this.dateList.addAll(dateList);
	}

    @Override
    public PairArray timeRangeGrouping(int yearSpan) {
        PairArray newDateList = new PairArray(); 
		// liệt kê thời gian trong khoảng yearSpan
		LocalDate currentDate = LocalDate.now();
		LocalDate lastDate = currentDate.minusYears(yearSpan);
        lastDate = lastDate.minusDays(currentDate.getDayOfMonth()-1);
		while(currentDate.compareTo(lastDate) >= 0) {
			newDateList.add(getTimeRange(currentDate), 0);
			if(currentDate.getDayOfMonth() == 31){
				currentDate = currentDate.minusDays(11); //các tháng có độ dài khác nhau, tránh trùng lặp
			}
			currentDate = currentDate.minusDays(15);
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
            System.out.println(dateString);
			newDateList.setValue(index, newDateList.getValue(index) + date.getValue());
		}
		return newDateList;
    }

    @Override
    public String getTimeRange(LocalDate date) {
		String monthSide = "";
		if(date.getDayOfMonth() <= 15) {
			monthSide = "01_15";
		}
		else {
			String endOfMonth = String.valueOf(date.lengthOfMonth());
			monthSide  = "16_".concat(endOfMonth);
		}
		String dateString = date.toString().substring(0, 8).concat(monthSide);
		return dateString;
    }

}
