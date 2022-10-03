package javax.mail.internet;

import java.text.ParseException;
import java.util.logging.Level;
import java.text.DateFormatSymbols;
import java.text.NumberFormat;
import java.util.Calendar;
import java.text.ParsePosition;
import java.text.FieldPosition;
import java.util.Date;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectStreamException;
import java.util.Locale;
import java.util.TimeZone;
import com.sun.mail.util.MailLogger;
import java.text.SimpleDateFormat;

public class MailDateFormat extends SimpleDateFormat
{
    private static final long serialVersionUID = -8148227605210628779L;
    private static final String PATTERN = "EEE, d MMM yyyy HH:mm:ss Z (z)";
    private static final MailLogger LOGGER;
    private static final int UNKNOWN_DAY_NAME = -1;
    private static final TimeZone UTC;
    private static final int LEAP_SECOND = 60;
    
    public MailDateFormat() {
        super("EEE, d MMM yyyy HH:mm:ss Z (z)", Locale.US);
    }
    
    private Object writeReplace() throws ObjectStreamException {
        final MailDateFormat fmt = new MailDateFormat();
        fmt.superApplyPattern("EEE, d MMM yyyy HH:mm:ss 'XXXXX' (z)");
        fmt.setTimeZone(this.getTimeZone());
        return fmt;
    }
    
    private void readObject(final ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        super.applyPattern("EEE, d MMM yyyy HH:mm:ss Z (z)");
    }
    
    @Override
    public MailDateFormat clone() {
        return (MailDateFormat)super.clone();
    }
    
    @Override
    public StringBuffer format(final Date date, final StringBuffer dateStrBuf, final FieldPosition fieldPosition) {
        return super.format(date, dateStrBuf, fieldPosition);
    }
    
    @Override
    public Date parse(final String text, final ParsePosition pos) {
        if (text == null || pos == null) {
            throw new NullPointerException();
        }
        if (0 > pos.getIndex() || pos.getIndex() >= text.length()) {
            return null;
        }
        return this.isLenient() ? new Rfc2822LenientParser(text, pos).parse() : new Rfc2822StrictParser(text, pos).parse();
    }
    
    @Override
    public void setCalendar(final Calendar newCalendar) {
        throw new UnsupportedOperationException("Method setCalendar() shouldn't be called");
    }
    
    @Override
    public void setNumberFormat(final NumberFormat newNumberFormat) {
        throw new UnsupportedOperationException("Method setNumberFormat() shouldn't be called");
    }
    
    @Override
    public void applyLocalizedPattern(final String pattern) {
        throw new UnsupportedOperationException("Method applyLocalizedPattern() shouldn't be called");
    }
    
    @Override
    public void applyPattern(final String pattern) {
        throw new UnsupportedOperationException("Method applyPattern() shouldn't be called");
    }
    
    private void superApplyPattern(final String pattern) {
        super.applyPattern(pattern);
    }
    
    @Override
    public Date get2DigitYearStart() {
        throw new UnsupportedOperationException("Method get2DigitYearStart() shouldn't be called");
    }
    
    @Override
    public void set2DigitYearStart(final Date startDate) {
        throw new UnsupportedOperationException("Method set2DigitYearStart() shouldn't be called");
    }
    
    @Override
    public void setDateFormatSymbols(final DateFormatSymbols newFormatSymbols) {
        throw new UnsupportedOperationException("Method setDateFormatSymbols() shouldn't be called");
    }
    
    private Date toDate(final int dayName, final int day, final int month, final int year, final int hour, final int minute, int second, final int zone) {
        if (second == 60) {
            second = 59;
        }
        final TimeZone tz = this.calendar.getTimeZone();
        try {
            this.calendar.setTimeZone(MailDateFormat.UTC);
            this.calendar.clear();
            this.calendar.set(year, month, day, hour, minute, second);
            if (dayName == -1 || dayName == this.calendar.get(7)) {
                this.calendar.add(12, zone);
                return this.calendar.getTime();
            }
            throw new IllegalArgumentException("Inconsistent day-name");
        }
        finally {
            this.calendar.setTimeZone(tz);
        }
    }
    
    static {
        LOGGER = new MailLogger(MailDateFormat.class, "DEBUG", false, System.out);
        UTC = TimeZone.getTimeZone("UTC");
    }
    
    private abstract static class AbstractDateParser
    {
        static final int INVALID_CHAR = -1;
        static final int MAX_YEAR_DIGITS = 8;
        final String text;
        final ParsePosition pos;
        
        AbstractDateParser(final String text, final ParsePosition pos) {
            this.text = text;
            this.pos = pos;
        }
        
