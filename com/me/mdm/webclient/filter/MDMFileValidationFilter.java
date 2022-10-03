package com.me.mdm.webclient.filter;

import java.io.File;
import java.security.cert.Certificate;
import com.adventnet.sym.server.mdm.certificates.CertificateUtil;
import org.apache.tika.Tika;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import java.util.Iterator;
import com.adventnet.persistence.DataObject;
import com.adventnet.ds.query.SelectQuery;
import java.util.List;
import com.me.devicemanagement.framework.server.util.I18NUtil;
import com.adventnet.persistence.Row;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.me.mdm.files.FileFacade;
import java.util.ArrayList;
import com.me.mdm.api.APIUtil;
import java.io.InputStream;
import org.apache.commons.io.IOUtils;
import javax.servlet.ServletException;
import java.io.IOException;
import java.util.Collection;
import java.util.Map;
import com.adventnet.iam.security.ActionRule;
import com.me.mdm.api.error.APIHTTPException;
import com.adventnet.iam.security.ParameterRule;
import com.adventnet.iam.security.JSONTemplateRule;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.HashMap;
import com.me.mdm.api.APIRequestProcessor;
import com.adventnet.iam.security.SecurityRequestWrapper;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.util.logging.Level;
import com.me.mdm.server.factory.MDMApiFactoryProvider;
import javax.servlet.FilterChain;
import javax.servlet.ServletResponse;
import javax.servlet.ServletRequest;
import javax.servlet.FilterConfig;
import java.util.logging.Logger;
import javax.servlet.Filter;

public class MDMFileValidationFilter implements Filter
{
    protected static final Logger LOGGER;
    protected static final String JSON_OBJECT = "JSONObject";
    protected static final String JSON_ARRAY = "Array";
    
    public void init(final FilterConfig fc) {
    }
    
    public void doFilter(final ServletRequest servletRequest, final ServletResponse servletResponse, final FilterChain filterChain) throws IOException, ServletException {
        if (MDMApiFactoryProvider.getMDMUtilAPI().isFeatureAllowedForUser("enrollment.debug.logs")) {
            MDMFileValidationFilter.LOGGER.log(Level.INFO, "Entered {0} Filter", MDMFileValidationFilter.class.getName());
        }
        final HttpServletRequest hreq = (HttpServletRequest)servletRequest;
        final HttpServletResponse hres = (HttpServletResponse)servletResponse;
        try {
            final SecurityRequestWrapper secureRequest = SecurityRequestWrapper.getInstance(hreq);
            final Long customerId = APIRequestProcessor.getNewInstance().getCustomerIdFromRequest(hreq);
            if (secureRequest != null) {
                final ActionRule actionrule = secureRequest.getURLActionRule();
                final String paramName = actionrule.getCustomAttribute("fileParam");
                if (paramName != null && !paramName.isEmpty()) {
                    final long currentMillisec = System.currentTimeMillis();
                    MDMFileValidationFilter.LOGGER.log(Level.INFO, "validating the file for the param {0}", paramName);
                    final String templateName = actionrule.getInputStreamRule().getTemplateName();
                    final String templateType = actionrule.getInputStreamRule().getDataType();
                    Map<Long, String> fileMap = new HashMap<Long, String>();
                    final String[] paramNames = paramName.split(",");
                    final Object requestBody = this.getRequestBody(hreq, templateType);
                    if (requestBody instanceof JSONObject) {
                        fileMap = this.getFileMapFromRequestAndSecuirty(paramNames, (JSONObject)requestBody, templateName, fileMap);
                    }
                    else if (requestBody instanceof JSONArray) {
                        for (int arrayIndex = 0; arrayIndex < ((JSONArray)requestBody).length(); ++arrayIndex) {
                            final JSONObject jsonObject = ((JSONArray)requestBody).getJSONObject(arrayIndex);
                            final JSONTemplateRule jsonTemplateRule = JSONTemplateRule.getKeyRule(templateName);
                            final Collection<ParameterRule> jsonArrayIndex = jsonTemplateRule.getJsonArrayIndexMap().values();
                            if (!jsonArrayIndex.isEmpty()) {
                                final ParameterRule arrayParameterRule = jsonArrayIndex.iterator().next();
                                final String arrayTemplateName = arrayParameterRule.getTemplateName();
                                fileMap = this.getFileMapFromRequestAndSecuirty(paramNames, jsonObject, arrayTemplateName, fileMap);
                            }
                        }
                    }
                    this.validateFileWithContentType(fileMap, customerId);
                    MDMFileValidationFilter.LOGGER.log(Level.INFO, "time taken for mdm file validation {0}", System.currentTimeMillis() - currentMillisec);
                }
            }
        }
        catch (final APIHTTPException ex) {
            ex.setErrorResponse(hres);
            return;
        }
        catch (final Exception ex2) {
            MDMFileValidationFilter.LOGGER.log(Level.SEVERE, "Exception while validating file ", ex2);
            new APIHTTPException("COM0004", new Object[0]).setErrorResponse(hres);
            return;
        }
        filterChain.doFilter(servletRequest, servletResponse);
    }
    
