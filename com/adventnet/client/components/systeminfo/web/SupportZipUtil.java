package com.adventnet.client.components.systeminfo.web;

import java.util.Hashtable;
import java.io.FileInputStream;
import java.util.logging.Level;
import java.io.InputStream;
import java.util.Properties;
import java.net.URLConnection;
import java.io.BufferedInputStream;
import java.io.DataOutputStream;
import java.util.Base64;
import java.net.URL;
import java.util.logging.Logger;

public class SupportZipUtil
{
    private static Logger logger;
    static final int BUFF_SIZE = 1024;
    static final byte[] BUFFER;
    
    public static void uploadFile(final String url, String supportFile, String fromAddress, String userMessage, final String toAddress, final String proxyHost, final String proxyPort, final String proxyUser, final String proxyPass) throws Exception {
        final String message = "";
        DataOutputStream out = null;
        BufferedInputStream in = null;
        try {
            if (fromAddress != null) {
                fromAddress = fromAddress.trim();
                userMessage = userMessage.trim();
                supportFile = supportFile.trim();
                final URL servlet = new URL(url);
                final URLConnection conn = servlet.openConnection();
                final Properties prop = System.getProperties();
                if (proxyHost == null || proxyHost.trim().equals("")) {
                    ((Hashtable<String, String>)prop).put("proxySet", "false");
                }
                else {
                    ((Hashtable<String, String>)prop).put("proxySet", "true");
                    ((Hashtable<String, String>)prop).put("proxyHost", proxyHost);
                    ((Hashtable<String, String>)prop).put("proxyPort", proxyPort);
                }
                final String login = proxyUser + ":" + proxyPass;
                final String encoded = Base64.getEncoder().encodeToString(login.getBytes("utf-8"));
                conn.setRequestProperty("Proxy-Authorization", "Basic " + encoded);
                conn.setDoOutput(true);
                conn.setDoInput(true);
                conn.setUseCaches(false);
                final String boundary = "---------------------------7d226f700d0";
                conn.setRequestProperty("Content-type", "multipart/form-data; boundary=" + boundary);
                conn.setRequestProperty("Cache-Control", "no-cache");
                out = new DataOutputStream(conn.getOutputStream());
                out.writeBytes("--" + boundary + "\r\n");
                writeParam("fromAddress", fromAddress, out, boundary);
                writeParam("toAddress", toAddress, out, boundary);
                writeParam("todo", "upload", out, boundary);
                writeParam("userMessage", userMessage, out, boundary);
                writeFile("uploadfile", supportFile, out, boundary);
                out.flush();
                final InputStream stream = conn.getInputStream();
                in = new BufferedInputStream(stream);
                int i = 0;
                while ((i = in.read()) != -1) {}
            }
        }
        finally {
            if (out != null) {
                out.close();
            }
            if (in != null) {
                in.close();
            }
        }
    }
    
    private static void writeParam(final String name, final String value, final DataOutputStream out, final String boundary) throws Exception {
        try {
            out.writeBytes("content-disposition: form-data; name=\"" + name + "\"\r\n\r\n");
            out.writeBytes(value);
            out.writeBytes("\r\n--" + boundary + "\r\n");
        }
        catch (final Exception e) {
            SupportZipUtil.logger.log(Level.INFO, "SupportFileUploader -> Problem occurred in writeParam " + e.toString());
            throw new Exception(e.getMessage());
        }
    }
    
    private static void writeFile(final String name, final String filePath, final DataOutputStream out, final String boundary) throws Exception {
        FileInputStream fis = null;
        try {
            out.writeBytes("content-disposition: form-data; name=\"" + name + "\"; filename=\"" + filePath + "\"\r\n");
            out.writeBytes("content-type: application/octet-stream\r\n\r\n");
            fis = new FileInputStream(filePath);
            while (true) {
                synchronized (SupportZipUtil.BUFFER) {
                    final int amountRead = fis.read(SupportZipUtil.BUFFER);
                    if (amountRead == -1) {
                        break;
                    }
                    out.write(SupportZipUtil.BUFFER, 0, amountRead);
                }
            }
            out.writeBytes("\r\n--" + boundary + "\r\n");
        }
        catch (final Exception e) {
            SupportZipUtil.logger.log(Level.INFO, "SupportFileUploader-> writeFile-> Problem occurred" + e.toString());
            throw new Exception(e.getMessage());
        }
        finally {
            if (fis != null) {
                fis.close();
            }
        }
    }
    
    static {
        SupportZipUtil.logger = Logger.getLogger(SupportZipUtil.class.getName());
        BUFFER = new byte[1024];
    }
}
