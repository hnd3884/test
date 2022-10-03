package io.netty.handler.codec.mqtt;

import java.util.Iterator;
import java.util.Collection;
import java.util.Arrays;
import io.netty.util.internal.ObjectUtil;
import java.util.ArrayList;
import java.util.List;
import io.netty.util.CharsetUtil;
import io.netty.buffer.Unpooled;
import io.netty.buffer.ByteBuf;

public final class MqttMessageBuilders
{
    public static ConnectBuilder connect() {
        return new ConnectBuilder();
    }
    
    public static ConnAckBuilder connAck() {
        return new ConnAckBuilder();
    }
    
    public static PublishBuilder publish() {
        return new PublishBuilder();
    }
    
    public static SubscribeBuilder subscribe() {
        return new SubscribeBuilder();
    }
    
    public static UnsubscribeBuilder unsubscribe() {
        return new UnsubscribeBuilder();
    }
    
    public static PubAckBuilder pubAck() {
        return new PubAckBuilder();
    }
    
    public static SubAckBuilder subAck() {
        return new SubAckBuilder();
    }
    
    public static UnsubAckBuilder unsubAck() {
        return new UnsubAckBuilder();
    }
    
    public static DisconnectBuilder disconnect() {
        return new DisconnectBuilder();
    }
    
    public static AuthBuilder auth() {
        return new AuthBuilder();
    }
    
    private MqttMessageBuilders() {
    }
    
    public static final class PublishBuilder
    {
        private String topic;
        private boolean retained;
        private MqttQoS qos;
        private ByteBuf payload;
        private int messageId;
        private MqttProperties mqttProperties;
        
        PublishBuilder() {
        }
        
        public PublishBuilder topicName(final String topic) {
            this.topic = topic;
            return this;
        }
        
        public PublishBuilder retained(final boolean retained) {
            this.retained = retained;
            return this;
        }
        
        public PublishBuilder qos(final MqttQoS qos) {
            this.qos = qos;
            return this;
        }
        
        public PublishBuilder payload(final ByteBuf payload) {
            this.payload = payload;
            return this;
        }
        
        public PublishBuilder messageId(final int messageId) {
            this.messageId = messageId;
            return this;
        }
        
        public PublishBuilder properties(final MqttProperties properties) {
            this.mqttProperties = properties;
            return this;
        }
        
        public MqttPublishMessage build() {
            final MqttFixedHeader mqttFixedHeader = new MqttFixedHeader(MqttMessageType.PUBLISH, false, this.qos, this.retained, 0);
            final MqttPublishVariableHeader mqttVariableHeader = new MqttPublishVariableHeader(this.topic, this.messageId, this.mqttProperties);
            return new MqttPublishMessage(mqttFixedHeader, mqttVariableHeader, Unpooled.buffer().writeBytes(this.payload));
        }
    }
    
    public static final class ConnectBuilder
    {
        private MqttVersion version;
        private String clientId;
        private boolean cleanSession;
        private boolean hasUser;
        private boolean hasPassword;
        private int keepAliveSecs;
        private MqttProperties willProperties;
        private boolean willFlag;
        private boolean willRetain;
        private MqttQoS willQos;
        private String willTopic;
        private byte[] willMessage;
        private String username;
        private byte[] password;
        private MqttProperties properties;
        
        ConnectBuilder() {
            this.version = MqttVersion.MQTT_3_1_1;
            this.willProperties = MqttProperties.NO_PROPERTIES;
            this.willQos = MqttQoS.AT_MOST_ONCE;
            this.properties = MqttProperties.NO_PROPERTIES;
        }
        
        public ConnectBuilder protocolVersion(final MqttVersion version) {
            this.version = version;
            return this;
        }
        
        public ConnectBuilder clientId(final String clientId) {
            this.clientId = clientId;
            return this;
        }
        
        public ConnectBuilder cleanSession(final boolean cleanSession) {
            this.cleanSession = cleanSession;
            return this;
        }
        
        public ConnectBuilder keepAlive(final int keepAliveSecs) {
            this.keepAliveSecs = keepAliveSecs;
            return this;
        }
        
        public ConnectBuilder willFlag(final boolean willFlag) {
            this.willFlag = willFlag;
            return this;
        }
        
