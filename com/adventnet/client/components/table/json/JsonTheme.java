package com.adventnet.client.components.table.json;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletRequest;

public interface JsonTheme
{
    void generateJSON(final String p0, final HttpServletRequest p1, final HttpServletResponse p2) throws Exception;
}
