package com.me.ems.framework.reports.core;

import java.io.FileOutputStream;
import com.adventnet.client.view.web.HttpReqWrapper;

public interface ReportGenerationUtil
{
    int generateCsvReport(final String p0, final HttpReqWrapper p1, final FileOutputStream p2, final Boolean p3);
    
    void generatePdfReport(final String p0, final HttpReqWrapper p1, final FileOutputStream p2, final Long p3);
    
    int generateXslReport(final String p0, final HttpReqWrapper p1, final FileOutputStream p2, final Boolean p3);
}