        public ConnectBuilder willQoS(final MqttQoS willQos) {
            this.willQos = willQos;
            return this;
        }
        
        public ConnectBuilder willTopic(final String willTopic) {
            this.willTopic = willTopic;
            return this;
        }
        
        @Deprecated
        public ConnectBuilder willMessage(final String willMessage) {
            this.willMessage((byte[])((willMessage == null) ? null : willMessage.getBytes(CharsetUtil.UTF_8)));
            return this;
        }
        
        public ConnectBuilder willMessage(final byte[] willMessage) {
            this.willMessage = willMessage;
            return this;
        }
        
        public ConnectBuilder willRetain(final boolean willRetain) {
            this.willRetain = willRetain;
            return this;
        }
        
        public ConnectBuilder willProperties(final MqttProperties willProperties) {
            this.willProperties = willProperties;
            return this;
        }
        
        public ConnectBuilder hasUser(final boolean value) {
            this.hasUser = value;
            return this;
        }
        
        public ConnectBuilder hasPassword(final boolean value) {
            this.hasPassword = value;
            return this;
        }
        
        public ConnectBuilder username(final String username) {
            this.hasUser = (username != null);
            this.username = username;
            return this;
        }
        
        @Deprecated
        public ConnectBuilder password(final String password) {
            this.password((byte[])((password == null) ? null : password.getBytes(CharsetUtil.UTF_8)));
            return this;
        }
        
        public ConnectBuilder password(final byte[] password) {
            this.hasPassword = (password != null);
            this.password = password;
            return this;
        }
        
        public ConnectBuilder properties(final MqttProperties properties) {
            this.properties = properties;
            return this;
        }
        
        public MqttConnectMessage build() {
            final MqttFixedHeader mqttFixedHeader = new MqttFixedHeader(MqttMessageType.CONNECT, false, MqttQoS.AT_MOST_ONCE, false, 0);
            final MqttConnectVariableHeader mqttConnectVariableHeader = new MqttConnectVariableHeader(this.version.protocolName(), this.version.protocolLevel(), this.hasUser, this.hasPassword, this.willRetain, this.willQos.value(), this.willFlag, this.cleanSession, this.keepAliveSecs, this.properties);
            final MqttConnectPayload mqttConnectPayload = new MqttConnectPayload(this.clientId, this.willProperties, this.willTopic, this.willMessage, this.username, this.password);
            return new MqttConnectMessage(mqttFixedHeader, mqttConnectVariableHeader, mqttConnectPayload);
        }
    }
    
    public static final class SubscribeBuilder
    {
        private List<MqttTopicSubscription> subscriptions;
        private int messageId;
        private MqttProperties properties;
        
        SubscribeBuilder() {
        }
        
        public SubscribeBuilder addSubscription(final MqttQoS qos, final String topic) {
            this.ensureSubscriptionsExist();
            this.subscriptions.add(new MqttTopicSubscription(topic, qos));
            return this;
        }
        
        public SubscribeBuilder addSubscription(final String topic, final MqttSubscriptionOption option) {
            this.ensureSubscriptionsExist();
            this.subscriptions.add(new MqttTopicSubscription(topic, option));
            return this;
        }
        
        public SubscribeBuilder messageId(final int messageId) {
            this.messageId = messageId;
            return this;
        }
        
        public SubscribeBuilder properties(final MqttProperties properties) {
            this.properties = properties;
            return this;
        }
        
        public MqttSubscribeMessage build() {
            final MqttFixedHeader mqttFixedHeader = new MqttFixedHeader(MqttMessageType.SUBSCRIBE, false, MqttQoS.AT_LEAST_ONCE, false, 0);
            final MqttMessageIdAndPropertiesVariableHeader mqttVariableHeader = new MqttMessageIdAndPropertiesVariableHeader(this.messageId, this.properties);
            final MqttSubscribePayload mqttSubscribePayload = new MqttSubscribePayload(this.subscriptions);
            return new MqttSubscribeMessage(mqttFixedHeader, mqttVariableHeader, mqttSubscribePayload);
        }
        
        private void ensureSubscriptionsExist() {
            if (this.subscriptions == null) {
                this.subscriptions = new ArrayList<MqttTopicSubscription>(5);
            }
        }
    }
    