    public void destroy() {
    }
    
    public Map<Long, String> getFileMapFromRequestAndSecuirty(final String[] paramNames, final JSONObject requestBody, final String templateName, final Map<Long, String> fileMap) {
        for (int index = 0; index < paramNames.length; ++index) {
            final String param = paramNames[index];
            this.getFileMapFromRequestAndSecuirty(param, requestBody, templateName, fileMap);
        }
        return fileMap;
    }
    
    public Map<Long, String> getFileMapFromRequestAndSecuirty(final String paramName, final JSONObject requestBody, final String templateName, final Map<Long, String> fileMap) {
        JSONTemplateRule jsonTemplateRule = JSONTemplateRule.getKeyRule(templateName);
        final Map<String, ParameterRule> keyValueRules = (jsonTemplateRule != null) ? jsonTemplateRule.getKeyValueRule() : null;
        if (paramName.contains(".")) {
            final String[] splitParam = paramName.split("\\.");
            final String parentParam = splitParam[0];
            final String childParam = paramName.substring(parentParam.length() + 1);
            final ParameterRule parameterRule = keyValueRules.get(parentParam);
            final String parentTemplateName = parameterRule.getTemplateName();
            final String parentTemplateType = parameterRule.getDataType();
            if (parentTemplateType.contains("JSONObject")) {
                final JSONObject subRequestBody = requestBody.getJSONObject(parentParam);
                this.getFileMapFromRequestAndSecuirty(childParam, subRequestBody, parentTemplateName, fileMap);
            }
            else if (parentTemplateType.contains("Array")) {
                jsonTemplateRule = JSONTemplateRule.getKeyRule(parentTemplateName);
                final Collection<ParameterRule> jsonArrayIndex = jsonTemplateRule.getJsonArrayIndexMap().values();
                if (!jsonArrayIndex.isEmpty()) {
                    final ParameterRule arrayParameterRule = jsonArrayIndex.iterator().next();
                    final String arrayTemplateName = arrayParameterRule.getTemplateName();
                    final JSONArray subRequestArray = requestBody.getJSONArray(parentParam);
                    for (int index = 0; index < subRequestArray.length(); ++index) {
                        final JSONObject subRequestBody2 = subRequestArray.getJSONObject(index);
                        this.getFileMapFromRequestAndSecuirty(childParam, subRequestBody2, arrayTemplateName, fileMap);
                    }
                }
            }
        }
        else if (requestBody.has(paramName)) {
            final ParameterRule parameterRule2 = keyValueRules.get(paramName);
            final String dataType = parameterRule2.getDataType();
            final String fileType = (parameterRule2 != null) ? parameterRule2.getCustomAttribute("file-type") : null;
            if (dataType != null && dataType.contains("Array")) {
                final JSONArray fileIds = (fileType != null) ? requestBody.optJSONArray(paramName) : null;
                if (fileIds != null && fileIds.length() > 0) {
                    for (int index2 = 0; index2 < fileIds.length(); ++index2) {
                        fileMap.put(fileIds.getLong(index2), fileType);
                    }
                }
            }
            else {
                final Long fileId = (fileType != null) ? Long.valueOf(requestBody.optLong(paramName)) : null;
                fileMap.put(fileId, fileType);
            }
        }
        return fileMap;
    }
    
