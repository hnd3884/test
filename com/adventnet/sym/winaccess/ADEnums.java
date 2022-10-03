package com.adventnet.sym.winaccess;

public class ADEnums
{
    public static final int ADS_UF_SCRIPT = 1;
    public static final int ADS_UF_ACCOUNTDISABLE = 2;
    public static final int ADS_UF_HOMEDIR_REQUIRED = 8;
    public static final int ADS_UF_LOCKOUT = 16;
    public static final int ADS_UF_PASSWD_NOTREQD = 32;
    public static final int ADS_UF_PASSWD_CANT_CHANGE = 64;
    public static final int ADS_UF_ENCRYPTED_TEXT_PASSWORD_ALLOWED = 128;
    public static final int ADS_UF_TEMP_DUPLICATE_ACCOUNT = 256;
    public static final int ADS_UF_NORMAL_ACCOUNT = 512;
    public static final int ADS_UF_INTERDOMAIN_TRUST_ACCOUNT = 2048;
    public static final int ADS_UF_WORKSTATION_TRUST_ACCOUNT = 4096;
    public static final int ADS_UF_SERVER_TRUST_ACCOUNT = 8192;
    public static final int ADS_UF_DONT_EXPIRE_PASSWD = 65536;
    public static final int ADS_UF_MNS_LOGON_ACCOUNT = 131072;
    public static final int ADS_UF_SMARTCARD_REQUIRED = 262144;
    public static final int ADS_UF_TRUSTED_FOR_DELEGATION = 524288;
    public static final int ADS_UF_NOT_DELEGATED = 1048576;
    public static final int ADS_UF_USE_DES_KEY_ONLY = 2097152;
    public static final int ADS_UF_DONT_REQUIRE_PREAUTH = 4194304;
    public static final int ADS_UF_PASSWORD_EXPIRED = 8388608;
    public static final int ADS_UF_TRUSTED_TO_AUTHENTICATE_FOR_DELEGATION = 16777216;
    public static final long DOMAIN_PASSWORD_COMPLEX = 1L;
    public static final long DOMAIN_PASSWORD_NO_ANON_CHANGE = 2L;
    public static final long DOMAIN_PASSWORD_NO_CLEAR_CHANGE = 4L;
    public static final long DOMAIN_LOCKOUT_ADMINS = 8L;
    public static final long DOMAIN_PASSWORD_STORE_CLEARTEXT = 16L;
    public static final long DOMAIN_REFUSE_PASSWORD_CHANGE = 32L;
}
