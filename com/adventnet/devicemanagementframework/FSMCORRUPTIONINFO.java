package com.adventnet.devicemanagementframework;

public final class FSMCORRUPTIONINFO
{
    public static final String TABLE = "FSMCorruptionInfo";
    public static final String FSM_ID = "FSM_ID";
    public static final int FSM_ID_IDX = 1;
    public static final String FSM_CORRUPTED_TABLENAME = "FSM_CORRUPTED_TABLENAME";
    public static final int FSM_CORRUPTED_TABLENAME_IDX = 2;
    public static final String FSM_CORRUPTED_FINDING_DATE = "FSM_CORRUPTED_FINDING_DATE";
    public static final int FSM_CORRUPTED_FINDING_DATE_IDX = 3;
    public static final String IS_FSM_CORRUPTION_HANDLED = "IS_FSM_CORRUPTION_HANDLED";
    public static final int IS_FSM_CORRUPTION_HANDLED_IDX = 4;
    public static final String FSM_CORRUPTION_COUNT = "FSM_CORRUPTION_COUNT";
    public static final int FSM_CORRUPTION_COUNT_IDX = 5;
    
    private FSMCORRUPTIONINFO() {
    }
}
