package com.me.devicemanagement.framework.server.deletionfw;

enum PERSISTENCE_DELETION
{
    TRUE("1"), 
    FALSE("0");
    
    final String value;
    
    private PERSISTENCE_DELETION(final String value) {
        this.value = value;
    }
    
    public PERSISTENCE_DELETION getCompletedStatus(final String value) {
        for (final PERSISTENCE_DELETION o1 : values()) {
            if (o1.value.equals(value)) {
                return o1;
            }
        }
        return null;
    }
    
    public boolean equals(final PERSISTENCE_DELETION o2) {
        return this.value.equals(o2.value);
    }
    
    public boolean equals(final String value) {
        return this.value.equals(value);
    }
    
    @Override
    public String toString() {
        return this.value;
    }
}
