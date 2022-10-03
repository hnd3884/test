package com.me.devicemanagement.framework.server.httpclient;

import java.io.File;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;

public class DMHttpConstants
{
    public static final String GET_METHOD = "GET";
    public static final String POST_METHOD = "POST";
    public static final String PUT_METHOD = "PUT";
    public static final String DELETE_METHOD = "DELETE";
    public static final String ACCEPT_ENCODING = "Accept-Encoding";
    public static final String CONTENT_ENCODING = "Content-Encoding";
    public static final String CONTENT_LENGTH = "Content-Length";
    public static final String CONTENT_TYPE = "Content-Type";
    public static final String DEFAULT_CHARSET = "ISO-8859-1";
    public static final String AUTHORIZATION = "Authorization";
    public static final String ERROR_CODE = "ERROR_CODE";
    public static final int NETWORK_ERROR = 80007;
    public static final String SERVER_KEYSTORE_PATH;
    public static final String SELF_HTTPS_PROTOCOL_PREFIX = "self";
    
    static {
        SERVER_KEYSTORE_PATH = ApiFactoryProvider.getUtilAccessAPI().getServerHome() + File.separator + "conf" + File.separator + "server.keystore";
    }
}
