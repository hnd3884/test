package com.zoho.security.util;

import java.io.InputStream;
import java.io.IOException;
import java.util.Iterator;
import java.io.DataOutputStream;
import com.zoho.security.agent.MultipartFile;
import java.util.List;
import java.util.Map;
import java.net.HttpURLConnection;

public class MultipartUtil
{
    private static final String CRLF = "\r\n";
    private static final String CHARSET = "UTF-8";
    
    public static void appendMultipartData(final HttpURLConnection connection, final Map<String, String> multipartParams, final List<MultipartFile> multipartFiles) throws IOException {
        final String boundary = Long.toHexString(System.currentTimeMillis());
        connection.setDoOutput(true);
        connection.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);
        DataOutputStream outputStream = null;
        try {
            outputStream = new DataOutputStream(connection.getOutputStream());
            if (multipartParams != null) {
                for (final Map.Entry<String, String> entry : multipartParams.entrySet()) {
                    addFormField(outputStream, boundary, entry.getKey(), entry.getValue());
                }
            }
            if (multipartFiles != null) {
                for (final MultipartFile file : multipartFiles) {
                    addFilePart(file, boundary, outputStream);
                }
            }
            outputStream.writeBytes("--" + boundary + "--" + "\r\n");
            outputStream.flush();
        }
        finally {
            if (outputStream != null) {
                outputStream.close();
            }
        }
    }
    
    public static void addFilePart(final MultipartFile file, final String boundary, final DataOutputStream outputStream) throws IOException {
        InputStream inputStream = null;
        try {
            outputStream.writeBytes("--" + boundary + "\r\n");
            outputStream.writeBytes("Content-Disposition: form-data; name=\"" + file.getFieldName() + "\"");
            outputStream.writeBytes("; filename=\"");
            outputStream.write(file.getFileName().getBytes("UTF-8"));
            outputStream.writeBytes("\"\r\n");
            outputStream.writeBytes("Content-Type: " + file.getContentType() + "\r\n");
            outputStream.writeBytes("\r\n");
            outputStream.flush();
            if (file.getBytes() != null) {
                outputStream.write(file.getBytes());
            }
            else {
                final byte[] buffer = new byte[4096];
                int bytesRead = -1;
                inputStream = file.getInputStream();
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, bytesRead);
                }
            }
            outputStream.writeBytes("\r\n");
            outputStream.flush();
        }
        finally {
            if (inputStream != null) {
                inputStream.close();
            }
        }
    }
    
    private static void addFormField(final DataOutputStream outStream, final String boundary, final String name, final String value) throws IOException {
        outStream.writeBytes("--" + boundary + "\r\n");
        outStream.writeBytes("Content-Disposition: form-data; name=\"" + name + "\"" + "\r\n");
        outStream.writeBytes("Content-Type: text/plain; charset=UTF-8\r\n");
        outStream.writeBytes("\r\n");
        outStream.write(value.getBytes("UTF-8"));
        outStream.writeBytes("\r\n");
        outStream.flush();
    }
}
