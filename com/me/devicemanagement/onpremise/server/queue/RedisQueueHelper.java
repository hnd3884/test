package com.me.devicemanagement.onpremise.server.queue;

import java.util.logging.Level;
import java.io.File;
import com.me.devicemanagement.framework.server.queue.DCQueueData;
import java.io.FileNotFoundException;
import java.io.Reader;
import java.io.BufferedReader;
import java.io.FileReader;
import redis.clients.jedis.Jedis;
import com.me.devicemanagement.onpremise.server.redis.RedisErrorTracker;
import com.me.devicemanagement.framework.server.logger.SyMLogger;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import org.apache.commons.io.FilenameUtils;
import java.util.logging.Logger;
import com.me.devicemanagement.framework.server.queue.DCQueueMetaData;
import com.me.devicemanagement.framework.server.queue.DCQueueHelper;
import com.me.devicemanagement.framework.server.queue.DCQueueConstants;

public class RedisQueueHelper implements DCQueueConstants, DCQueueHelper
{
    private DCQueueMetaData qMetaData;
    private String agentDataLocation;
    private String redisAgentDataFilenameSet;
    private Logger logger;
    private Logger qErrorLogger;
    private String sourceClass;
    
    public RedisQueueHelper(final DCQueueMetaData qMetaDataFromQueue) {
        this.sourceClass = RedisQueueHelper.class.getName();
        this.qMetaData = qMetaDataFromQueue;
        this.agentDataLocation = "AGENTFILELOCATION_" + this.qMetaData.queueName;
        this.redisAgentDataFilenameSet = "FILENAME_" + this.qMetaData.queueName;
        this.logger = Logger.getLogger(this.qMetaData.loggerName);
        this.qErrorLogger = Logger.getLogger(this.qMetaData.qErrorLoggerName);
    }
    
    public String readFile(final String filePath) throws Exception {
        final String sourceMethod = "readFileFromRedis";
        Jedis jedis = null;
        final String fileName = FilenameUtils.getName(filePath);
        String record = null;
        try {
            jedis = ApiFactoryProvider.getRedisQueueAPI().getJedis();
            if (jedis != null) {
                if (jedis.hget(this.agentDataLocation, fileName) != null && jedis.hget(this.agentDataLocation, fileName).equalsIgnoreCase("redis")) {
                    record = this.readFileFromRedis(fileName, jedis);
                }
                else {
                    record = this.readFileFromDisk(filePath);
                }
            }
        }
        catch (final Exception ex) {
            final Boolean isShutdownTriggered = Boolean.valueOf(System.getProperty("isRedisShutdownTriggered"));
            if (!isShutdownTriggered) {
                SyMLogger.error(this.logger, this.sourceClass, sourceMethod, "Caught exception while reading the data from Redis : " + fileName, (Throwable)ex);
                RedisErrorTracker.logRedisErrors(ex);
            }
        }
        finally {
            if (jedis != null) {
                jedis.close();
            }
        }
        return record;
    }
    
    public String readFileFromRedis(final String fileName, final Jedis jedis) {
        final String sourceMethod = "readFileFromRedis";
        String record = "";
        try {
            record = jedis.hget(this.redisAgentDataFilenameSet, fileName);
            SyMLogger.info(this.logger, this.sourceClass, sourceMethod, "Agent data has been read from redis successfully.FileName: " + fileName);
        }
        catch (final Exception ex) {
            final Boolean isShutdownTriggered = Boolean.valueOf(System.getProperty("isRedisShutdownTriggered"));
            if (!isShutdownTriggered) {
                SyMLogger.error(this.logger, this.sourceClass, sourceMethod, "Caught exception while reading data from redis DB: " + fileName, (Throwable)ex);
                RedisErrorTracker.logRedisErrors(ex);
            }
        }
        return record;
    }
    
    public String readFileFromDisk(final String filePath) throws Exception {
        final String sourceMethod = "readFile";
        try {
            final FileReader fr = new FileReader(filePath);
            final BufferedReader br = new BufferedReader(fr);
            String record = "";
            final StringBuffer buffer = new StringBuffer();
            try {
                while ((record = br.readLine()) != null) {
                    buffer.append(record);
                }
                SyMLogger.info(this.logger, this.sourceClass, sourceMethod, "Agent data has been read from file successfully.FilePath: " + filePath);
            }
            catch (final Exception e) {
                SyMLogger.error(this.logger, this.sourceClass, sourceMethod, "Caught exception while reading file from : " + filePath, (Throwable)e);
                throw e;
            }
            finally {
                if (fr != null) {
                    fr.close();
                }
                if (br != null) {
                    br.close();
                }
            }
            return buffer.toString();
        }
        catch (final FileNotFoundException ex) {
            throw ex;
        }
        catch (final Exception ex2) {
            SyMLogger.error(this.logger, this.sourceClass, sourceMethod, "Caught exception while reading file from : " + filePath, (Throwable)ex2);
            throw ex2;
        }
    }
    