    private Object getRequestBody(final HttpServletRequest hreq, final String dataType) throws Exception {
        byte[] byteBuf = null;
        final String input = hreq.getParameter("zoho-inputstream");
        if (input != null) {
            byteBuf = input.getBytes();
        }
        else if (!hreq.getInputStream().isFinished()) {
            byteBuf = IOUtils.toByteArray((InputStream)hreq.getInputStream());
        }
        Object requestBody = null;
        if (dataType.contains("JSONObject")) {
            requestBody = APIUtil.getNewInstance().wrapUserJSONToServerJSON(new JSONObject(new String(byteBuf, "UTF-8")));
        }
        else if (dataType.contains("Array")) {
            requestBody = APIUtil.getNewInstance().wrapUserJSONToServerJSON(new JSONArray(new String(byteBuf, "UTF-8")));
        }
        return requestBody;
    }
    
    private void validateFileWithContentType(final Map<Long, String> fileMap, final Long customerId) throws Exception {
        final List<Long> fileIds = new ArrayList<Long>(fileMap.keySet());
        final SelectQuery selectQuery = FileFacade.getInstance().getFileBaseQuery(customerId);
        Criteria criteria = new Criteria(Column.getColumn("DMFiles", "FILE_ID"), (Object)fileIds.toArray(), 8);
        if (selectQuery.getCriteria() != null) {
            criteria = criteria.and(selectQuery.getCriteria());
        }
        selectQuery.setCriteria(criteria);
        final DataObject dataObject = MDMUtil.getPersistence().get(selectQuery);
        final Iterator<Row> fileRows = dataObject.getRows("DMFiles");
        while (fileRows.hasNext()) {
            final Row fileRow = fileRows.next();
            final int fileStatus = (int)fileRow.get("FILE_STATUS");
            if (fileStatus != 2) {
                throw new APIHTTPException("COM0014", new Object[] { (fileStatus == 2) ? I18NUtil.transformRemarks("mdm.file.unavailable.reason", (String)dataObject.getFirstValue("DMFiles", "FILE_STATUS_REMARKS")) : I18NUtil.transformRemarks("mdm.file.unavailabel.status", "") });
            }
            final Long fileId = (Long)fileRow.get("FILE_ID");
            final String fileType = fileMap.get(fileId);
            final String filePath = (String)fileRow.get("FILE_SYSTEM_LOCATION");
            boolean isValid = false;
            final String s = fileType;
            switch (s) {
                case "image": {
                    isValid = this.validateIfImageFile(filePath);
                    break;
                }
                case "certificate": {
                    isValid = this.validateIfCertificateFile(filePath);
                    break;
                }
                case "enterprise_app": {
                    isValid = this.validateIfApplicationFile(filePath);
                    break;
                }
                case "csv": {
                    isValid = this.validateIfCsvFile(filePath);
                    break;
                }
                case "html": {
                    isValid = this.validateIfHtmlFile(filePath);
                    break;
                }
                case "content_mgmt": {
                    isValid = this.validateIfContentMgmtFile(filePath);
                    break;
                }
                case "json": {
                    isValid = this.validateIfJSONFile(filePath);
                    break;
                }
                case "plain": {
                    isValid = this.validateIfPlainFile(filePath);
                    break;
                }
                case "xml": {
                    isValid = this.validateIfXMLFile(filePath);
                    break;
                }
                case "mac_font": {
                    isValid = this.validateMacFontFile(filePath);
                    break;
                }
                case "custom_profile": {
                    isValid = this.validateCustomProfileFile(filePath);
                    break;
                }
            }
            if (!isValid) {
                throw new APIHTTPException("FIL0002", new Object[0]);
            }
            fileIds.remove(fileId);
        }
        if (!fileIds.isEmpty()) {
            throw new APIHTTPException("COM0008", new Object[] { "File Ids: " + fileIds.toString() });
        }
    }
    
