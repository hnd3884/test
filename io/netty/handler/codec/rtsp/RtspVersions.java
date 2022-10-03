package io.netty.handler.codec.rtsp;

import io.netty.util.internal.ObjectUtil;
import io.netty.handler.codec.http.HttpVersion;

public final class RtspVersions
{
    public static final HttpVersion RTSP_1_0;
    
    public static HttpVersion valueOf(String text) {
        ObjectUtil.checkNotNull(text, "text");
        text = text.trim().toUpperCase();
        if ("RTSP/1.0".equals(text)) {
            return RtspVersions.RTSP_1_0;
        }
        return new HttpVersion(text, true);
    }
    
    private RtspVersions() {
    }
    
    static {
        RTSP_1_0 = new HttpVersion("RTSP", 1, 0, true);
    }
}
