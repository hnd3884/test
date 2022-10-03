package jdk.jfr;

import jdk.Exported;

@Exported
public enum RecordingState
{
    NEW, 
    DELAYED, 
    RUNNING, 
    STOPPED, 
    CLOSED;
}
