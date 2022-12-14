package sun.text.normalizer;

import java.util.Iterator;

public class UnicodeSetIterator
{
    public static int IS_STRING;
    public int codepoint;
    public int codepointEnd;
    public String string;
    private UnicodeSet set;
    private int endRange;
    private int range;
    protected int endElement;
    protected int nextElement;
    private Iterator<String> stringIterator;
    
    public UnicodeSetIterator(final UnicodeSet set) {
        this.endRange = 0;
        this.range = 0;
        this.stringIterator = null;
        this.reset(set);
    }
    
    public boolean nextRange() {
        if (this.nextElement <= this.endElement) {
            this.codepointEnd = this.endElement;
            this.codepoint = this.nextElement;
            this.nextElement = this.endElement + 1;
            return true;
        }
        if (this.range < this.endRange) {
            this.loadRange(++this.range);
            this.codepointEnd = this.endElement;
            this.codepoint = this.nextElement;
            this.nextElement = this.endElement + 1;
            return true;
        }
        if (this.stringIterator == null) {
            return false;
        }
        this.codepoint = UnicodeSetIterator.IS_STRING;
        this.string = this.stringIterator.next();
        if (!this.stringIterator.hasNext()) {
            this.stringIterator = null;
        }
        return true;
    }
    
    public void reset(final UnicodeSet set) {
        this.set = set;
        this.reset();
    }
    
    public void reset() {
        this.endRange = this.set.getRangeCount() - 1;
        this.range = 0;
        this.endElement = -1;
        this.nextElement = 0;
        if (this.endRange >= 0) {
            this.loadRange(this.range);
        }
        this.stringIterator = null;
        if (this.set.strings != null) {
            this.stringIterator = this.set.strings.iterator();
            if (!this.stringIterator.hasNext()) {
                this.stringIterator = null;
            }
        }
    }
    
    protected void loadRange(final int n) {
        this.nextElement = this.set.getRangeStart(n);
        this.endElement = this.set.getRangeEnd(n);
    }
    
    static {
        UnicodeSetIterator.IS_STRING = -1;
    }
}
