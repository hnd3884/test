package com.me.ems.onpremise.summaryserver.probe.sync.post;

import java.io.Reader;
import com.me.devicemanagement.framework.webclient.common.SYMClientUtil;
import org.json.JSONArray;
import com.me.ems.onpremise.summaryserver.probe.sync.utils.SSDataUpdateUtil;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.logging.Level;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.util.logging.Logger;
import javax.servlet.http.HttpServlet;

public class SSDataSyncServlet extends HttpServlet
{
    private static Logger out;
    
    public void doGet(final HttpServletRequest request, final HttpServletResponse res) throws ServletException, IOException {
        this.doPost(request, res);
    }
    
    public void doPost(final HttpServletRequest request, final HttpServletResponse response) throws ServletException, IOException {
        SSDataSyncServlet.out.log(Level.INFO, "---> Inside SSDataSyncServlet");
        try {
            String reqBody = "";
            try {
                reqBody = this.readRequest(request);
            }
            catch (final Exception e) {
                SSDataSyncServlet.out.log(Level.SEVERE, "--->Error Inside SSDataSyncServlet", e);
            }
            JSONArray jsonArray;
            String tableName;
            try {
                final JSONObject jsonObject = new JSONObject(reqBody);
                jsonArray = jsonObject.getJSONArray("data");
                tableName = (String)jsonObject.get("tableName");
            }
            catch (final JSONException e2) {
                SSDataSyncServlet.out.log(Level.SEVERE, "--->Error Inside SSDataSyncServlet", (Throwable)e2);
                throw new IOException("Error parsing JSON request string");
            }
            SSDataUpdateUtil.getInstance().updateSSData(jsonArray, tableName);
        }
        catch (final Exception e3) {
            SSDataSyncServlet.out.log(Level.INFO, " Exception in SSDataSyncServlet", e3);
        }
    }
    
    private String readRequest(final HttpServletRequest request) throws IOException, Exception {
        Reader reader = null;
        try {
            int read = 0;
            final char[] chBuf = new char[500];
            final StringBuilder strBuilder = new StringBuilder();
            try {
                final SYMClientUtil symclientUtil = new SYMClientUtil();
                reader = symclientUtil.getProperEncodedReader(request, reader);
                while ((read = reader.read(chBuf)) > -1) {
                    strBuilder.append(chBuf, 0, read);
                }
            }
            catch (final IOException ex) {
                SSDataSyncServlet.out.log(Level.WARNING, "readRequest -> IOException occurred while reading request : " + ex);
                throw ex;
            }
            finally {
                if (reader != null) {
                    try {
                        reader.close();
                    }
                    catch (final Exception ex2) {
                        SSDataSyncServlet.out.log(Level.WARNING, "Exception occurred while closing the reader : ", ex2);
                    }
                }
            }
            return strBuilder.toString();
        }
        catch (final Exception ex3) {
            SSDataSyncServlet.out.log(Level.WARNING, "readRequest -> Exception occurred while reading request : " + ex3);
            throw ex3;
        }
    }
    
    static {
        SSDataSyncServlet.out = Logger.getLogger("ProbeSyncLogger");
    }
}
