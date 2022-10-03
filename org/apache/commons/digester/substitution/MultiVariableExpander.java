package org.apache.commons.digester.substitution;

import java.util.Map;
import java.util.ArrayList;

public class MultiVariableExpander implements VariableExpander
{
    private int nEntries;
    private ArrayList markers;
    private ArrayList sources;
    
    public MultiVariableExpander() {
        this.nEntries = 0;
        this.markers = new ArrayList(2);
        this.sources = new ArrayList(2);
    }
    
    public void addSource(final String marker, final Map source) {
        ++this.nEntries;
        this.markers.add(marker);
        this.sources.add(source);
    }
    
    public String expand(String param) {
        for (int i = 0; i < this.nEntries; ++i) {
            param = this.expand(param, this.markers.get(i), this.sources.get(i));
        }
        return param;
    }
    
    public String expand(String str, final String marker, final Map source) {
        final String startMark = marker + "{";
        final int markLen = startMark.length();
        int index = 0;
        while (true) {
            index = str.indexOf(startMark, index);
            if (index == -1) {
                return str;
            }
            final int startIndex = index + markLen;
            if (startIndex > str.length()) {
                throw new IllegalArgumentException("var expression starts at end of string");
            }
            final int endIndex = str.indexOf("}", index + markLen);
            if (endIndex == -1) {
                throw new IllegalArgumentException("var expression starts but does not end");
            }
            final String key = str.substring(index + markLen, endIndex);
            final Object value = source.get(key);
            if (value == null) {
                throw new IllegalArgumentException("parameter [" + key + "] is not defined.");
            }
            final String varValue = value.toString();
            str = str.substring(0, index) + varValue + str.substring(endIndex + 1);
            index += varValue.length();
        }
    }
}
