package com.adventnet.client.components.table.web;

import java.util.Iterator;
import java.util.List;
import java.util.Collections;
import java.util.Arrays;

public class DefaultExportRedactHandler implements ExportRedactHandler
{
    @Override
    public String mask(final String data) {
        final int length = data.length();
        return new String(new char[length]).replace("\u0000", "*");
    }
    
    @Override
    public String mask(final String data, final String type) {
        return this.mask(data);
    }
    
    @Override
    public String shuffle(final String data) {
        final List<String> valueAsList = Arrays.asList(data.split(""));
        Collections.shuffle(valueAsList);
        final StringBuilder shuffledData = new StringBuilder();
        final Iterator<String> dataIterator = valueAsList.iterator();
        while (dataIterator.hasNext()) {
            shuffledData.append(dataIterator.next());
        }
        return shuffledData.toString();
    }
    
    @Override
    public String prefixMask(String data, final int offset) {
        if (data.length() > offset) {
            data = data.replaceAll("\\b(\\w{" + offset + "})+(\\w{" + offset + "})", new String(new char[data.length() - offset]).replace("\u0000", "*") + "$2");
            return data;
        }
        return this.mask(data);
    }
    
    @Override
    public String suffixMask(final String data, final int offset) {
        if (data.length() > offset) {
            final StringBuilder sb = new StringBuilder(data.substring(0, data.length() - offset));
            sb.append(new String(new char[offset]).replace("\u0000", "*"));
            return sb.toString();
        }
        return this.mask(data);
    }
    
    @Override
    public String phone(final String data) {
        return data.replaceAll("\\b(\\d{2})\\d+(\\d{2})", "$1*******$2");
    }
    
    @Override
    public String email(final String data) {
        return data.replaceAll("\\b(\\w{2})[^@]+@(\\w{2})\\S+(\\.[^\\s.]+)", "$1***@$2****$3");
    }
}
