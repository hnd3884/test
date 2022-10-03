package com.adventnet.sym.server.mdm.terms;

import java.util.Enumeration;
import java.util.Properties;
import com.me.devicemanagement.framework.server.util.SyMUtil;
import java.net.URLDecoder;
import java.nio.file.Files;
import java.io.OutputStream;
import java.io.BufferedOutputStream;
import com.me.mdm.server.deploy.MDMMetaDataUtil;
import java.io.File;
import com.me.mdm.api.APIRequest;
import java.util.Iterator;
import java.util.List;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import com.me.mdm.files.FileFacade;
import com.me.mdm.files.upload.FileUploadManager;
import com.adventnet.sym.server.mdm.util.JSONUtil;
import java.util.ArrayList;
import java.util.logging.Level;
import com.me.mdm.api.error.APIHTTPException;
import java.util.Collection;
import org.json.JSONArray;
import java.util.Arrays;
import com.me.devicemanagement.framework.server.customer.CustomerInfoUtil;
import com.me.mdm.api.APIUtil;
import org.json.JSONObject;
import java.util.logging.Logger;

public class TermsOfUseFacade
{
    public Logger logger;
    
    public TermsOfUseFacade() {
        this.logger = Logger.getLogger("MDMLogger");
    }
    
    public JSONObject getTermsDetails(final JSONObject requestJSON) throws Exception {
        try {
            final Long termsID = APIUtil.getResourceID(requestJSON, "terms_of_us_id");
            final JSONObject termsJSON = MDMTermsHandler.getInstance().getTermsDetails(termsID, CustomerInfoUtil.getInstance().getCustomerId());
            final JSONObject result = (JSONObject)termsJSON.get("0");
            result.remove("DOC_IDS");
            final String termsLang = String.valueOf(result.get("LANGUAGES"));
            result.remove("LANGUAGES");
            result.put("LANGUAGES", (Object)new JSONArray((Collection)Arrays.asList(termsLang.split(","))));
            return result;
        }
        catch (final Exception e) {
            if (e instanceof APIHTTPException) {
                throw (APIHTTPException)e;
            }
            this.logger.log(Level.SEVERE, "error in getting terms of use details...", e);
            throw new APIHTTPException("TER0005", new Object[0]);
        }
    }
    
    public JSONObject removeTerms(final JSONObject requestJSON) throws Exception {
        try {
            final JSONObject result = new JSONObject();
            final String removedTermsName = MDMTermsHandler.getInstance().removeTerms(APIUtil.getResourceID(requestJSON, "terms_of_us_id"));
            result.put("success", true);
            result.put("removed_terms_name", (Object)removedTermsName);
            return result;
        }
        catch (final Exception e) {
            if (e instanceof APIHTTPException) {
                throw (APIHTTPException)e;
            }
            this.logger.log(Level.SEVERE, "error in deleting terms of use...", e);
            throw new APIHTTPException("TER0003", new Object[0]);
        }
    }
    
