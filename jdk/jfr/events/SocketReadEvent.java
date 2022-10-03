package jdk.jfr.events;

import jdk.jfr.DataAmount;
import jdk.jfr.Timespan;
import jdk.jfr.Description;
import jdk.jfr.Category;
import jdk.jfr.Label;
import jdk.jfr.Name;

@Name("jdk.SocketRead")
@Label("Socket Read")
@Category({ "Java Application" })
@Description("Reading data from a socket")
public final class SocketReadEvent extends AbstractJDKEvent
{
    public static final ThreadLocal<SocketReadEvent> EVENT;
    @Label("Remote Host")
    public String host;
    @Label("Remote Address")
    public String address;
    @Label("Remote Port")
    public int port;
    @Label("Timeout Value")
    @Timespan("MILLISECONDS")
    public long timeout;
    @Label("Bytes Read")
    @Description("Number of bytes read from the socket")
    @DataAmount
    public long bytesRead;
    @Label("End of Stream")
    @Description("If end of stream was reached")
    public boolean endOfStream;
    
    public void reset() {
        this.host = null;
        this.address = null;
        this.port = 0;
        this.timeout = 0L;
        this.bytesRead = 0L;
        this.endOfStream = false;
    }
    
    static {
        EVENT = new ThreadLocal<SocketReadEvent>() {
            @Override
            protected SocketReadEvent initialValue() {
                return new SocketReadEvent();
            }
        };
    }
}
