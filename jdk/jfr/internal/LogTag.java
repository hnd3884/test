package jdk.jfr.internal;

public enum LogTag
{
    JFR(0), 
    JFR_SYSTEM(1), 
    JFR_SYSTEM_EVENT(2), 
    JFR_SYSTEM_SETTING(3), 
    JFR_SYSTEM_BYTECODE(4), 
    JFR_SYSTEM_PARSER(5), 
    JFR_SYSTEM_METADATA(6), 
    JFR_METADATA(7), 
    JFR_EVENT(8), 
    JFR_SETTING(9), 
    JFR_DCMD(10);
    
    volatile int tagSetLevel;
    final int id;
    
    private LogTag(final int id) {
        this.tagSetLevel = 100;
        this.id = id;
    }
}