    public JSONObject saveTerms(final JSONObject jsonObject) throws Exception {
        try {
            final JSONObject result = new JSONObject();
            final JSONObject inputJSON = new JSONObject();
            final List<String> existingLang = new ArrayList<String>();
            final List<String> editedLang = new ArrayList<String>();
            final JSONObject body = jsonObject.getJSONObject("msg_body");
            final List<String> termsLang = new ArrayList<String>();
            final JSONArray termsLangArray = body.getJSONArray("terms_languages");
            String terms_languages = "";
            String editedLangStr = "";
            for (int i = 0; i < termsLangArray.length(); ++i) {
                final String individualLang = String.valueOf(termsLangArray.get(i));
                termsLang.add(individualLang);
                terms_languages = ((i == 0) ? individualLang : (terms_languages + "," + individualLang));
            }
            Long termsID = APIUtil.getResourceID(jsonObject, "terms_of_us_id");
            termsID = ((termsID == null) ? -1L : termsID);
            if (termsID != -1L) {
                inputJSON.put("TERMS_ID", (Object)termsID);
                final JSONObject currentDetails = MDMTermsHandler.getInstance().getTermsDetails(termsID, CustomerInfoUtil.getInstance().getCustomerId()).getJSONObject("0");
                inputJSON.put("TERMS_JSON", (Object)currentDetails.toString());
                final String[] langArray = String.valueOf(currentDetails.get("LANGUAGES")).split(",");
                String language = "";
                String termsLangBeforeEdit = "";
                for (int j = 0; j < langArray.length; ++j) {
                    language = langArray[j];
                    if (!language.equalsIgnoreCase("")) {
                        existingLang.add(language);
                        termsLangBeforeEdit = (termsLangBeforeEdit.equalsIgnoreCase("") ? language : (termsLangBeforeEdit + "," + language));
                    }
                }
                termsLang.removeAll(existingLang);
                inputJSON.put("TERMS_LANGUAGES_BEFORE_EDIT", (Object)termsLangBeforeEdit);
                JSONArray editedLanguagesArray = body.optJSONArray("edited_lang");
                editedLanguagesArray = ((editedLanguagesArray == null) ? new JSONArray() : editedLanguagesArray);
                for (int k = 0; k < editedLanguagesArray.length(); ++k) {
                    final String editedLanguage = String.valueOf(editedLanguagesArray.get(k));
                    editedLang.add(editedLanguage);
                    editedLangStr = (editedLangStr.equalsIgnoreCase("") ? editedLanguage : (editedLangStr + "," + editedLanguage));
                }
                termsLang.addAll(editedLang);
            }
            else {
                inputJSON.put("TERMS_ID", -1L);
                inputJSON.put("EDITED_LANG", (Object)"");
                inputJSON.put("TERMS_LANGUAGES_BEFORE_EDIT", (Object)"");
            }
            final int input_type = body.getInt("input_type");
            for (int l = 0; l < termsLang.size(); ++l) {
                if (input_type == 1) {
                    final String FILE_UPLOAD = String.valueOf(FileUploadManager.getFilePath(JSONUtil.toJSON("file_id", Long.valueOf(body.get("doc_upload_" + termsLang.get(l).toLowerCase()).toString()))).get("file_path"));
                    new FileFacade().writeFile(FILE_UPLOAD, ApiFactoryProvider.getFileAccessAPI().readFileContentAsArray(FILE_UPLOAD));
                    inputJSON.put("DOC_UPLOAD_" + termsLang.get(l), (Object)FILE_UPLOAD);
                }
                else if (input_type == 2) {
                    inputJSON.put("DOC_URL_" + termsLang.get(l), (Object)String.valueOf(body.get("doc_url_" + termsLang.get(l).toLowerCase())));
                }
            }
            final Iterator iter = body.keys();
            if (editedLangStr.equalsIgnoreCase("")) {
                inputJSON.put("EDITED_LANG", (Object)"");
            }
            while (iter.hasNext()) {
                final String key = iter.next();
                if (key.indexOf("doc_upload") == -1 && key.indexOf("doc_url") == -1) {
                    inputJSON.put(key.toUpperCase(), body.get(key));
                }
                if (key.equalsIgnoreCase("terms_languages")) {
                    inputJSON.put(key.toUpperCase(), (Object)terms_languages);
                }
                if (key.equalsIgnoreCase("edited_lang")) {
                    inputJSON.put(key.toUpperCase(), (Object)editedLangStr);
                }
                if (key.equalsIgnoreCase("apply_terms")) {
                    switch (body.getInt(key)) {
                        case 1: {
                            inputJSON.put(key.toUpperCase(), (Object)"Corporate Devices");
                            continue;
                        }
                        case 2: {
                            inputJSON.put(key.toUpperCase(), (Object)"Personal Devices");
                            continue;
                        }
                        case 3: {
                            inputJSON.put(key.toUpperCase(), (Object)"All Devices");
                            continue;
                        }
                    }
                }
            }
            final Long termsId = MDMTermsHandler.getInstance().addOrUpdateTermsDetails(inputJSON);
            if (termsId != -1L && termsID == -1L) {
                result.put("TERMS_ID", (Object)termsId);
            }
            else {
                if (termsId == -1L) {
                    throw new APIHTTPException("TER0001", new Object[0]);
                }
                result.put("TERMS_ID", (Object)termsId);
                result.put("success", true);
            }
            return result;
        }
        catch (final Exception e) {
            if (e instanceof APIHTTPException) {
                throw (APIHTTPException)e;
            }
            this.logger.log(Level.SEVERE, "error in saving terms of use...", e);
            throw new APIHTTPException("TER0001", new Object[0]);
        }
    }
    
    public JSONArray getTerms(final JSONObject jsonObject) throws Exception {
        try {
            final JSONObject result = MDMTermsHandler.getInstance().getTermsDetails(-1L, CustomerInfoUtil.getInstance().getCustomerId());
            final JSONArray jsonArray = new JSONArray();
            for (int i = 0; i < result.getInt("count"); ++i) {
                final JSONObject termsjson = result.getJSONObject(String.valueOf(i));
                termsjson.remove("DOC_IDS");
                final String termsLang = String.valueOf(termsjson.get("LANGUAGES"));
                termsjson.remove("LANGUAGES");
                termsjson.put("LANGUAGES", (Object)new JSONArray((Collection)Arrays.asList(termsLang.split(","))));
                jsonArray.put((Object)termsjson);
            }
            return jsonArray;
        }
        catch (final Exception e) {
            if (e instanceof APIHTTPException) {
                throw (APIHTTPException)e;
            }
            this.logger.log(Level.SEVERE, "error in getting all terms of use details...", e);
            throw new APIHTTPException("TER0002", new Object[0]);
        }
    }
    
