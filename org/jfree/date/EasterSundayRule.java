package org.jfree.date;

public class EasterSundayRule extends AnnualDateRule
{
    public SerialDate getDate(final int year) {
        final int g = year % 19;
        final int c = year / 100;
        final int h = (c - c / 4 - (8 * c + 13) / 25 + 19 * g + 15) % 30;
        final int i = h - h / 28 * (1 - h / 28 * 29 / (h + 1) * (21 - g) / 11);
        final int j = (year + year / 4 + i + 2 - c + c / 4) % 7;
        final int l = i - j;
        final int month = 3 + (l + 40) / 44;
        final int day = l + 28 - 31 * (month / 4);
        return SerialDate.createInstance(day, month, year);
    }
}
