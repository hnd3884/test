package com.me.devicemanagement.onpremise.tools.backuprestore.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.ArrayList;

public class StringUtil
{
    private static StringUtil objStringUtil;
    
    public static StringUtil getInstance() {
        if (StringUtil.objStringUtil == null) {
            StringUtil.objStringUtil = new StringUtil();
        }
        return StringUtil.objStringUtil;
    }
    
    public ArrayList<String> splitToArrayList(final CharSequence input, final String regex) {
        return this.splitToArrayList(input, regex, 0);
    }
    
    public ArrayList<String> splitToArrayList(final CharSequence input, final String regex, final int limit) {
        int index = 0;
        final boolean matchLimited = limit > 0;
        final ArrayList<String> matchList = new ArrayList<String>();
        final Pattern pattern = Pattern.compile(regex);
        final Matcher m = pattern.matcher(input);
        while (m.find()) {
            if (!matchLimited || matchList.size() < limit - 1) {
                final String match = input.subSequence(index, m.start()).toString();
                matchList.add(match);
                index = m.end();
            }
            else {
                if (matchList.size() != limit - 1) {
                    continue;
                }
                final String match = input.subSequence(index, input.length()).toString();
                matchList.add(match);
                index = m.end();
            }
        }
        if (index == 0) {
            matchList.add(input.toString());
            return matchList;
        }
        if (!matchLimited || matchList.size() < limit) {
            matchList.add(input.subSequence(index, input.length()).toString());
        }
        int resultSize = matchList.size();
        if (limit == 0) {
            while (resultSize > 0 && matchList.get(resultSize - 1).equals("")) {
                --resultSize;
            }
        }
        return (ArrayList)matchList.subList(0, resultSize);
    }
    
    static {
        StringUtil.objStringUtil = null;
    }
}
