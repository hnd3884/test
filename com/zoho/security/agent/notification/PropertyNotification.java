package com.zoho.security.agent.notification;

import javax.xml.transform.Transformer;
import java.util.Iterator;
import org.w3c.dom.Element;
import org.w3c.dom.Document;
import javax.xml.parsers.DocumentBuilder;
import com.zoho.security.agent.LocalConfigurations;
import java.io.IOException;
import javax.xml.transform.TransformerException;
import java.util.logging.Level;
import com.zoho.security.agent.AppSenseFileStore;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import com.zoho.security.agent.AppSenseConstants;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.TransformerFactory;
import org.w3c.dom.Node;
import com.adventnet.iam.security.SecurityFrameworkUtil;
import com.zoho.security.agent.AppSenseAgent;
import org.json.JSONObject;
import com.zoho.security.agent.Components;
import java.util.logging.Logger;

public class PropertyNotification extends DefaultNotificationReceiver
{
    private static final String SECURTIY_TAG = "security";
    private static final String PROPERTIES_TAG = "properties";
    private static final String PROPERTY_TAG = "property";
    public static final boolean ENABLE_CSP_REPORT_DV = true;
    public static final String CSPREPORT_URI_DV = "https://logsapi.localzoho.com/csplog";
    public static final boolean ENABLE_REQ_INFO_FILEHASH_DV = true;
    public static final boolean ENABLE_SECXMLPUSH_DV = false;
    public static final boolean ENABLE_CA_CERT_PUSH_DV = false;
    public static final boolean MILESTONE_VERSION_PUSH_DV = true;
    public static final Logger LOGGER;
    
    @Override
    public boolean receive(final Components.COMPONENT component, final Components.COMPONENT_NAME subComponent, final JSONObject dataObj) {
        final String value = dataObj.getString("VALUE");
        switch (subComponent) {
            case ENABLE_CSP_REPORT: {
                AppSenseAgent.setEnableCSPReport(Boolean.parseBoolean(value));
                break;
            }
            case CSP_REPORT_URI: {
                AppSenseAgent.setCSPReportURI(value);
                break;
            }
            case ENABLE_CACERT_PUSH: {
                AppSenseAgent.setEnableCACertPush(Boolean.parseBoolean(value));
                break;
            }
            case ENABLE_MILESTONEVERSION_PUSH: {
                AppSenseAgent.setMilestoneVersionPush(Boolean.parseBoolean(value));
                break;
            }
            case ENABLE_SECXML_PUSH: {
                AppSenseAgent.setEnableSecurityXMLPush(Boolean.parseBoolean(value));
                break;
            }
            case ENBALE_REQINFO_FILEHASH: {
                AppSenseAgent.setEnableReqInfoFileHash(Boolean.parseBoolean(value));
                break;
            }
            case REQINFO_FILEHASH_ALGO: {
                AppSenseAgent.setRequestInfoFileHashAlgorithm(value);
                break;
            }
            case SECRET_REQ_PARAM_NAMES:
            case SECRET_REQ_HEADER_NAMES:
            case SECRET_RES_HEADER_NAMES:
            case ENABLE_APP_FIREWALL: {
                AppSenseAgent.setProperty(subComponent, value);
                break;
            }
            default: {
                return false;
            }
        }
        return true;
    }
    
