package io.netty.handler.codec.http2;

public final class Http2ConnectionPrefaceAndSettingsFrameWrittenEvent
{
    static final Http2ConnectionPrefaceAndSettingsFrameWrittenEvent INSTANCE;
    
    private Http2ConnectionPrefaceAndSettingsFrameWrittenEvent() {
    }
    
    static {
        INSTANCE = new Http2ConnectionPrefaceAndSettingsFrameWrittenEvent();
    }
}
