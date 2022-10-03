package com.me.devicemanagement.onpremise.server.sensitive.data;

import java.util.ArrayList;
import java.util.Iterator;
import com.adventnet.sym.logging.SensitiveFileHandler;
import java.util.logging.Level;
import java.util.HashSet;
import java.util.logging.Logger;
import java.util.List;

public class SensitiveDataHandler
{
    private static List<SensitiveData> sensitiveDataClassList;
    private static SensitiveDataHandler sensitiveDataHandler;
    private Logger logger;
    private HashSet hashSet;
    
    public SensitiveDataHandler() {
        this.logger = Logger.getLogger(SensitiveDataHandler.class.getName());
        this.hashSet = new HashSet();
    }
    
    public static SensitiveDataHandler getInstance() {
        if (SensitiveDataHandler.sensitiveDataHandler == null) {
            SensitiveDataHandler.sensitiveDataHandler = new SensitiveDataHandler();
        }
        return SensitiveDataHandler.sensitiveDataHandler;
    }
    
    private void invokeSensitiveDataListeners() {
        final int l = SensitiveDataHandler.sensitiveDataClassList.size();
        this.logger.log(Level.INFO, "invokeSensitiveDataListeners() called : {0}", SensitiveDataHandler.sensitiveDataClassList.toString());
        this.hashSet.clear();
        for (int s = 0; s < l; ++s) {
            final SensitiveData listener = SensitiveDataHandler.sensitiveDataClassList.get(s);
            listener.collectDataList().parallelStream().filter(obj -> obj != null && !obj.equals("") && obj.toString().length() >= 3).forEach(obj -> this.hashSet.add(obj));
        }
    }
    
    public void initiate() {
        this.invokeSensitiveDataListeners();
        final String[] matchList = new String[this.hashSet.size()];
        int i = 0;
        for (final Object object : this.hashSet) {
            matchList[i++] = (String)object;
        }
        SensitiveFileHandler.initiateDataProvider(matchList, SensitiveDataUtil.getSensitiveProps());
        this.removeDataList();
    }
    
    public void removeDataList() {
        if (this.hashSet != null) {
            this.hashSet.clear();
        }
    }
    
    public void addSensitiveDataListener(final SensitiveData listener) {
        this.logger.log(Level.INFO, "addSensitiveDataListener() called : {0}", listener.getClass().getName());
        SensitiveDataHandler.sensitiveDataClassList.add(listener);
    }
    
    public void removeSensitiveDataListener(final SensitiveData listener) {
        this.logger.log(Level.INFO, "removeSensitiveDataListener() called : {0}", listener.getClass().getName());
        SensitiveDataHandler.sensitiveDataClassList.remove(listener);
    }
    
    static {
        SensitiveDataHandler.sensitiveDataClassList = new ArrayList<SensitiveData>();
        SensitiveDataHandler.sensitiveDataHandler = null;
    }
}