    public static final class UnsubscribeBuilder
    {
        private List<String> topicFilters;
        private int messageId;
        private MqttProperties properties;
        
        UnsubscribeBuilder() {
        }
        
        public UnsubscribeBuilder addTopicFilter(final String topic) {
            if (this.topicFilters == null) {
                this.topicFilters = new ArrayList<String>(5);
            }
            this.topicFilters.add(topic);
            return this;
        }
        
        public UnsubscribeBuilder messageId(final int messageId) {
            this.messageId = messageId;
            return this;
        }
        
        public UnsubscribeBuilder properties(final MqttProperties properties) {
            this.properties = properties;
            return this;
        }
        
        public MqttUnsubscribeMessage build() {
            final MqttFixedHeader mqttFixedHeader = new MqttFixedHeader(MqttMessageType.UNSUBSCRIBE, false, MqttQoS.AT_LEAST_ONCE, false, 0);
            final MqttMessageIdAndPropertiesVariableHeader mqttVariableHeader = new MqttMessageIdAndPropertiesVariableHeader(this.messageId, this.properties);
            final MqttUnsubscribePayload mqttSubscribePayload = new MqttUnsubscribePayload(this.topicFilters);
            return new MqttUnsubscribeMessage(mqttFixedHeader, mqttVariableHeader, mqttSubscribePayload);
        }
    }
    
    public static final class ConnAckBuilder
    {
        private MqttConnectReturnCode returnCode;
        private boolean sessionPresent;
        private MqttProperties properties;
        private ConnAckPropertiesBuilder propsBuilder;
        
        private ConnAckBuilder() {
            this.properties = MqttProperties.NO_PROPERTIES;
        }
        
        public ConnAckBuilder returnCode(final MqttConnectReturnCode returnCode) {
            this.returnCode = returnCode;
            return this;
        }
        
        public ConnAckBuilder sessionPresent(final boolean sessionPresent) {
            this.sessionPresent = sessionPresent;
            return this;
        }
        
        public ConnAckBuilder properties(final MqttProperties properties) {
            this.properties = properties;
            return this;
        }
        
        public ConnAckBuilder properties(final PropertiesInitializer<ConnAckPropertiesBuilder> consumer) {
            if (this.propsBuilder == null) {
                this.propsBuilder = new ConnAckPropertiesBuilder();
            }
            consumer.apply(this.propsBuilder);
            return this;
        }
        
        public MqttConnAckMessage build() {
            if (this.propsBuilder != null) {
                this.properties = this.propsBuilder.build();
            }
            final MqttFixedHeader mqttFixedHeader = new MqttFixedHeader(MqttMessageType.CONNACK, false, MqttQoS.AT_MOST_ONCE, false, 0);
            final MqttConnAckVariableHeader mqttConnAckVariableHeader = new MqttConnAckVariableHeader(this.returnCode, this.sessionPresent, this.properties);
            return new MqttConnAckMessage(mqttFixedHeader, mqttConnAckVariableHeader);
        }
    }
    
    public static final class ConnAckPropertiesBuilder
    {
        private String clientId;
        private Long sessionExpiryInterval;
        private int receiveMaximum;
        private Byte maximumQos;
        private boolean retain;
        private Long maximumPacketSize;
        private int topicAliasMaximum;
        private String reasonString;
        private final MqttProperties.UserProperties userProperties;
        private Boolean wildcardSubscriptionAvailable;
        private Boolean subscriptionIdentifiersAvailable;
        private Boolean sharedSubscriptionAvailable;
        private Integer serverKeepAlive;
        private String responseInformation;
        private String serverReference;
        private String authenticationMethod;
        private byte[] authenticationData;
        
        public ConnAckPropertiesBuilder() {
            this.userProperties = new MqttProperties.UserProperties();
        }
        
