package io.netty.handler.codec.mqtt;

import java.util.Collections;
import java.util.Collection;
import java.util.Iterator;
import java.util.ArrayList;
import java.util.List;
import io.netty.util.collection.IntObjectHashMap;

public final class MqttProperties
{
    public static final MqttProperties NO_PROPERTIES;
    private IntObjectHashMap<MqttProperty> props;
    private List<UserProperty> userProperties;
    private List<IntegerProperty> subscriptionIds;
    private final boolean canModify;
    
    static MqttProperties withEmptyDefaults(final MqttProperties properties) {
        if (properties == null) {
            return MqttProperties.NO_PROPERTIES;
        }
        return properties;
    }
    
    public MqttProperties() {
        this(true);
    }
    
    private MqttProperties(final boolean canModify) {
        this.canModify = canModify;
    }
    
    public void add(final MqttProperty property) {
        if (!this.canModify) {
            throw new UnsupportedOperationException("adding property isn't allowed");
        }
        IntObjectHashMap<MqttProperty> props = this.props;
        if (property.propertyId == MqttPropertyType.USER_PROPERTY.value) {
            List<UserProperty> userProperties = this.userProperties;
            if (userProperties == null) {
                userProperties = new ArrayList<UserProperty>(1);
                this.userProperties = userProperties;
            }
            if (property instanceof UserProperty) {
                userProperties.add((UserProperty)property);
            }
            else {
                if (!(property instanceof UserProperties)) {
                    throw new IllegalArgumentException("User property must be of UserProperty or UserProperties type");
                }
                for (final StringPair pair : (List)((UserProperties)property).value) {
                    userProperties.add(new UserProperty(pair.key, pair.value));
                }
            }
        }
        else if (property.propertyId == MqttPropertyType.SUBSCRIPTION_IDENTIFIER.value) {
            List<IntegerProperty> subscriptionIds = this.subscriptionIds;
            if (subscriptionIds == null) {
                subscriptionIds = new ArrayList<IntegerProperty>(1);
                this.subscriptionIds = subscriptionIds;
            }
            if (!(property instanceof IntegerProperty)) {
                throw new IllegalArgumentException("Subscription ID must be an integer property");
            }
            subscriptionIds.add((IntegerProperty)property);
        }
        else {
            if (props == null) {
                props = new IntObjectHashMap<MqttProperty>();
                this.props = props;
            }
            props.put(property.propertyId, property);
        }
    }
    
    public Collection<? extends MqttProperty> listAll() {
        final IntObjectHashMap<MqttProperty> props = this.props;
        if (props == null && this.subscriptionIds == null && this.userProperties == null) {
            return (Collection<? extends MqttProperty>)Collections.emptyList();
        }
        if (this.subscriptionIds == null && this.userProperties == null) {
            return props.values();
        }
        if (props == null && this.userProperties == null) {
            return this.subscriptionIds;
        }
        final List<MqttProperty> propValues = new ArrayList<MqttProperty>((props != null) ? props.size() : 1);
        if (props != null) {
            propValues.addAll(props.values());
        }
        if (this.subscriptionIds != null) {
            propValues.addAll(this.subscriptionIds);
        }
        if (this.userProperties != null) {
            propValues.add(fromUserPropertyCollection(this.userProperties));
        }
        return propValues;
    }
    
    public boolean isEmpty() {
        final IntObjectHashMap<MqttProperty> props = this.props;
        return props == null || props.isEmpty();
    }
    
    public MqttProperty getProperty(final int propertyId) {
        if (propertyId == MqttPropertyType.USER_PROPERTY.value) {
            final List<UserProperty> userProperties = this.userProperties;
            if (userProperties == null) {
                return null;
            }
            return fromUserPropertyCollection(userProperties);
        }
        else {
            if (propertyId != MqttPropertyType.SUBSCRIPTION_IDENTIFIER.value) {
                final IntObjectHashMap<MqttProperty> props = this.props;
                return (props == null) ? null : props.get(propertyId);
            }
            final List<IntegerProperty> subscriptionIds = this.subscriptionIds;
            if (subscriptionIds == null || subscriptionIds.isEmpty()) {
                return null;
            }
            return subscriptionIds.get(0);
        }
    }
    
    public List<? extends MqttProperty> getProperties(final int propertyId) {
        if (propertyId == MqttPropertyType.USER_PROPERTY.value) {
            return (this.userProperties == null) ? Collections.emptyList() : this.userProperties;
        }
        if (propertyId == MqttPropertyType.SUBSCRIPTION_IDENTIFIER.value) {
            return (this.subscriptionIds == null) ? Collections.emptyList() : this.subscriptionIds;
        }
        final IntObjectHashMap<MqttProperty> props = this.props;
        return (props == null || !props.containsKey(propertyId)) ? Collections.emptyList() : Collections.singletonList(props.get(propertyId));
    }
    
    static {
        NO_PROPERTIES = new MqttProperties(false);
    }
    
