package sun.management.jdp;

import java.util.Objects;
import java.io.IOException;
import java.util.Map;
import java.util.UUID;

public final class JdpJmxPacket extends JdpGenericPacket implements JdpPacket
{
    public static final String UUID_KEY = "DISCOVERABLE_SESSION_UUID";
    public static final String MAIN_CLASS_KEY = "MAIN_CLASS";
    public static final String JMX_SERVICE_URL_KEY = "JMX_SERVICE_URL";
    public static final String INSTANCE_NAME_KEY = "INSTANCE_NAME";
    public static final String PROCESS_ID_KEY = "PROCESS_ID";
    public static final String RMI_HOSTNAME_KEY = "RMI_HOSTNAME";
    public static final String BROADCAST_INTERVAL_KEY = "BROADCAST_INTERVAL";
    private UUID id;
    private String mainClass;
    private String jmxServiceUrl;
    private String instanceName;
    private String processId;
    private String rmiHostname;
    private String broadcastInterval;
    
    public JdpJmxPacket(final UUID id, final String jmxServiceUrl) {
        this.id = id;
        this.jmxServiceUrl = jmxServiceUrl;
    }
    
    public JdpJmxPacket(final byte[] array) throws JdpException {
        final Map<String, String> discoveryDataAsMap = new JdpPacketReader(array).getDiscoveryDataAsMap();
        final String s = discoveryDataAsMap.get("DISCOVERABLE_SESSION_UUID");
        this.id = ((s == null) ? null : UUID.fromString(s));
        this.jmxServiceUrl = discoveryDataAsMap.get("JMX_SERVICE_URL");
        this.mainClass = discoveryDataAsMap.get("MAIN_CLASS");
        this.instanceName = discoveryDataAsMap.get("INSTANCE_NAME");
        this.processId = discoveryDataAsMap.get("PROCESS_ID");
        this.rmiHostname = discoveryDataAsMap.get("RMI_HOSTNAME");
        this.broadcastInterval = discoveryDataAsMap.get("BROADCAST_INTERVAL");
    }
    
    public void setMainClass(final String mainClass) {
        this.mainClass = mainClass;
    }
    
    public void setInstanceName(final String instanceName) {
        this.instanceName = instanceName;
    }
    
    public UUID getId() {
        return this.id;
    }
    
    public String getMainClass() {
        return this.mainClass;
    }
    
    public String getJmxServiceUrl() {
        return this.jmxServiceUrl;
    }
    
    public String getInstanceName() {
        return this.instanceName;
    }
    
    public String getProcessId() {
        return this.processId;
    }
    
    public void setProcessId(final String processId) {
        this.processId = processId;
    }
    
    public String getRmiHostname() {
        return this.rmiHostname;
    }
    
    public void setRmiHostname(final String rmiHostname) {
        this.rmiHostname = rmiHostname;
    }
    
    public String getBroadcastInterval() {
        return this.broadcastInterval;
    }
    
    public void setBroadcastInterval(final String broadcastInterval) {
        this.broadcastInterval = broadcastInterval;
    }
    
    @Override
    public byte[] getPacketData() throws IOException {
        final JdpPacketWriter jdpPacketWriter = new JdpPacketWriter();
        jdpPacketWriter.addEntry("DISCOVERABLE_SESSION_UUID", (this.id == null) ? null : this.id.toString());
        jdpPacketWriter.addEntry("MAIN_CLASS", this.mainClass);
        jdpPacketWriter.addEntry("JMX_SERVICE_URL", this.jmxServiceUrl);
        jdpPacketWriter.addEntry("INSTANCE_NAME", this.instanceName);
        jdpPacketWriter.addEntry("PROCESS_ID", this.processId);
        jdpPacketWriter.addEntry("RMI_HOSTNAME", this.rmiHostname);
        jdpPacketWriter.addEntry("BROADCAST_INTERVAL", this.broadcastInterval);
        return jdpPacketWriter.getPacketBytes();
    }
    
    @Override
    public int hashCode() {
        return (1 * 31 + this.id.hashCode()) * 31 + this.jmxServiceUrl.hashCode();
    }
    
    @Override
    public boolean equals(final Object o) {
        if (o == null || !(o instanceof JdpJmxPacket)) {
            return false;
        }
        final JdpJmxPacket jdpJmxPacket = (JdpJmxPacket)o;
        return Objects.equals(this.id, jdpJmxPacket.getId()) && Objects.equals(this.jmxServiceUrl, jdpJmxPacket.getJmxServiceUrl());
    }
}