    public static void saveProperties() {
        boolean createDefaultProperties = false;
        boolean configpush = false;
        final DocumentBuilder xmlDocument = SecurityFrameworkUtil.getDocumentBuilder();
        final Document doc = xmlDocument.newDocument();
        final Element rootElement = doc.createElement("security");
        final Element properties = doc.createElement("properties");
        for (final Components.COMPONENT_NAME subcomponent : Components.COMPONENT.PROPERTY.getSubComponents()) {
            final String name = subcomponent.getValue();
            String value = null;
            switch (subcomponent) {
                case ENABLE_CSP_REPORT: {
                    final boolean enableCSPReport = AppSenseAgent.isEnableCSPReport();
                    if (!enableCSPReport) {
                        value = Boolean.toString(enableCSPReport);
                        break;
                    }
                    break;
                }
                case CSP_REPORT_URI: {
                    final String reportURI = AppSenseAgent.getCSPReportURI();
                    if (!reportURI.equals("https://logsapi.localzoho.com/csplog")) {
                        value = reportURI;
                        break;
                    }
                    break;
                }
                case ENABLE_CACERT_PUSH: {
                    final boolean cacertPush = AppSenseAgent.isEnableCACertPush();
                    if (cacertPush) {
                        value = Boolean.toString(cacertPush);
                        configpush = true;
                        break;
                    }
                    break;
                }
                case ENABLE_MILESTONEVERSION_PUSH: {
                    final boolean milestoneVersionPush = AppSenseAgent.isMilestoneVersionPush();
                    if (!milestoneVersionPush) {
                        value = Boolean.toString(milestoneVersionPush);
                        configpush = true;
                        break;
                    }
                    break;
                }
                case ENABLE_SECXML_PUSH: {
                    final boolean enablesecurityXMLPush = AppSenseAgent.isEnableSecurityXMLPush();
                    if (enablesecurityXMLPush) {
                        value = Boolean.toString(enablesecurityXMLPush);
                        configpush = true;
                        break;
                    }
                    break;
                }
                case ENBALE_REQINFO_FILEHASH: {
                    final boolean reqInfoFilehash = AppSenseAgent.isEnableReqInfoFileHash();
                    if (!reqInfoFilehash) {
                        value = Boolean.toString(reqInfoFilehash);
                        break;
                    }
                    break;
                }
                case REQINFO_FILEHASH_ALGO: {
                    final String reqInfoFilehashAlgorithm = AppSenseAgent.getReqInfoFileHashAlgorithm();
                    if (reqInfoFilehashAlgorithm != null && !reqInfoFilehashAlgorithm.equals("MD5")) {
                        value = reqInfoFilehashAlgorithm;
                        break;
                    }
                    break;
                }
                case SECRET_REQ_PARAM_NAMES:
                case SECRET_REQ_HEADER_NAMES:
                case SECRET_RES_HEADER_NAMES:
                case ENABLE_APP_FIREWALL: {
                    if (AppSenseAgent.wafProperties.containsKey(name)) {
                        value = AppSenseAgent.wafProperties.getProperty(name);
                        break;
                    }
                    break;
                }
            }
            if (name != null && value != null) {
                final Element property = doc.createElement("property");
                property.setAttribute("name", name);
                property.setAttribute("value", value);
                properties.appendChild(property);
                createDefaultProperties = true;
            }
        }
        if (createDefaultProperties) {
            try {
                rootElement.appendChild(properties);
                doc.appendChild(rootElement);
                final TransformerFactory transformerFactory = TransformerFactory.newInstance();
                final Transformer transformer = transformerFactory.newTransformer();
                transformer.setOutputProperty("indent", "yes");
                final DOMSource source = new DOMSource(doc);
                final File propetyFile = new File(AppSenseConstants.PROPERTY_TMPFILE);
                if (!propetyFile.exists()) {
                    propetyFile.createNewFile();
                }
                final StreamResult file = new StreamResult(propetyFile);
                transformer.transform(source, file);
                AppSenseFileStore.saveTmpToPersistantFile(AppSenseConstants.PROPERTY_TMPFILE, AppSenseConstants.PROPERTY_FILE);
                PropertyNotification.LOGGER.log(Level.INFO, "AppSense configurations written successfully in local file \"{0}\"", AppSenseConstants.PROPERTY_FILE);
            }
            catch (final TransformerException | IOException e) {
                PropertyNotification.LOGGER.log(Level.SEVERE, "Exception occurred while transforming properties to localfile - {0} ", new Object[] { e.getMessage() });
            }
        }
        else {
            LocalConfigurations.deletFile(LocalConfigurations.getPropertyFileName());
        }
        if (configpush) {
            LocalConfigurations.notifyServerOnModifiedConfigurations();
        }
    }
    
    static {
        LOGGER = Logger.getLogger(PropertyNotification.class.getName());
    }
}
