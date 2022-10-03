package org.apache.lucene.util.automaton;

import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;

public final class UTF32ToUTF8
{
    private static final int[] startCodes;
    private static final int[] endCodes;
    static int[] MASKS;
    private final UTF8Sequence startUTF8;
    private final UTF8Sequence endUTF8;
    private final UTF8Sequence tmpUTF8a;
    private final UTF8Sequence tmpUTF8b;
    Automaton.Builder utf8;
    
    public UTF32ToUTF8() {
        this.startUTF8 = new UTF8Sequence();
        this.endUTF8 = new UTF8Sequence();
        this.tmpUTF8a = new UTF8Sequence();
        this.tmpUTF8b = new UTF8Sequence();
    }
    
    void convertOneEdge(final int start, final int end, final int startCodePoint, final int endCodePoint) {
        this.startUTF8.set(startCodePoint);
        this.endUTF8.set(endCodePoint);
        this.build(start, end, this.startUTF8, this.endUTF8, 0);
    }
    
    private void build(final int start, final int end, final UTF8Sequence startUTF8, final UTF8Sequence endUTF8, final int upto) {
        if (startUTF8.byteAt(upto) == endUTF8.byteAt(upto)) {
            if (upto == startUTF8.len - 1 && upto == endUTF8.len - 1) {
                this.utf8.addTransition(start, end, startUTF8.byteAt(upto), endUTF8.byteAt(upto));
                return;
            }
            assert startUTF8.len > upto + 1;
            assert endUTF8.len > upto + 1;
            final int n = this.utf8.createState();
            this.utf8.addTransition(start, n, startUTF8.byteAt(upto));
            this.build(n, end, startUTF8, endUTF8, 1 + upto);
        }
        else if (startUTF8.len == endUTF8.len) {
            if (upto == startUTF8.len - 1) {
                this.utf8.addTransition(start, end, startUTF8.byteAt(upto), endUTF8.byteAt(upto));
            }
            else {
                this.start(start, end, startUTF8, upto, false);
                if (endUTF8.byteAt(upto) - startUTF8.byteAt(upto) > 1) {
                    this.all(start, end, startUTF8.byteAt(upto) + 1, endUTF8.byteAt(upto) - 1, startUTF8.len - upto - 1);
                }
                this.end(start, end, endUTF8, upto, false);
            }
        }
        else {
            this.start(start, end, startUTF8, upto, true);
            for (int byteCount = 1 + startUTF8.len - upto, limit = endUTF8.len - upto; byteCount < limit; ++byteCount) {
                this.tmpUTF8a.set(UTF32ToUTF8.startCodes[byteCount - 1]);
                this.tmpUTF8b.set(UTF32ToUTF8.endCodes[byteCount - 1]);
                this.all(start, end, this.tmpUTF8a.byteAt(0), this.tmpUTF8b.byteAt(0), this.tmpUTF8a.len - 1);
            }
            this.end(start, end, endUTF8, upto, true);
        }
    }
    
    private void start(final int start, final int end, final UTF8Sequence startUTF8, final int upto, final boolean doAll) {
        if (upto == startUTF8.len - 1) {
            this.utf8.addTransition(start, end, startUTF8.byteAt(upto), startUTF8.byteAt(upto) | UTF32ToUTF8.MASKS[startUTF8.numBits(upto) - 1]);
        }
        else {
            final int n = this.utf8.createState();
            this.utf8.addTransition(start, n, startUTF8.byteAt(upto));
            this.start(n, end, startUTF8, 1 + upto, true);
            final int endCode = startUTF8.byteAt(upto) | UTF32ToUTF8.MASKS[startUTF8.numBits(upto) - 1];
            if (doAll && startUTF8.byteAt(upto) != endCode) {
                this.all(start, end, startUTF8.byteAt(upto) + 1, endCode, startUTF8.len - upto - 1);
            }
        }
    }
    
    private void end(final int start, final int end, final UTF8Sequence endUTF8, final int upto, final boolean doAll) {
        if (upto == endUTF8.len - 1) {
            this.utf8.addTransition(start, end, endUTF8.byteAt(upto) & ~UTF32ToUTF8.MASKS[endUTF8.numBits(upto) - 1], endUTF8.byteAt(upto));
        }
        else {
            int startCode;
            if (endUTF8.numBits(upto) == 5) {
                startCode = 194;
            }
            else {
                startCode = (endUTF8.byteAt(upto) & ~UTF32ToUTF8.MASKS[endUTF8.numBits(upto) - 1]);
            }
            if (doAll && endUTF8.byteAt(upto) != startCode) {
                this.all(start, end, startCode, endUTF8.byteAt(upto) - 1, endUTF8.len - upto - 1);
            }
            final int n = this.utf8.createState();
            this.utf8.addTransition(start, n, endUTF8.byteAt(upto));
            this.end(n, end, endUTF8, 1 + upto, true);
        }
    }
    