        final Date parse() {
            final int startPosition = this.pos.getIndex();
            try {
                return this.tryParse();
            }
            catch (final Exception e) {
                if (MailDateFormat.LOGGER.isLoggable(Level.FINE)) {
                    MailDateFormat.LOGGER.log(Level.FINE, "Bad date: '" + this.text + "'", e);
                }
                this.pos.setErrorIndex(this.pos.getIndex());
                this.pos.setIndex(startPosition);
                return null;
            }
        }
        
        abstract Date tryParse() throws ParseException;
        
        final int parseDayName() throws ParseException {
            switch (this.getChar()) {
                case 83: {
                    if (this.skipPair('u', 'n')) {
                        return 1;
                    }
                    if (this.skipPair('a', 't')) {
                        return 7;
                    }
                    break;
                }
                case 84: {
                    if (this.skipPair('u', 'e')) {
                        return 3;
                    }
                    if (this.skipPair('h', 'u')) {
                        return 5;
                    }
                    break;
                }
                case 77: {
                    if (this.skipPair('o', 'n')) {
                        return 2;
                    }
                    break;
                }
                case 87: {
                    if (this.skipPair('e', 'd')) {
                        return 4;
                    }
                    break;
                }
                case 70: {
                    if (this.skipPair('r', 'i')) {
                        return 6;
                    }
                    break;
                }
                case -1: {
                    throw new ParseException("Invalid day-name", this.pos.getIndex());
                }
            }
            this.pos.setIndex(this.pos.getIndex() - 1);
            throw new ParseException("Invalid day-name", this.pos.getIndex());
        }
        
        final int parseMonthName(final boolean caseSensitive) throws ParseException {
            switch (this.getChar()) {
                case 106: {
                    if (caseSensitive) {
                        break;
                    }
                }
                case 74: {
                    if (this.skipChar('u') || (!caseSensitive && this.skipChar('U'))) {
                        if (this.skipChar('l') || (!caseSensitive && this.skipChar('L'))) {
                            return 6;
                        }
                        if (this.skipChar('n') || (!caseSensitive && this.skipChar('N'))) {
                            return 5;
                        }
                        this.pos.setIndex(this.pos.getIndex() - 1);
                        break;
                    }
                    else {
                        if (this.skipPair('a', 'n') || (!caseSensitive && this.skipAlternativePair('a', 'A', 'n', 'N'))) {
                            return 0;
                        }
                        break;
                    }
                    break;
                }
                case 109: {
                    if (caseSensitive) {
                        break;
                    }
                }
                case 77: {
                    if (!this.skipChar('a') && (caseSensitive || !this.skipChar('A'))) {
                        break;
                    }
                    if (this.skipChar('r') || (!caseSensitive && this.skipChar('R'))) {
                        return 2;
                    }
                    if (this.skipChar('y') || (!caseSensitive && this.skipChar('Y'))) {
                        return 4;
                    }
                    this.pos.setIndex(this.pos.getIndex() - 1);
                    break;
                }
                case 97: {
                    if (caseSensitive) {
                        break;
                    }
                }
                case 65: {
                    if (this.skipPair('u', 'g') || (!caseSensitive && this.skipAlternativePair('u', 'U', 'g', 'G'))) {
                        return 7;
                    }
                    if (this.skipPair('p', 'r') || (!caseSensitive && this.skipAlternativePair('p', 'P', 'r', 'R'))) {
                        return 3;
                    }
                    break;
                }
                case 100: {
                    if (caseSensitive) {
                        break;
                    }
                }
                case 68: {
                    if (this.skipPair('e', 'c') || (!caseSensitive && this.skipAlternativePair('e', 'E', 'c', 'C'))) {
                        return 11;
                    }
                    break;
                }
                case 111: {
                    if (caseSensitive) {
                        break;
                    }
                }
                case 79: {
                    if (this.skipPair('c', 't') || (!caseSensitive && this.skipAlternativePair('c', 'C', 't', 'T'))) {
                        return 9;
                    }
                    break;
                }
                case 115: {
                    if (caseSensitive) {
                        break;
                    }
                }
                case 83: {
                    if (this.skipPair('e', 'p') || (!caseSensitive && this.skipAlternativePair('e', 'E', 'p', 'P'))) {
                        return 8;
                    }
                    break;
                }
                case 110: {
                    if (caseSensitive) {
                        break;
                    }
                }
                case 78: {
                    if (this.skipPair('o', 'v') || (!caseSensitive && this.skipAlternativePair('o', 'O', 'v', 'V'))) {
                        return 10;
                    }
                    break;
                }
                case 102: {
                    if (caseSensitive) {
                        break;
                    }
                }
                case 70: {
                    if (this.skipPair('e', 'b') || (!caseSensitive && this.skipAlternativePair('e', 'E', 'b', 'B'))) {
                        return 1;
                    }
                    break;
                }
                case -1: {
                    throw new ParseException("Invalid month", this.pos.getIndex());
                }
            }
            this.pos.setIndex(this.pos.getIndex() - 1);
            throw new ParseException("Invalid month", this.pos.getIndex());
        }
        
