package sun.security.jgss;

import org.ietf.jgss.MessageProp;
import java.util.LinkedList;

public class TokenTracker
{
    static final int MAX_INTERVALS = 5;
    private int initNumber;
    private int windowStart;
    private int expectedNumber;
    private int windowStartIndex;
    private LinkedList<Entry> list;
    
    public TokenTracker(final int expectedNumber) {
        this.windowStartIndex = 0;
        this.list = new LinkedList<Entry>();
        this.initNumber = expectedNumber;
        this.windowStart = expectedNumber;
        this.expectedNumber = expectedNumber;
        this.list.add(new Entry(expectedNumber - 1));
    }
    
    private int getIntervalIndex(final int n) {
        int n2;
        for (n2 = this.list.size() - 1; n2 >= 0 && this.list.get(n2).compareTo(n) > 0; --n2) {}
        return n2;
    }
    
    public final synchronized void getProps(final int n, final MessageProp messageProp) {
        boolean b = false;
        boolean b2 = false;
        boolean b3 = false;
        boolean b4 = false;
        final int intervalIndex = this.getIntervalIndex(n);
        Entry entry = null;
        if (intervalIndex != -1) {
            entry = this.list.get(intervalIndex);
        }
        if (n == this.expectedNumber) {
            ++this.expectedNumber;
        }
        else if (entry != null && entry.contains(n)) {
            b4 = true;
        }
        else if (this.expectedNumber >= this.initNumber) {
            if (n > this.expectedNumber) {
                b = true;
            }
            else if (n >= this.windowStart) {
                b3 = true;
            }
            else if (n >= this.initNumber) {
                b2 = true;
            }
            else {
                b = true;
            }
        }
        else if (n > this.expectedNumber) {
            if (n < this.initNumber) {
                b = true;
            }
            else if (this.windowStart >= this.initNumber) {
                if (n >= this.windowStart) {
                    b3 = true;
                }
                else {
                    b2 = true;
                }
            }
            else {
                b2 = true;
            }
        }
        else if (this.windowStart > this.expectedNumber) {
            b3 = true;
        }
        else if (n < this.windowStart) {
            b2 = true;
        }
        else {
            b3 = true;
        }
        if (!b4 && !b2) {
            this.add(n, intervalIndex);
        }
        if (b) {
            this.expectedNumber = n + 1;
        }
        messageProp.setSupplementaryStates(b4, b2, b3, b, 0, null);
    }
    
    private void add(final int windowStart, int n) {
        Entry entry = null;
        boolean b = false;
        boolean b2 = false;
        if (n != -1) {
            entry = this.list.get(n);
            if (windowStart == entry.getEnd() + 1) {
                entry.setEnd(windowStart);
                b = true;
            }
        }
        final int n2 = n + 1;
        if (n2 < this.list.size()) {
            final Entry entry2 = this.list.get(n2);
            if (windowStart == entry2.getStart() - 1) {
                if (!b) {
                    entry2.setStart(windowStart);
                }
                else {
                    entry2.setStart(entry.getStart());
                    this.list.remove(n);
                    if (this.windowStartIndex > n) {
                        --this.windowStartIndex;
                    }
                }
                b2 = true;
            }
        }
        if (b2 || b) {
            return;
        }
        Entry entry3;
        if (this.list.size() < 5) {
            entry3 = new Entry(windowStart);
            if (n < this.windowStartIndex) {
                ++this.windowStartIndex;
            }
        }
        else {
            final int windowStartIndex = this.windowStartIndex;
            if (this.windowStartIndex == this.list.size() - 1) {
                this.windowStartIndex = 0;
            }
            entry3 = this.list.remove(windowStartIndex);
            this.windowStart = this.list.get(this.windowStartIndex).getStart();
            entry3.setStart(windowStart);
            entry3.setEnd(windowStart);
            if (n >= windowStartIndex) {
                --n;
            }
            else if (windowStartIndex != this.windowStartIndex) {
                if (n == -1) {
                    this.windowStart = windowStart;
                }
            }
            else {
                ++this.windowStartIndex;
            }
        }
        this.list.add(n + 1, entry3);
    }
    
    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("TokenTracker: ");
        sb.append(" initNumber=").append(this.initNumber);
        sb.append(" windowStart=").append(this.windowStart);
        sb.append(" expectedNumber=").append(this.expectedNumber);
        sb.append(" windowStartIndex=").append(this.windowStartIndex);
        sb.append("\n\tIntervals are: {");
        for (int i = 0; i < this.list.size(); ++i) {
            if (i != 0) {
                sb.append(", ");
            }
            sb.append(this.list.get(i).toString());
        }
        sb.append('}');
        return sb.toString();
    }
    
    class Entry
    {
        private int start;
        private int end;
        
        Entry(final int n) {
            this.start = n;
            this.end = n;
        }
        
        final int compareTo(final int n) {
            if (this.start > n) {
                return 1;
            }
            if (this.end < n) {
                return -1;
            }
            return 0;
        }
        
        final boolean contains(final int n) {
            return n >= this.start && n <= this.end;
        }
        
        final void append(final int end) {
            if (end == this.end + 1) {
                this.end = end;
            }
        }
        
        final void setInterval(final int start, final int end) {
            this.start = start;
            this.end = end;
        }
        
        final void setEnd(final int end) {
            this.end = end;
        }
        
        final void setStart(final int start) {
            this.start = start;
        }
        
        final int getStart() {
            return this.start;
        }
        
        final int getEnd() {
            return this.end;
        }
        
        @Override
        public String toString() {
            return "[" + this.start + ", " + this.end + "]";
        }
    }
}
