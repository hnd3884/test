package com.me.ems.onpremise.summaryserver.common;

import com.adventnet.persistence.PersistenceException;
import java.util.logging.Level;
import java.util.logging.Logger;
import com.adventnet.db.persistence.LongSequenceGenerator;

public class CustomerInfoLongSeqGenerator extends LongSequenceGenerator
{
    private static Logger out;
    
    public void init(final String name) throws PersistenceException {
        super.name = name;
        if (name == null || name.trim().length() == 0) {
            throw new IllegalArgumentException("SeqName cannot be null or empty");
        }
        final String seqGenStartValue = SSCommonDetails.getSpecificValue("CustSeqGenStartValue");
        if (seqGenStartValue != null) {
            this.startValue = Long.parseLong(seqGenStartValue);
        }
        final long sv = (long)this.getStartValue();
        final long mv = (long)super.getMaxValue();
        if (sv > mv) {
            throw new IllegalArgumentException("startValue :: [" + sv + "] cannot be more than maxValue :: [" + mv + "] in the sequence generator :: [" + name + "]");
        }
        if (LongSequenceGenerator.seqApi == null) {
            LongSequenceGenerator.initSeqApi();
        }
        super.getNextBatch();
        CustomerInfoLongSeqGenerator.out.log(Level.FINEST, "{0} : after init", this);
    }
    
    static {
        CustomerInfoLongSeqGenerator.out = Logger.getLogger(CustomerInfoLongSeqGenerator.class.getName());
    }
}
