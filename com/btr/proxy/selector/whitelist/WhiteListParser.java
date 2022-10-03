package com.btr.proxy.selector.whitelist;

import com.btr.proxy.util.UriFilter;
import java.util.List;

public interface WhiteListParser
{
    List<UriFilter> parseWhiteList(final String p0);
}
