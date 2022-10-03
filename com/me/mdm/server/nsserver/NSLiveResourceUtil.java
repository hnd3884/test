package com.me.mdm.server.nsserver;

import java.util.Collection;
import java.util.ArrayList;
import java.io.OutputStream;
import java.io.InputStream;
import java.io.DataInputStream;
import java.io.BufferedInputStream;
import java.util.logging.Level;
import java.io.IOException;
import java.net.UnknownHostException;
import java.net.Socket;
import java.util.HashMap;
import java.util.logging.Logger;

public class NSLiveResourceUtil
{
    private Logger logger;
    private int nsPortNumber;
    private String nsServerName;
    private HashMap resourceList;
    private static NSLiveResourceUtil nsLiveListIns;
    public String sourceClass;
    
    public NSLiveResourceUtil() {
        this.logger = Logger.getLogger("NSClientLogger");
        this.nsPortNumber = 0;
        this.nsServerName = "localhost";
        this.resourceList = null;
        this.sourceClass = "NSLiveResourceUtil : ";
    }
    
    public static NSLiveResourceUtil getInstance() {
        if (NSLiveResourceUtil.nsLiveListIns == null) {
            NSLiveResourceUtil.nsLiveListIns = new NSLiveResourceUtil();
        }
        return NSLiveResourceUtil.nsLiveListIns;
    }
    
    public Socket connect() throws UnknownHostException, IOException {
        this.nsPortNumber = NSUtil.getInstance().getNSPort();
        final Socket clientsocket = new Socket(this.nsServerName, this.nsPortNumber);
        return clientsocket;
    }
    