        public MqttProperties build() {
            final MqttProperties props = new MqttProperties();
            if (this.clientId != null) {
                props.add(new MqttProperties.StringProperty(MqttProperties.MqttPropertyType.ASSIGNED_CLIENT_IDENTIFIER.value(), this.clientId));
            }
            if (this.sessionExpiryInterval != null) {
                props.add(new MqttProperties.IntegerProperty(MqttProperties.MqttPropertyType.SESSION_EXPIRY_INTERVAL.value(), this.sessionExpiryInterval.intValue()));
            }
            if (this.receiveMaximum > 0) {
                props.add(new MqttProperties.IntegerProperty(MqttProperties.MqttPropertyType.RECEIVE_MAXIMUM.value(), this.receiveMaximum));
            }
            if (this.maximumQos != null) {
                props.add(new MqttProperties.IntegerProperty(MqttProperties.MqttPropertyType.MAXIMUM_QOS.value(), this.receiveMaximum));
            }
            props.add(new MqttProperties.IntegerProperty(MqttProperties.MqttPropertyType.RETAIN_AVAILABLE.value(), (int)(this.retain ? 1 : 0)));
            if (this.maximumPacketSize != null) {
                props.add(new MqttProperties.IntegerProperty(MqttProperties.MqttPropertyType.MAXIMUM_PACKET_SIZE.value(), this.maximumPacketSize.intValue()));
            }
            props.add(new MqttProperties.IntegerProperty(MqttProperties.MqttPropertyType.TOPIC_ALIAS_MAXIMUM.value(), this.topicAliasMaximum));
            if (this.reasonString != null) {
                props.add(new MqttProperties.StringProperty(MqttProperties.MqttPropertyType.REASON_STRING.value(), this.reasonString));
            }
            props.add(this.userProperties);
            if (this.wildcardSubscriptionAvailable != null) {
                props.add(new MqttProperties.IntegerProperty(MqttProperties.MqttPropertyType.WILDCARD_SUBSCRIPTION_AVAILABLE.value(), (int)(((boolean)this.wildcardSubscriptionAvailable) ? 1 : 0)));
            }
            if (this.subscriptionIdentifiersAvailable != null) {
                props.add(new MqttProperties.IntegerProperty(MqttProperties.MqttPropertyType.SUBSCRIPTION_IDENTIFIER_AVAILABLE.value(), (int)(((boolean)this.subscriptionIdentifiersAvailable) ? 1 : 0)));
            }
            if (this.sharedSubscriptionAvailable != null) {
                props.add(new MqttProperties.IntegerProperty(MqttProperties.MqttPropertyType.SHARED_SUBSCRIPTION_AVAILABLE.value(), (int)(((boolean)this.sharedSubscriptionAvailable) ? 1 : 0)));
            }
            if (this.serverKeepAlive != null) {
                props.add(new MqttProperties.IntegerProperty(MqttProperties.MqttPropertyType.SERVER_KEEP_ALIVE.value(), this.serverKeepAlive));
            }
            if (this.responseInformation != null) {
                props.add(new MqttProperties.StringProperty(MqttProperties.MqttPropertyType.RESPONSE_INFORMATION.value(), this.responseInformation));
            }
            if (this.serverReference != null) {
                props.add(new MqttProperties.StringProperty(MqttProperties.MqttPropertyType.SERVER_REFERENCE.value(), this.serverReference));
            }
            if (this.authenticationMethod != null) {
                props.add(new MqttProperties.StringProperty(MqttProperties.MqttPropertyType.AUTHENTICATION_METHOD.value(), this.authenticationMethod));
            }
            if (this.authenticationData != null) {
                props.add(new MqttProperties.BinaryProperty(MqttProperties.MqttPropertyType.AUTHENTICATION_DATA.value(), this.authenticationData));
            }
            return props;
        }
        
        public ConnAckPropertiesBuilder sessionExpiryInterval(final long seconds) {
            this.sessionExpiryInterval = seconds;
            return this;
        }
        
        public ConnAckPropertiesBuilder receiveMaximum(final int value) {
            this.receiveMaximum = ObjectUtil.checkPositive(value, "value");
            return this;
        }
        
        public ConnAckPropertiesBuilder maximumQos(final byte value) {
            if (value != 0 && value != 1) {
                throw new IllegalArgumentException("maximum QoS property could be 0 or 1");
            }
            this.maximumQos = value;
            return this;
        }
        
        public ConnAckPropertiesBuilder retainAvailable(final boolean retain) {
            this.retain = retain;
            return this;
        }
        
