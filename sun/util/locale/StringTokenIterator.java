package sun.util.locale;

public class StringTokenIterator
{
    private String text;
    private String dlms;
    private char delimiterChar;
    private String token;
    private int start;
    private int end;
    private boolean done;
    
    public StringTokenIterator(final String text, final String dlms) {
        this.text = text;
        if (dlms.length() == 1) {
            this.delimiterChar = dlms.charAt(0);
        }
        else {
            this.dlms = dlms;
        }
        this.setStart(0);
    }
    
    public String first() {
        this.setStart(0);
        return this.token;
    }
    
    public String current() {
        return this.token;
    }
    
    public int currentStart() {
        return this.start;
    }
    
    public int currentEnd() {
        return this.end;
    }
    
    public boolean isDone() {
        return this.done;
    }
    
    public String next() {
        if (this.hasNext()) {
            this.start = this.end + 1;
            this.end = this.nextDelimiter(this.start);
            this.token = this.text.substring(this.start, this.end);
        }
        else {
            this.start = this.end;
            this.token = null;
            this.done = true;
        }
        return this.token;
    }
    
    public boolean hasNext() {
        return this.end < this.text.length();
    }
    
    public StringTokenIterator setStart(final int start) {
        if (start > this.text.length()) {
            throw new IndexOutOfBoundsException();
        }
        this.start = start;
        this.end = this.nextDelimiter(this.start);
        this.token = this.text.substring(this.start, this.end);
        this.done = false;
        return this;
    }
    
    public StringTokenIterator setText(final String text) {
        this.text = text;
        this.setStart(0);
        return this;
    }
    
    private int nextDelimiter(final int n) {
        final int length = this.text.length();
        if (this.dlms == null) {
            for (int i = n; i < length; ++i) {
                if (this.text.charAt(i) == this.delimiterChar) {
                    return i;
                }
            }
        }
        else {
            final int length2 = this.dlms.length();
            for (int j = n; j < length; ++j) {
                final char char1 = this.text.charAt(j);
                for (int k = 0; k < length2; ++k) {
                    if (char1 == this.dlms.charAt(k)) {
                        return j;
                    }
                }
            }
        }
        return length;
    }
}
