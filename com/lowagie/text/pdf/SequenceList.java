package com.lowagie.text.pdf;

import java.util.ListIterator;
import java.util.LinkedList;
import java.util.List;

public class SequenceList
{
    protected static final int COMMA = 1;
    protected static final int MINUS = 2;
    protected static final int NOT = 3;
    protected static final int TEXT = 4;
    protected static final int NUMBER = 5;
    protected static final int END = 6;
    protected static final char EOT = '\uffff';
    private static final int FIRST = 0;
    private static final int DIGIT = 1;
    private static final int OTHER = 2;
    private static final int DIGIT2 = 3;
    private static final String NOT_OTHER = "-,!0123456789";
    protected char[] text;
    protected int ptr;
    protected int number;
    protected String other;
    protected int low;
    protected int high;
    protected boolean odd;
    protected boolean even;
    protected boolean inverse;
    
    protected SequenceList(final String range) {
        this.ptr = 0;
        this.text = range.toCharArray();
    }
    
    protected char nextChar() {
        while (this.ptr < this.text.length) {
            final char c = this.text[this.ptr++];
            if (c > ' ') {
                return c;
            }
        }
        return '\uffff';
    }
    
    protected void putBack() {
        --this.ptr;
        if (this.ptr < 0) {
            this.ptr = 0;
        }
    }
    
    protected int getType() {
        final StringBuffer buf = new StringBuffer();
        int state = 0;
        while (true) {
            final char c = this.nextChar();
            if (c == '\uffff') {
                if (state == 1) {
                    final String string = buf.toString();
                    this.other = string;
                    this.number = Integer.parseInt(string);
                    return 5;
                }
                if (state == 2) {
                    this.other = buf.toString().toLowerCase();
                    return 4;
                }
                return 6;
            }
            else {
                switch (state) {
                    case 0: {
                        switch (c) {
                            case '!': {
                                return 3;
                            }
                            case '-': {
                                return 2;
                            }
                            case ',': {
                                return 1;
                            }
                            default: {
                                buf.append(c);
                                if (c >= '0' && c <= '9') {
                                    state = 1;
                                    continue;
                                }
                                state = 2;
                                continue;
                            }
                        }
                        break;
                    }
                    case 1: {
                        if (c >= '0' && c <= '9') {
                            buf.append(c);
                            continue;
                        }
                        this.putBack();
                        final String string2 = buf.toString();
                        this.other = string2;
                        this.number = Integer.parseInt(string2);
                        return 5;
                    }
                    case 2: {
                        if ("-,!0123456789".indexOf(c) < 0) {
                            buf.append(c);
                            continue;
                        }
                        this.putBack();
                        this.other = buf.toString().toLowerCase();
                        return 4;
                    }
                }
            }
        }
    }
    
    private void otherProc() {
        if (this.other.equals("odd") || this.other.equals("o")) {
            this.odd = true;
            this.even = false;
        }
        else if (this.other.equals("even") || this.other.equals("e")) {
            this.odd = false;
            this.even = true;
        }
    }
    
    protected boolean getAttributes() {
        this.low = -1;
        this.high = -1;
        final boolean odd = false;
        this.inverse = odd;
        this.even = odd;
        this.odd = odd;
        int state = 2;
        int type;
        while (true) {
            type = this.getType();
            if (type == 6 || type == 1) {
                break;
            }
            switch (state) {
                case 2: {
                    switch (type) {
                        case 3: {
                            this.inverse = true;
                            continue;
                        }
                        case 2: {
                            state = 3;
                            continue;
                        }
                        default: {
                            if (type == 5) {
                                this.low = this.number;
                                state = 1;
                                continue;
                            }
                            this.otherProc();
                            continue;
                        }
                    }
                    break;
                }
                case 1: {
                    switch (type) {
                        case 3: {
                            this.inverse = true;
                            state = 2;
                            this.high = this.low;
                            continue;
                        }
                        case 2: {
                            state = 3;
                            continue;
                        }
                        default: {
                            this.high = this.low;
                            state = 2;
                            this.otherProc();
                            continue;
                        }
                    }
                    break;
                }
                case 3: {
                    switch (type) {
                        case 3: {
                            this.inverse = true;
                            state = 2;
                            continue;
                        }
                        case 2: {
                            continue;
                        }
                        case 5: {
                            this.high = this.number;
                            state = 2;
                            continue;
                        }
                        default: {
                            state = 2;
                            this.otherProc();
                            continue;
                        }
                    }
                    break;
                }
            }
        }
        if (state == 1) {
            this.high = this.low;
        }
        return type == 6;
    }
    
    public static List expand(final String ranges, final int maxNumber) {
        final SequenceList parse = new SequenceList(ranges);
        final LinkedList list = new LinkedList();
        boolean sair = false;
        while (!sair) {
            sair = parse.getAttributes();
            if (parse.low == -1 && parse.high == -1 && !parse.even && !parse.odd) {
                continue;
            }
            if (parse.low < 1) {
                parse.low = 1;
            }
            if (parse.high < 1 || parse.high > maxNumber) {
                parse.high = maxNumber;
            }
            if (parse.low > maxNumber) {
                parse.low = maxNumber;
            }
            int inc = 1;
            if (parse.inverse) {
                if (parse.low > parse.high) {
                    final int t = parse.low;
                    parse.low = parse.high;
                    parse.high = t;
                }
                final ListIterator it = list.listIterator();
                while (it.hasNext()) {
                    final int n = it.next();
                    if (parse.even && (n & 0x1) == 0x1) {
                        continue;
                    }
                    if (parse.odd && (n & 0x1) == 0x0) {
                        continue;
                    }
                    if (n < parse.low || n > parse.high) {
                        continue;
                    }
                    it.remove();
                }
            }
            else if (parse.low > parse.high) {
                inc = -1;
                if (parse.odd || parse.even) {
                    --inc;
                    if (parse.even) {
                        final SequenceList list2 = parse;
                        list2.low &= 0xFFFFFFFE;
                    }
                    else {
                        final SequenceList list3 = parse;
                        list3.low -= (((parse.low & 0x1) != 0x1) ? 1 : 0);
                    }
                }
                for (int k = parse.low; k >= parse.high; k += inc) {
                    list.add(new Integer(k));
                }
            }
            else {
                if (parse.odd || parse.even) {
                    ++inc;
                    if (parse.odd) {
                        final SequenceList list4 = parse;
                        list4.low |= 0x1;
                    }
                    else {
                        final SequenceList list5 = parse;
                        list5.low += (((parse.low & 0x1) == 0x1) ? 1 : 0);
                    }
                }
                for (int k = parse.low; k <= parse.high; k += inc) {
                    list.add(new Integer(k));
                }
            }
        }
        return list;
    }
}
