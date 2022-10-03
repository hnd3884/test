package com.adventnet.cli.config;

import java.io.OutputStream;
import java.io.FileOutputStream;
import org.apache.crimson.tree.XmlDocument;
import java.util.ArrayList;
import java.util.Iterator;
import java.io.IOException;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import javax.xml.parsers.DocumentBuilderFactory;
import java.util.Hashtable;
import org.w3c.dom.Document;

class ConfigXmlWriter
{
    private static final String config_driver = "CONFIG-DRIVER";
    private static final String device = "DEVICE";
    private static final String type = "TYPE";
    private static final String config_list = "CONFIG-LIST";
    private static final String config = "CONFIG";
    private static final String name = "NAME";
    private static final String loginLevel = "LOGINLEVEL";
    private static final String dataInterface = "DATAINTERFACE";
    private static final String cmdTask = "CMDTASK";
    private static final String taskName = "TASKNAME";
    private static final String taskValue = "TASKVALUE";
    private static final String mandatory = "MANDATORY";
    private static final String dataRequired = "DATAREQUIRED";
    private static final String description = "DESCRIPTION";
    private static final String scriptTask = "SCRIPTTASK";
    private static final String scriptType = "SCRIPTTYPE";
    private static final String login_levels = "LOGIN-LEVELS";
    private static final String loginInterface = "LOGININTERFACE";
    private static final String level = "LEVEL";
    private static final String command = "COMMAND";
    private static final String userName = "USERNAME";
    private static final String passwd = "PASSWORD";
    private static final String loginPrompt = "LOGINPROMPT";
    private static final String passwdPrompt = "PASSWORDPROMPT";
    private static final String prompt = "PROMPT";
    private static final String levelExitCmd = "LEVELEXITCMD";
    private Document doc;
    private Hashtable configEntries;
    private Hashtable logLevelEntries;
    private String loginIfc;
    private String dataIfc;
    
    public void setConfigEntries(final Hashtable configEntries) {
        this.configEntries = configEntries;
    }
    
    public void setLogLevelEntries(final Hashtable logLevelEntries) {
        this.logLevelEntries = logLevelEntries;
    }
    
    public void setLoginIfcName(final String loginIfc) {
        this.loginIfc = loginIfc;
    }
    
    public void setDataIfcName(final String dataIfc) {
        this.dataIfc = dataIfc;
    }
    
    public ConfigXmlWriter() {
        this.doc = null;
        this.configEntries = null;
        this.logLevelEntries = null;
        this.loginIfc = null;
        this.dataIfc = null;
    }
    
    public void writeXmlToFile(final String s) throws IOException {
        try {
            this.doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
        }
        catch (final Exception ex) {
            ex.printStackTrace();
            return;
        }
        final Element element = this.doc.createElement("CONFIG-DRIVER");
        this.doc.appendChild(element);
        this.appendConfigLevelTag(element);
        this.writeToFile(s, this.doc);
    }
    
    private void appendConfigLevelTag(final Element element) {
        final Element element2 = this.doc.createElement("LOGIN-LEVELS");
        if (this.loginIfc != null) {
            element2.setAttribute("LOGININTERFACE", this.loginIfc);
        }
        element.appendChild(element2);
        if (this.logLevelEntries != null) {
            this.appendLogLevelsTag(element2);
        }
        final Element element3 = this.doc.createElement("CONFIG-LIST");
        if (this.dataIfc != null) {
            element3.setAttribute("DATAINTERFACE", this.dataIfc);
        }
        element.appendChild(element3);
        if (this.configEntries != null) {
            this.appendConfigTags(element3);
        }
    }
    
    private void appendLogLevelsTag(final Element element) {
        final Iterator iterator = this.logLevelEntries.values().iterator();
        while (iterator.hasNext()) {
            final LoginLevel loginLevel = (LoginLevel)iterator.next();
            if (loginLevel.getParentLevel() == null) {
                this.appendLevelTag(element, loginLevel);
            }
        }
    }
    