    public HashMap getLiveListFromNS() throws IOException {
        final int portNumber = NSUtil.getInstance().getNSPort();
        this.nsPortNumber = portNumber;
        final String sourceMethod = "getLiveListFromNS : ";
        this.logger.log(Level.INFO, "{0}{1}Entered in to getLiveListFromNS()", new Object[] { this.sourceClass, sourceMethod });
        final String register = "/L=" + new Long(System.currentTimeMillis());
        OutputStream out = null;
        DataInputStream in = null;
        int loopCounter = 0;
        int available = 0;
        HashMap currentLiveList = null;
        byte[] responseBytes = null;
        String responseStr = null;
        Socket clientsocket = null;
        try {
            this.logger.log(Level.INFO, "{0}{1}NS Server ::{2}", new Object[] { this.sourceClass, sourceMethod, this.nsServerName });
            clientsocket = this.connect();
            if (clientsocket == null) {
                clientsocket = this.connect();
            }
            if (clientsocket.isClosed() || !clientsocket.isConnected()) {
                clientsocket = this.connect();
            }
            try {
                out = clientsocket.getOutputStream();
                out.write(register.getBytes());
                out.flush();
                this.logger.log(Level.INFO, "{0}{1}LiveList Request Buffer has been successfully written", new Object[] { this.sourceClass, sourceMethod });
            }
            catch (final IOException exp) {
                this.logger.log(Level.INFO, "{1}{2}LiveList Request Buffer IOException has been occurred {0}. Hence performing the reconnection", new Object[] { exp.getMessage(), this.sourceClass, sourceMethod });
                clientsocket = this.connect();
                out = clientsocket.getOutputStream();
                out.write(register.getBytes());
                out.flush();
                this.logger.log(Level.INFO, "{0}{1}LiveList Request Buffer has been successfully written", new Object[] { this.sourceClass, sourceMethod });
            }
            final StringBuffer responseBuff = new StringBuffer();
            in = new DataInputStream(new BufferedInputStream(clientsocket.getInputStream()));
            if (in != null) {
                while ((available = in.available()) < 1 && loopCounter < 300) {
                    ++loopCounter;
                    Thread.sleep(100L);
                }
                this.logger.log(Level.INFO, "{0}{1} Reading LiveList buffer from Notification Server", new Object[] { this.sourceClass, sourceMethod });
                while (available > 0) {
                    Thread.sleep(5L);
                    final byte[] tempBuff = new byte[available];
                    in.read(tempBuff);
                    final String responseStr2 = new String(tempBuff, "UTF-8");
                    responseBuff.append(responseStr2);
                    this.logger.log(Level.INFO, "{0}{1} Received Data:::::{2}", new Object[] { this.sourceClass, sourceMethod, responseStr2 });
                    in.mark(available);
                    available = in.available();
                }
                this.logger.log(Level.INFO, "{0}{1}LiveList buffer Readed successfully", new Object[] { this.sourceClass, sourceMethod });
            }
            responseStr = new String(responseBuff);
            responseBytes = responseStr.getBytes();
            responseStr = this.parseResponse(responseBytes);
            if (responseStr != null) {
                currentLiveList = this.convertStrToHashMap(responseStr);
                this.resourceList = currentLiveList;
                this.logger.log(Level.INFO, "{0}{1}successfully get the live resource list from NS", new Object[] { this.sourceClass, sourceMethod });
            }
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "{0}{1}Exception in getting live computer list from NS : {2}", new Object[] { this.sourceClass, sourceMethod, e });
        }
        finally {
            if (in != null) {
                in.close();
            }
            if (out != null) {
                out.close();
            }
            if (clientsocket != null) {
                clientsocket.close();
            }
        }
        this.logger.log(Level.INFO, "{0}{1}Exited from getLiveListFromNS()", new Object[] { this.sourceClass, sourceMethod });
        return currentLiveList;
    }
    
    private String parseResponse(final byte[] responseBytes) throws Exception {
        final String sourceMethod = "parseResponse : ";
        this.logger.log(Level.INFO, "{0}{1}Entered into parseResponse() ", new Object[] { this.sourceClass, sourceMethod });
        String returnStr = null;
        try {
            final String responseStr = new String(responseBytes, "UTF-8");
            if (responseStr != null) {
                final int startIndex = responseStr.indexOf("{");
                final int endIndex = responseStr.lastIndexOf("}");
                if (startIndex != -1 && endIndex != -1) {
                    returnStr = responseStr.substring(startIndex + 1, endIndex);
                }
            }
            this.logger.log(Level.INFO, "{0}{1}live list parsed successfully", new Object[] { this.sourceClass, sourceMethod });
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "{0}{1}Exception in parseresponse() :: {2}", new Object[] { this.sourceClass, sourceMethod, e });
        }
        this.logger.log(Level.INFO, "{0}{1}Exited from parseResponse()", new Object[] { this.sourceClass, sourceMethod });
        return returnStr;
    }
    
    private HashMap<Long, HashMap> convertStrToHashMap(final String responseStr) {
        final String sourceMethod = "convertStrToHashMap : ";
        this.logger.log(Level.INFO, "{0}{1}Entered into convertStrToHashMap() ", new Object[] { this.sourceClass, sourceMethod });
        this.logger.log(Level.INFO, "Received String : {0}", responseStr);
        final HashMap<Long, HashMap> currentlist = new HashMap<Long, HashMap>();
        if (responseStr != null && !responseStr.trim().equals("")) {
            final String[] resStrArray = responseStr.split(",");
            for (int i = 0; i < resStrArray.length; ++i) {
                final String[] resDetails = resStrArray[i].split(":");
                final HashMap timeMap = new HashMap();
                timeMap.put(Long.parseLong(resDetails[1]), Long.parseLong(resDetails[2]));
                currentlist.put(Long.parseLong(resDetails[0]), timeMap);
            }
        }
        this.logger.log(Level.INFO, "{0}{1}Exited from convertStrToHashMap()", new Object[] { this.sourceClass, sourceMethod });
        return currentlist;
    }
    
    public boolean isResourceLive(final Long resId) {
        final String sourceMethod = "isResourceLive : ";
        try {
            final HashMap liveResourceList = this.getLiveListFromNS();
            if (liveResourceList != null && liveResourceList.containsKey(resId)) {
                return true;
            }
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "{0}{1}Exception in isResourceLive() method", new Object[] { this.sourceClass, sourceMethod });
        }
        return false;
    }
    
    public HashMap getLiveResourceMap() {
        return this.resourceList;
    }
    
    public ArrayList getLiveResourceList() {
        if (this.resourceList != null) {
            return new ArrayList(this.resourceList.keySet());
        }
        return new ArrayList();
    }
    
    public ArrayList getLiveResourceList(final Long[] resIdList) {
        final String sourceMethod = "getLiveResourceList : ";
        this.logger.log(Level.INFO, "{0}{1}Entered into getLiveResourceList()", new Object[] { this.sourceClass, sourceMethod });
        ArrayList<Long> livelist = null;
        try {
            this.resourceList = this.getLiveListFromNS();
            if (this.resourceList != null) {
                for (final Long resId : resIdList) {
                    if (this.resourceList.containsKey(resId)) {
                        if (livelist == null) {
                            livelist = new ArrayList<Long>();
                        }
                        livelist.add(resId);
                    }
                }
            }
            this.logger.log(Level.INFO, "{0}{1}Live resource list for given resource list is constructed successfully", new Object[] { this.sourceClass, sourceMethod });
        }
        catch (final Exception ex) {
            this.logger.log(Level.INFO, "{0}{1}Exception while getting Live list from given List::{2}", new Object[] { this.sourceClass, sourceMethod, ex });
        }
        this.logger.log(Level.INFO, "{0}{1}Exited from getLiveResourceList()", new Object[] { this.sourceClass, sourceMethod });
        return livelist;
    }
    
    public void removeResFromLiveList(final Long resId) {
        if (this.resourceList != null) {
            this.resourceList.remove(resId);
        }
    }
    
    public void cleanUpLiveList() {
        this.resourceList = null;
    }
    
    public boolean getLiveStatusFromNS(final Long resourceID) throws IOException {
        final int portNumber = NSUtil.getInstance().getNSPort();
        this.nsPortNumber = portNumber;
        final String sourceMethod = "getLiveStatusFromNS : ";
        this.logger.log(Level.INFO, "{0}{1}Entered in to getLiveListFromNS()", new Object[] { this.sourceClass, sourceMethod });
        final String register = "/A=" + new Long(System.currentTimeMillis()) + ";" + resourceID;
        Socket clientsocket = null;
        OutputStream out = null;
        DataInputStream in = null;
        int loopCounter = 0;
        int available = 0;
        byte[] responseBytes = null;
        String responseStr = null;
        boolean status = false;
        try {
            this.logger.log(Level.INFO, "{0}{1}NS Server ::{2}", new Object[] { this.sourceClass, sourceMethod, this.nsServerName });
            clientsocket = this.connect();
            if (clientsocket == null) {
                clientsocket = this.connect();
            }
            if (clientsocket.isClosed() || !clientsocket.isConnected()) {
                clientsocket = this.connect();
            }
            try {
                out = clientsocket.getOutputStream();
                out.write(register.getBytes());
                out.flush();
                this.logger.log(Level.INFO, "{0}{1}LiveList Request Buffer has been successfully written", new Object[] { this.sourceClass, sourceMethod });
            }
            catch (final IOException exp) {
                this.logger.log(Level.INFO, "{1}{2}LiveList Request Buffer IOException has been occurred {0}. Hence performing the reconnection", new Object[] { exp.getMessage(), this.sourceClass, sourceMethod });
                clientsocket = this.connect();
                out = clientsocket.getOutputStream();
                out.write(register.getBytes());
                out.flush();
                this.logger.log(Level.INFO, "{0}{1}LiveList Request Buffer has been successfully written", new Object[] { this.sourceClass, sourceMethod });
            }
            final StringBuffer responseBuff = new StringBuffer();
            in = new DataInputStream(new BufferedInputStream(clientsocket.getInputStream()));
            if (in != null) {
                while ((available = in.available()) < 1 && loopCounter < 300) {
                    ++loopCounter;
                    Thread.sleep(100L);
                }
                this.logger.log(Level.INFO, "{0}{1} Reading LiveList buffer from Notification Server", new Object[] { this.sourceClass, sourceMethod });
                while (available > 0) {
                    Thread.sleep(5L);
                    final byte[] tempBuff = new byte[available];
                    in.read(tempBuff);
                    final String responseStr2 = new String(tempBuff, "UTF-8");
                    responseBuff.append(responseStr2);
                    this.logger.log(Level.INFO, "{0}{1} Received Data:::::{2}", new Object[] { this.sourceClass, sourceMethod, responseStr2 });
                    in.mark(available);
                    available = in.available();
                }
                this.logger.log(Level.INFO, "{0}{1}LiveList buffer Readed successfully", new Object[] { this.sourceClass, sourceMethod });
                responseStr = new String(responseBuff);
                responseBytes = responseStr.getBytes();
                responseStr = this.parseResponse(responseBytes);
                final String[] responseStrAry = responseStr.split("\\:");
                status = responseStrAry[1].equals("1");
            }
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "{0}{1}Exception in getting live computer list from NS : {2}", new Object[] { this.sourceClass, sourceMethod, e });
        }
        finally {
            if (in != null) {
                in.close();
            }
            if (out != null) {
                out.close();
            }
            if (clientsocket != null) {
                clientsocket.close();
            }
        }
        this.logger.log(Level.INFO, "{0}{1}Exited from getLiveListFromNS()", new Object[] { this.sourceClass, sourceMethod });
        return status;
    }
    
    static {
        NSLiveResourceUtil.nsLiveListIns = null;
    }
}
