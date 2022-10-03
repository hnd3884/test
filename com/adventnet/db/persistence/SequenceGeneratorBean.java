package com.adventnet.db.persistence;

import com.adventnet.ds.query.UpdateQuery;
import com.adventnet.ds.query.UpdateQueryImpl;
import com.adventnet.persistence.DataAccessException;
import com.adventnet.persistence.DataObject;
import com.adventnet.ds.query.SelectQuery;
import java.util.HashMap;
import com.adventnet.persistence.DataAccess;
import com.adventnet.persistence.QueryConstructor;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.persistence.Row;
import java.util.logging.Level;
import com.adventnet.ds.adapter.mds.DBThreadLocal;
import com.adventnet.persistence.PersistenceInitializer;
import java.util.logging.Logger;

public class SequenceGeneratorBean
{
    private static final int BATCHEND = 1;
    private static final int CURBATCHNUM = 0;
    private static final Logger LOGGER;
    
    public Long[] getNextBatch(final SequenceGenerator seqGen) throws DataAccessException, IllegalStateException {
        Object oldThreadLocal = null;
        if (PersistenceInitializer.isMDS()) {
            oldThreadLocal = DBThreadLocal.get();
            DBThreadLocal.set("default");
        }
        try {
            SequenceGeneratorBean.LOGGER.log(Level.FINER, "getNextBatch called for sequence name [{0}]", seqGen.getName());
            Row r = new Row("SeqGenState");
            r.set(1, seqGen.getName());
            final SelectQuery sq = QueryConstructor.get("SeqGenState", new Criteria(Column.getColumn("SeqGenState", "SEQNAME"), seqGen.getName(), 0));
            DataObject seqNumDo = DataAccess.get(sq);
            Long[] retValue;
            if (!seqNumDo.isEmpty()) {
                SequenceGeneratorBean.LOGGER.log(Level.FINER, "Entry already present for sequence [{0}]", seqGen.getName());
                r = seqNumDo.getFirstRow("SeqGenState");
                final Long curBatchEnd = (Long)r.get(2);
                SequenceGeneratorBean.LOGGER.log(Level.FINER, "Current batch end for  sequence [{0}] is {1}", new Object[] { seqGen.getName(), curBatchEnd });
                retValue = this.getNextValue(curBatchEnd, seqGen);
                r.set(2, retValue[1]);
                seqNumDo.updateRow(r);
                DataAccess.update(seqNumDo);
            }
            else {
                SequenceGeneratorBean.LOGGER.log(Level.FINER, "No Entry present for sequence [{0}]", seqGen.getName());
                seqNumDo = DataAccess.constructDataObject();
                retValue = this.getNextValue(Long.parseLong(seqGen.getStartValue().toString()), seqGen);
                r.set(2, retValue[1]);
                seqNumDo.addRow(r);
                DataAccess.add(seqNumDo);
            }
            SequenceGeneratorBean.LOGGER.log(Level.FINER, "The new batch start for sequence [{0}] is  {1} and end is {2}", new Object[] { seqGen.getName(), retValue[0], retValue[1] });
            return retValue;
        }
        finally {
            if (oldThreadLocal != null) {
                DBThreadLocal.set((HashMap)oldThreadLocal);
            }
        }
    }
    
    public void setBatchEnd(final String seqName, final Long val) throws DataAccessException {
        Object oldThreadLocal = null;
        if (PersistenceInitializer.isMDS()) {
            oldThreadLocal = DBThreadLocal.get();
            DBThreadLocal.set("default");
        }
        try {
            final UpdateQuery uq = new UpdateQueryImpl("SeqGenState");
            uq.setUpdateColumn("CURRENTBATCHEND", val);
            final Criteria criteria = new Criteria(Column.getColumn("SeqGenState", "SEQNAME"), seqName, 0);
            uq.setCriteria(criteria);
            DataAccess.update(uq);
        }
        finally {
            if (oldThreadLocal != null) {
                DBThreadLocal.set((HashMap)oldThreadLocal);
            }
        }
    }
    
    private Long[] getNextValue(final long curBatchEnd, final SequenceGenerator seqGen) {
        final long maxValue = Long.parseLong(seqGen.getMaxValue().toString());
        long nextBatchEnd = curBatchEnd + seqGen.getBatchSize();
        final long nextBatchStart = curBatchEnd + 1L;
        if (maxValue - seqGen.getBatchSize() < curBatchEnd) {
            nextBatchEnd = maxValue;
            if (nextBatchEnd == curBatchEnd) {
                throw new IllegalStateException("Max value exceeded for " + seqGen.getName());
            }
        }
        return new Long[] { new Long(nextBatchStart), new Long(nextBatchEnd) };
    }
    
    public long getBatchSize() {
        return 300L;
    }
    
    public void removeSequence(final String seqName, final String sqlType) throws DataAccessException, IllegalStateException {
        Object oldThreadLocal = null;
        if (PersistenceInitializer.isMDS()) {
            oldThreadLocal = DBThreadLocal.get();
            DBThreadLocal.set("default");
        }
        try {
            SequenceGeneratorBean.LOGGER.log(Level.FINER, "removeSequence called for sequence name [{0}]", seqName);
            if (seqName == null || seqName.equals("")) {
                throw new IllegalArgumentException(" SeqName  cannot be null or empty");
            }
            if (!sqlType.equals("INTEGER") && !sqlType.equals("BIGINT")) {
                throw new IllegalArgumentException("Unkown SqlType : " + sqlType + " for sequence name " + seqName);
            }
            final Row r = new Row("SeqGenState");
            r.set(1, seqName);
            DataAccess.delete(r);
            SequenceGeneratorBean.LOGGER.log(Level.FINER, "removeSequence completed for sequence name [{0}]", seqName);
        }
        finally {
            if (oldThreadLocal != null) {
                DBThreadLocal.set((HashMap)oldThreadLocal);
            }
        }
    }
    
    public void remove() {
    }
    
    public void renameSequenceName(final String oldName, final String newName) throws DataAccessException {
        SequenceGeneratorBean.LOGGER.info("Going to update SeqName '" + oldName + "' to '" + newName + "' in SeqGenState table.");
        final UpdateQuery uq = new UpdateQueryImpl("SeqGenState");
        uq.setUpdateColumn("SEQNAME", newName);
        final Criteria criteria = new Criteria(Column.getColumn("SeqGenState", "SEQNAME"), oldName, 0);
        uq.setCriteria(criteria);
        DataAccess.update(uq);
    }
    
    static {
        LOGGER = Logger.getLogger(SequenceGeneratorBean.class.getName());
    }
}