    private void appendLevelTag(final Element element, final LoginLevel loginLevel) {
        final Element element2 = this.doc.createElement("LEVEL");
        element2.setAttribute("NAME", loginLevel.getLoginLevel());
        element2.setAttribute("COMMAND", loginLevel.getLoginCommand());
        if (loginLevel.getLoginName() != null) {
            if (!loginLevel.userNameRequired) {
                element2.setAttribute("USERNAME", loginLevel.getLoginName());
            }
            else {
                element2.setAttribute("USERNAME", "");
            }
        }
        if (loginLevel.getLoginPassword() != null) {
            if (!loginLevel.passwordRequired) {
                element2.setAttribute("PASSWORD", loginLevel.getLoginPassword());
            }
            else {
                element2.setAttribute("PASSWORD", "");
            }
        }
        if (loginLevel.getLoginPrompt() != null) {
            element2.setAttribute("LOGINPROMPT", loginLevel.getLoginPrompt());
        }
        if (loginLevel.getPasswordPrompt() != null) {
            element2.setAttribute("PASSWORDPROMPT", loginLevel.getPasswordPrompt());
        }
        element2.setAttribute("PROMPT", loginLevel.getCommandPrompt());
        if (loginLevel.getLevelExitCmd() != null) {
            element2.setAttribute("LEVELEXITCMD", loginLevel.getLevelExitCmd());
        }
        if (loginLevel.getSubLevels() != null) {
            final String[] subLevels = loginLevel.getSubLevels();
            for (int i = 0; i < subLevels.length; ++i) {
                this.appendLevelTag(element2, (LoginLevel)this.logLevelEntries.get(subLevels[i]));
            }
        }
        element.appendChild(element2);
    }
    
    private void appendConfigTags(final Element element) {
        final Iterator iterator = this.configEntries.values().iterator();
        while (iterator.hasNext()) {
            final ConfigObject configObject = (ConfigObject)iterator.next();
            final Element element2 = this.doc.createElement("CONFIG");
            element2.setAttribute("NAME", configObject.getConfigID());
            element2.setAttribute("LOGINLEVEL", configObject.getLoginLevel());
            if (configObject.getDataInterface() != null) {
                element2.setAttribute("DATAINTERFACE", configObject.getDataInterface().getClass().getName());
            }
            if (configObject.getDescription() != null) {
                element2.setAttribute("DESCRIPTION", configObject.getDescription());
            }
            if (configObject.getTaskList() != null) {
                this.appendTaskTags(element2, configObject.getTaskList());
            }
            element.appendChild(element2);
        }
    }
    
    private void appendTaskTags(final Element element, final ArrayList list) {
        for (int i = 0; i < list.size(); ++i) {
            final Task task = list.get(i);
            Element element2;
            if (task.getTaskType() == 2) {
                element2 = this.doc.createElement("CMDTASK");
                element2.setAttribute("TASKNAME", task.getTaskName());
                element2.setAttribute("TASKVALUE", task.getCommand());
            }
            else {
                element2 = this.doc.createElement("SCRIPTTASK");
                element2.setAttribute("TASKNAME", task.getTaskName());
                element2.setAttribute("TASKVALUE", task.getScriptName());
                element2.setAttribute("SCRIPTTYPE", task.getScriptType());
            }
            if (task.getLoginLevel() != null) {
                element2.setAttribute("LOGINLEVEL", task.getLoginLevel());
            }
            if (!task.getMandatory()) {
                element2.setAttribute("MANDATORY", String.valueOf(task.getMandatory()));
            }
            if (!task.getDataRequired()) {
                element2.setAttribute("DATAREQUIRED", String.valueOf(task.getDataRequired()));
            }
            if (task.getDescription() != null) {
                element2.setAttribute("DESCRIPTION", task.getDescription());
            }
            element.appendChild(element2);
        }
    }
    
    public void writeToFile(final String s, final Document document) throws IOException {
        ((XmlDocument)document).write((OutputStream)new FileOutputStream(s));
    }
}
