package sun.nio.ch;

import java.net.SocketOption;

class ExtendedSocketOption
{
    static final SocketOption<Boolean> SO_OOBINLINE;
    
    private ExtendedSocketOption() {
    }
    
    static {
        SO_OOBINLINE = new SocketOption<Boolean>() {
            @Override
            public String name() {
                return "SO_OOBINLINE";
            }
            
            @Override
            public Class<Boolean> type() {
                return Boolean.class;
            }
            
            @Override
            public String toString() {
                return this.name();
            }
        };
    }
}
