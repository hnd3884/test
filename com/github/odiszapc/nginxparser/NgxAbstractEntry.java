package com.github.odiszapc.nginxparser;

import java.util.List;
import java.util.Iterator;
import java.util.ArrayList;
import java.util.Collection;

public abstract class NgxAbstractEntry implements NgxEntry
{
    private Collection<NgxToken> tokens;
    
    public NgxAbstractEntry(final String... array) {
        this.tokens = new ArrayList<NgxToken>();
        for (int length = array.length, i = 0; i < length; ++i) {
            this.tokens.add(new NgxToken(array[i]));
        }
    }
    
    public Collection<NgxToken> getTokens() {
        return this.tokens;
    }
    
    public void addValue(final NgxToken ngxToken) {
        this.tokens.add(ngxToken);
    }
    
    public void addValue(final String s) {
        this.addValue(new NgxToken(s));
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        final Iterator<NgxToken> iterator = this.tokens.iterator();
        while (iterator.hasNext()) {
            sb.append(iterator.next()).append(" ");
        }
        final String string = sb.toString();
        return string.substring(0, string.length() - 1);
    }
    
    public String getName() {
        if (this.getTokens().isEmpty()) {
            return null;
        }
        return this.getTokens().iterator().next().toString();
    }
    
    public List<String> getValues() {
        final ArrayList list = new ArrayList();
        if (this.getTokens().size() < 2) {
            return list;
        }
        final Iterator<NgxToken> iterator = this.getTokens().iterator();
        iterator.next();
        while (iterator.hasNext()) {
            list.add(iterator.next().toString());
        }
        return list;
    }
    
    public String getValue() {
        final Iterator<String> iterator = this.getValues().iterator();
        final StringBuilder sb = new StringBuilder();
        while (iterator.hasNext()) {
            sb.append(iterator.next());
            if (iterator.hasNext()) {
                sb.append(' ');
            }
        }
        return sb.toString();
    }
}
