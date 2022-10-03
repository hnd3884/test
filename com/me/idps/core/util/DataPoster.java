package com.me.idps.core.util;

import org.json.simple.JSONObject;

public abstract class DataPoster implements Runnable
{
    protected JSONObject taskDetails;
    
    protected abstract byte[] handlePre();
    
    protected abstract void handlePost(final String p0, final int p1, final int p2) throws Exception;
    
    protected DataPoster(final JSONObject taskDetails) {
        this.taskDetails = taskDetails;
    }
    
    @Override
    public void run() {
        long timeTaken = 99999999999999999L;
        try {
            final byte[] byteAr = this.handlePre();
            final long fileWriteStart = System.currentTimeMillis();
            final String dataFilePath = DirectoryUtil.getInstance().writeDataIntoFileForProcessingLater(byteAr);
            final long fileWriteEnd = System.currentTimeMillis();
            timeTaken = fileWriteEnd - fileWriteStart;
            this.handlePost(dataFilePath, Integer.valueOf(String.valueOf(timeTaken)), byteAr.length);
        }
        catch (final Exception ex) {
            final String domainName = (String)this.taskDetails.get((Object)"NAME");
            final Long dmDomainID = (Long)this.taskDetails.get((Object)"DOMAIN_ID");
            final Long customerID = (Long)this.taskDetails.get((Object)"CUSTOMER_ID");
            final Integer clientID = (Integer)this.taskDetails.get((Object)"CLIENT_ID");
            DirectorySyncErrorHandler.getInstance().handleError(dmDomainID, customerID, domainName, clientID, ex, null);
        }
    }
}
