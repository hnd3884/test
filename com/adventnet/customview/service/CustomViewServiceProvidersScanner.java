package com.adventnet.customview.service;

import java.io.IOException;
import org.xml.sax.SAXException;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.DocumentBuilder;
import org.xml.sax.InputSource;
import java.util.Properties;
import com.adventnet.iam.security.SecurityUtil;
import org.w3c.dom.Text;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import com.adventnet.persistence.DataAccess;
import com.adventnet.persistence.DataAccessException;
import org.w3c.dom.Element;
import com.adventnet.persistence.Row;
import com.adventnet.persistence.DataObject;
import org.w3c.dom.Document;

public class CustomViewServiceProvidersScanner
{
    Document document;
    private static final Integer[] INDICES;
    private String cvType;
    private Integer mode;
    private int index;
    private String vmString;
    DataObject cvServiceProvidersDO;
    Row cvspRow;
    Row msspRow;
    
    public CustomViewServiceProvidersScanner(final Document document) {
        this.document = document;
    }
    
    public DataObject getCustomViewServiceProviders() {
        return this.cvServiceProvidersDO;
    }
    
    public void visitDocument() throws DataAccessException {
        final Element element = this.document.getDocumentElement();
        if (element != null && element.getTagName().equals("customview-service-providers")) {
            this.visitElement_customview_service_providers(element);
        }
        if (element != null && element.getTagName().equals("customview-type")) {
            this.visitElement_customview_type(element);
        }
        if (element != null && element.getTagName().equals("core-client-service")) {
            this.visitElement_core_client_service(element);
        }
        if (element != null && element.getTagName().equals("core-server-service")) {
            this.visitElement_core_server_service(element);
        }
        if (element != null && element.getTagName().equals("remote-session-bean")) {
            this.visitElement_remote_session_bean(element);
        }
        if (element != null && element.getTagName().equals("local-session-bean")) {
            this.visitElement_local_session_bean(element);
        }
        if (element != null && element.getTagName().equals("add-on-services")) {
            this.visitElement_add_on_services(element);
        }
        if (element != null && element.getTagName().equals("client")) {
            this.visitElement_client(element);
        }
        if (element != null && element.getTagName().equals("server")) {
            this.visitElement_server(element);
        }
        if (element != null && element.getTagName().equals("service-provider")) {
            this.visitElement_service_provider(element);
        }
    }
    
    void visitElement_customview_service_providers(final Element element) throws DataAccessException {
        this.cvServiceProvidersDO = DataAccess.constructDataObject();
        this.cvspRow = new Row("CustomViewServiceProviders");
        final NodeList nodes = element.getChildNodes();
        for (int i = 0; i < nodes.getLength(); ++i) {
            final Node node = nodes.item(i);
            switch (node.getNodeType()) {
                case 1: {
                    final Element nodeElement = (Element)node;
                    if (nodeElement.getTagName().equals("customview-type")) {
                        this.visitElement_customview_type(nodeElement);
                    }
                    if (nodeElement.getTagName().equals("core-client-service")) {
                        this.visitElement_core_client_service(nodeElement);
                    }
                    if (nodeElement.getTagName().equals("core-server-service")) {
                        this.visitElement_core_server_service(nodeElement);
                    }
                    if (nodeElement.getTagName().equals("remote-session-bean")) {
                        this.visitElement_remote_session_bean(nodeElement);
                    }
                    if (nodeElement.getTagName().equals("local-session-bean")) {
                        this.visitElement_local_session_bean(nodeElement);
                        break;
                    }
                    break;
                }
            }
        }
    }
    
    void visitElement_customview_type(final Element element) throws DataAccessException {
        final NodeList nodes = element.getChildNodes();
        for (int i = 0; i < nodes.getLength(); ++i) {
            final Node node = nodes.item(i);
            switch (node.getNodeType()) {
                case 1: {
                    final Element nodeElement = (Element)node;
                }
                case 3: {
                    this.cvType = ((Text)node).getData();
                    this.cvspRow.set("CVTYPE", (Object)this.cvType);
                    break;
                }
            }
        }
    }
    
    void visitElement_core_client_service(final Element element) throws DataAccessException {
        final NodeList nodes = element.getChildNodes();
        for (int i = 0; i < nodes.getLength(); ++i) {
            final Node node = nodes.item(i);
            switch (node.getNodeType()) {
                case 1: {
                    final Element nodeElement = (Element)node;
                }
                case 3: {
                    this.cvspRow.set("CORECLIENTSERVICE", (Object)((Text)node).getData());
                    break;
                }
            }
        }
    }
    
    void visitElement_core_server_service(final Element element) throws DataAccessException {
        final NodeList nodes = element.getChildNodes();
        for (int i = 0; i < nodes.getLength(); ++i) {
            final Node node = nodes.item(i);
            switch (node.getNodeType()) {
                case 1: {
                    final Element nodeElement = (Element)node;
                }
                case 3: {
                    this.cvspRow.set("CORESERVERSERVICE", (Object)((Text)node).getData());
                    this.cvServiceProvidersDO.addRow(this.cvspRow);
                    break;
                }
            }
        }
    }
    
