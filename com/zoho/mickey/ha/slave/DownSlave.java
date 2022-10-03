package com.zoho.mickey.ha.slave;

import com.zoho.framework.utils.crypto.CryptoUtil;
import java.nio.file.Files;
import com.zoho.mickey.ha.HAImpl;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.util.logging.Logger;
import javax.servlet.http.HttpServlet;

public class DownSlave extends HttpServlet
{
    private static final long serialVersionUID = 1L;
    private static final Logger LOG;
    private static String uniqueID;
    
    protected void doGet(final HttpServletRequest request, final HttpServletResponse response) throws ServletException, IOException {
        this.processRequest(request, response);
    }
    
    protected void doPost(final HttpServletRequest request, final HttpServletResponse response) throws ServletException, IOException {
        this.processRequest(request, response);
    }
    
    private void processRequest(final HttpServletRequest request, final HttpServletResponse response) throws IOException {
        throw new UnsupportedOperationException("Preferred master is not yet supported. So, downgrading slave through servlet is avoided");
    }
    
    static {
        LOG = Logger.getLogger(DownSlave.class.getName());
        DownSlave.uniqueID = null;
        if (HAImpl.UNIQUE_ID_FILE.exists()) {
            try {
                DownSlave.uniqueID = CryptoUtil.decrypt(new String(Files.readAllBytes(HAImpl.UNIQUE_ID_FILE.toPath())));
            }
            catch (final IOException e) {
                e.printStackTrace();
            }
        }
    }
}
