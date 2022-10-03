package com.adventnet.sym.server.mdm.certificates;

import java.io.File;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;

public class MdmCertAuthUtil
{
    public static String getCertAuthParentFolderPath() {
        final String certAuthParentFolder = ApiFactoryProvider.getUtilAccessAPI().getServerHome() + File.separator + "mdm" + File.separator + "certauth";
        return certAuthParentFolder;
    }
    
    public static class Scep
    {
        public static String getScepRootCAFolderPath(final Long customerID) {
            final String rootCAParentFolder = MdmCertAuthUtil.getCertAuthParentFolderPath() + File.separator + customerID + File.separator + "scep";
            return rootCAParentFolder;
        }
        
        public static String getScepRootCACertificatePath(final Long customerID) {
            final String scepRootCACertificatePath = getScepRootCAFolderPath(customerID) + File.separator + "MdmRootCA.crt";
            return scepRootCACertificatePath;
        }
        
        public static String getScepRootCAPrivateKeyPath(final Long customerID) {
            final String scepRootCACertificatePath = getScepRootCAFolderPath(customerID) + File.separator + "MdmRootCA.key";
            return scepRootCACertificatePath;
        }
    }
}
