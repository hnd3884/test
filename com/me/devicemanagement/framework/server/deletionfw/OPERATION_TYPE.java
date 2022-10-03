package com.me.devicemanagement.framework.server.deletionfw;

enum OPERATION_TYPE
{
    DEPENDENT_DATA_DELETION(1001), 
    DELETE_TABLE_WITH_CRITERIA(1002), 
    DELETE_BY_OPTIMAL_GROUPING(1003), 
    DELETE_MULTIPLE_TABLE_WITH_SAME_CRITERIA(1004), 
    DELETE_DUPLICATE_DATA_BY_GROUPING(1005), 
    DELETE_WITH_DELETE_QUERY(1006), 
    DELETE_WITH_SELECT_QUERY(1007), 
    INDEPENDENT_DATA_CLEANUP(1008), 
    ADDITION_PRE_HANDLING(1009), 
    ORPHAN_COUNT_INFO(2001), 
    ORPHAN_CLEANUP_INFO(2002), 
    HISTORY_CLEANUP_INFO(2003);
    
    final int id;
    
    private OPERATION_TYPE(final int id) {
        this.id = id;
    }
    
    public static OPERATION_TYPE getOperationType(final Number id) {
        return (id != null) ? getOperationType(id.intValue()) : null;
    }
    
    public static OPERATION_TYPE getOperationType(final int id) {
        for (final OPERATION_TYPE type : values()) {
            if (type.id == id) {
                return type;
            }
        }
        return null;
    }
    
    public boolean equals(final OPERATION_TYPE o2) {
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