    void visitElement_remote_session_bean(final Element element) throws DataAccessException {
        (this.msspRow = new Row("ModeSpecificServiceProviders")).set("CVTYPE", this.cvspRow.get("CVTYPE"));
        this.mode = new Integer(2);
        this.msspRow.set("COMM_MODE", (Object)this.mode);
        this.cvServiceProvidersDO.addRow(this.msspRow);
        final NodeList nodes = element.getChildNodes();
        for (int i = 0; i < nodes.getLength(); ++i) {
            final Node node = nodes.item(i);
            switch (node.getNodeType()) {
                case 1: {
                    final Element nodeElement = (Element)node;
                    if (nodeElement.getTagName().equals("add-on-services")) {
                        this.visitElement_add_on_services(nodeElement);
                        break;
                    }
                    break;
                }
            }
        }
    }
    
    void visitElement_local_session_bean(final Element element) throws DataAccessException {
        (this.msspRow = new Row("ModeSpecificServiceProviders")).set("CVTYPE", this.cvspRow.get("CVTYPE"));
        this.mode = new Integer(1);
        this.msspRow.set("COMM_MODE", (Object)this.mode);
        this.cvServiceProvidersDO.addRow(this.msspRow);
        final NodeList nodes = element.getChildNodes();
        for (int i = 0; i < nodes.getLength(); ++i) {
            final Node node = nodes.item(i);
            switch (node.getNodeType()) {
                case 1: {
                    final Element nodeElement = (Element)node;
                    if (nodeElement.getTagName().equals("add-on-services")) {
                        this.visitElement_add_on_services(nodeElement);
                        break;
                    }
                    break;
                }
            }
        }
    }
    
    void visitElement_add_on_services(final Element element) throws DataAccessException {
        final NodeList nodes = element.getChildNodes();
        for (int i = 0; i < nodes.getLength(); ++i) {
            final Node node = nodes.item(i);
            switch (node.getNodeType()) {
                case 1: {
                    final Element nodeElement = (Element)node;
                    if (nodeElement.getTagName().equals("client")) {
                        this.index = 0;
                        this.vmString = "Client";
                        this.visitElement_client(nodeElement);
                    }
                    if (nodeElement.getTagName().equals("server")) {
                        this.index = 0;
                        this.vmString = "Server";
                        this.visitElement_server(nodeElement);
                        break;
                    }
                    break;
                }
            }
        }
    }
    
    void visitElement_client(final Element element) throws DataAccessException {
        final NodeList nodes = element.getChildNodes();
        for (int i = 0; i < nodes.getLength(); ++i) {
            final Node node = nodes.item(i);
            switch (node.getNodeType()) {
                case 1: {
                    final Element nodeElement = (Element)node;
                    if (nodeElement.getTagName().equals("service-provider")) {
                        this.visitElement_service_provider(nodeElement);
                        break;
                    }
                    break;
                }
            }
        }
    }
    
    void visitElement_server(final Element element) throws DataAccessException {
        final NodeList nodes = element.getChildNodes();
        for (int i = 0; i < nodes.getLength(); ++i) {
            final Node node = nodes.item(i);
            switch (node.getNodeType()) {
                case 1: {
                    final Element nodeElement = (Element)node;
                    if (nodeElement.getTagName().equals("service-provider")) {
                        this.visitElement_service_provider(nodeElement);
                        break;
                    }
                    break;
                }
            }
        }
    }
    
    void visitElement_service_provider(final Element element) throws DataAccessException {
        final NodeList nodes = element.getChildNodes();
        for (int i = 0; i < nodes.getLength(); ++i) {
            final Node node = nodes.item(i);
            switch (node.getNodeType()) {
                case 1: {
                    final Element nodeElement = (Element)node;
                }
                case 3: {
                    final Row spRow = new Row(this.vmString + "ServiceProviders");
                    spRow.set("CVTYPE", (Object)this.cvType);
                    spRow.set("COMM_MODE", (Object)this.mode);
                    spRow.set("SERVICEPROVIDER", (Object)((Text)node).getData());
                    spRow.set("SPINDEX", (Object)CustomViewServiceProvidersScanner.INDICES[this.index++]);
                    this.cvServiceProvidersDO.addRow(spRow);
                    break;
                }
            }
        }
    }
    
    public static void main(final String[] args) throws ParserConfigurationException, SAXException, DataAccessException, IOException {
        if (args == null || args.length != 1) {
            usage();
            return;
        }
        final DocumentBuilder builder = SecurityUtil.createDocumentBuilder(true, false, (Properties)null);
        final Document document = builder.parse(new InputSource(args[0]));
        final CustomViewServiceProvidersScanner scanner = new CustomViewServiceProvidersScanner(document);
        scanner.visitDocument();
        System.out.println(" scanner.getCustomViewServiceProviders().....");
        System.out.println(scanner.getCustomViewServiceProviders());
    }
    
    public static void usage() {
        System.out.println(" java ServiceProvidersConfigScanner ServiceProvidersConfigFile");
    }
    
    static {
        INDICES = new Integer[] { new Integer(0), new Integer(1), new Integer(2), new Integer(3), new Integer(4), new Integer(5), new Integer(6), new Integer(7), new Integer(8), new Integer(9) };
    }
}
