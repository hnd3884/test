package io.netty.handler.codec.http2;

import io.netty.util.internal.StringUtil;
import io.netty.util.internal.ObjectUtil;

public class DefaultHttp2SettingsFrame implements Http2SettingsFrame
{
    private final Http2Settings settings;
    
    public DefaultHttp2SettingsFrame(final Http2Settings settings) {
        this.settings = ObjectUtil.checkNotNull(settings, "settings");
    }
    
    @Override
    public Http2Settings settings() {
        return this.settings;
    }
    
    @Override
    public String name() {
        return "SETTINGS";
    }
    
    @Override
    public boolean equals(final Object o) {
        if (!(o instanceof Http2SettingsFrame)) {
            return false;
        }
        final Http2SettingsFrame other = (Http2SettingsFrame)o;
        return this.settings.equals(other.settings());
    }
    
    @Override
    public int hashCode() {
        return this.settings.hashCode();
    }
    
    @Override
    public String toString() {
        return StringUtil.simpleClassName(this) + "(settings=" + this.settings + ')';
    }
}
