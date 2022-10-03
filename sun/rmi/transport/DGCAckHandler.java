package sun.rmi.transport;

import java.util.Collections;
import java.util.HashMap;
import sun.rmi.runtime.RuntimeUtil;
import java.security.PrivilegedAction;
import java.security.AccessController;
import sun.security.action.GetLongAction;
import java.util.concurrent.TimeUnit;
import java.util.ArrayList;
import java.util.concurrent.Future;
import java.util.List;
import java.rmi.server.UID;
import java.util.Map;
import java.util.concurrent.ScheduledExecutorService;

public class DGCAckHandler
{
    private static final long dgcAckTimeout;
    private static final ScheduledExecutorService scheduler;
    private static final Map<UID, DGCAckHandler> idTable;
    private final UID id;
    private List<Object> objList;
    private Future<?> task;
    
    DGCAckHandler(final UID id) {
        this.objList = new ArrayList<Object>();
        this.task = null;
        this.id = id;
        if (id != null) {
            assert !DGCAckHandler.idTable.containsKey(id);
            DGCAckHandler.idTable.put(id, this);
        }
    }
    
    synchronized void add(final Object o) {
        if (this.objList != null) {
            this.objList.add(o);
        }
    }
    
    synchronized void startTimer() {
        if (this.objList != null && this.task == null) {
            this.task = DGCAckHandler.scheduler.schedule(new Runnable() {
                @Override
                public void run() {
                    if (DGCAckHandler.this.id != null) {
                        DGCAckHandler.idTable.remove(DGCAckHandler.this.id);
                    }
                    DGCAckHandler.this.release();
                }
            }, DGCAckHandler.dgcAckTimeout, TimeUnit.MILLISECONDS);
        }
    }
    
    synchronized void release() {
        if (this.task != null) {
            this.task.cancel(false);
            this.task = null;
        }
        this.objList = null;
    }
    
    public static void received(final UID uid) {
        final DGCAckHandler dgcAckHandler = DGCAckHandler.idTable.remove(uid);
        if (dgcAckHandler != null) {
            dgcAckHandler.release();
        }
    }
    
    static {
        dgcAckTimeout = AccessController.doPrivileged((PrivilegedAction<Long>)new GetLongAction("sun.rmi.dgc.ackTimeout", 300000L));
        scheduler = AccessController.doPrivileged((PrivilegedAction<RuntimeUtil>)new RuntimeUtil.GetInstanceAction()).getScheduler();
        idTable = Collections.synchronizedMap(new HashMap<UID, DGCAckHandler>());
    }
}
