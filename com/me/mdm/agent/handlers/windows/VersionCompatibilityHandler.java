package com.me.mdm.agent.handlers.windows;

import java.util.Iterator;
import java.util.List;
import java.util.Collection;
import com.me.mdm.framework.syncml.requestcmds.AtomicRequestCommand;
import com.me.mdm.framework.syncml.requestcmds.SequenceRequestCommand;
import com.me.mdm.framework.syncml.core.SyncMLRequestCommand;

public class VersionCompatibilityHandler
{
    Integer versionClass;
    CommandApplicableInterface commandApplicableInterface;
    public static final Integer LTSB_CLASS;
    public static final Integer WIN11_CLASS;
    
    VersionCompatibilityHandler(final Integer versionClass) {
        this.versionClass = null;
        this.commandApplicableInterface = null;
        this.versionClass = versionClass;
        this.commandApplicableInterface = this.getInstance();
    }
    
    private CommandApplicableInterface getInstance() {
        if (this.versionClass.equals(VersionCompatibilityHandler.LTSB_CLASS)) {
            return new LTSBCommandEvaluator();
        }
        if (this.versionClass.equals(VersionCompatibilityHandler.WIN11_CLASS)) {
            return new Windows11CommandEvaluator();
        }
        return null;
    }
    
    public SyncMLRequestCommand removeNotApplicablePayloads(final SyncMLRequestCommand syncMLRequestCommand) {
        if (this.commandApplicableInterface != null) {
            final List removalList = this.commandApplicableInterface.getRemovalList();
            final List reqItems = ((SequenceRequestCommand)syncMLRequestCommand).getRequestCmds();
            for (SyncMLRequestCommand payload : reqItems) {
                if (payload instanceof AtomicRequestCommand) {
                    payload = ((AtomicRequestCommand)payload).getRequestCmds().get(0);
                }
                final List itemList = payload.getRequestItems();
                itemList.removeAll(removalList);
            }
        }
        return syncMLRequestCommand;
    }
    
    static {
        LTSB_CLASS = 1;
        WIN11_CLASS = 2;
    }
}