        final int parseZoneOffset() throws ParseException {
            final int sign = this.getChar();
            if (sign != 43 && sign != 45) {
                if (sign != -1) {
                    this.pos.setIndex(this.pos.getIndex() - 1);
                }
                throw new ParseException("Invalid zone", this.pos.getIndex());
            }
            final int offset = this.parseAsciiDigits(4, 4, true);
            if (!this.isValidZoneOffset(offset)) {
                this.pos.setIndex(this.pos.getIndex() - 5);
                throw new ParseException("Invalid zone", this.pos.getIndex());
            }
            return ((sign == 43) ? -1 : 1) * (offset / 100 * 60 + offset % 100);
        }
        
        boolean isValidZoneOffset(final int offset) {
            return offset % 100 < 60;
        }
        
        final int parseAsciiDigits(final int count) throws ParseException {
            return this.parseAsciiDigits(count, count);
        }
        
        final int parseAsciiDigits(final int min, final int max) throws ParseException {
            return this.parseAsciiDigits(min, max, false);
        }
        
        final int parseAsciiDigits(final int min, final int max, final boolean isEOF) throws ParseException {
            int result = 0;
            int nbDigitsParsed;
            for (nbDigitsParsed = 0; nbDigitsParsed < max && this.peekAsciiDigit(); ++nbDigitsParsed) {
                result = result * 10 + this.getAsciiDigit();
            }
            if (nbDigitsParsed < min || (nbDigitsParsed == max && !isEOF && this.peekAsciiDigit())) {
                this.pos.setIndex(this.pos.getIndex() - nbDigitsParsed);
                final String range = (min == max) ? Integer.toString(min) : ("between " + min + " and " + max);
                throw new ParseException("Invalid input: expected " + range + " ASCII digits", this.pos.getIndex());
            }
            return result;
        }
        
        final void parseFoldingWhiteSpace() throws ParseException {
            if (!this.skipFoldingWhiteSpace()) {
                throw new ParseException("Invalid input: expected FWS", this.pos.getIndex());
            }
        }
        
        final void parseChar(final char ch) throws ParseException {
            if (!this.skipChar(ch)) {
                throw new ParseException("Invalid input: expected '" + ch + "'", this.pos.getIndex());
            }
        }
        
        final int getAsciiDigit() {
            final int ch = this.getChar();
            if (48 <= ch && ch <= 57) {
                return Character.digit((char)ch, 10);
            }
            if (ch != -1) {
                this.pos.setIndex(this.pos.getIndex() - 1);
            }
            return -1;
        }
        
        final int getChar() {
            if (this.pos.getIndex() < this.text.length()) {
                final char ch = this.text.charAt(this.pos.getIndex());
                this.pos.setIndex(this.pos.getIndex() + 1);
                return ch;
            }
            return -1;
        }
        
        boolean skipFoldingWhiteSpace() {
            if (this.skipChar(' ')) {
                if (!this.peekFoldingWhiteSpace()) {
                    return true;
                }
                this.pos.setIndex(this.pos.getIndex() - 1);
            }
            else if (!this.peekFoldingWhiteSpace()) {
                return false;
            }
            final int startIndex = this.pos.getIndex();
            if (this.skipWhiteSpace()) {
                while (this.skipNewline()) {
                    if (!this.skipWhiteSpace()) {
                        this.pos.setIndex(startIndex);
                        return false;
                    }
                }
                return true;
            }
            if (this.skipNewline() && this.skipWhiteSpace()) {
                return true;
            }
            this.pos.setIndex(startIndex);
            return false;
        }
        
        final boolean skipWhiteSpace() {
            final int startIndex = this.pos.getIndex();
            while (this.skipAlternative(' ', '\t')) {}
            return this.pos.getIndex() > startIndex;
        }
        
        final boolean skipNewline() {
            return this.skipPair('\r', '\n');
        }
        
