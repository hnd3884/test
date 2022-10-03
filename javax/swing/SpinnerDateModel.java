package javax.swing;

import java.util.Date;
import java.util.Calendar;
import java.io.Serializable;

public class SpinnerDateModel extends AbstractSpinnerModel implements Serializable
{
    private Comparable start;
    private Comparable end;
    private Calendar value;
    private int calendarField;
    
    private boolean calendarFieldOK(final int n) {
        switch (n) {
            case 0:
            case 1:
            case 2:
            case 3:
            case 4:
            case 5:
            case 6:
            case 7:
            case 8:
            case 9:
            case 10:
            case 11:
            case 12:
            case 13:
            case 14: {
                return true;
            }
            default: {
                return false;
            }
        }
    }
    
    public SpinnerDateModel(final Date time, final Comparable start, final Comparable end, final int calendarField) {
        if (time == null) {
            throw new IllegalArgumentException("value is null");
        }
        if (!this.calendarFieldOK(calendarField)) {
            throw new IllegalArgumentException("invalid calendarField");
        }
        if ((start != null && start.compareTo(time) > 0) || (end != null && end.compareTo(time) < 0)) {
            throw new IllegalArgumentException("(start <= value <= end) is false");
        }
        this.value = Calendar.getInstance();
        this.start = start;
        this.end = end;
        this.calendarField = calendarField;
        this.value.setTime(time);
    }
    
    public SpinnerDateModel() {
        this(new Date(), null, null, 5);
    }
    
    public void setStart(final Comparable start) {
        if (start == null) {
            if (this.start == null) {
                return;
            }
        }
        else if (start.equals(this.start)) {
            return;
        }
        this.start = start;
        this.fireStateChanged();
    }
    
    public Comparable getStart() {
        return this.start;
    }
    
    public void setEnd(final Comparable end) {
        if (end == null) {
            if (this.end == null) {
                return;
            }
        }
        else if (end.equals(this.end)) {
            return;
        }
        this.end = end;
        this.fireStateChanged();
    }
    
    public Comparable getEnd() {
        return this.end;
    }
    
    public void setCalendarField(final int calendarField) {
        if (!this.calendarFieldOK(calendarField)) {
            throw new IllegalArgumentException("invalid calendarField");
        }
        if (calendarField != this.calendarField) {
            this.calendarField = calendarField;
            this.fireStateChanged();
        }
    }
    
    public int getCalendarField() {
        return this.calendarField;
    }
    
    @Override
    public Object getNextValue() {
        final Calendar instance = Calendar.getInstance();
        instance.setTime(this.value.getTime());
        instance.add(this.calendarField, 1);
        final Date time = instance.getTime();
        return (this.end == null || this.end.compareTo(time) >= 0) ? time : null;
    }
    
    @Override
    public Object getPreviousValue() {
        final Calendar instance = Calendar.getInstance();
        instance.setTime(this.value.getTime());
        instance.add(this.calendarField, -1);
        final Date time = instance.getTime();
        return (this.start == null || this.start.compareTo(time) <= 0) ? time : null;
    }
    
    public Date getDate() {
        return this.value.getTime();
    }
    
    @Override
    public Object getValue() {
        return this.value.getTime();
    }
    
    @Override
    public void setValue(final Object o) {
        if (o == null || !(o instanceof Date)) {
            throw new IllegalArgumentException("illegal value");
        }
        if (!o.equals(this.value.getTime())) {
            this.value.setTime((Date)o);
            this.fireStateChanged();
        }
    }
}
