package io.netty.handler.codec.mqtt;

import io.netty.util.internal.StringUtil;
import java.util.Iterator;
import java.util.Collections;
import java.util.ArrayList;
import io.netty.util.internal.ObjectUtil;
import java.util.List;

public class MqttSubAckPayload
{
    private final List<Integer> reasonCodes;
    
    public MqttSubAckPayload(final int... reasonCodes) {
        ObjectUtil.checkNotNull(reasonCodes, "reasonCodes");
        final List<Integer> list = new ArrayList<Integer>(reasonCodes.length);
        for (final int v : reasonCodes) {
            list.add(v);
        }
        this.reasonCodes = Collections.unmodifiableList((List<? extends Integer>)list);
    }
    
    public MqttSubAckPayload(final Iterable<Integer> reasonCodes) {
        ObjectUtil.checkNotNull(reasonCodes, "reasonCodes");
        final List<Integer> list = new ArrayList<Integer>();
        for (final Integer v : reasonCodes) {
            if (v == null) {
                break;
            }
            list.add(v);
        }
        this.reasonCodes = Collections.unmodifiableList((List<? extends Integer>)list);
    }
    
    public List<Integer> grantedQoSLevels() {
        final List<Integer> qosLevels = new ArrayList<Integer>(this.reasonCodes.size());
        for (final int code : this.reasonCodes) {
            if (code > MqttQoS.EXACTLY_ONCE.value()) {
                qosLevels.add(MqttQoS.FAILURE.value());
            }
            else {
                qosLevels.add(code);
            }
        }
        return qosLevels;
    }
    
    public List<Integer> reasonCodes() {
        return this.reasonCodes;
    }
    
    @Override
    public String toString() {
        return StringUtil.simpleClassName(this) + '[' + "reasonCodes=" + this.reasonCodes + ']';
    }
}
