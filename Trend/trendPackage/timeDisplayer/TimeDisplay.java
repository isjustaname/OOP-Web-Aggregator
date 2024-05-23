package trendPackage.timeDisplayer;

import java.time.LocalDate;

import pairPackage.PairArray;

public interface TimeDisplay {

    /**
     * Nhóm thời gian theo 1 cấu tạo cố định
     * @param yearSpan
     * @return
     */
    public PairArray timeRangeGrouping(int yearSpan);

    /**
     * Tìm nhóm thời gian của 1 thời điểm nhất định
     * @param date
     * @return
     */
    abstract String getTimeRange(LocalDate date);
}
