package com.me.mdm.server.datausage.data;

import java.util.Iterator;
import java.util.ArrayList;
import java.util.List;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.Criteria;

public class DataEntityCriteria
{
    Criteria finalCriteria;
    
    public DataEntityCriteria(final Object dataEntity, final int operator) {
        switch (operator) {
            case 0: {
                final Criteria startCriteria = new Criteria(Column.getColumn("DataEntity", "ENTITY_IDENTIFIER"), (Object)((DataEntity)dataEntity).identifier, 0);
                final Criteria endCriteria = new Criteria(Column.getColumn("DataEntity", "ENTITY_TYPE"), (Object)((DataEntity)dataEntity).type, 0);
                this.finalCriteria = startCriteria.and(endCriteria);
                break;
            }
            case 8: {
                final List<DataEntity> list = (List<DataEntity>)dataEntity;
                final List identifiers = new ArrayList();
                for (final DataEntity entity : list) {
                    identifiers.add(entity.identifier);
                    this.finalCriteria = new Criteria(Column.getColumn("DataEntity", "ENTITY_IDENTIFIER"), (Object)identifiers.toArray(), 8);
                }
                break;
            }
        }
    }
    
    public Criteria getFinalCriteria() {
        return this.finalCriteria;
    }
}
