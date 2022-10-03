package org.jfree.date;

public class DayAndMonthRule extends AnnualDateRule
{
    private int dayOfMonth;
    private int month;
    
    public DayAndMonthRule() {
        this(1, 1);
    }
    
    public DayAndMonthRule(final int dayOfMonth, final int month) {
        this.setMonth(month);
        this.setDayOfMonth(dayOfMonth);
    }
    
    public SerialDate getDate(final int yyyy) {
        return SerialDate.createInstance(this.dayOfMonth, this.month, yyyy);
    }
    
    public int getDayOfMonth() {
        return this.dayOfMonth;
    }
    
    public int getMonth() {
        return this.month;
    }
    
    public void setDayOfMonth(final int dayOfMonth) {
        if (dayOfMonth < 1 || dayOfMonth > SerialDate.LAST_DAY_OF_MONTH[this.month]) {
            throw new IllegalArgumentException("DayAndMonthRule(): dayOfMonth outside valid range.");
        }
        this.dayOfMonth = dayOfMonth;
    }
    
    public void setMonth(final int month) {
        if (!SerialDate.isValidMonthCode(month)) {
            throw new IllegalArgumentException("DayAndMonthRule(): month code not valid.");
        }
        this.month = month;
    }
}
