package com.adventnet.client.view.csv;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletRequest;
import com.adventnet.client.view.common.PostExportHandler;

public interface CSVTheme extends PostExportHandler
{
    void generateCSV(final String p0, final HttpServletRequest p1, final HttpServletResponse p2) throws Exception;
}
