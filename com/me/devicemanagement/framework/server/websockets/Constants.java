package com.me.devicemanagement.framework.server.websockets;

import java.io.File;

public final class Constants
{
    private Constants() {
        throw new AssertionError();
    }
    
    public enum ClientSocketType
    {
        TCP, 
        WEBSOCKET;
    }
    
    enum ClientConnentionMode
    {
        BLOCKING_READ, 
        ASYNC_READ;
    }
    
    public enum ClientStatus
    {
        CONNECTED, 
        VERIFYING, 
        READY, 
        ACTIVE, 
        INACTIVE, 
        DISCONNECTED;
    }
    
    static class RequestParams
    {
        static final String TOOL_ID = "toolId";
        static final String CONNECTION_MODE = "connMode";
        static final String SESSION_ID = "sessionId";
        static final String CLIENT_ID = "clientId";
        static final String CLIENT_TYPE = "clientType";
        static final String CLIENT_NAME = "clientName";
    }
    
    static class ConfConstants
    {
        static final String HANDLER_MAPPER_FILE_LIST = "wshandlermapper-files.conf";
        static final String WS_CONF_FILE_NAME = "wssettings.conf";
        static final String HANDLER_MAPPER_FILE_LIST_PATH;
        static final String WS_CONF_FILE_PATH;
        static final String HANDLER_MAPPER = "HandlerMapper";
        static final String WS_SETTINGS = "wssettings";
        static final String WS_SETTINGS_MAPPER_FILEPATH = "wssettings_mapper_filepath";
        static final String MAX_BINARY_BUFFER_SIZE = "MaxBinaryBufferSize";
        static final String MAX_TEXT_BUFFER_SIZE = "MaxTextBufferSize";
        static final String ALLOWED_CIPHERS = "AllowedCiphers";
        static final String ALLOWED_PROTOCOLS = "AllowedProtocols";
        static final String TICKET_EXPIRY_TIME = "TicketExpiryTime";
        static final String PARAM_NAME = "ParamName";
        static final String AUTH_TYPE = "AuthType";
        
        static {
            HANDLER_MAPPER_FILE_LIST_PATH = System.getProperty("server.home") + File.separator + "conf" + File.separator + "wshandlermapper-files.conf";
            WS_CONF_FILE_PATH = System.getProperty("server.home") + File.separator + "conf" + File.separator + "wssettings.conf";
        }
    }
    
    static class DefaultSettings
    {
        static final long DEFAULT_IDLE_TIMEOUT = 18000L;
    }
    
    static class TcpConnectionConstants
    {
        static final int MAX_REQUEST_PARAM_LENGTH = 100;
        static final String CMD_CONN_DETAILS = "CONN_DETAILS";
    }
}
