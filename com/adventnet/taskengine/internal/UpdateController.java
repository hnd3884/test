package com.adventnet.taskengine.internal;

import java.util.ArrayList;
import java.util.Map;
import com.adventnet.ds.query.UpdateQuery;
import java.util.Iterator;
import java.util.Set;
import java.sql.Timestamp;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.UpdateQueryImpl;
import java.util.List;
import java.util.HashMap;
import java.util.logging.Level;
import com.adventnet.mfw.bean.BeanUtil;
import java.util.Hashtable;
import com.adventnet.taskengine.Scheduler;
import java.util.logging.Logger;
import java.util.TimerTask;

public class UpdateController extends TimerTask
{
    private static Logger logger;
    private static Scheduler executorBean;
    private static Hashtable mainIDMap;
    private static Long toleranceLevel;
    
    public UpdateController() {
        try {
            UpdateController.executorBean = (Scheduler)BeanUtil.lookup("ScheduleExecutorWT");
        }
        catch (final Exception exp) {
            exp.printStackTrace();
        }
    }
    
    @Override
    public void run() {
        try {
            this.batchUpdate();
        }
        catch (final Exception excp) {
            UpdateController.logger.log(Level.SEVERE, "", excp);
        }
    }
    
    public void setToleranceLevel(final long level) throws Exception {
        UpdateController.toleranceLevel = level;
    }
    
    public Hashtable getMap() throws Exception {
        return UpdateController.mainIDMap;
    }
    
    public void addToBatch(final long schTime, final Long instanceID) throws Exception {
        final Hashtable mainIDMap = this.getMap();
        mainIDMap.put(instanceID, schTime);
        if (System.getProperty("updatecontroller.disable", "false").equalsIgnoreCase("true")) {
            this.run();
        }
    }
    
    private long getRoundedTime(final long time) {
        final double d = time / (double)UpdateController.toleranceLevel;
        final long a = Math.round(d);
        final long roundTime = a * UpdateController.toleranceLevel;
        return roundTime;
    }
    
    private void updateInDB(final HashMap timeMap) throws Exception {
        final Set bulkSet = timeMap.keySet();
        for (final Long timeInBatch : bulkSet) {
            final List IDList = timeMap.get(timeInBatch);
            Long[] IDs = new Long[IDList.size()];
            IDs = IDList.toArray(new Long[0]);
            final UpdateQuery updateQuery = (UpdateQuery)new UpdateQueryImpl("Task_Input");
            final Column col = Column.getColumn("Task_Input", "INSTANCE_ID");
            final Criteria cri = new Criteria(col, (Object)IDs, 8);
            updateQuery.setCriteria(cri);
            updateQuery.setUpdateColumn("SCHEDULE_TIME", (Object)((timeInBatch != 0L) ? new Timestamp(timeInBatch) : null));
            UpdateController.executorBean.updateNextScheduleTime(updateQuery);
        }
    }
    
    public void batchUpdate() throws Exception {
        final HashMap timeMap = new HashMap();
        final Map mainIDMap = this.getMap();
        final HashMap idMap;
        synchronized (mainIDMap) {
            idMap = new HashMap(mainIDMap);
            mainIDMap.clear();
        }
        if (idMap.isEmpty()) {
            return;
        }
        final Set set = idMap.keySet();
        for (final Long instanceID : set) {
            final Long time = idMap.get(instanceID);
            final Long roundedTime = this.getRoundedTime(time);
            putInMap(timeMap, roundedTime, instanceID);
        }
        this.updateInDB(timeMap);
    }
    
    private static void putInMap(final Map map, final Long roundTime, final Long id) {
        List idList = map.get(roundTime);
        if (idList == null) {
            idList = new ArrayList();
            map.put(roundTime, idList);
        }
        idList.add(id);
    }
    
    static {
        UpdateController.logger = Logger.getLogger(UpdateController.class.getName());
        UpdateController.mainIDMap = new Hashtable();
        UpdateController.toleranceLevel = new Long(2000L);
    }
}
