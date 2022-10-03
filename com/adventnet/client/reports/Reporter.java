package com.adventnet.client.reports;

import java.io.FileOutputStream;
import javax.servlet.http.HttpServletRequest;

public interface Reporter
{
    void generateReport(final String p0, final HttpServletRequest p1, final FileOutputStream p2) throws Exception;
}