        public ConnAckPropertiesBuilder maximumPacketSize(final long size) {
            this.maximumPacketSize = ObjectUtil.checkPositive(size, "size");
            return this;
        }
        
        public ConnAckPropertiesBuilder assignedClientId(final String clientId) {
            this.clientId = clientId;
            return this;
        }
        
        public ConnAckPropertiesBuilder topicAliasMaximum(final int value) {
            this.topicAliasMaximum = value;
            return this;
        }
        
        public ConnAckPropertiesBuilder reasonString(final String reason) {
            this.reasonString = reason;
            return this;
        }
        
        public ConnAckPropertiesBuilder userProperty(final String name, final String value) {
            this.userProperties.add(name, value);
            return this;
        }
        
        public ConnAckPropertiesBuilder wildcardSubscriptionAvailable(final boolean value) {
            this.wildcardSubscriptionAvailable = value;
            return this;
        }
        
        public ConnAckPropertiesBuilder subscriptionIdentifiersAvailable(final boolean value) {
            this.subscriptionIdentifiersAvailable = value;
            return this;
        }
        
        public ConnAckPropertiesBuilder sharedSubscriptionAvailable(final boolean value) {
            this.sharedSubscriptionAvailable = value;
            return this;
        }
        
        public ConnAckPropertiesBuilder serverKeepAlive(final int seconds) {
            this.serverKeepAlive = seconds;
            return this;
        }
        
        public ConnAckPropertiesBuilder responseInformation(final String value) {
            this.responseInformation = value;
            return this;
        }
        
        public ConnAckPropertiesBuilder serverReference(final String host) {
            this.serverReference = host;
            return this;
        }
        
        public ConnAckPropertiesBuilder authenticationMethod(final String methodName) {
            this.authenticationMethod = methodName;
            return this;
        }
        
        public ConnAckPropertiesBuilder authenticationData(final byte[] rawData) {
            this.authenticationData = rawData.clone();
            return this;
        }
    }
    
    public static final class PubAckBuilder
    {
        private int packetId;
        private byte reasonCode;
        private MqttProperties properties;
        
        PubAckBuilder() {
        }
        
        public PubAckBuilder reasonCode(final byte reasonCode) {
            this.reasonCode = reasonCode;
            return this;
        }
        
        public PubAckBuilder packetId(final int packetId) {
            this.packetId = packetId;
            return this;
        }
        
        @Deprecated
        public PubAckBuilder packetId(final short packetId) {
            return this.packetId(packetId & 0xFFFF);
        }
        
        public PubAckBuilder properties(final MqttProperties properties) {
            this.properties = properties;
            return this;
        }
        
        public MqttMessage build() {
            final MqttFixedHeader mqttFixedHeader = new MqttFixedHeader(MqttMessageType.PUBACK, false, MqttQoS.AT_MOST_ONCE, false, 0);
            final MqttPubReplyMessageVariableHeader mqttPubAckVariableHeader = new MqttPubReplyMessageVariableHeader(this.packetId, this.reasonCode, this.properties);
            return new MqttMessage(mqttFixedHeader, mqttPubAckVariableHeader);
        }
    }
    
    public static final class SubAckBuilder
    {
        private int packetId;
        private MqttProperties properties;
        private final List<MqttQoS> grantedQoses;
        
        SubAckBuilder() {
            this.grantedQoses = new ArrayList<MqttQoS>();
        }
        
        public SubAckBuilder packetId(final int packetId) {
            this.packetId = packetId;
            return this;
        }
        
        @Deprecated
        public SubAckBuilder packetId(final short packetId) {
            return this.packetId(packetId & 0xFFFF);
        }
        
        public SubAckBuilder properties(final MqttProperties properties) {
            this.properties = properties;
            return this;
        }
        
        public SubAckBuilder addGrantedQos(final MqttQoS qos) {
            this.grantedQoses.add(qos);
            return this;
        }
        
        public SubAckBuilder addGrantedQoses(final MqttQoS... qoses) {
            this.grantedQoses.addAll(Arrays.asList(qoses));
            return this;
        }
        
