package com.adventnet.sym.webclient.mdm.reports.schedulereport;

import java.io.FileOutputStream;
import com.adventnet.client.view.web.HttpReqWrapper;
import com.me.ems.framework.reports.core.ReportGenerationUtil;

public class LocationReportUtil implements ReportGenerationUtil
{
    public int generateCsvReport(final String viewName, final HttpReqWrapper requestWrapper, final FileOutputStream fileOutputStream, final Boolean isEmptyReportNeeded) {
        return 0;
    }
    
    public void generatePdfReport(final String viewName, final HttpReqWrapper reqWrapper, final FileOutputStream os, final Long customerId) {
        new LocationReportPdfUtil().generateDeviceLocationPDFReport(viewName, reqWrapper, os, customerId);
    }
    
    public int generateXslReport(final String viewName, final HttpReqWrapper requestWrapper, final FileOutputStream fileOutputStream, final Boolean isEmptyReportNeeded) {
        return 0;
    }
}
