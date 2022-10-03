package com.me.mdm.server.datausage.data;

import java.util.Iterator;
import java.util.List;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.Criteria;

public class DataPeriodCriteria
{
    Criteria finalCriteria;
    
    public DataPeriodCriteria(final Object dataPeriod, final int operator) {
        switch (operator) {
            case 0: {
                final Criteria startCriteria = new Criteria(Column.getColumn("DataTrackingPeriods", "PERIOD_START_TIME"), (Object)((DataPeriod)dataPeriod).startTime, 0);
                final Criteria endCriteria = new Criteria(Column.getColumn("DataTrackingPeriods", "PERIOD_END_TIME"), (Object)((DataPeriod)dataPeriod).endTime, 0);
                this.finalCriteria = startCriteria.and(endCriteria);
                break;
            }
            case 8: {
                final List<DataPeriod> list = (List<DataPeriod>)dataPeriod;
                for (final DataPeriod period : list) {
                    final Criteria sCriteria = new Criteria(Column.getColumn("DataTrackingPeriods", "PERIOD_START_TIME"), (Object)period.startTime, 0);
                    final Criteria eCriteria = new Criteria(Column.getColumn("DataTrackingPeriods", "PERIOD_END_TIME"), (Object)period.endTime, 0);
                    if (this.finalCriteria == null) {
                        this.finalCriteria = sCriteria.and(eCriteria);
                    }
                    else {
                        this.finalCriteria = this.finalCriteria.or(sCriteria.and(eCriteria));
                    }
                }
                break;
            }
            case 7: {
                final DataPeriod comparingPeriod = (DataPeriod)dataPeriod;
                this.finalCriteria = new Criteria(Column.getColumn("DataTrackingPeriods", "PERIOD_START_TIME"), (Object)comparingPeriod.endTime, 6);
                break;
            }
        }
    }
    
    public Criteria getFinalCriteria() {
        return this.finalCriteria;
    }
}
