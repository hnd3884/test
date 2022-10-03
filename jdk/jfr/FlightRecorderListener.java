package jdk.jfr;

import jdk.Exported;

@Exported
public interface FlightRecorderListener
{
    default void recorderInitialized(final FlightRecorder flightRecorder) {
    }
    
    default void recordingStateChanged(final Recording recording) {
    }
}
