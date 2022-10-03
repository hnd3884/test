package org.apache.catalina.util;

public class TLSUtil
{
    public static boolean isTLSRequestAttribute(final String name) {
        switch (name) {
            case "javax.servlet.request.X509Certificate":
            case "javax.servlet.request.cipher_suite":
            case "javax.servlet.request.key_size":
            case "javax.servlet.request.ssl_session_id":
            case "javax.servlet.request.ssl_session_mgr":
            case "org.apache.tomcat.util.net.secure_protocol_version":
            case "org.apache.tomcat.util.net.secure_requested_protocol_versions":
            case "org.apache.tomcat.util.net.secure_requested_ciphers": {
                return true;
            }
            default: {
                return false;
            }
        }
    }
}
