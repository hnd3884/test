package com.btr.proxy.selector.whitelist;

import java.util.ArrayList;
import com.btr.proxy.util.UriFilter;
import java.util.List;

public class DefaultWhiteListParser implements WhiteListParser
{
    public List<UriFilter> parseWhiteList(final String whiteList) {
        final List<UriFilter> result = new ArrayList<UriFilter>();
        final String[] token = whiteList.split("[, ]+");
        for (int i = 0; i < token.length; ++i) {
            token[i] = token[i].trim();
            if (token[i].contains("/")) {
                result.add(new IpRangeFilter(token[i]));
            }
            else if (token[i].endsWith("*")) {
                token[i] = token[i].substring(0, token[i].length() - 1);
                result.add(new HostnameFilter(HostnameFilter.Mode.BEGINS_WITH, token[i]));
            }
            else if (token[i].trim().startsWith("*")) {
                token[i] = token[i].substring(1);
                result.add(new HostnameFilter(HostnameFilter.Mode.ENDS_WITH, token[i]));
            }
            else {
                result.add(new HostnameFilter(HostnameFilter.Mode.ENDS_WITH, token[i]));
            }
        }
        return result;
    }
}