    public void downloadSampleTerms(final APIRequest apiRequest) throws Exception {
        BufferedOutputStream buffOut = null;
        try {
            apiRequest.httpServletResponse.setContentType("application/html");
            apiRequest.httpServletResponse.setHeader("Content-Disposition", "attachment; filename=TermsOfUse_Sample.html");
            final String path = File.separatorChar + "html" + File.separatorChar + "terms_sample.html";
            final String filepath = MDMMetaDataUtil.getInstance().getClientDataParentDir() + path;
            buffOut = new BufferedOutputStream((OutputStream)apiRequest.httpServletResponse.getOutputStream());
            final File file = new File(filepath);
            final byte[] b = Files.readAllBytes(file.toPath());
            buffOut.write(b);
            buffOut.flush();
        }
        catch (final Exception exp) {
            this.logger.log(Level.SEVERE, "Exception while download sample file", exp);
            throw new APIHTTPException("TER0004", new Object[0]);
        }
        finally {
            try {
                buffOut.close();
            }
            catch (final Exception e) {
                this.logger.log(Level.SEVERE, "Exception while closing streams", e);
                throw new APIHTTPException("TER0004", new Object[0]);
            }
        }
    }
    
    public void downloadTerms(final APIRequest apiRequest) throws Exception {
        BufferedOutputStream buffOut = null;
        try {
            final JSONObject requestJSON = apiRequest.toJSONObject();
            final Long termsID = APIUtil.getResourceID(requestJSON, "terms_of_us_id");
            final String lang = URLDecoder.decode(requestJSON.getJSONObject("msg_header").getJSONObject("filters").optString("language"), "UTF-8");
            final JSONObject json = MDMTermsHandler.getInstance().getTermsDetails(termsID, CustomerInfoUtil.getInstance().getCustomerId());
            final JSONObject termsjson = (JSONObject)json.get("0");
            final JSONArray docPaths = (JSONArray)termsjson.get("DOC_PATHS");
            int index = -1;
            if (!lang.equalsIgnoreCase("")) {
                final ArrayList<String> languages = new ArrayList<String>(Arrays.asList(String.valueOf(termsjson.get("LANGUAGES")).split(",")));
                index = languages.indexOf(lang);
            }
            apiRequest.httpServletResponse.setContentType("text/html");
            final String type = String.valueOf(docPaths.get(index)).substring(String.valueOf(docPaths.get(index)).lastIndexOf(46) + 1);
            apiRequest.httpServletResponse.setHeader("Content-Disposition", "attachment; filename=TermsOfUse_" + lang + "." + type);
            String path = String.valueOf(docPaths.get(index));
            buffOut = new BufferedOutputStream((OutputStream)apiRequest.httpServletResponse.getOutputStream());
            CustomerInfoUtil.getInstance();
            if (CustomerInfoUtil.isSAS()) {
                path = path.substring(1).replace('/', File.separatorChar);
            }
            else {
                path = MDMMetaDataUtil.getInstance().getClientDataParentDir() + path;
            }
            final byte[] b = ApiFactoryProvider.getFileAccessAPI().readFileContentAsArray(path);
            buffOut.write(b);
            buffOut.flush();
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "Exception while download sample file", e);
            throw new APIHTTPException("TER0004", new Object[0]);
        }
        finally {
            try {
                buffOut.close();
            }
            catch (final Exception e2) {
                this.logger.log(Level.SEVERE, "Exception while closing streams", e2);
                throw new APIHTTPException("TER0004", new Object[0]);
            }
        }
    }
    
    public JSONObject checkIfTermsExistsForOwnedBy(final JSONObject requestJSON) throws Exception {
        final JSONObject result = new JSONObject();
        final Long termsID = Long.parseLong(requestJSON.getJSONObject("msg_header").getJSONObject("filters").optString("terms_id", "-1"));
        final int ownedby = requestJSON.getJSONObject("msg_header").getJSONObject("filters").optInt("owned_by");
        final JSONObject jsonObject = MDMTermsHandler.getInstance().checkIfTermsExistForOwnedBy(termsID, ownedby, CustomerInfoUtil.getInstance().getCustomerId());
        final int success = jsonObject.getInt("success");
        if (success == -1) {
            final int exists = jsonObject.getInt("exists");
            result.put("terms_already_exist_for_ownedby", true);
            if (exists == 3) {
                result.put("already_existing_group", 3);
            }
            else if (exists == 2) {
                result.put("already_existing_group", 2);
            }
            else if (exists == 1) {
                result.put("already_existing_group", 1);
            }
        }
        else {
            result.put("terms_already_exist_for_ownedby", false);
        }
        return result;
    }
    
    public JSONObject getSupportedLanguages() throws Exception {
        final JSONObject json = new JSONObject();
        final Properties props = SyMUtil.getLocalesProperties();
        final Enumeration e = props.propertyNames();
        while (e.hasMoreElements()) {
            final String key = e.nextElement();
            json.put(key, (Object)props.getProperty(key));
        }
        return json;
    }
}
