package com.me.mdm.framework.qr;

import java.awt.Color;
import java.util.Iterator;
import java.util.HashMap;
import java.io.OutputStream;
import java.util.logging.Level;
import com.adventnet.sym.server.mdm.util.MDMStringUtils;
import java.util.List;
import java.util.Arrays;
import com.me.mdm.api.error.APIHTTPException;
import com.me.mdm.api.APIRequest;
import java.util.logging.Logger;
import com.me.mdm.api.ApiRequestHandler;

public class MDMQRCodeServlet extends ApiRequestHandler
{
    private static final Logger LOGGER;
    public static final String PATH = "/api/v1/mdm/enroll/qr";
    public static final String QUERY_PARAM_DATA = "data";
    public static final String QUERY_PARAM_HEIGHT = "h";
    public static final String QUERY_PARAM_WIDTH = "w";
    public static final String QUERY_PARAM_IMAGE_TYPE = "type";
    public static final String QUERY_PARAM_FRAME = "frame";
    public static final String BACKGROUND_BLACK = "blackbg";
    public static final String PNG_IMAGE = "png";
    public static final String JPEG_IMAGE = "jpeg";
    
    @Override
    public Object doGet(final APIRequest apiRequest) throws APIHTTPException {
        this.processRequest(apiRequest);
        return null;
    }
    
    private void processRequest(final APIRequest request) {
        try {
            final HashMap parmsMap = request.getParameterList();
            final List<String> contentType = Arrays.asList("png", "jpeg");
            this.validateMandatoryParams(Arrays.asList("data"), parmsMap);
            this.validateIntegerPramas("w", 100, 5000, parmsMap);
            this.validateIntegerPramas("h", 100, 5000, parmsMap);
            this.validateStringPrarams("type", contentType, parmsMap);
            final String data = parmsMap.get("data").toString();
            final String width = parmsMap.containsKey("w") ? parmsMap.get("w").toString() : null;
            final String height = parmsMap.containsKey("h") ? parmsMap.get("h").toString() : null;
            final String format = parmsMap.containsKey("type") ? parmsMap.get("type").toString() : null;
            final String frameneeded = parmsMap.containsKey("frame") ? parmsMap.get("frame").toString() : null;
            final String blackBackGround = parmsMap.containsKey("blackbg") ? parmsMap.get("blackbg").toString() : null;
            if (MDMStringUtils.isEmpty(data)) {
                throw new APIHTTPException("COM0005", new Object[] { "No 'data' attibute from server" });
            }
            final QRCodeGenerator qrgenerator = this.getQRGenerator(width, height, format, frameneeded, blackBackGround);
            request.httpServletResponse.setContentType("image/" + qrgenerator.imageFormat);
            MDMQRCodeServlet.LOGGER.log(Level.INFO, "Generating QR for data{0}", data);
            qrgenerator.createQRCode(data, (OutputStream)request.httpServletResponse.getOutputStream());
        }
        catch (final APIHTTPException ex) {
            MDMQRCodeServlet.LOGGER.log(Level.SEVERE, "Exception in MDMQR Code Servlet.. ", ex);
            throw ex;
        }
        catch (final Exception ex2) {
            MDMQRCodeServlet.LOGGER.log(Level.SEVERE, "Exception in MDMQR Code Servlet.. ", ex2);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
    
    private void validateIntegerPramas(final String param, final int min, final int max, final HashMap params) throws Exception {
        if (params.containsKey(param)) {
            final int intAtib = Integer.parseInt(params.get(param).toString());
            if (min > intAtib || intAtib > max) {
                throw new APIHTTPException("COM0014", new Object[] { "Paramater " + param + " shoud be of range " + min + "to " + max });
            }
        }
    }
    
    private void validateStringPrarams(final String param, final List allowedValues, final HashMap params) throws Exception {
        if (params.containsKey(param) && !allowedValues.isEmpty() && !allowedValues.contains(params.get(param))) {
            throw new APIHTTPException("COM0014", new Object[] { "Paramater " + param + " shoud be in " + allowedValues.toString() });
        }
    }
    
    private void validateMandatoryParams(final List mandatoryValues, final HashMap params) throws Exception {
        for (final String s : mandatoryValues) {
            if (!params.containsKey(s)) {
                throw new APIHTTPException("COM0005", new Object[0]);
            }
        }
    }
    
    private QRCodeGenerator getQRGenerator(final String w, final String h, final String type, final String frame, final String isBgBlack) {
        final Integer width = (w == null) ? 300 : Integer.valueOf(w);
        final Integer height = (h == null) ? 300 : Integer.valueOf(h);
        final String Imagetype = (type == null) ? "png" : type;
        final Boolean isBlackBackGround = isBgBlack != null && Boolean.parseBoolean(isBgBlack);
        final Boolean isWhiteBackGround = !isBlackBackGround;
        final Boolean frameNeeded = frame == null || Boolean.parseBoolean(frame);
        final Color bgColor = isWhiteBackGround ? Color.WHITE : new Color(255, 255, 255, 0);
        return new QRCodeGenerator(width, height, Imagetype, frameNeeded, bgColor);
    }
    
    static {
        LOGGER = Logger.getLogger(MDMQRCodeServlet.class.getName());
    }
}
