package org.apache.commons.net.nntp;

import java.util.Calendar;

public final class NewGroupsOrNewsQuery
{
    private final String __date;
    private final String __time;
    private StringBuffer __distributions;
    private StringBuffer __newsgroups;
    private final boolean __isGMT;
    
    public NewGroupsOrNewsQuery(final Calendar date, final boolean gmt) {
        this.__distributions = null;
        this.__newsgroups = null;
        this.__isGMT = gmt;
        final StringBuilder buffer = new StringBuilder();
        int num = date.get(1);
        String str = Integer.toString(num);
        num = str.length();
        if (num >= 2) {
            buffer.append(str.substring(num - 2));
        }
        else {
            buffer.append("00");
        }
        num = date.get(2) + 1;
        str = Integer.toString(num);
        num = str.length();
        if (num == 1) {
            buffer.append('0');
            buffer.append(str);
        }
        else if (num == 2) {
            buffer.append(str);
        }
        else {
            buffer.append("01");
        }
        num = date.get(5);
        str = Integer.toString(num);
        num = str.length();
        if (num == 1) {
            buffer.append('0');
            buffer.append(str);
        }
        else if (num == 2) {
            buffer.append(str);
        }
        else {
            buffer.append("01");
        }
        this.__date = buffer.toString();
        buffer.setLength(0);
        num = date.get(11);
        str = Integer.toString(num);
        num = str.length();
        if (num == 1) {
            buffer.append('0');
            buffer.append(str);
        }
        else if (num == 2) {
            buffer.append(str);
        }
        else {
            buffer.append("00");
        }
        num = date.get(12);
        str = Integer.toString(num);
        num = str.length();
        if (num == 1) {
            buffer.append('0');
            buffer.append(str);
        }
        else if (num == 2) {
            buffer.append(str);
        }
        else {
            buffer.append("00");
        }
        num = date.get(13);
        str = Integer.toString(num);
        num = str.length();
        if (num == 1) {
            buffer.append('0');
            buffer.append(str);
        }
        else if (num == 2) {
            buffer.append(str);
        }
        else {
            buffer.append("00");
        }
        this.__time = buffer.toString();
    }
    
    public void addNewsgroup(final String newsgroup) {
        if (this.__newsgroups != null) {
            this.__newsgroups.append(',');
        }
        else {
            this.__newsgroups = new StringBuffer();
        }
        this.__newsgroups.append(newsgroup);
    }
    
    public void omitNewsgroup(final String newsgroup) {
        this.addNewsgroup("!" + newsgroup);
    }
    
    public void addDistribution(final String distribution) {
        if (this.__distributions != null) {
            this.__distributions.append(',');
        }
        else {
            this.__distributions = new StringBuffer();
        }
        this.__distributions.append(distribution);
    }
    
    public String getDate() {
        return this.__date;
    }
    
    public String getTime() {
        return this.__time;
    }
    
    public boolean isGMT() {
        return this.__isGMT;
    }
    
    public String getDistributions() {
        return (this.__distributions == null) ? null : this.__distributions.toString();
    }
    
    public String getNewsgroups() {
        return (this.__newsgroups == null) ? null : this.__newsgroups.toString();
    }
}
