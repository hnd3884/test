package com.me.mdm.agent.servlets.file;

import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import java.util.UUID;
import java.io.IOException;
import javax.servlet.ServletException;
import java.io.PrintWriter;
import org.json.JSONException;
import java.util.logging.Level;
import java.io.InputStream;
import java.io.FileInputStream;
import com.me.mdm.files.FileFacade;
import java.io.File;
import com.adventnet.sym.server.mdm.util.JSONUtil;
import org.json.JSONObject;
import com.me.mdm.agent.handlers.DeviceRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.util.logging.Logger;
import com.me.mdm.webclient.filter.DeviceAuthenticatedRequestServlet;

public class MDMFileUploaderServlet extends DeviceAuthenticatedRequestServlet
{
    Logger logger;
    
    public MDMFileUploaderServlet() {
        this.logger = Logger.getLogger("MDMLogger");
    }
    
    public void doPost(final HttpServletRequest request, final HttpServletResponse response, final DeviceRequest deviceRequest) throws ServletException, IOException {
        JSONObject requestJSON = new JSONObject();
        final JSONObject responseDetails = new JSONObject();
        File file = null;
        try {
            final String udid = request.getParameter("udid");
            final Long customerID = Long.parseLong(request.getParameter("customerId"));
            responseDetails.put("status", 200);
            requestJSON = this.downloadToFile(request);
            requestJSON.put("CUSTOMER_ID", (Object)customerID);
            requestJSON.put("expiry_offset", 1800000L);
            requestJSON.put("msg_header", (Object)JSONUtil.toJSON("filters", JSONUtil.toJSON("customer_id", customerID)));
            file = new File(String.valueOf(requestJSON.get("file_path")));
            new FileFacade().verifyFileContentType(requestJSON, file);
            final JSONObject responseJSON = new FileFacade().addFile(requestJSON, new FileInputStream(file), true);
            responseJSON.remove("msg_header");
            responseDetails.put("RESPONSE", (Object)responseJSON);
            response.setHeader("Content-Type", "application/json;charset=UTF-8");
            response.setCharacterEncoding("UTF-8");
            final PrintWriter pout = response.getWriter();
            pout.print(responseDetails.toString());
            pout.close();
        }
        catch (final JSONException e) {
            this.logger.log(Level.WARNING, "Exception while uploading file ", (Throwable)e);
        }
        catch (final Exception e2) {
            this.logger.log(Level.WARNING, "Exception while uploading file ", e2);
        }
        finally {
            if (file != null && FileFacade.getInstance().isSafePathToDelete(file.getPath())) {
                final File parentFolder = new File(file.getParent());
                if (file.exists()) {
                    if (file.delete()) {
                        this.logger.log(Level.INFO, "Temporary file deleted successfully");
                    }
                    else {
                        this.logger.log(Level.SEVERE, "Temporary file uploaded for /files endpoint not deleted");
                    }
                }
                if (parentFolder.delete()) {
                    if (file.delete()) {
                        this.logger.log(Level.INFO, "Temporary folder deleted successfully");
                    }
                    else {
                        this.logger.log(Level.SEVERE, "Temporary folder created by /files endpoint not deleted");
                    }
                }
            }
        }
    }
    
    public JSONObject downloadToFile(final HttpServletRequest request) throws Exception {
        final UUID randomid = UUID.randomUUID();
        String fileName = request.getParameter("filename");
        final String[] fileNameSplit = fileName.split("\\.");
        final String strContentType = (fileNameSplit.length > 1) ? fileNameSplit[fileNameSplit.length - 1] : "";
        fileName = Long.toString(MDMUtil.getCurrentTime()) + "." + strContentType;
        final String fileType = request.getParameter("filetype");
        final Long customerID = Long.parseLong(request.getParameter("customerId"));
        final String serverHome = ApiFactoryProvider.getUtilAccessAPI().getServerHome();
        final String folderPath = serverHome + File.separator + "api_temp_downloads" + File.separator + customerID + File.separator + randomid;
        final File file = new File(folderPath);
        file.mkdirs();
        final String completedFileName = folderPath + File.separator + fileName;
        FileFacade.getInstance().writeFile(completedFileName, (InputStream)request.getInputStream());
        final JSONObject fileJSON = new JSONObject();
        fileJSON.put("file_name", (Object)fileName);
        fileJSON.put("content_type", (Object)fileType);
        fileJSON.put("content_length", new File(completedFileName).length());
        fileJSON.put("file_path", (Object)completedFileName);
        return fileJSON;
    }
}
