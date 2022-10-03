package org.apache.poi.common.usermodel;

import java.util.List;
import java.util.function.Supplier;
import java.util.Map;

public interface GenericRecord
{
    default Enum getGenericRecordType() {
        return null;
    }
    
    Map<String, Supplier<?>> getGenericProperties();
    
    default List<? extends GenericRecord> getGenericChildren() {
        return null;
    }
}
