package org.bouncycastle.crypto.tls;

import java.util.Vector;

class DTLSReassembler
{
    private short msg_type;
    private byte[] body;
    private Vector missing;
    
    DTLSReassembler(final short msg_type, final int n) {
        this.missing = new Vector();
        this.msg_type = msg_type;
        this.body = new byte[n];
        this.missing.addElement(new Range(0, n));
    }
    
    short getMsgType() {
        return this.msg_type;
    }
    
    byte[] getBodyIfComplete() {
        return (byte[])(this.missing.isEmpty() ? this.body : null);
    }
    
    void contributeFragment(final short n, final int n2, final byte[] array, final int n3, final int n4, final int n5) {
        final int n6 = n4 + n5;
        if (this.msg_type != n || this.body.length != n2 || n6 > n2) {
            return;
        }
        if (n5 == 0) {
            if (n4 == 0 && !this.missing.isEmpty() && this.missing.firstElement().getEnd() == 0) {
                this.missing.removeElementAt(0);
            }
            return;
        }
        for (int i = 0; i < this.missing.size(); ++i) {
            final Range range = this.missing.elementAt(i);
            if (range.getStart() >= n6) {
                break;
            }
            if (range.getEnd() > n4) {
                final int max = Math.max(range.getStart(), n4);
                final int min = Math.min(range.getEnd(), n6);
                System.arraycopy(array, n3 + max - n4, this.body, max, min - max);
                if (max == range.getStart()) {
                    if (min == range.getEnd()) {
                        this.missing.removeElementAt(i--);
                    }
                    else {
                        range.setStart(min);
                    }
                }
                else {
                    if (min != range.getEnd()) {
                        this.missing.insertElementAt(new Range(min, range.getEnd()), ++i);
                    }
                    range.setEnd(max);
                }
            }
        }
    }
    
    void reset() {
        this.missing.removeAllElements();
        this.missing.addElement(new Range(0, this.body.length));
    }
    
    private static class Range
    {
        private int start;
        private int end;
        
        Range(final int start, final int end) {
            this.start = start;
            this.end = end;
        }
        
        public int getStart() {
            return this.start;
        }
        
        public void setStart(final int start) {
            this.start = start;
        }
        
        public int getEnd() {
            return this.end;
        }
        
        public void setEnd(final int end) {
            this.end = end;
        }
    }
}
