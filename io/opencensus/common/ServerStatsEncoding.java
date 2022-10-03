package io.opencensus.common;

import java.nio.ByteOrder;
import java.nio.ByteBuffer;

public final class ServerStatsEncoding
{
    public static final byte CURRENT_VERSION = 0;
    
    private ServerStatsEncoding() {
    }
    
    public static byte[] toBytes(final ServerStats stats) {
        final ByteBuffer bb = ByteBuffer.allocate(ServerStatsFieldEnums.getTotalSize() + 1);
        bb.order(ByteOrder.LITTLE_ENDIAN);
        bb.put((byte)0);
        bb.put((byte)ServerStatsFieldEnums.Id.SERVER_STATS_LB_LATENCY_ID.value());
        bb.putLong(stats.getLbLatencyNs());
        bb.put((byte)ServerStatsFieldEnums.Id.SERVER_STATS_SERVICE_LATENCY_ID.value());
        bb.putLong(stats.getServiceLatencyNs());
        bb.put((byte)ServerStatsFieldEnums.Id.SERVER_STATS_TRACE_OPTION_ID.value());
        bb.put(stats.getTraceOption());
        return bb.array();
    }
    
    public static ServerStats parseBytes(final byte[] serialized) throws ServerStatsDeserializationException {
        final ByteBuffer bb = ByteBuffer.wrap(serialized);
        bb.order(ByteOrder.LITTLE_ENDIAN);
        long serviceLatencyNs = 0L;
        long lbLatencyNs = 0L;
        byte traceOption = 0;
        if (!bb.hasRemaining()) {
            throw new ServerStatsDeserializationException("Serialized ServerStats buffer is empty");
        }
        final byte version = bb.get();
        if (version > 0 || version < 0) {
            throw new ServerStatsDeserializationException("Invalid ServerStats version: " + version);
        }
        while (bb.hasRemaining()) {
            final ServerStatsFieldEnums.Id id = ServerStatsFieldEnums.Id.valueOf(bb.get() & 0xFF);
            if (id == null) {
                bb.position(bb.limit());
            }
            else {
                switch (id) {
                    case SERVER_STATS_LB_LATENCY_ID: {
                        lbLatencyNs = bb.getLong();
                        continue;
                    }
                    case SERVER_STATS_SERVICE_LATENCY_ID: {
                        serviceLatencyNs = bb.getLong();
                        continue;
                    }
                    case SERVER_STATS_TRACE_OPTION_ID: {
                        traceOption = bb.get();
                        continue;
                    }
                }
            }
        }
        try {
            return ServerStats.create(lbLatencyNs, serviceLatencyNs, traceOption);
        }
        catch (final IllegalArgumentException e) {
            throw new ServerStatsDeserializationException("Serialized ServiceStats contains invalid values: " + e.getMessage());
        }
    }
}
