package org.apache.poi.ss.formula.atp;

import java.util.Calendar;
import org.apache.poi.util.LocaleUtil;
import org.apache.poi.ss.usermodel.DateUtil;
import java.util.Date;

public class WorkdayCalculator
{
    public static final WorkdayCalculator instance;
    
    private WorkdayCalculator() {
    }
    
    public int calculateWorkdays(final double start, final double end, final double[] holidays) {
        final int saturdaysPast = this.pastDaysOfWeek(start, end, 7);
        final int sundaysPast = this.pastDaysOfWeek(start, end, 1);
        final int nonWeekendHolidays = this.calculateNonWeekendHolidays(start, end, holidays);
        return (int)(end - start + 1.0) - saturdaysPast - sundaysPast - nonWeekendHolidays;
    }
    
    public Date calculateWorkdays(final double start, int workdays, final double[] holidays) {
        final Date startDate = DateUtil.getJavaDate(start);
        final int direction = (workdays < 0) ? -1 : 1;
        final Calendar endDate = LocaleUtil.getLocaleCalendar();
        endDate.setTime(startDate);
        double excelEndDate = DateUtil.getExcelDate(endDate.getTime());
        while (workdays != 0) {
            endDate.add(6, direction);
            excelEndDate += direction;
            if (endDate.get(7) != 7 && endDate.get(7) != 1 && !this.isHoliday(excelEndDate, holidays)) {
                workdays -= direction;
            }
        }
        return endDate.getTime();
    }
    
    protected int pastDaysOfWeek(final double start, final double end, final int dayOfWeek) {
        int pastDaysOfWeek = 0;
        for (int startDay = (int)Math.floor((start < end) ? start : end), endDay = (int)Math.floor((end > start) ? end : start); startDay <= endDay; ++startDay) {
            final Calendar today = LocaleUtil.getLocaleCalendar();
            today.setTime(DateUtil.getJavaDate(startDay));
            if (today.get(7) == dayOfWeek) {
                ++pastDaysOfWeek;
            }
        }
        return (start <= end) ? pastDaysOfWeek : (-pastDaysOfWeek);
    }
    
    protected int calculateNonWeekendHolidays(final double start, final double end, final double[] holidays) {
        int nonWeekendHolidays = 0;
        final double startDay = (start < end) ? start : end;
        final double endDay = (end > start) ? end : start;
        for (final double holiday : holidays) {
            if (this.isInARange(startDay, endDay, holiday) && !this.isWeekend(holiday)) {
                ++nonWeekendHolidays;
            }
        }
        return (start <= end) ? nonWeekendHolidays : (-nonWeekendHolidays);
    }
    
    protected boolean isWeekend(final double aDate) {
        final Calendar date = LocaleUtil.getLocaleCalendar();
        date.setTime(DateUtil.getJavaDate(aDate));
        return date.get(7) == 7 || date.get(7) == 1;
    }
    
    protected boolean isHoliday(final double aDate, final double[] holidays) {
        for (final double holiday : holidays) {
            if (Math.round(holiday) == Math.round(aDate)) {
                return true;
            }
        }
        return false;
    }
    
    protected boolean isInARange(final double start, final double end, final double aDate) {
        return aDate >= start && aDate <= end;
    }
    
    static {
        instance = new WorkdayCalculator();
    }
}
