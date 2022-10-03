package org.apache.coyote;

import java.nio.charset.StandardCharsets;
import java.nio.charset.Charset;

public final class Constants
{
    @Deprecated
    public static final String DEFAULT_CHARACTER_ENCODING = "ISO-8859-1";
    public static final Charset DEFAULT_URI_CHARSET;
    public static final Charset DEFAULT_BODY_CHARSET;
    public static final int MAX_NOTES = 32;
    public static final int STAGE_NEW = 0;
    public static final int STAGE_PARSE = 1;
    public static final int STAGE_PREPARE = 2;
    public static final int STAGE_SERVICE = 3;
    public static final int STAGE_ENDINPUT = 4;
    public static final int STAGE_ENDOUTPUT = 5;
    public static final int STAGE_KEEPALIVE = 6;
    public static final int STAGE_ENDED = 7;
    public static final int DEFAULT_CONNECTION_LINGER = -1;
    public static final boolean DEFAULT_TCP_NO_DELAY = true;
    public static final boolean IS_SECURITY_ENABLED;
    @Deprecated
    public static final boolean USE_CUSTOM_STATUS_MSG_IN_HEADER;
    public static final String SENDFILE_SUPPORTED_ATTR = "org.apache.tomcat.sendfile.support";
    public static final String SENDFILE_FILENAME_ATTR = "org.apache.tomcat.sendfile.filename";
    public static final String SENDFILE_FILE_START_ATTR = "org.apache.tomcat.sendfile.start";
    public static final String SENDFILE_FILE_END_ATTR = "org.apache.tomcat.sendfile.end";
    public static final String REMOTE_ADDR_ATTRIBUTE = "org.apache.tomcat.remoteAddr";
    public static final String PEER_ADDR_ATTRIBUTE = "org.apache.tomcat.peerAddr";
    
    static {
        DEFAULT_URI_CHARSET = StandardCharsets.ISO_8859_1;
        DEFAULT_BODY_CHARSET = StandardCharsets.ISO_8859_1;
        IS_SECURITY_ENABLED = (System.getSecurityManager() != null);
        USE_CUSTOM_STATUS_MSG_IN_HEADER = Boolean.getBoolean("org.apache.coyote.USE_CUSTOM_STATUS_MSG_IN_HEADER");
    }
}
