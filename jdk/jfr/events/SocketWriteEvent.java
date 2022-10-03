package jdk.jfr.events;

import jdk.jfr.DataAmount;
import jdk.jfr.Description;
import jdk.jfr.Category;
import jdk.jfr.Label;
import jdk.jfr.Name;

@Name("jdk.SocketWrite")
@Label("Socket Write")
@Category({ "Java Application" })
@Description("Writing data to a socket")
public final class SocketWriteEvent extends AbstractJDKEvent
{
    public static final ThreadLocal<SocketWriteEvent> EVENT;
    @Label("Remote Host")
    public String host;
    @Label("Remote Address")
    public String address;
    @Label("Remote Port")
    public int port;
    @Label("Bytes Written")
    @Description("Number of bytes written to the socket")
    @DataAmount
    public long bytesWritten;
    
    public void reset() {
        this.host = null;
        this.address = null;
        this.port = 0;
        this.bytesWritten = 0L;
    }
    
    static {
        EVENT = new ThreadLocal<SocketWriteEvent>() {
            @Override
            protected SocketWriteEvent initialValue() {
                return new SocketWriteEvent();
            }
        };
    }
}