        final boolean skipAlternativeTriple(final char firstStandard, final char firstAlternative, final char secondStandard, final char secondAlternative, final char thirdStandard, final char thirdAlternative) {
            if (this.skipAlternativePair(firstStandard, firstAlternative, secondStandard, secondAlternative)) {
                if (this.skipAlternative(thirdStandard, thirdAlternative)) {
                    return true;
                }
                this.pos.setIndex(this.pos.getIndex() - 2);
            }
            return false;
        }
        
        final boolean skipAlternativePair(final char firstStandard, final char firstAlternative, final char secondStandard, final char secondAlternative) {
            if (this.skipAlternative(firstStandard, firstAlternative)) {
                if (this.skipAlternative(secondStandard, secondAlternative)) {
                    return true;
                }
                this.pos.setIndex(this.pos.getIndex() - 1);
            }
            return false;
        }
        
        final boolean skipAlternative(final char standard, final char alternative) {
            return this.skipChar(standard) || this.skipChar(alternative);
        }
        
        final boolean skipPair(final char first, final char second) {
            if (this.skipChar(first)) {
                if (this.skipChar(second)) {
                    return true;
                }
                this.pos.setIndex(this.pos.getIndex() - 1);
            }
            return false;
        }
        
        final boolean skipChar(final char ch) {
            if (this.pos.getIndex() < this.text.length() && this.text.charAt(this.pos.getIndex()) == ch) {
                this.pos.setIndex(this.pos.getIndex() + 1);
                return true;
            }
            return false;
        }
        
        final boolean peekAsciiDigit() {
            return this.pos.getIndex() < this.text.length() && '0' <= this.text.charAt(this.pos.getIndex()) && this.text.charAt(this.pos.getIndex()) <= '9';
        }
        
        boolean peekFoldingWhiteSpace() {
            return this.pos.getIndex() < this.text.length() && (this.text.charAt(this.pos.getIndex()) == ' ' || this.text.charAt(this.pos.getIndex()) == '\t' || this.text.charAt(this.pos.getIndex()) == '\r');
        }
        
        final boolean peekChar(final char ch) {
            return this.pos.getIndex() < this.text.length() && this.text.charAt(this.pos.getIndex()) == ch;
        }
    }
    
    private class Rfc2822StrictParser extends AbstractDateParser
    {
        Rfc2822StrictParser(final String text, final ParsePosition pos) {
            super(text, pos);
        }
        
        @Override
        Date tryParse() throws ParseException {
            final int dayName = this.parseOptionalBegin();
            final int day = this.parseDay();
            final int month = this.parseMonth();
            final int year = this.parseYear();
            this.parseFoldingWhiteSpace();
            final int hour = this.parseHour();
            this.parseChar(':');
            final int minute = this.parseMinute();
            final int second = this.skipChar(':') ? this.parseSecond() : 0;
            this.parseFwsBetweenTimeOfDayAndZone();
            final int zone = this.parseZone();
            try {
                return MailDateFormat.this.toDate(dayName, day, month, year, hour, minute, second, zone);
            }
            catch (final IllegalArgumentException e) {
                throw new ParseException("Invalid input: some of the calendar fields have invalid values, or day-name is inconsistent with date", this.pos.getIndex());
            }
        }
        
        int parseOptionalBegin() throws ParseException {
            int dayName;
            if (!this.peekAsciiDigit()) {
                this.skipFoldingWhiteSpace();
                dayName = this.parseDayName();
                this.parseChar(',');
            }
            else {
                dayName = -1;
            }
            return dayName;
        }
        
        int parseDay() throws ParseException {
            this.skipFoldingWhiteSpace();
            return this.parseAsciiDigits(1, 2);
        }
        
        int parseMonth() throws ParseException {
            this.parseFwsInMonth();
            final int month = this.parseMonthName(this.isMonthNameCaseSensitive());
            this.parseFwsInMonth();
            return month;
        }
        
        void parseFwsInMonth() throws ParseException {
            this.parseFoldingWhiteSpace();
        }
        
        boolean isMonthNameCaseSensitive() {
            return true;
        }
        
        int parseYear() throws ParseException {
            final int year = this.parseAsciiDigits(4, 8);
            if (year >= 1900) {
                return year;
            }
            this.pos.setIndex(this.pos.getIndex() - 4);
            while (this.text.charAt(this.pos.getIndex() - 1) == '0') {
                this.pos.setIndex(this.pos.getIndex() - 1);
            }
            throw new ParseException("Invalid year", this.pos.getIndex());
        }
        
        int parseHour() throws ParseException {
            return this.parseAsciiDigits(2);
        }
        
        int parseMinute() throws ParseException {
            return this.parseAsciiDigits(2);
        }
        
        int parseSecond() throws ParseException {
            return this.parseAsciiDigits(2);
        }
        
