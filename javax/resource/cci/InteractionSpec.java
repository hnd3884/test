package javax.resource.cci;

import java.io.Serializable;

public interface InteractionSpec extends Serializable
{
    public static final int SYNC_SEND = 0;
    public static final int SYNC_SEND_RECEIVE = 1;
    public static final int SYNC_RECEIVE = 2;
}
