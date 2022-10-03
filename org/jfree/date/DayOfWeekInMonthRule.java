package org.jfree.date;

public class DayOfWeekInMonthRule extends AnnualDateRule
{
    private int count;
    private int dayOfWeek;
    private int month;
    
    public DayOfWeekInMonthRule() {
        this(1, 2, 1);
    }
    
    public DayOfWeekInMonthRule(final int count, final int dayOfWeek, final int month) {
        this.count = count;
        this.dayOfWeek = dayOfWeek;
        this.month = month;
    }
    
    public int getCount() {
        return this.count;
    }
    
    public SerialDate getDate(final int year) {
        SerialDate result;
        if (this.count != 0) {
            for (result = SerialDate.createInstance(1, this.month, year); result.getDayOfWeek() != this.dayOfWeek; result = SerialDate.addDays(1, result)) {}
            result = SerialDate.addDays(7 * (this.count - 1), result);
        }
        else {
            for (result = SerialDate.createInstance(1, this.month, year), result = result.getEndOfCurrentMonth(result); result.getDayOfWeek() != this.dayOfWeek; result = SerialDate.addDays(-1, result)) {}
        }
        return result;
    }
    
    public int getDayOfWeek() {
        return this.dayOfWeek;
    }
    
    public int getMonth() {
        return this.month;
    }
    
    public void setCount(final int count) {
        this.count = count;
    }
    
    public void setDayOfWeek(final int dayOfWeek) {
        this.dayOfWeek = dayOfWeek;
    }
    
    public void setMonth(final int month) {
        this.month = month;
    }
}
