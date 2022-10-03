package com.adventnet.sym.server.mdm.certificates.csr;

import java.util.logging.Level;
import com.adventnet.sym.server.mdm.certificates.integrations.certificateauthority.ThirdPartyCAUtil;
import org.json.JSONObject;
import java.util.logging.Logger;

public class DigicertCsrHandler
{
    private static Logger logger;
    private static final String CSR_FILE_NAME = "DigicertCsr.csr";
    private static final String PRIVATE_KEY_FILE_NAME = "DigicertCsrPrivate.key";
    public static final int VERSION = 1;
    
    public static Long createCSR(final JSONObject json) throws Exception {
        json.put("CSR_PURPOSE", 1);
        json.put("CSR_FILE_NAME", (Object)"DigicertCsr.csr");
        json.put("PRIVATE_KEY_FILE_NAME", (Object)"DigicertCsrPrivate.key");
        final String csrDirectoryPath = ThirdPartyCAUtil.getDigicertFolderPath(json.getLong("CUSTOMER_ID"), 1);
        json.put("CSR_DIRECTORY", (Object)csrDirectoryPath);
        DigicertCsrHandler.logger.log(Level.FINE, "Going to save the CSR in {0}", csrDirectoryPath);
        DigicertCsrHandler.logger.log(Level.FINE, "Creating CSR for the following data: {0}", json.toString());
        final Long csrId = MdmCsrHandler.createCSR(json);
        DigicertCsrHandler.logger.log(Level.FINE, "CSR created successfully.");
        return csrId;
    }
    
    static {
        DigicertCsrHandler.logger = Logger.getLogger("MdmCertificateIntegLogger");
    }
}
