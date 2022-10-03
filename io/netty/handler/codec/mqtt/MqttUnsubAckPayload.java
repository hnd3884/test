package io.netty.handler.codec.mqtt;

import io.netty.util.internal.StringUtil;
import java.util.Iterator;
import java.util.Collections;
import java.util.ArrayList;
import io.netty.util.internal.ObjectUtil;
import java.util.List;

public final class MqttUnsubAckPayload
{
    private final List<Short> unsubscribeReasonCodes;
    private static final MqttUnsubAckPayload EMPTY;
    
    public static MqttUnsubAckPayload withEmptyDefaults(final MqttUnsubAckPayload payload) {
        if (payload == null) {
            return MqttUnsubAckPayload.EMPTY;
        }
        return payload;
    }
    
    public MqttUnsubAckPayload(final short... unsubscribeReasonCodes) {
        ObjectUtil.checkNotNull(unsubscribeReasonCodes, "unsubscribeReasonCodes");
        final List<Short> list = new ArrayList<Short>(unsubscribeReasonCodes.length);
        for (final Short v : unsubscribeReasonCodes) {
            list.add(v);
        }
        this.unsubscribeReasonCodes = Collections.unmodifiableList((List<? extends Short>)list);
    }
    
    public MqttUnsubAckPayload(final Iterable<Short> unsubscribeReasonCodes) {
        ObjectUtil.checkNotNull(unsubscribeReasonCodes, "unsubscribeReasonCodes");
        final List<Short> list = new ArrayList<Short>();
        for (final Short v : unsubscribeReasonCodes) {
            ObjectUtil.checkNotNull(v, "unsubscribeReasonCode");
            list.add(v);
        }
        this.unsubscribeReasonCodes = Collections.unmodifiableList((List<? extends Short>)list);
    }
    
    public List<Short> unsubscribeReasonCodes() {
        return this.unsubscribeReasonCodes;
    }
    
    @Override
    public String toString() {
        return StringUtil.simpleClassName(this) + '[' + "unsubscribeReasonCodes=" + this.unsubscribeReasonCodes + ']';
    }
    
    static {
        EMPTY = new MqttUnsubAckPayload(new short[0]);
    }
}
