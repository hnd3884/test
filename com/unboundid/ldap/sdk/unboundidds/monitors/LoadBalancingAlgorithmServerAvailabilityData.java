package com.unboundid.ldap.sdk.unboundidds.monitors;

import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.util.NotMutable;
import java.io.Serializable;

@NotMutable
@ThreadSafety(level = ThreadSafetyLevel.COMPLETELY_THREADSAFE)
public final class LoadBalancingAlgorithmServerAvailabilityData implements Serializable
{
    private static final long serialVersionUID = -2195372034654700615L;
    private final HealthCheckState healthCheckState;
    private final int serverPort;
    private final String serverAddress;
    
    LoadBalancingAlgorithmServerAvailabilityData(final String s) {
        final int firstColonPos = s.indexOf(58);
        final int secondColonPos = s.indexOf(58, firstColonPos + 1);
        this.serverAddress = s.substring(0, firstColonPos);
        this.serverPort = Integer.parseInt(s.substring(firstColonPos + 1, secondColonPos));
        this.healthCheckState = HealthCheckState.forName(s.substring(secondColonPos + 1));
    }
    
    public String getServerAddress() {
        return this.serverAddress;
    }
    
    public int getServerPort() {
        return this.serverPort;
    }
    
    public HealthCheckState getHealthCheckState() {
        return this.healthCheckState;
    }
    
    @Override
    public String toString() {
        final StringBuilder buffer = new StringBuilder();
        this.toString(buffer);
        return buffer.toString();
    }
    
    public void toString(final StringBuilder buffer) {
        buffer.append("LoadBalancingAlgorithmServerAvailabilityData(address=");
        buffer.append(this.serverAddress);
        buffer.append(", port=");
        buffer.append(this.serverPort);
        buffer.append(", healthCheckState=");
        buffer.append(this.healthCheckState.name());
        buffer.append(')');
    }
    
    public String toCompactString() {
        return this.serverAddress + ':' + this.serverPort + ':' + this.healthCheckState.name();
    }
}
