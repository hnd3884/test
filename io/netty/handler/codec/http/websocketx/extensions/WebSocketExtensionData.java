package io.netty.handler.codec.http.websocketx.extensions;

import java.util.Collections;
import io.netty.util.internal.ObjectUtil;
import java.util.Map;

public final class WebSocketExtensionData
{
    private final String name;
    private final Map<String, String> parameters;
    
    public WebSocketExtensionData(final String name, final Map<String, String> parameters) {
        this.name = ObjectUtil.checkNotNull(name, "name");
        this.parameters = Collections.unmodifiableMap((Map<? extends String, ? extends String>)ObjectUtil.checkNotNull((Map<? extends K, ? extends V>)parameters, "parameters"));
    }
    
    public String name() {
        return this.name;
    }
    
    public Map<String, String> parameters() {
        return this.parameters;
    }
}
