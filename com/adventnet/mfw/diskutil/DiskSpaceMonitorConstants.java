package com.adventnet.mfw.diskutil;

public interface DiskSpaceMonitorConstants
{
    public enum DISKSPACE_STATUS
    {
        THRESHOLD_NOT_REACHED(0), 
        CRITICAL_LIMIT_REACHED(1), 
        CRITICAL_LIMIT_EXCEEDED(2), 
        REVALIDATE(3), 
        SHUTDOWN_AND_EXIT(4);
        
        private int message;
        
        private DISKSPACE_STATUS(final int statusCode) {
            this.message = statusCode;
        }
        
        public int getValue() {
            return this.message;
        }
        
        @Override
        public String toString() {
            return String.valueOf(this.message);
        }
    }
    
    public enum TIMEUNIT
    {
        MILLIS(0), 
        SECONDS(1), 
        MINUTES(2), 
        HOURS(3);
        
        private int value;
        
        private TIMEUNIT(final int time_unit) {
            this.value = time_unit;
        }
        
        public int getValue() {
            return this.value;
        }
        
        @Override
        public String toString() {
            return String.valueOf(this.value);
        }
    }
    
    public enum SPACEUNIT
    {
        BYTES(0), 
        MB(1), 
        GB(2);
        
        private int value;
        
        private SPACEUNIT(final int space_unit) {
            this.value = space_unit;
        }
        
        public int getValue() {
            return this.value;
        }
        
        @Override
        public String toString() {
            return String.valueOf(this.value);
        }
    }
}
