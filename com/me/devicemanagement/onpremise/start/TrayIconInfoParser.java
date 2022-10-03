package com.me.devicemanagement.onpremise.start;

import java.util.Hashtable;
import java.util.Iterator;
import java.util.logging.Level;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.NodeList;
import java.util.ArrayList;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import java.io.File;
import com.me.devicemanagement.framework.utils.XMLUtils;
import java.util.Properties;
import java.util.HashMap;
import org.w3c.dom.Document;
import javax.xml.parsers.DocumentBuilder;
import java.util.logging.Logger;

public class TrayIconInfoParser
{
    private static final Logger LOGGER;
    private static final String PROCESS = "PROCESS";
    private static final String TRAYICON_PROPS = "TRAYICON-PROPS";
    private static final String TRAYICON_MENU = "TRAYICON-MENU";
    DocumentBuilder dBuild;
    Document doc;
    HashMap processInfos;
    Properties trayIconInfo;
    
    public TrayIconInfoParser(final String fileName) throws Exception {
        this.dBuild = null;
        this.doc = null;
        this.processInfos = new HashMap();
        this.trayIconInfo = new Properties();
        this.dBuild = XMLUtils.getDocumentBuilderInstance();
        this.readXmlFile(fileName);
    }
    
    void readXmlFile(final String fileName) throws Exception {
        this.doc = this.dBuild.parse(new File(fileName));
        final Node rootNode = this.doc.getDocumentElement();
        this.updateProcessInfo(rootNode);
        this.updateTrayIconInfo(rootNode);
    }
    
    void updateTrayIconInfo(final Node rootNode) {
        final NodeList trayIconProps = ((Element)rootNode).getElementsByTagName("TRAYICON-PROPS");
        final Element trayIconPropsElem = (Element)trayIconProps.item(0);
        this.trayIconInfo = this.getAttributesAsProperties(trayIconPropsElem);
        final NodeList trayIconMenuList = ((Element)rootNode).getElementsByTagName("TRAYICON-MENU");
        final Element trayIconMenuElem = (Element)trayIconMenuList.item(0);
        final NodeList trayIconMenuItemList = trayIconMenuElem.getElementsByTagName("MENUITEM");
        final ArrayList menus = new ArrayList();
        for (int i = 0; i < trayIconMenuItemList.getLength(); ++i) {
            final Element trayIconMenuItem = (Element)trayIconMenuItemList.item(i);
            final Properties trayIconMenuItemProps = this.getAttributesAsProperties(trayIconMenuItem);
            menus.add(trayIconMenuItemProps);
        }
        ((Hashtable<String, ArrayList>)this.trayIconInfo).put("Menu", menus);
    }
    
    public Properties getAttributesAsProperties(final Element ele) {
        final Properties props = new Properties();
        final NamedNodeMap nnMap = ele.getAttributes();
        for (int i = 0; i < nnMap.getLength(); ++i) {
            final Node node = nnMap.item(i);
            props.setProperty(node.getNodeName(), node.getNodeValue());
        }
        return props;
    }
    
    void updateProcessInfo(final Node rootNode) {
        final NodeList processList = ((Element)rootNode).getElementsByTagName("PROCESS");
        for (int totProcs = processList.getLength(), i = 0; i < totProcs; ++i) {
            final Element proccessElem = (Element)processList.item(i);
            final String processName = proccessElem.getAttribute("Name");
            final NodeList commandList = proccessElem.getElementsByTagName("COMMAND");
            final int totComms = commandList.getLength();
            final ArrayList commands = new ArrayList(totComms);
            for (int j = 0; j < totComms; ++j) {
                final Element commElem = (Element)commandList.item(j);
                final Properties commandStruct = this.getAttributesAsProperties(commElem);
                String originalCommand = new String();
                final ArrayList envVaribles = new ArrayList();
                final Properties additionalParams = new Properties();
                final NodeList comms = commElem.getChildNodes();
                for (int totCommands = comms.getLength(), k = 0; k < totCommands; ++k) {
                    final Node comm = comms.item(k);
                    if (comm.getNodeType() == 4) {
                        originalCommand = comms.item(k).getNodeValue();
                    }
                    if (comm.getNodeName().equals("ENVIRONMENT-VARIABLE")) {
                        final String envVar = ((Element)comm).getAttribute("Variable");
                        envVaribles.add(envVar);
                    }
                    if (comm.getNodeName().equals("ADDITIONALPARAMS")) {
                        final String paramName = ((Element)comm).getAttribute("ParamName");
                        final String paramValue = ((Element)comm).getAttribute("ParamValue");
                        additionalParams.setProperty(paramName, paramValue);
                    }
                }
                commandStruct.setProperty("OriginalCommand", originalCommand);
                ((Hashtable<String, ArrayList>)commandStruct).put("EnvironmentVariables", envVaribles);
                ((Hashtable<String, Properties>)commandStruct).put("AdditionalParams", additionalParams);
                commands.add(commandStruct);
            }
            this.processInfos.put(processName, commands);
        }
    }
    
    public Properties getTrayIconInfo() {
        return this.trayIconInfo;
    }
    
    public HashMap getAllProcessInfos() {
        return this.processInfos;
    }
    
    public ArrayList getProcessInfo(final String processName) {
        return this.processInfos.get(processName);
    }
    
    public static void main(final String[] args) throws Exception {
        final TrayIconInfoParser parser = new TrayIconInfoParser(args[0]);
        final ArrayList commands = parser.getProcessInfo(args[1]);
        for (final Properties command : commands) {
            TrayIconInfoParser.LOGGER.log(Level.INFO, "command " + command);
            TrayIconInfoParser.LOGGER.log(Level.INFO, "");
        }
        TrayIconInfoParser.LOGGER.log(Level.INFO, "parser.getTrayIconInfo " + parser.getTrayIconInfo());
    }
    
    static {
        LOGGER = Logger.getLogger(TrayIconInfoParser.class.getName());
    }
}
