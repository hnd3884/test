package com.me.devicemanagement.framework.server.adaccess;

import com.me.devicemanagement.framework.server.domain.DomainConstants;

public class ADConstants extends DomainConstants
{
    public static final String GUID = "objectGUID";
    public static final String NAME = "name";
    public static final String COMPUTER_DNS_NAME = "dNSHostName";
    public static final int SEARCH_PREFIX = 200;
    public static final int SEARCH_CONTAINS = 300;
    public static final int SEARCH_SUFFIX = 100;
    public static final String DN = "distinguishedName";
    public static final String DOMAIN = "domainName";
    public static final int ONE_LEVEL = 1;
    public static final int NORMAL_LEVEL = 2;
    public static final int ALL_LEVEL = 3;
    public static final String IS_DOMAIN_EXISTING_IN_MANAGEDDOMAIN = "IS_DOMAIN_EXISTING_IN_MANAGEDDOMAIN";
    public static final String EXISTING_DOMAIN_CLIENT_ID = "EXISTING_DOMAIN_CLIENT_ID";
    public static final int INVALID_USNM_PWD = 60010;
    public static final int INVALID_DOMAIN_CONTROLLER = 60011;
    public static final int INVALID_AD_DOMIAN = 60012;
    public static final int LDAP_SSL_FAILURE = 60013;
    public static final String MOBILE = "mobile";
}
