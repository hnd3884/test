package com.me.ems.summaryserver.listener;

import java.util.ArrayList;
import java.util.List;

public class ProbeMgmtListenerUtil
{
    private static ProbeMgmtListenerUtil probeMgmtListenerUtil;
    private static List probeMgmtListenerList;
    
    public static ProbeMgmtListenerUtil getInstance() {
        if (ProbeMgmtListenerUtil.probeMgmtListenerUtil == null) {
            ProbeMgmtListenerUtil.probeMgmtListenerUtil = new ProbeMgmtListenerUtil();
        }
        return ProbeMgmtListenerUtil.probeMgmtListenerUtil;
    }
    
    public void addProbeMgmtListener(final ProbeMgmtListener listener) {
        ProbeMgmtListenerUtil.probeMgmtListenerList.add(listener);
    }
    
    public static void removeProbeMgmtListener(final ProbeMgmtListener listener) {
        ProbeMgmtListenerUtil.probeMgmtListenerList.remove(listener);
    }
    
    public void invokeProbeMgmtListeners(final ProbeMgmtEvent probeMgmtEvent, final int operation) {
        final int listenerCount = ProbeMgmtListenerUtil.probeMgmtListenerList.size();
        if (operation == 1) {
            for (int i = 0; i < listenerCount; ++i) {
                final ProbeMgmtListener listener = ProbeMgmtListenerUtil.probeMgmtListenerList.get(i);
                listener.probeAdded(probeMgmtEvent);
            }
        }
        else if (operation == 2) {
            for (int i = 0; i < listenerCount; ++i) {
                final ProbeMgmtListener listener = ProbeMgmtListenerUtil.probeMgmtListenerList.get(i);
                listener.probeInstalled(probeMgmtEvent);
            }
        }
        else if (operation == 3) {
            for (int i = 0; i < listenerCount; ++i) {
                final ProbeMgmtListener listener = ProbeMgmtListenerUtil.probeMgmtListenerList.get(i);
                listener.probeModified(probeMgmtEvent);
            }
        }
        else if (operation == 4) {
            for (int i = 0; i < listenerCount; ++i) {
                final ProbeMgmtListener listener = ProbeMgmtListenerUtil.probeMgmtListenerList.get(i);
                listener.probeDeleted(probeMgmtEvent);
            }
        }
        else if (operation == 5) {
            for (int i = 0; i < listenerCount; ++i) {
                final ProbeMgmtListener listener = ProbeMgmtListenerUtil.probeMgmtListenerList.get(i);
                listener.probeBackToLive(probeMgmtEvent);
            }
        }
    }
    
    static {
        ProbeMgmtListenerUtil.probeMgmtListenerUtil = null;
        ProbeMgmtListenerUtil.probeMgmtListenerList = new ArrayList(5);
    }
}
