package io.netty.handler.codec.mqtt;

import java.util.Arrays;
import io.netty.util.internal.StringUtil;
import io.netty.util.CharsetUtil;

public final class MqttConnectPayload
{
    private final String clientIdentifier;
    private final MqttProperties willProperties;
    private final String willTopic;
    private final byte[] willMessage;
    private final String userName;
    private final byte[] password;
    
    @Deprecated
    public MqttConnectPayload(final String clientIdentifier, final String willTopic, final String willMessage, final String userName, final String password) {
        this(clientIdentifier, MqttProperties.NO_PROPERTIES, willTopic, willMessage.getBytes(CharsetUtil.UTF_8), userName, password.getBytes(CharsetUtil.UTF_8));
    }
    
    public MqttConnectPayload(final String clientIdentifier, final String willTopic, final byte[] willMessage, final String userName, final byte[] password) {
        this(clientIdentifier, MqttProperties.NO_PROPERTIES, willTopic, willMessage, userName, password);
    }
    
    public MqttConnectPayload(final String clientIdentifier, final MqttProperties willProperties, final String willTopic, final byte[] willMessage, final String userName, final byte[] password) {
        this.clientIdentifier = clientIdentifier;
        this.willProperties = MqttProperties.withEmptyDefaults(willProperties);
        this.willTopic = willTopic;
        this.willMessage = willMessage;
        this.userName = userName;
        this.password = password;
    }
    
    public String clientIdentifier() {
        return this.clientIdentifier;
    }
    
    public MqttProperties willProperties() {
        return this.willProperties;
    }
    
    public String willTopic() {
        return this.willTopic;
    }
    
    @Deprecated
    public String willMessage() {
        return (this.willMessage == null) ? null : new String(this.willMessage, CharsetUtil.UTF_8);
    }
    
    public byte[] willMessageInBytes() {
        return this.willMessage;
    }
    
    public String userName() {
        return this.userName;
    }
    
    @Deprecated
    public String password() {
        return (this.password == null) ? null : new String(this.password, CharsetUtil.UTF_8);
    }
    
    public byte[] passwordInBytes() {
        return this.password;
    }
    
    @Override
    public String toString() {
        return StringUtil.simpleClassName(this) + '[' + "clientIdentifier=" + this.clientIdentifier + ", willTopic=" + this.willTopic + ", willMessage=" + Arrays.toString(this.willMessage) + ", userName=" + this.userName + ", password=" + Arrays.toString(this.password) + ']';
    }
}
