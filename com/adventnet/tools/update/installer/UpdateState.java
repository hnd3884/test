package com.adventnet.tools.update.installer;

import java.util.Iterator;
import com.adventnet.tools.update.CommonUtil;
import java.util.Collection;
import com.adventnet.tools.update.UpdateManagerConts;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.logging.Logger;

public class UpdateState
{
    private static Logger out;
    private LinkedHashMap<String, Long> timeTaken;
    private LinkedHashMap<String, Long> startTime;
    private LinkedHashMap<String, Integer> exeStatus;
    private ArrayList<String> preInstall;
    private ArrayList<String> postInstall;
    private int previousStateCode;
    private int stateCode;
    private int errorCode;
    private long previousActionStartTime;
    private long currentActionStartTime;
    private String currentClassInExecution;
    private String previousClassExecuted;
    
    public UpdateState() {
        this.timeTaken = null;
        this.startTime = null;
        this.exeStatus = null;
        this.preInstall = null;
        this.postInstall = null;
        this.previousStateCode = 0;
        this.stateCode = 0;
        this.errorCode = 0;
        this.previousActionStartTime = 0L;
        this.currentActionStartTime = 0L;
        this.currentClassInExecution = null;
        this.previousClassExecuted = null;
        this.timeTaken = new LinkedHashMap<String, Long>();
        this.startTime = new LinkedHashMap<String, Long>();
        this.exeStatus = new LinkedHashMap<String, Integer>();
        this.stateCode = 0;
        this.previousStateCode = 0;
        this.errorCode = 0;
        this.previousActionStartTime = 0L;
        this.currentActionStartTime = 0L;
        this.currentClassInExecution = null;
        this.previousClassExecuted = null;
        this.preInstall = new ArrayList<String>();
        this.postInstall = new ArrayList<String>();
    }
    
    public int getCurrentState() {
        return this.stateCode;
    }
    
    public void setCurrentState(final int statecode, final long start) {
        this.previousStateCode = this.stateCode;
        this.stateCode = statecode;
        this.previousActionStartTime = this.currentActionStartTime;
        this.currentActionStartTime = start;
        this.startTime.put(UpdateManagerConts.getType(statecode), start);
        if (this.previousActionStartTime != 0L) {
            this.timeTaken.put(UpdateManagerConts.getType(this.previousStateCode), this.currentActionStartTime - this.previousActionStartTime);
        }
    }
    
    public void setErrorCode(final int errorCode) {
        this.errorCode = errorCode;
    }
    
    public int getErrorCode() {
        return this.errorCode;
    }
    
    public void setCurrentPrePostClassInProgress(final String className, final long startTime) {
        this.previousClassExecuted = this.currentClassInExecution;
        this.currentClassInExecution = className;
        this.startTime.put(className, startTime);
    }
    
    public String getCurrentPrePostClassInProgress() {
        return this.currentClassInExecution;
    }
    
    public String getPreviousClassExecuted() {
        return this.previousClassExecuted;
    }
    
    public void setCurrentClassStats(final boolean errorCode, final long endTime) {
        this.timeTaken.put(this.currentClassInExecution, endTime - this.startTime.get(this.currentClassInExecution));
        if (errorCode) {
            this.exeStatus.put(this.currentClassInExecution, 1);
        }
        else {
            this.exeStatus.put(this.currentClassInExecution, 0);
        }
        if (this.stateCode == 10) {
            this.preInstall.add(this.currentClassInExecution);
        }
        else {
            this.postInstall.add(this.currentClassInExecution);
        }
    }
    
    public long getTimeTakenToCompletion(final String className) {
        return this.timeTaken.get(className);
    }
    
    public int getPrePostExecutionStatus(final String className) {
        return this.exeStatus.get(className);
    }
    
    public ArrayList<String> getAllCompletedClasses() {
        ArrayList<String> temp = new ArrayList<String>();
        temp = (ArrayList)this.preInstall.clone();
        temp.addAll(this.postInstall);
        return temp;
    }
    
    public ArrayList<String> getCompletedPreClasses() {
        return this.preInstall;
    }
    
    public ArrayList<String> getCompletedPostClasses() {
        return this.postInstall;
    }
    
    public void printStates() {
        try {
            int longKey = 0;
            for (final String tempKey : this.timeTaken.keySet()) {
                if (tempKey.length() > longKey) {
                    longKey = tempKey.length();
                }
            }
            longKey += 10;
            UpdateState.out.info(String.format("%s %" + (longKey - CommonUtil.getString("State").length()) + "s %10s", CommonUtil.getString("State"), CommonUtil.getString("Time taken"), CommonUtil.getString("Execution status") + "\n"));
            for (String propsKey : this.timeTaken.keySet()) {
                String status = "-";
                if (this.exeStatus.get(propsKey) != null && this.exeStatus.get(propsKey) == 0) {
                    status = "failed";
                }
                else if (this.exeStatus.get(propsKey) != null && this.exeStatus.get(propsKey) == 1) {
                    status = "success";
                }
                final String timeStr = Long.toString(this.timeTaken.get(propsKey));
                if (this.preInstall.indexOf(propsKey) >= 0) {
                    propsKey = propsKey.substring(propsKey.lastIndexOf("/") + 1);
                }
                else if (this.postInstall.indexOf(propsKey) >= 0) {
                    propsKey = propsKey.substring(propsKey.lastIndexOf("/") + 1);
                }
                UpdateState.out.info(String.format("%s %" + (longKey - propsKey.length()) + "s %10s", propsKey, timeStr, CommonUtil.getString(status) + "\n"));
            }
        }
        catch (final Exception e) {
            e.printStackTrace();
            UpdateState.out.info(CommonUtil.getString("Exception while printing stats") + " " + e.getMessage());
        }
    }
    
    static {
        UpdateState.out = Logger.getLogger(UpdateState.class.getName());
    }
}