        void parseFwsBetweenTimeOfDayAndZone() throws ParseException {
            this.parseFoldingWhiteSpace();
        }
        
        int parseZone() throws ParseException {
            return this.parseZoneOffset();
        }
    }
    
    private class Rfc2822LenientParser extends Rfc2822StrictParser
    {
        private Boolean hasDefaultFws;
        
        Rfc2822LenientParser(final String text, final ParsePosition pos) {
            super(text, pos);
        }
        
        @Override
        int parseOptionalBegin() {
            while (this.pos.getIndex() < this.text.length() && !this.peekAsciiDigit()) {
                this.pos.setIndex(this.pos.getIndex() + 1);
            }
            return -1;
        }
        
        @Override
        int parseDay() throws ParseException {
            this.skipFoldingWhiteSpace();
            return this.parseAsciiDigits(1, 3);
        }
        
        @Override
        void parseFwsInMonth() throws ParseException {
            if (this.hasDefaultFws == null) {
                this.hasDefaultFws = !this.skipChar('-');
                this.skipFoldingWhiteSpace();
            }
            else if (this.hasDefaultFws) {
                this.skipFoldingWhiteSpace();
            }
            else {
                this.parseChar('-');
            }
        }
        
        @Override
        boolean isMonthNameCaseSensitive() {
            return false;
        }
        
        @Override
        int parseYear() throws ParseException {
            final int year = this.parseAsciiDigits(1, 8);
            if (year >= 1000) {
                return year;
            }
            if (year >= 50) {
                return year + 1900;
            }
            return year + 2000;
        }
        
        @Override
        int parseHour() throws ParseException {
            return this.parseAsciiDigits(1, 2);
        }
        
        @Override
        int parseMinute() throws ParseException {
            return this.parseAsciiDigits(1, 2);
        }
        
        @Override
        int parseSecond() throws ParseException {
            return this.parseAsciiDigits(1, 2);
        }
        
        @Override
        void parseFwsBetweenTimeOfDayAndZone() throws ParseException {
            this.skipFoldingWhiteSpace();
        }
        
        @Override
        int parseZone() throws ParseException {
            try {
                if (this.pos.getIndex() >= this.text.length()) {
                    throw new ParseException("Missing zone", this.pos.getIndex());
                }
                if (this.peekChar('+') || this.peekChar('-')) {
                    return this.parseZoneOffset();
                }
                if (this.skipAlternativePair('U', 'u', 'T', 't')) {
                    return 0;
                }
                if (this.skipAlternativeTriple('G', 'g', 'M', 'm', 'T', 't')) {
                    return 0;
                }
                int hoursOffset;
                if (this.skipAlternative('E', 'e')) {
                    hoursOffset = 4;
                }
                else if (this.skipAlternative('C', 'c')) {
                    hoursOffset = 5;
                }
                else if (this.skipAlternative('M', 'm')) {
                    hoursOffset = 6;
                }
                else {
                    if (!this.skipAlternative('P', 'p')) {
                        throw new ParseException("Invalid zone", this.pos.getIndex());
                    }
                    hoursOffset = 7;
                }
                if (this.skipAlternativePair('S', 's', 'T', 't')) {
                    ++hoursOffset;
                }
                else if (!this.skipAlternativePair('D', 'd', 'T', 't')) {
                    this.pos.setIndex(this.pos.getIndex() - 1);
                    throw new ParseException("Invalid zone", this.pos.getIndex());
                }
                return hoursOffset * 60;
            }
            catch (final ParseException e) {
                if (MailDateFormat.LOGGER.isLoggable(Level.FINE)) {
                    MailDateFormat.LOGGER.log(Level.FINE, "No timezone? : '" + this.text + "'", e);
                }
                return 0;
            }
        }
        
        @Override
        boolean isValidZoneOffset(final int offset) {
            return true;
        }
        
        @Override
        boolean skipFoldingWhiteSpace() {
            final boolean result = this.peekFoldingWhiteSpace();
        Label_0102:
            while (this.pos.getIndex() < this.text.length()) {
                switch (this.text.charAt(this.pos.getIndex())) {
                    case '\t':
                    case '\n':
                    case '\r':
                    case ' ': {
                        this.pos.setIndex(this.pos.getIndex() + 1);
                        continue;
                    }
                    default: {
                        break Label_0102;
                    }
                }
            }
            return result;
        }
        
        @Override
        boolean peekFoldingWhiteSpace() {
            return super.peekFoldingWhiteSpace() || (this.pos.getIndex() < this.text.length() && this.text.charAt(this.pos.getIndex()) == '\n');
        }
    }
}