    public enum MqttPropertyType
    {
        PAYLOAD_FORMAT_INDICATOR(1), 
        REQUEST_PROBLEM_INFORMATION(23), 
        REQUEST_RESPONSE_INFORMATION(25), 
        MAXIMUM_QOS(36), 
        RETAIN_AVAILABLE(37), 
        WILDCARD_SUBSCRIPTION_AVAILABLE(40), 
        SUBSCRIPTION_IDENTIFIER_AVAILABLE(41), 
        SHARED_SUBSCRIPTION_AVAILABLE(42), 
        SERVER_KEEP_ALIVE(19), 
        RECEIVE_MAXIMUM(33), 
        TOPIC_ALIAS_MAXIMUM(34), 
        TOPIC_ALIAS(35), 
        PUBLICATION_EXPIRY_INTERVAL(2), 
        SESSION_EXPIRY_INTERVAL(17), 
        WILL_DELAY_INTERVAL(24), 
        MAXIMUM_PACKET_SIZE(39), 
        SUBSCRIPTION_IDENTIFIER(11), 
        CONTENT_TYPE(3), 
        RESPONSE_TOPIC(8), 
        ASSIGNED_CLIENT_IDENTIFIER(18), 
        AUTHENTICATION_METHOD(21), 
        RESPONSE_INFORMATION(26), 
        SERVER_REFERENCE(28), 
        REASON_STRING(31), 
        USER_PROPERTY(38), 
        CORRELATION_DATA(9), 
        AUTHENTICATION_DATA(22);
        
        private static final MqttPropertyType[] VALUES;
        private final int value;
        
        private MqttPropertyType(final int value) {
            this.value = value;
        }
        
        public int value() {
            return this.value;
        }
        
        public static MqttPropertyType valueOf(final int type) {
            MqttPropertyType t = null;
            try {
                t = MqttPropertyType.VALUES[type];
            }
            catch (final ArrayIndexOutOfBoundsException ex) {}
            if (t == null) {
                throw new IllegalArgumentException("unknown property type: " + type);
            }
            return t;
        }
        
        static {
            VALUES = new MqttPropertyType[43];
            for (final MqttPropertyType v : values()) {
                MqttPropertyType.VALUES[v.value] = v;
            }
        }
    }
    
    public abstract static class MqttProperty<T>
    {
        final T value;
        final int propertyId;
        
        protected MqttProperty(final int propertyId, final T value) {
            this.propertyId = propertyId;
            this.value = value;
        }
        
        public T value() {
            return this.value;
        }
        
        public int propertyId() {
            return this.propertyId;
        }
        
        @Override
        public int hashCode() {
            return this.propertyId + 31 * this.value.hashCode();
        }
        
        @Override
        public boolean equals(final Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null || this.getClass() != obj.getClass()) {
                return false;
            }
            final MqttProperty that = (MqttProperty)obj;
            return this.propertyId == that.propertyId && this.value.equals(that.value);
        }
    }
    
    public static final class IntegerProperty extends MqttProperty<Integer>
    {
        public IntegerProperty(final int propertyId, final Integer value) {
            super(propertyId, value);
        }
        
        @Override
        public String toString() {
            return "IntegerProperty(" + this.propertyId + ", " + this.value + ")";
        }
    }
    
    public static final class StringProperty extends MqttProperty<String>
    {
        public StringProperty(final int propertyId, final String value) {
            super(propertyId, value);
        }
        
        @Override
        public String toString() {
            return "StringProperty(" + this.propertyId + ", " + (String)this.value + ")";
        }
    }
    
    public static final class StringPair
    {
        public final String key;
        public final String value;
        
        public StringPair(final String key, final String value) {
            this.key = key;
            this.value = value;
        }
        
        @Override
        public int hashCode() {
            return this.key.hashCode() + 31 * this.value.hashCode();
        }
        
        @Override
        public boolean equals(final Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null || this.getClass() != obj.getClass()) {
                return false;
            }
            final StringPair that = (StringPair)obj;
            return that.key.equals(this.key) && that.value.equals(this.value);
        }
    }
    
    public static final class UserProperties extends MqttProperty<List<StringPair>>
    {
        public UserProperties() {
            super(MqttPropertyType.USER_PROPERTY.value, new ArrayList());
        }
        
        public UserProperties(final Collection<StringPair> values) {
            this();
            ((List)this.value).addAll(values);
        }
        
        private static UserProperties fromUserPropertyCollection(final Collection<UserProperty> properties) {
            final UserProperties userProperties = new UserProperties();
            for (final UserProperty property : properties) {
                userProperties.add(new StringPair(((StringPair)property.value).key, ((StringPair)property.value).value));
            }
            return userProperties;
        }
        
        public void add(final StringPair pair) {
            ((List)this.value).add(pair);
        }
        
        public void add(final String key, final String value) {
            ((List)this.value).add(new StringPair(key, value));
        }
        
        @Override
        public String toString() {
            final StringBuilder builder = new StringBuilder("UserProperties(");
            boolean first = true;
            for (final StringPair pair : (List)this.value) {
                if (!first) {
                    builder.append(", ");
                }
                builder.append(pair.key + "->" + pair.value);
                first = false;
            }
            builder.append(")");
            return builder.toString();
        }
    }
    
    public static final class UserProperty extends MqttProperty<StringPair>
    {
        public UserProperty(final String key, final String value) {
            super(MqttPropertyType.USER_PROPERTY.value, new StringPair(key, value));
        }
        
        @Override
        public String toString() {
            return "UserProperty(" + ((StringPair)this.value).key + ", " + ((StringPair)this.value).value + ")";
        }
    }
    
    public static final class BinaryProperty extends MqttProperty<byte[]>
    {
        public BinaryProperty(final int propertyId, final byte[] value) {
            super(propertyId, value);
        }
        
        @Override
        public String toString() {
            return "BinaryProperty(" + this.propertyId + ", " + ((byte[])(Object)this.value).length + " bytes)";
        }
    }
}
