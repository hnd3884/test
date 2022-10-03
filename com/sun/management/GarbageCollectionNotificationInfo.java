package com.sun.management;

import javax.management.openmbean.CompositeType;
import sun.management.GarbageCollectionNotifInfoCompositeData;
import javax.management.openmbean.CompositeData;
import jdk.Exported;
import javax.management.openmbean.CompositeDataView;

@Exported
public class GarbageCollectionNotificationInfo implements CompositeDataView
{
    private final String gcName;
    private final String gcAction;
    private final String gcCause;
    private final GcInfo gcInfo;
    private final CompositeData cdata;
    public static final String GARBAGE_COLLECTION_NOTIFICATION = "com.sun.management.gc.notification";
    
    public GarbageCollectionNotificationInfo(final String gcName, final String gcAction, final String gcCause, final GcInfo gcInfo) {
        if (gcName == null) {
            throw new NullPointerException("Null gcName");
        }
        if (gcAction == null) {
            throw new NullPointerException("Null gcAction");
        }
        if (gcCause == null) {
            throw new NullPointerException("Null gcCause");
        }
        this.gcName = gcName;
        this.gcAction = gcAction;
        this.gcCause = gcCause;
        this.gcInfo = gcInfo;
        this.cdata = new GarbageCollectionNotifInfoCompositeData(this);
    }
    
    GarbageCollectionNotificationInfo(final CompositeData cdata) {
        GarbageCollectionNotifInfoCompositeData.validateCompositeData(cdata);
        this.gcName = GarbageCollectionNotifInfoCompositeData.getGcName(cdata);
        this.gcAction = GarbageCollectionNotifInfoCompositeData.getGcAction(cdata);
        this.gcCause = GarbageCollectionNotifInfoCompositeData.getGcCause(cdata);
        this.gcInfo = GarbageCollectionNotifInfoCompositeData.getGcInfo(cdata);
        this.cdata = cdata;
    }
    
    public String getGcName() {
        return this.gcName;
    }
    
    public String getGcAction() {
        return this.gcAction;
    }
    
    public String getGcCause() {
        return this.gcCause;
    }
    
    public GcInfo getGcInfo() {
        return this.gcInfo;
    }
    
    public static GarbageCollectionNotificationInfo from(final CompositeData compositeData) {
        if (compositeData == null) {
            return null;
        }
        if (compositeData instanceof GarbageCollectionNotifInfoCompositeData) {
            return ((GarbageCollectionNotifInfoCompositeData)compositeData).getGarbageCollectionNotifInfo();
        }
        return new GarbageCollectionNotificationInfo(compositeData);
    }
    
    @Override
    public CompositeData toCompositeData(final CompositeType compositeType) {
        return this.cdata;
    }
}
