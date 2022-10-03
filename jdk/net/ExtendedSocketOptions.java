package jdk.net;

import java.net.SocketOption;
import jdk.Exported;

@Exported
public final class ExtendedSocketOptions
{
    public static final SocketOption<SocketFlow> SO_FLOW_SLA;
    public static final SocketOption<Integer> TCP_KEEPIDLE;
    public static final SocketOption<Integer> TCP_KEEPINTERVAL;
    public static final SocketOption<Integer> TCP_KEEPCOUNT;
    
    private ExtendedSocketOptions() {
    }
    
    static {
        SO_FLOW_SLA = new ExtSocketOption<SocketFlow>("SO_FLOW_SLA", SocketFlow.class);
        TCP_KEEPIDLE = new ExtSocketOption<Integer>("TCP_KEEPIDLE", Integer.class);
        TCP_KEEPINTERVAL = new ExtSocketOption<Integer>("TCP_KEEPINTERVAL", Integer.class);
        TCP_KEEPCOUNT = new ExtSocketOption<Integer>("TCP_KEEPCOUNT", Integer.class);
    }
    
    private static class ExtSocketOption<T> implements SocketOption<T>
    {
        private final String name;
        private final Class<T> type;
        
        ExtSocketOption(final String name, final Class<T> type) {
            this.name = name;
            this.type = type;
        }
        
        @Override
        public String name() {
            return this.name;
        }
        
        @Override
        public Class<T> type() {
            return this.type;
        }
        
        @Override
        public String toString() {
            return this.name;
        }
    }
}
