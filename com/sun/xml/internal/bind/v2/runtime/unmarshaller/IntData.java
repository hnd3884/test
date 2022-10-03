package com.sun.xml.internal.bind.v2.runtime.unmarshaller;

import java.io.IOException;
import com.sun.xml.internal.bind.v2.runtime.output.UTF8XmlOutput;
import com.sun.xml.internal.bind.v2.runtime.output.Pcdata;

public class IntData extends Pcdata
{
    private int data;
    private int length;
    private static final int[] sizeTable;
    
    public void reset(final int i) {
        this.data = i;
        if (i == Integer.MIN_VALUE) {
            this.length = 11;
        }
        else {
            this.length = ((i < 0) ? (stringSizeOfInt(-i) + 1) : stringSizeOfInt(i));
        }
    }
    
    private static int stringSizeOfInt(final int x) {
        int i;
        for (i = 0; x > IntData.sizeTable[i]; ++i) {}
        return i + 1;
    }
    
    @Override
    public String toString() {
        return String.valueOf(this.data);
    }
    
    @Override
    public int length() {
        return this.length;
    }
    
    @Override
    public char charAt(final int index) {
        return this.toString().charAt(index);
    }
    
    @Override
    public CharSequence subSequence(final int start, final int end) {
        return this.toString().substring(start, end);
    }
    
    @Override
    public void writeTo(final UTF8XmlOutput output) throws IOException {
        output.text(this.data);
    }
    
    static {
        sizeTable = new int[] { 9, 99, 999, 9999, 99999, 999999, 9999999, 99999999, 999999999, Integer.MAX_VALUE };
    }
}
