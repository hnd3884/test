package org.jfree.date;

public class RelativeDayOfWeekRule extends AnnualDateRule
{
    private AnnualDateRule subrule;
    private int dayOfWeek;
    private int relative;
    
    public RelativeDayOfWeekRule() {
        this(new DayAndMonthRule(), 2, 1);
    }
    
    public RelativeDayOfWeekRule(final AnnualDateRule subrule, final int dayOfWeek, final int relative) {
        this.subrule = subrule;
        this.dayOfWeek = dayOfWeek;
        this.relative = relative;
    }
    
    public Object clone() throws CloneNotSupportedException {
        final RelativeDayOfWeekRule duplicate = (RelativeDayOfWeekRule)super.clone();
        duplicate.subrule = (AnnualDateRule)duplicate.getSubrule().clone();
        return duplicate;
    }
    
    public SerialDate getDate(final int year) {
        if (year < 1900 || year > 9999) {
            throw new IllegalArgumentException("RelativeDayOfWeekRule.getDate(): year outside valid range.");
        }
        SerialDate result = null;
        final SerialDate base = this.subrule.getDate(year);
        if (base != null) {
            switch (this.relative) {
                case -1: {
                    result = SerialDate.getPreviousDayOfWeek(this.dayOfWeek, base);
                    break;
                }
                case 0: {
                    result = SerialDate.getNearestDayOfWeek(this.dayOfWeek, base);
                    break;
                }
                case 1: {
                    result = SerialDate.getFollowingDayOfWeek(this.dayOfWeek, base);
                    break;
                }
            }
        }
        return result;
    }
    
    public int getDayOfWeek() {
        return this.dayOfWeek;
    }
    
    public int getRelative() {
        return this.relative;
    }
    
    public AnnualDateRule getSubrule() {
        return this.subrule;
    }
    
    public void setDayOfWeek(final int dayOfWeek) {
        this.dayOfWeek = dayOfWeek;
    }
    
    public void setRelative(final int relative) {
        this.relative = relative;
    }
    
    public void setSubrule(final AnnualDateRule subrule) {
        this.subrule = subrule;
    }
}