        public MqttSubAckMessage build() {
            final MqttFixedHeader mqttFixedHeader = new MqttFixedHeader(MqttMessageType.SUBACK, false, MqttQoS.AT_MOST_ONCE, false, 0);
            final MqttMessageIdAndPropertiesVariableHeader mqttSubAckVariableHeader = new MqttMessageIdAndPropertiesVariableHeader(this.packetId, this.properties);
            final int[] grantedQoses = new int[this.grantedQoses.size()];
            int i = 0;
            for (final MqttQoS grantedQos : this.grantedQoses) {
                grantedQoses[i++] = grantedQos.value();
            }
            final MqttSubAckPayload subAckPayload = new MqttSubAckPayload(grantedQoses);
            return new MqttSubAckMessage(mqttFixedHeader, mqttSubAckVariableHeader, subAckPayload);
        }
    }
    
    public static final class UnsubAckBuilder
    {
        private int packetId;
        private MqttProperties properties;
        private final List<Short> reasonCodes;
        
        UnsubAckBuilder() {
            this.reasonCodes = new ArrayList<Short>();
        }
        
        public UnsubAckBuilder packetId(final int packetId) {
            this.packetId = packetId;
            return this;
        }
        
        @Deprecated
        public UnsubAckBuilder packetId(final short packetId) {
            return this.packetId(packetId & 0xFFFF);
        }
        
        public UnsubAckBuilder properties(final MqttProperties properties) {
            this.properties = properties;
            return this;
        }
        
        public UnsubAckBuilder addReasonCode(final short reasonCode) {
            this.reasonCodes.add(reasonCode);
            return this;
        }
        
        public UnsubAckBuilder addReasonCodes(final Short... reasonCodes) {
            this.reasonCodes.addAll(Arrays.asList(reasonCodes));
            return this;
        }
        
        public MqttUnsubAckMessage build() {
            final MqttFixedHeader mqttFixedHeader = new MqttFixedHeader(MqttMessageType.UNSUBACK, false, MqttQoS.AT_MOST_ONCE, false, 0);
            final MqttMessageIdAndPropertiesVariableHeader mqttSubAckVariableHeader = new MqttMessageIdAndPropertiesVariableHeader(this.packetId, this.properties);
            final MqttUnsubAckPayload subAckPayload = new MqttUnsubAckPayload(this.reasonCodes);
            return new MqttUnsubAckMessage(mqttFixedHeader, mqttSubAckVariableHeader, subAckPayload);
        }
    }
    
    public static final class DisconnectBuilder
    {
        private MqttProperties properties;
        private byte reasonCode;
        
        DisconnectBuilder() {
        }
        
        public DisconnectBuilder properties(final MqttProperties properties) {
            this.properties = properties;
            return this;
        }
        
        public DisconnectBuilder reasonCode(final byte reasonCode) {
            this.reasonCode = reasonCode;
            return this;
        }
        
        public MqttMessage build() {
            final MqttFixedHeader mqttFixedHeader = new MqttFixedHeader(MqttMessageType.DISCONNECT, false, MqttQoS.AT_MOST_ONCE, false, 0);
            final MqttReasonCodeAndPropertiesVariableHeader mqttDisconnectVariableHeader = new MqttReasonCodeAndPropertiesVariableHeader(this.reasonCode, this.properties);
            return new MqttMessage(mqttFixedHeader, mqttDisconnectVariableHeader);
        }
    }
    
    public static final class AuthBuilder
    {
        private MqttProperties properties;
        private byte reasonCode;
        
        AuthBuilder() {
        }
        
        public AuthBuilder properties(final MqttProperties properties) {
            this.properties = properties;
            return this;
        }
        
        public AuthBuilder reasonCode(final byte reasonCode) {
            this.reasonCode = reasonCode;
            return this;
        }
        
        public MqttMessage build() {
            final MqttFixedHeader mqttFixedHeader = new MqttFixedHeader(MqttMessageType.AUTH, false, MqttQoS.AT_MOST_ONCE, false, 0);
            final MqttReasonCodeAndPropertiesVariableHeader mqttAuthVariableHeader = new MqttReasonCodeAndPropertiesVariableHeader(this.reasonCode, this.properties);
            return new MqttMessage(mqttFixedHeader, mqttAuthVariableHeader);
        }
    }
    
    public interface PropertiesInitializer<T>
    {
        void apply(final T p0);
    }
}
