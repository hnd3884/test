package com.me.devicemanagement.framework.server.deletionfw;

enum COMPLETED_STATUS
{
    NOT_STARTED(0), 
    STARTED(1), 
    PARENT_COMPLETED(2), 
    DEPENDENT_DELETION_STARTED(3), 
    SUCCESS(5), 
    FAILED(-5), 
    ABORTED(-10);
    
    final int id;
    
    private COMPLETED_STATUS(final int id) {
        this.id = id;
    }
    
    public static COMPLETED_STATUS getCompletedStatus(final int id) {
        for (final COMPLETED_STATUS type : values()) {
            if (type.id == id) {
                return type;
            }
        }
        return null;
    }
    
    public boolean equals(final COMPLETED_STATUS o2) {
        return this.id == o2.id;
    }
    
    public boolean equals(final int id) {
        return this.id == id;
    }
    
    @Override
    public String toString() {
        return Integer.toString(this.id);
    }
}
