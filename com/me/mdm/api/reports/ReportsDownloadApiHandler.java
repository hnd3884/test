package com.me.mdm.api.reports;

import javax.servlet.http.HttpServletResponse;
import java.io.OutputStream;
import java.io.InputStream;
import java.io.IOException;
import com.me.mdm.api.error.APIHTTPException;
import java.util.logging.Level;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import java.io.File;
import com.me.devicemanagement.framework.server.util.SyMUtil;
import com.me.mdm.server.reports.MDMReportsFacade;
import com.me.mdm.api.APIRequest;
import com.me.mdm.api.ApiRequestHandler;

public class ReportsDownloadApiHandler extends ApiRequestHandler
{
    @Override
    public Object doGet(final APIRequest apiRequest) throws APIHTTPException {
        final MDMReportsFacade mdmReportsFacade = new MDMReportsFacade();
        InputStream inStream = null;
        OutputStream outStream = null;
        try {
            String path = mdmReportsFacade.getReportDownloadURL(apiRequest.toJSONObject());
            final String server_home = SyMUtil.getInstallationDir();
            String file = null;
            final File reportFile = new File(path);
            final String fileName = reportFile.getName();
            file = fileName.replaceAll(" ", "_");
            path = server_home + File.separator + path;
            if (ApiFactoryProvider.getFileAccessAPI().isFileExists(path)) {
                final HttpServletResponse response = apiRequest.httpServletResponse;
                outStream = (OutputStream)response.getOutputStream();
                final int startIndex = file.lastIndexOf(".");
                final String format = file.substring(startIndex + 1, file.length());
                this.logger.log(Level.INFO, "The url for file to be download is", path);
                if (format.equals("pdf")) {
                    response.reset();
                    response.setContentType("application/pdf");
                }
                else if (format.equals("csv")) {
                    response.setContentType("application/text");
                }
                else {
                    response.setContentType("application/vnd.ms-excel");
                }
                response.setContentLength((int)ApiFactoryProvider.getFileAccessAPI().getFileSize(path));
                final String reportName = new String(file);
                response.setHeader("Content-Disposition", "attachment; filename=" + reportName);
                final byte[] buf = new byte[1024];
                inStream = ApiFactoryProvider.getFileAccessAPI().getInputStream(path);
                int sizeRead = 0;
                while ((sizeRead = inStream.read(buf, 0, buf.length)) > 0) {
                    outStream.write(buf, 0, sizeRead);
                }
                outStream.flush();
            }
        }
        catch (final APIHTTPException e) {
            throw e;
        }
        catch (final Exception e2) {
            this.logger.log(Level.WARNING, "Issue on getting download URL", e2);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
        finally {
            try {
                if (outStream != null) {
                    outStream.close();
                }
                if (inStream != null) {
                    inStream.close();
                }
            }
            catch (final IOException e3) {
                this.logger.log(Level.SEVERE, "Issue on closing streams", e3);
                throw new APIHTTPException("COM0004", new Object[0]);
            }
        }
        return null;
    }
}