    public boolean validateIfImageFile(final String filePath) throws Exception {
        final InputStream inputStream = ApiFactoryProvider.getFileAccessAPI().readFile(filePath);
        final Tika tika = new Tika();
        final String contentType = tika.detect(inputStream);
        return APIUtil.isAllowedImageMimeType(contentType);
    }
    
    public boolean validateIfCertificateFile(final String filePath) throws Exception {
        final InputStream inputStream = ApiFactoryProvider.getFileAccessAPI().readFile(filePath);
        final Tika tika = new Tika();
        final String contentType = tika.detect(inputStream);
        if (!APIUtil.isAllowedCertificateMimeType(contentType)) {
            final InputStream certificateStream = ApiFactoryProvider.getFileAccessAPI().readFile(filePath);
            final Certificate[] certificate = CertificateUtil.convertInputStreamToX509CertificateChain(certificateStream);
            return certificate.length > 0;
        }
        return true;
    }
    
    public boolean validateIfApplicationFile(final String filePath) throws Exception {
        final InputStream inputStream = ApiFactoryProvider.getFileAccessAPI().readFile(filePath);
        final Tika tika = new Tika();
        final String contentType = tika.detect(inputStream);
        return APIUtil.isAllowedApplicationMimeType(contentType);
    }
    
    public boolean validateIfCsvFile(final String filePath) throws Exception {
        final InputStream inputStream = ApiFactoryProvider.getFileAccessAPI().readFile(filePath);
        final Tika tika = new Tika();
        final String contentType = tika.detect(inputStream);
        return APIUtil.isAllowedCSVMimeType(contentType);
    }
    
    public boolean validateIfHtmlFile(final String filePath) throws Exception {
        final InputStream inputStream = ApiFactoryProvider.getFileAccessAPI().readFile(filePath);
        final Tika tika = new Tika();
        final String contentType = tika.detect(inputStream);
        return APIUtil.isAllowedHTMLMimeType(contentType);
    }
    
    public boolean validateIfContentMgmtFile(final String filePath) throws Exception {
        final InputStream inputStream = ApiFactoryProvider.getFileAccessAPI().readFile(filePath);
        final Tika tika = new Tika();
        final String contentType = tika.detect(inputStream);
        return APIUtil.isAllowedContentMgmtMimeType(contentType);
    }
    
    public boolean validateIfJSONFile(final String filePath) throws Exception {
        final Tika tika = new Tika();
        final String contentType = tika.detect(filePath);
        return APIUtil.isAllowedJSONMimeType(contentType);
    }
    
    public boolean validateIfXMLFile(final String filePath) throws Exception {
        final InputStream inputStream = ApiFactoryProvider.getFileAccessAPI().readFile(filePath);
        final Tika tika = new Tika();
        final String contentType = tika.detect(inputStream);
        return APIUtil.isAllowedXmlMimeType(contentType);
    }
    
    public boolean validateIfPlainFile(final String filePath) throws Exception {
        final InputStream inputStream = ApiFactoryProvider.getFileAccessAPI().readFile(filePath);
        final Tika tika = new Tika();
        final String contentType = tika.detect(inputStream);
        return contentType.equalsIgnoreCase("text/plain");
    }
    
    public boolean validateMacFontFile(final String filePath) throws Exception {
        new FileFacade().writeFile(filePath, ApiFactoryProvider.getFileAccessAPI().readFileContentAsArray(filePath));
        final File file = new File(filePath);
        final Tika tika = new Tika();
        final String contentType = tika.detect(file);
        return APIUtil.isAllowedFontType(contentType);
    }
    
    public boolean validateCustomProfileFile(final String filePath) throws Exception {
        final InputStream inputStream = ApiFactoryProvider.getFileAccessAPI().readFile(filePath);
        final Tika tika = new Tika();
        final String contentType = tika.detect(inputStream);
        return APIUtil.isAllowedCustomProfileType(contentType);
    }
    
    static {
        LOGGER = Logger.getLogger(MDMFileValidationFilter.class.getName());
    }
}