    private void all(final int start, final int end, final int startCode, final int endCode, int left) {
        if (left == 0) {
            this.utf8.addTransition(start, end, startCode, endCode);
        }
        else {
            int lastN = this.utf8.createState();
            this.utf8.addTransition(start, lastN, startCode, endCode);
            while (left > 1) {
                final int n = this.utf8.createState();
                this.utf8.addTransition(lastN, n, 128, 191);
                --left;
                lastN = n;
            }
            this.utf8.addTransition(lastN, end, 128, 191);
        }
    }
    
    public Automaton convert(final Automaton utf32) {
        if (utf32.getNumStates() == 0) {
            return utf32;
        }
        final int[] map = new int[utf32.getNumStates()];
        Arrays.fill(map, -1);
        final List<Integer> pending = new ArrayList<Integer>();
        int utf32State = 0;
        pending.add(utf32State);
        this.utf8 = new Automaton.Builder();
        int utf8State = this.utf8.createState();
        this.utf8.setAccept(utf8State, utf32.isAccept(utf32State));
        map[utf32State] = utf8State;
        final Transition scratch = new Transition();
        while (pending.size() != 0) {
            utf32State = pending.remove(pending.size() - 1);
            utf8State = map[utf32State];
            assert utf8State != -1;
            final int numTransitions = utf32.getNumTransitions(utf32State);
            utf32.initTransition(utf32State, scratch);
            for (int i = 0; i < numTransitions; ++i) {
                utf32.getNextTransition(scratch);
                final int destUTF32 = scratch.dest;
                int destUTF33 = map[destUTF32];
                if (destUTF33 == -1) {
                    destUTF33 = this.utf8.createState();
                    this.utf8.setAccept(destUTF33, utf32.isAccept(destUTF32));
                    map[destUTF32] = destUTF33;
                    pending.add(destUTF32);
                }
                this.convertOneEdge(utf8State, destUTF33, scratch.min, scratch.max);
            }
        }
        return this.utf8.finish();
    }
    
    static {
        startCodes = new int[] { 0, 128, 2048, 65536 };
        endCodes = new int[] { 127, 2047, 65535, 1114111 };
        UTF32ToUTF8.MASKS = new int[32];
        int v = 2;
        for (int i = 0; i < 32; ++i) {
            UTF32ToUTF8.MASKS[i] = v - 1;
            v *= 2;
        }
    }
    
    private static class UTF8Byte
    {
        int value;
        byte bits;
    }
    
    private static class UTF8Sequence
    {
        private final UTF8Byte[] bytes;
        private int len;
        
        public UTF8Sequence() {
            this.bytes = new UTF8Byte[4];
            for (int i = 0; i < 4; ++i) {
                this.bytes[i] = new UTF8Byte();
            }
        }
        
        public int byteAt(final int idx) {
            return this.bytes[idx].value;
        }
        
        public int numBits(final int idx) {
            return this.bytes[idx].bits;
        }
        
        private void set(final int code) {
            if (code < 128) {
                this.bytes[0].value = code;
                this.bytes[0].bits = 7;
                this.len = 1;
            }
            else if (code < 2048) {
                this.bytes[0].value = (0xC0 | code >> 6);
                this.bytes[0].bits = 5;
                this.setRest(code, 1);
                this.len = 2;
            }
            else if (code < 65536) {
                this.bytes[0].value = (0xE0 | code >> 12);
                this.bytes[0].bits = 4;
                this.setRest(code, 2);
                this.len = 3;
            }
            else {
                this.bytes[0].value = (0xF0 | code >> 18);
                this.bytes[0].bits = 3;
                this.setRest(code, 3);
                this.len = 4;
            }
        }
        
        private void setRest(int code, final int numBytes) {
            for (int i = 0; i < numBytes; ++i) {
                this.bytes[numBytes - i].value = (0x80 | (code & UTF32ToUTF8.MASKS[5]));
                this.bytes[numBytes - i].bits = 6;
                code >>= 6;
            }
        }
        
        @Override
        public String toString() {
            final StringBuilder b = new StringBuilder();
            for (int i = 0; i < this.len; ++i) {
                if (i > 0) {
                    b.append(' ');
                }
                b.append(Integer.toBinaryString(this.bytes[i].value));
            }
            return b.toString();
        }
    }
}
