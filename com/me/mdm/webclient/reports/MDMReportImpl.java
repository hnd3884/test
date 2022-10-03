package com.me.mdm.webclient.reports;

import java.util.logging.Level;
import com.adventnet.sym.server.mdm.util.MDMiOSEntrollmentUtil;
import com.me.devicemanagement.framework.webclient.schedulereport.PublishedReportHandler;
import java.io.File;
import java.util.Collection;
import java.util.ArrayList;
import java.util.Arrays;
import com.me.devicemanagement.framework.server.license.FreeEditionHandler;
import java.util.logging.Logger;
import com.me.devicemanagement.framework.webclient.reports.ReportsProductInvoker;

public class MDMReportImpl extends ReportsProductInvoker
{
    private static Logger logger;
    
    public boolean isValidViewForThisEdition(final Integer viewID) {
        final boolean isFreeEdition = FreeEditionHandler.getInstance().isFreeEdition();
        final ArrayList<Integer> FREE_EDN_REPORTS = new ArrayList<Integer>(Arrays.asList(new Integer(40101), new Integer(40102), new Integer(40103), new Integer(40104), new Integer(40105), new Integer(40201), new Integer(40301), new Integer(40302), new Integer(40303), new Integer(40401), new Integer(40402), new Integer(40403), new Integer(40304), new Integer(40404), new Integer(40305), new Integer(40306), new Integer(40106), new Integer(40503), new Integer(40501), new Integer(40502), new Integer(40107)));
        return !isFreeEdition || FREE_EDN_REPORTS.contains(viewID);
    }
    
    public String getDownloadUrlForFile(final Object object) {
        final File file = new File(object.toString());
        final String filePath = file.getPath();
        final int lastIndex = filePath.lastIndexOf("webapps");
        final String relativeFilePath = filePath.substring(lastIndex, filePath.length());
        String downloadURL = "";
        try {
            final String reportID;
            if ((reportID = PublishedReportHandler.addReportRelatedDetails(relativeFilePath, true)) != null) {
                downloadURL = MDMiOSEntrollmentUtil.getInstance().getServerBaseURL() + "/webclient#/uems/mdm/exportFile?scheduleReportID=" + reportID;
                return downloadURL;
            }
        }
        catch (final Exception e) {
            MDMReportImpl.logger.log(Level.WARNING, "Exception occured while fetching DO for PublishedReportDetails{0}", e);
        }
        return downloadURL;
    }
    
    static {
        MDMReportImpl.logger = Logger.getLogger("ScheduleReportLogger");
    }
}
