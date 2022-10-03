package com.me.devicemanagement.onpremise.server.queue;

import com.adventnet.persistence.DataAccessException;
import com.adventnet.persistence.DataAccess;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.me.devicemanagement.framework.server.queue.DCQueueMetaData;
import com.me.devicemanagement.framework.server.queue.DCQueueData;
import java.io.File;
import java.io.Reader;
import java.io.BufferedReader;
import java.io.FileReader;
import com.me.devicemanagement.framework.server.queue.DCQueueHelper;

public class DefaultQueueHelper implements DCQueueHelper
{
    public String readFile(final String filePath) throws Exception {
        final FileReader fr = new FileReader(filePath);
        final BufferedReader br = new BufferedReader(fr);
        String record = "";
        final StringBuilder buffer = new StringBuilder();
        try {
            while ((record = br.readLine()) != null) {
                buffer.append(record);
                buffer.append("\n");
            }
        }
        finally {
            fr.close();
            br.close();
        }
        return buffer.toString();
    }
    
    public boolean deleteFile(final String filePath) {
        final File dataFile = new File(filePath);
        return dataFile.delete();
    }
    
    public void deleteDBEntry(final DCQueueData dcQData, final boolean isFileDeleted, final DCQueueMetaData qMetadata) throws DataAccessException {
        Criteria criteria = null;
        if (dcQData.queueDataId != null) {
            final Column qInfoIdCol = Column.getColumn(qMetadata.queueTableName, "QINFO_ID");
            criteria = new Criteria(qInfoIdCol, (Object)dcQData.queueDataId, 0);
        }
        else {
            final Column fileNameCol = Column.getColumn(qMetadata.queueTableName, "DATA_FILE_NAME");
            criteria = new Criteria(fileNameCol, (Object)dcQData.fileName, 0, false);
        }
        DataAccess.delete(criteria);
        QueueDataMETracking.decrementTrackingMap(qMetadata.queueName);
    }
    
    public String unCompressString(final DCQueueData dcQData) {
        return (String)dcQData.queueData;
    }
}
