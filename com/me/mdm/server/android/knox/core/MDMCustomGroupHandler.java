package com.me.mdm.server.android.knox.core;

import com.adventnet.sym.server.mdm.group.GroupEvent;
import java.util.ArrayList;
import com.adventnet.sym.server.mdm.group.MDMCustomGroupListner;
import java.util.List;
import java.util.logging.Logger;

public class MDMCustomGroupHandler
{
    Logger logger;
    String sourceClass;
    private static MDMCustomGroupHandler deviceHandler;
    public static final int GROUP_ADDED = 1;
    public static final int GROUP_REMOVED = 2;
    public static final int GROUP_MODIFIED = 3;
    public static final int GROUP_PRE_REMOVAL = 4;
    private List<MDMCustomGroupListner> groupListenerList;
    
    public static MDMCustomGroupHandler getInstance() {
        if (MDMCustomGroupHandler.deviceHandler == null) {
            MDMCustomGroupHandler.deviceHandler = new MDMCustomGroupHandler();
        }
        return MDMCustomGroupHandler.deviceHandler;
    }
    
    private MDMCustomGroupHandler() {
        this.logger = Logger.getLogger("MDMLogger");
        this.sourceClass = MDMCustomGroupHandler.class.getName();
        this.groupListenerList = new ArrayList<MDMCustomGroupListner>();
    }
    
    public void addCustomGroupListener(final MDMCustomGroupListner customGroupListener) {
        this.groupListenerList.add(customGroupListener);
    }
    
    public void invokeGroupListeners(final GroupEvent groupEvent, final int operation) {
        final int l = this.groupListenerList.size();
        if (operation == 1) {
            for (int s = 0; s < l; ++s) {
                final MDMCustomGroupListner listener = this.groupListenerList.get(s);
                listener.customGroupAdded(groupEvent);
            }
        }
        else if (operation == 2) {
            for (int s = 0; s < l; ++s) {
                final MDMCustomGroupListner listener = this.groupListenerList.get(s);
                listener.customGroupDeleted(groupEvent);
            }
        }
        else if (operation == 3) {
            for (int s = 0; s < l; ++s) {
                final MDMCustomGroupListner listener = this.groupListenerList.get(s);
                listener.customGroupModified(groupEvent);
            }
        }
        else if (operation == 4) {
            for (int s = 0; s < l; ++s) {
                final MDMCustomGroupListner listener = this.groupListenerList.get(s);
                listener.customGroupPreDelete(groupEvent);
            }
        }
    }
    
    static {
        MDMCustomGroupHandler.deviceHandler = null;
    }
}
