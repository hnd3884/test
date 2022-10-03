package com.adventnet.audit;

import java.util.Hashtable;
import com.adventnet.persistence.DataObject;

public interface CustomAuditProvider
{
    DataObject getCustomRecords(final DataObject p0, final Hashtable p1);
}
