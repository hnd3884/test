package com.adventnet.client.view.xls;

import javax.servlet.http.HttpServletRequest;
import com.adventnet.client.view.common.PostExportHandler;

public interface XLSTheme extends PostExportHandler
{
    void generateXLS(final String p0, final HttpServletRequest p1, final Object p2) throws Exception;
}
