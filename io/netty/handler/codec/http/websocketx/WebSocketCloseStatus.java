package io.netty.handler.codec.http.websocketx;

import io.netty.util.internal.ObjectUtil;

public final class WebSocketCloseStatus implements Comparable<WebSocketCloseStatus>
{
    public static final WebSocketCloseStatus NORMAL_CLOSURE;
    public static final WebSocketCloseStatus ENDPOINT_UNAVAILABLE;
    public static final WebSocketCloseStatus PROTOCOL_ERROR;
    public static final WebSocketCloseStatus INVALID_MESSAGE_TYPE;
    public static final WebSocketCloseStatus INVALID_PAYLOAD_DATA;
    public static final WebSocketCloseStatus POLICY_VIOLATION;
    public static final WebSocketCloseStatus MESSAGE_TOO_BIG;
    public static final WebSocketCloseStatus MANDATORY_EXTENSION;
    public static final WebSocketCloseStatus INTERNAL_SERVER_ERROR;
    public static final WebSocketCloseStatus SERVICE_RESTART;
    public static final WebSocketCloseStatus TRY_AGAIN_LATER;
    public static final WebSocketCloseStatus BAD_GATEWAY;
    public static final WebSocketCloseStatus EMPTY;
    public static final WebSocketCloseStatus ABNORMAL_CLOSURE;
    public static final WebSocketCloseStatus TLS_HANDSHAKE_FAILED;
    private final int statusCode;
    private final String reasonText;
    private String text;
    
    public WebSocketCloseStatus(final int statusCode, final String reasonText) {
        this(statusCode, reasonText, true);
    }
    
    public WebSocketCloseStatus(final int statusCode, final String reasonText, final boolean validate) {
        if (validate && !isValidStatusCode(statusCode)) {
            throw new IllegalArgumentException("WebSocket close status code does NOT comply with RFC-6455: " + statusCode);
        }
        this.statusCode = statusCode;
        this.reasonText = ObjectUtil.checkNotNull(reasonText, "reasonText");
    }
    
    public int code() {
        return this.statusCode;
    }
    
    public String reasonText() {
        return this.reasonText;
    }
    
    @Override
    public int compareTo(final WebSocketCloseStatus o) {
        return this.code() - o.code();
    }
    
    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (null == o || this.getClass() != o.getClass()) {
            return false;
        }
        final WebSocketCloseStatus that = (WebSocketCloseStatus)o;
        return this.statusCode == that.statusCode;
    }
    
    @Override
    public int hashCode() {
        return this.statusCode;
    }
    
    @Override
    public String toString() {
        String text = this.text;
        if (text == null) {
            text = (this.text = this.code() + " " + this.reasonText());
        }
        return text;
    }
    
    public static boolean isValidStatusCode(final int code) {
        return code < 0 || (1000 <= code && code <= 1003) || (1007 <= code && code <= 1014) || 3000 <= code;
    }
    
    public static WebSocketCloseStatus valueOf(final int code) {
        switch (code) {
            case 1000: {
                return WebSocketCloseStatus.NORMAL_CLOSURE;
            }
            case 1001: {
                return WebSocketCloseStatus.ENDPOINT_UNAVAILABLE;
            }
            case 1002: {
                return WebSocketCloseStatus.PROTOCOL_ERROR;
            }
            case 1003: {
                return WebSocketCloseStatus.INVALID_MESSAGE_TYPE;
            }
            case 1005: {
                return WebSocketCloseStatus.EMPTY;
            }
            case 1006: {
                return WebSocketCloseStatus.ABNORMAL_CLOSURE;
            }
            case 1007: {
                return WebSocketCloseStatus.INVALID_PAYLOAD_DATA;
            }
            case 1008: {
                return WebSocketCloseStatus.POLICY_VIOLATION;
            }
            case 1009: {
                return WebSocketCloseStatus.MESSAGE_TOO_BIG;
            }
            case 1010: {
                return WebSocketCloseStatus.MANDATORY_EXTENSION;
            }
            case 1011: {
                return WebSocketCloseStatus.INTERNAL_SERVER_ERROR;
            }
            case 1012: {
                return WebSocketCloseStatus.SERVICE_RESTART;
            }
            case 1013: {
                return WebSocketCloseStatus.TRY_AGAIN_LATER;
            }
            case 1014: {
                return WebSocketCloseStatus.BAD_GATEWAY;
            }
            case 1015: {
                return WebSocketCloseStatus.TLS_HANDSHAKE_FAILED;
            }
            default: {
                return new WebSocketCloseStatus(code, "Close status #" + code);
            }
        }
    }
    
    static {
        NORMAL_CLOSURE = new WebSocketCloseStatus(1000, "Bye");
        ENDPOINT_UNAVAILABLE = new WebSocketCloseStatus(1001, "Endpoint unavailable");
        PROTOCOL_ERROR = new WebSocketCloseStatus(1002, "Protocol error");
        INVALID_MESSAGE_TYPE = new WebSocketCloseStatus(1003, "Invalid message type");
        INVALID_PAYLOAD_DATA = new WebSocketCloseStatus(1007, "Invalid payload data");
        POLICY_VIOLATION = new WebSocketCloseStatus(1008, "Policy violation");
        MESSAGE_TOO_BIG = new WebSocketCloseStatus(1009, "Message too big");
        MANDATORY_EXTENSION = new WebSocketCloseStatus(1010, "Mandatory extension");
        INTERNAL_SERVER_ERROR = new WebSocketCloseStatus(1011, "Internal server error");
        SERVICE_RESTART = new WebSocketCloseStatus(1012, "Service Restart");
        TRY_AGAIN_LATER = new WebSocketCloseStatus(1013, "Try Again Later");
        BAD_GATEWAY = new WebSocketCloseStatus(1014, "Bad Gateway");
        EMPTY = new WebSocketCloseStatus(1005, "Empty", false);
        ABNORMAL_CLOSURE = new WebSocketCloseStatus(1006, "Abnormal closure", false);
        TLS_HANDSHAKE_FAILED = new WebSocketCloseStatus(1015, "TLS handshake failed", false);
    }
}
