package com.adventnet.sym.server.mdm.inv;

import org.bouncycastle.asn1.x500.style.BCStyle;

public class CertificateConstants
{
    public static final String IS_IDENTITY = "IsIdentity";
    public static final String COMMON_NAME = "CommonName";
    public static final String CERTIFICATE_SUBJECT_DN = "CERTIFICATE_SUBJECT_DN";
    public static final String IS_SCEP_CERTIFICATE = "IsScepCertificate";
    public static final String SCEP_ID = "SCEP_ID";
    public static final String SUBJECT_SERIAL_NUMBER;
    public static final String SUBJECT_UNIQUE_IDENTIFIER;
    public static final String DATA = "Data";
    public static final String IS_EXTERNAL_SCEP_SERVER = "IS_EXTERNAL_SCEP_SERVER";
    public static final int ROOT_CA_VALID_YEARS = 50;
    
    static {
        SUBJECT_SERIAL_NUMBER = BCStyle.SN.getId();
        SUBJECT_UNIQUE_IDENTIFIER = BCStyle.UNIQUE_IDENTIFIER.getId();
    }
    
    public static final class Cryptography
    {
        public static final class Keystore
        {
            public static final String PKCS12 = "PKCS12";
            public static final String JKS = "JKS";
        }
        
        public static final class Provider
        {
            public static final String BC = "BC";
        }
    }
    
    public static final class Alias
    {
        public static String raCertificateAlias;
        
        static {
            Alias.raCertificateAlias = "raCertificate";
        }
    }
    
    public static final class General
    {
        public static final String CERTIFICATE_ID = "CERTIFICATE_ID";
    }
    
    public static final class KestoreAndTruststore
    {
        public static final String KEYSTORE_LOCATION = "KEYSTORE_LOCATION";
        public static final String KEYSTORE_PASSWORD = "KEYSTORE_PASSWORD";
        public static final String TRUSTSTORE_LOCATION = "TRUSTSTORE_LOCATION";
        public static final String TRUSTSTORE_PASSWORD = "TRUSTSTORE_PASSWORD";
        public static String keystoreFileName;
        public static String trustStoreFileName;
        public static String trustStore_password;
        
        static {
            KestoreAndTruststore.keystoreFileName = "raCert.pfx";
            KestoreAndTruststore.trustStoreFileName = "truststore.jks";
            KestoreAndTruststore.trustStore_password = "changeit";
        }
    }
    
    public static final class OID
    {
        public static final String EMAIL = "1.2.840.113549.1.9.1";
        public static final String DC = "0.9.2342.19200300.100.1.25";
    }
}