    public boolean deleteFile(final String filePath) {
        final String sourceMethod = "deleteFile";
        Jedis jedis = null;
        final String fileName = FilenameUtils.getName(filePath);
        boolean fileDeleteStatus = false;
        try {
            jedis = ApiFactoryProvider.getRedisQueueAPI().getJedis();
            if (jedis != null) {
                if (jedis.hget(this.agentDataLocation, fileName) != null && jedis.hget(this.agentDataLocation, fileName).equalsIgnoreCase("redis")) {
                    fileDeleteStatus = this.deleteFileFromRedis(fileName, jedis);
                    jedis.hdel(this.agentDataLocation, new String[] { fileName });
                }
                else {
                    fileDeleteStatus = this.deleteFileFromDisk(filePath);
                    jedis.hdel(this.agentDataLocation, new String[] { fileName });
                }
            }
        }
        catch (final Exception ex) {
            final Boolean isShutdownTriggered = Boolean.valueOf(System.getProperty("isRedisShutdownTriggered"));
            if (!isShutdownTriggered) {
                SyMLogger.error(this.logger, this.sourceClass, sourceMethod, "Caught exception while deleting data from Redis DB " + fileName, (Throwable)ex);
                RedisErrorTracker.logRedisErrors(ex);
            }
        }
        finally {
            if (jedis != null) {
                jedis.close();
            }
        }
        return fileDeleteStatus;
    }
    
    public String unCompressString(final DCQueueData dcQData) {
        return null;
    }
    
    public boolean deleteFileFromRedis(final String fileName, final Jedis jedis) {
        final String sourceMethod = "deleteFileFromRedis";
        SyMLogger.debug(this.logger, this.sourceClass, sourceMethod, "Going to delete file: " + fileName);
        Long result = null;
        try {
            result = jedis.hdel(this.redisAgentDataFilenameSet, new String[] { fileName });
            SyMLogger.info(this.logger, this.sourceClass, sourceMethod, "Deleted file: " + fileName + " Location :Redis" + " Result: " + result);
        }
        catch (final Exception ex) {
            SyMLogger.error(this.logger, this.sourceClass, sourceMethod, "Caught exception while deleting file: " + fileName, (Throwable)ex);
            SyMLogger.info(this.qErrorLogger, this.sourceClass, sourceMethod, "File deletion failed ==> key : " + this.redisAgentDataFilenameSet + "File Name : " + fileName);
        }
        return result == 1L;
    }
    
    public boolean deleteFileFromDisk(final String filePath) throws Exception {
        final String sourceMethod = "deleteFile";
        SyMLogger.debug(this.logger, this.sourceClass, sourceMethod, "Going to delete file: " + filePath);
        boolean result = false;
        try {
            final File dataFile = new File(filePath);
            result = dataFile.delete();
            SyMLogger.info(this.logger, this.sourceClass, sourceMethod, "Deleted file: " + filePath + " Location: File " + " Result: " + result);
        }
        catch (final Exception ex) {
            SyMLogger.error(this.logger, this.sourceClass, sourceMethod, "Caught exception while deleting file: " + filePath, (Throwable)ex);
            SyMLogger.info(this.qErrorLogger, this.sourceClass, sourceMethod, "File deletion failed ==> Q : " + this.qMetaData.queueName + "||" + "File Name : " + filePath);
        }
        return result;
    }
    
    public void deleteDBEntry(final DCQueueData qData, final boolean isFileDeleted, final DCQueueMetaData qMetadata) {
        this.logger.log(Level.INFO, "Deleting data from redis queue");
        Jedis jedis = null;
        final String execQ = "EXECUTION_" + this.qMetaData.queueName;
        final String fileNameQ = "FILENAMEQ_" + this.qMetaData.queueName;
        try {
            jedis = ApiFactoryProvider.getRedisQueueAPI().getJedis();
            final long delStatus = jedis.hdel(fileNameQ, new String[] { qData.fileName });
            final long execDelStatus = jedis.hdel(execQ, new String[] { qData.fileName });
            long extnDelStatus = 1L;
            long priorRefDelStatus = 1L;
            Long rid = null;
            if (qData.priorityQRefTableData != null) {
                rid = qData.priorityQRefTableData.get("REFERENCE_ID");
            }
            if (this.qMetaData.queueExtnTableName != null) {
                if (qData.queueExtnTableData != null) {
                    rid = qData.queueExtnTableData.get("RESOURCE_ID");
                }
                extnDelStatus = jedis.hdel(this.qMetaData.queueExtnTableName, new String[] { qData.fileName });
            }
            if (rid != null) {
                final String keyName = this.qMetaData.queueTableName + "QREF_ID" + rid;
                priorRefDelStatus = jedis.lrem(keyName, -1L, qData.fileName);
            }
            if (delStatus == 1L && execDelStatus == 1L && extnDelStatus == 1L) {
                this.logger.log(Level.INFO, "Delete from Queue : Success");
            }
            else {
                this.logger.log(Level.INFO, "Delete from Queue : Failed");
            }
            QueueDataMETracking.decrementTrackingMap(qMetadata.queueName);
        }
        catch (final Exception e) {
            final Boolean isShutdownTriggered = Boolean.valueOf(System.getProperty("isRedisShutdownTriggered"));
            if (!isShutdownTriggered) {
                this.logger.log(Level.WARNING, "Error while deleting key", e);
                RedisErrorTracker.logRedisErrors(e);
            }
        }
        finally {
            if (jedis != null) {
                jedis.close();
            }
        }
    }
}
