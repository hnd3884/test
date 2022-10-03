package com.adventnet.sym.server.mdm.certificates.csr;

public class CsrConstants
{
    public static final class General
    {
        public static final String CSR_PURPOSE = "CSR_PURPOSE";
        public static final String CSR = "CSR";
        public static final String PRIVATE_KEY = "PRIVATE_KEY";
        public static final String ENCODED_CSR = "ENCODED_CSR";
    }
    
    public static final class Directory
    {
        public static final String CSR_DIRECTORY = "CSR_DIRECTORY";
        public static final String CSR_FILE_NAME = "CSR_FILE_NAME";
        public static final String PRIVATE_KEY_FILE_NAME = "PRIVATE_KEY_FILE_NAME";
    }
    
    public static final class Api
    {
        public static final class Purpose
        {
            public static final int DIGICERT = 1;
        }
        
        public static final class Key
        {
            public static String csrrequests;
            public static String csrrequest_id;
            public static String csr_id;
            public static String common_name;
            public static String org_email;
            public static String org_name;
            public static String org_unit_name;
            public static String locality;
            public static String street;
            public static String country;
            
            static {
                Key.csrrequests = "csrrequests";
                Key.csrrequest_id = "csrrequest_id";
                Key.csr_id = "cs_id";
                Key.common_name = "common_name";
                Key.org_email = "org_email";
                Key.org_name = "org_name";
                Key.org_unit_name = "org_unit_name";
                Key.locality = "locality";
                Key.street = "street";
                Key.country = "country";
            }
        }
    }
    
    public static final class Attributes
    {
        public static final String COMMON_NAME = "COMMON_NAME";
        public static final String EMAIL_ADDRESS = "EMAIL_ADDRESS";
        public static final String ORGANIZATION_NAME = "ORGANIZATION_NAME";
        public static final String ORGANIZATIONAL_UNIT = "ORGANIZATIONAL_UNIT";
        public static final String LOCALITY = "LOCALITY";
        public static final String STREET = "STREET";
        public static final String COUNTRY = "COUNTRY";
    }
}
