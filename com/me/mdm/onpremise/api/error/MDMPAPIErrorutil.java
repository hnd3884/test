package com.me.mdm.onpremise.api.error;

import com.me.mdm.api.error.APIErrorUtil;
import java.io.File;

public class MDMPAPIErrorutil
{
    public static void initializeErrorConstants() {
        final File errorConstantsXMLFile = new File(System.getProperty("server.home") + File.separator + "conf" + File.separator + "MDMP" + File.separator + "api" + File.separator + "mdmp-api-error-constants.xml");
        APIErrorUtil.getInstance().parseAndAddErrorsFromXML(errorConstantsXMLFile);
    }
}
