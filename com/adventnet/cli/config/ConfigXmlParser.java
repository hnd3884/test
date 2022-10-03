package com.adventnet.cli.config;

import java.io.IOException;
import java.util.Vector;
import java.util.Collection;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Attr;
import java.io.File;
import com.adventnet.util.parser.ParseException;
import javax.xml.parsers.DocumentBuilderFactory;
import java.util.ArrayList;
import java.util.Hashtable;
import org.w3c.dom.Element;
import org.w3c.dom.Document;
import javax.xml.parsers.DocumentBuilder;

class ConfigXmlParser
{
    DocumentBuilder dBuild;
    Document doc;
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
    private static final String loginLevels = "LOGIN-LEVELS";
    private static final String loginInterface = "LOGININTERFACE";
    private static final String level = "LEVEL";
    private static final String command = "COMMAND";
    private static final String userName = "USERNAME";
    private static final String passwd = "PASSWORD";
    private static final String loginPrompt = "LOGINPROMPT";
    private static final String passwdPrompt = "PASSWORDPROMPT";
    private static final String prompt = "PROMPT";
    private static final String levelExitCmd = "LEVELEXITCMD";
    private Element rootNode;
    private String defaultLoginLevel;
    private Hashtable configTable;
    private Hashtable logLevelTable;
    private DataInterface dataIfc;
    private LoginInterface loginIfc;
    private ArrayList path;
    private ConfigXmlWriter cxw;
    
    public String getDefaultLoginLevel() {
        return this.defaultLoginLevel;
    }
    
    public void setDefaultLoginLevel(final String defaultLoginLevel) {
        this.defaultLoginLevel = defaultLoginLevel;
    }
    
    public Hashtable getConfigTable() {
        return this.configTable;
    }
    
    public Hashtable getLogLevelTable() {
        return this.logLevelTable;
    }
    
    public DataInterface getDataInterface() {
        return this.dataIfc;
    }
    
    public LoginInterface getLoginInterface() {
        return this.loginIfc;
    }
    
    public ConfigXmlParser(final String s) throws ParseException {
        this.dBuild = null;
        this.doc = null;
        this.rootNode = null;
        this.defaultLoginLevel = null;
        this.configTable = null;
        this.logLevelTable = null;
        this.dataIfc = null;
        this.loginIfc = null;
        this.path = null;
        this.cxw = new ConfigXmlWriter();
        try {
            this.dBuild = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            this.loadFromXmlFile(s);
        }
        catch (final Exception ex) {
            throw new ParseException(ex.getMessage());
        }
    }
    
    public void loadFromXmlFile(final String s) throws ParseException {
        try {
            this.doc = this.dBuild.parse(new File(s));
            this.rootNode = this.doc.getDocumentElement();
            this.configTable = new Hashtable();
            this.logLevelTable = new Hashtable();
            this.getConfigObjects();
            this.getLoginLevels();
        }
        catch (final Exception ex) {
            throw new ParseException(ex.getMessage());
        }
    }
    
    private void getConfigObjects() throws ParseException {
        final NodeList elementsByTagName = this.rootNode.getElementsByTagName("CONFIG-LIST");
        if (elementsByTagName != null) {
            try {
                final Node item = elementsByTagName.item(0);
                if (item.getNodeType() == 1) {
                    final NamedNodeMap attributes = item.getAttributes();
                    if (attributes != null) {
                        final Attr attr = (Attr)attributes.getNamedItem("DATAINTERFACE");
                        if (attr != null) {
                            final String value = attr.getValue();
                            try {
                                this.dataIfc = (DataInterface)Class.forName(value).newInstance();
                            }
                            catch (final Exception ex) {
                                throw new Exception("cannot create DataInterface instance " + ex.getMessage());
                            }
                        }
                    }
                }
                final NodeList elementsByTagName2 = ((Element)item).getElementsByTagName("CONFIG");
                if (elementsByTagName2 != null) {
                    for (int i = 0; i < elementsByTagName2.getLength(); ++i) {
                        final ConfigObject configObject = this.getConfigObject(elementsByTagName2.item(i));
                        this.configTable.put(configObject.getConfigID(), configObject);
                    }
                }
            }
            catch (final Exception ex2) {
                throw new ParseException(ex2.getMessage());
            }
        }
    }
    
    private ConfigObject getConfigObject(final Node node) throws ParseException {
        final ConfigObject configObject = new ConfigObject();
        try {
            if (node != null && node.getNodeType() == 1) {
                final NamedNodeMap attributes = node.getAttributes();
                if (attributes != null) {
                    configObject.setConfigID(((Attr)attributes.getNamedItem("NAME")).getValue());
                    configObject.setLoginLevel(((Attr)attributes.getNamedItem("LOGINLEVEL")).getValue());
                    final Attr attr = (Attr)attributes.getNamedItem("DATAINTERFACE");
                    if (attr != null) {
                        final String trim = attr.getValue().trim();
                        if (trim != null && trim.length() != 0) {
                            try {
                                configObject.setDataInterface((DataInterface)Class.forName(trim).newInstance());
                            }
                            catch (final Exception ex) {
                                throw new Exception("cannot create DataInterface instance " + ex.getMessage());
                            }
                        }
                    }
                    final Attr attr2 = (Attr)attributes.getNamedItem("DESCRIPTION");
                    if (attr2 != null) {
                        configObject.setDescription(attr2.getValue());
                    }
                }
                configObject.setTaskList(this.getConfigTasks((Element)node));
            }
        }
        catch (final Exception ex2) {
            throw new ParseException(ex2.getMessage());
        }
        return configObject;
    }
    
    private ArrayList getConfigTasks(final Element element) {
        final NodeList elementsByTagName = element.getElementsByTagName("CMDTASK");
        final NodeList elementsByTagName2 = element.getElementsByTagName("SCRIPTTASK");
        final int length = elementsByTagName.getLength();
        final ArrayList list = new ArrayList();
        if (elementsByTagName != null) {
            for (int i = 0; i < length; ++i) {
                final Task task = new Task();
                task.setTaskType(2);
                final Node item = elementsByTagName.item(i);
                if (item != null && item.getNodeType() == 1) {
                    final NamedNodeMap attributes = item.getAttributes();
                    if (attributes != null) {
                        task.setTaskName(((Attr)attributes.getNamedItem("TASKNAME")).getValue());
                        task.setCommand(((Attr)attributes.getNamedItem("TASKVALUE")).getValue());
                        final Attr attr = (Attr)attributes.getNamedItem("LOGINLEVEL");
                        if (attr != null) {
                            task.setLoginLevel(attr.getValue());
                        }
                        final Attr attr2 = (Attr)attributes.getNamedItem("MANDATORY");
                        if (attr2 != null) {
                            task.setMandatory(Boolean.valueOf(attr2.getValue()));
                        }
                        final Attr attr3 = (Attr)attributes.getNamedItem("DATAREQUIRED");
                        if (attr3 != null) {
                            task.setDataRequired(Boolean.valueOf(attr3.getValue()));
                        }
                        final Attr attr4 = (Attr)attributes.getNamedItem("DESCRIPTION");
                        if (attr4 != null) {
                            task.setDescription(attr4.getValue());
                        }
                    }
                }
                list.add(task);
            }
        }
        Collection scriptTasks = null;
        if (elementsByTagName2 != null) {
            scriptTasks = this.getScriptTasks(elementsByTagName2);
        }
        list.addAll(scriptTasks);
        return list;
    }
    
    private ArrayList getScriptTasks(final NodeList list) {
        final ArrayList list2 = new ArrayList();
        for (int i = 0; i < list.getLength(); ++i) {
            final Task task = new Task();
            task.setTaskType(1);
            final Node item = list.item(i);
            if (item != null && item.getNodeType() == 1) {
                final NamedNodeMap attributes = item.getAttributes();
                if (attributes != null) {
                    task.setTaskName(((Attr)attributes.getNamedItem("TASKNAME")).getValue());
                    task.setScriptName(((Attr)attributes.getNamedItem("TASKVALUE")).getValue());
                    task.setScriptType(((Attr)attributes.getNamedItem("SCRIPTTYPE")).getValue());
                    final Attr attr = (Attr)attributes.getNamedItem("LOGINLEVEL");
                    if (attr != null) {
                        task.setLoginLevel(attr.getValue());
                    }
                    final Attr attr2 = (Attr)attributes.getNamedItem("MANDATORY");
                    if (attr2 != null) {
                        task.setMandatory(Boolean.valueOf(attr2.getValue()));
                    }
                    final Attr attr3 = (Attr)attributes.getNamedItem("DATAREQUIRED");
                    if (attr3 != null) {
                        task.setDataRequired(Boolean.valueOf(attr3.getValue()));
                    }
                    final Attr attr4 = (Attr)attributes.getNamedItem("DESCRIPTION");
                    if (attr4 != null) {
                        task.setDescription(attr4.getValue());
                    }
                }
            }
            list2.add(task);
        }
        return list2;
    }
    
    private void getLoginLevels() throws ParseException {
        final NodeList elementsByTagName = this.rootNode.getElementsByTagName("LOGIN-LEVELS");
        if (elementsByTagName != null) {
            try {
                final Node item = elementsByTagName.item(0);
                if (item.getNodeType() == 1) {
                    final NamedNodeMap attributes = item.getAttributes();
                    if (attributes != null) {
                        final Attr attr = (Attr)attributes.getNamedItem("LOGININTERFACE");
                        if (attr != null) {
                            final String value = attr.getValue();
                            try {
                                this.loginIfc = (LoginInterface)Class.forName(value).newInstance();
                            }
                            catch (final Exception ex) {
                                throw new Exception("cannot create LoginInterface instance " + ex.getMessage());
                            }
                        }
                    }
                }
                final NodeList childNodes = item.getChildNodes();
                if (childNodes != null) {
                    for (int i = 0; i < childNodes.getLength(); ++i) {
                        this.getLoginLevelName(childNodes.item(i), null);
                    }
                }
            }
            catch (final Exception ex2) {
                throw new ParseException(ex2.getMessage());
            }
        }
    }
    
    private String getLoginLevelName(final Node node, final String parentLevel) {
        final LoginLevel loginLevel = new LoginLevel();
        if (node != null && node.getNodeType() == 1) {
            final NamedNodeMap attributes = node.getAttributes();
            if (attributes != null) {
                loginLevel.setLoginLevel(((Attr)attributes.getNamedItem("NAME")).getValue());
                if (this.getDefaultLoginLevel() == null) {
                    this.setDefaultLoginLevel(loginLevel.getLoginLevel());
                }
                final Attr attr = (Attr)attributes.getNamedItem("COMMAND");
                if (attr != null) {
                    loginLevel.setLoginCommand(attr.getValue());
                }
                final Attr attr2 = (Attr)attributes.getNamedItem("USERNAME");
                if (attr2 != null) {
                    loginLevel.setLoginName(attr2.getValue());
                }
                final Attr attr3 = (Attr)attributes.getNamedItem("PASSWORD");
                if (attr3 != null) {
                    loginLevel.setLoginPassword(attr3.getValue());
                }
                final Attr attr4 = (Attr)attributes.getNamedItem("LOGINPROMPT");
                if (attr4 != null) {
                    loginLevel.setLoginPrompt(attr4.getValue());
                }
                final Attr attr5 = (Attr)attributes.getNamedItem("PASSWORDPROMPT");
                if (attr5 != null) {
                    loginLevel.setPasswordPrompt(attr5.getValue());
                }
                loginLevel.setCommandPrompt(((Attr)attributes.getNamedItem("PROMPT")).getValue());
                final Attr attr6 = (Attr)attributes.getNamedItem("LEVELEXITCMD");
                if (attr6 != null) {
                    loginLevel.setLevelExitCmd(attr6.getValue());
                }
            }
            if (node.hasChildNodes()) {
                final NodeList childNodes = node.getChildNodes();
                final Vector vector = new Vector<String>();
                for (int i = 0; i < childNodes.getLength(); ++i) {
                    final String loginLevelName = this.getLoginLevelName(childNodes.item(i), loginLevel.getLoginLevel());
                    if (loginLevelName != null) {
                        vector.addElement(loginLevelName);
                    }
                }
                if (vector.size() > 0) {
                    final String[] subLevels = new String[vector.size()];
                    vector.copyInto(subLevels);
                    loginLevel.setSubLevels(subLevels);
                }
            }
            if (parentLevel != null) {
                loginLevel.setParentLevel(parentLevel);
            }
            this.logLevelTable.put(loginLevel.getLoginLevel(), loginLevel);
            return loginLevel.getLoginLevel();
        }
        return null;
    }
    
    public ArrayList getPath() {
        return this.path;
    }
    
    private boolean searchTree(final String s, final String s2, final String s3) throws Exception {
        boolean searchTree = false;
        final LoginLevel loginLevelByName = this.getLoginLevelByName(s);
        if (loginLevelByName.getLoginLevel().equals(s2)) {
            this.path.add(0, loginLevelByName);
            return true;
        }
        if (loginLevelByName.getSubLevels() != null) {
            final String[] subLevels = loginLevelByName.getSubLevels();
            for (int i = 0; i < subLevels.length; ++i) {
                if (!subLevels[i].equals(s3)) {
                    searchTree = this.searchTree(subLevels[i], s2, null);
                    if (searchTree) {
                        this.path.add(0, loginLevelByName);
                        break;
                    }
                }
            }
        }
        return searchTree;
    }
    
    private boolean searchParent(final String s, final String s2) throws Exception {
        boolean b = false;
        final LoginLevel loginLevelByName = this.getLoginLevelByName(s);
        if (loginLevelByName.getParentLevel() != null) {
            b = this.searchTree(loginLevelByName.getParentLevel(), s2, s);
            if (!b) {
                b = this.searchParent(loginLevelByName.getParentLevel(), s2);
            }
            if (b) {
                this.path.add(0, loginLevelByName);
            }
        }
        return b;
    }
    
    public boolean searchTree(final String s, final String s2) throws Exception {
        boolean b = false;
        if (s != null && s2 != null) {
            this.path = new ArrayList();
            b = this.searchTree(s, s2, null);
            if (!b) {
                b = this.searchParent(s, s2);
            }
        }
        return b;
    }
    
    public LoginLevel getLoginLevelByName(final String s) throws Exception {
        if (s != null) {
            final LoginLevel loginLevel = this.logLevelTable.get(s);
            if (loginLevel != null) {
                return loginLevel;
            }
        }
        throw new Exception("loginlevel " + s + " doesn't exist");
    }
    
    public ConfigObject getConfigObjectByName(final String s) throws Exception {
        if (s != null) {
            final ConfigObject configObject = this.configTable.get(s);
            if (configObject != null) {
                return configObject;
            }
        }
        throw new Exception("configuration " + s + " doesn't exist");
    }
    
    public Task getTaskFromConfig(final String s, final String s2) throws Exception {
        if (s2 != null) {
            final ArrayList taskList = this.getConfigObjectByName(s).getTaskList();
            if (taskList != null) {
                for (int i = 0; i < taskList.size(); ++i) {
                    if (((Task)taskList.get(i)).getTaskName().equals(s2)) {
                        return (Task)taskList.get(i);
                    }
                }
            }
        }
        throw new Exception("task " + s2 + " doesn't exist");
    }
    
    public void addConfiguration(final ConfigObject configObject) throws Exception {
        if (configObject == null || configObject.getConfigID() == null) {
            throw new Exception("configuration is " + configObject);
        }
        if (this.configTable.get(configObject.getConfigID()) == null) {
            this.configTable.put(configObject.getConfigID(), configObject);
            return;
        }
        throw new Exception("configuration already exists " + configObject);
    }
    
    public ConfigObject deleteConfiguration(final String s) throws Exception {
        ConfigObject configObject = null;
        if (s != null) {
            configObject = this.configTable.remove(s);
        }
        return configObject;
    }
    
    public void addTaskToConfig(final String s, final Task task, final String s2) throws Exception {
        final ConfigObject configObjectByName = this.getConfigObjectByName(s);
        if (task != null && task.getTaskName() != null) {
            ArrayList taskList = configObjectByName.getTaskList();
            int n = -1;
            if (taskList != null) {
                if (s2 != null) {
                    for (int i = 0; i < taskList.size(); ++i) {
                        if (((Task)taskList.get(i)).getTaskName().equals(s2)) {
                            n = i + 1;
                        }
                    }
                    if (n == -1) {
                        throw new Exception("no task " + s2 + " cannot add " + task.getTaskName());
                    }
                }
                else {
                    n = 0;
                }
                for (int j = 0; j < taskList.size(); ++j) {
                    if (((Task)taskList.get(j)).getTaskName().equals(task.getTaskName())) {
                        throw new Exception("task already present, cannot add " + task.getTaskName());
                    }
                }
            }
            else {
                taskList = new ArrayList();
            }
            if (n > taskList.size() + 1) {
                taskList.add(task);
            }
            else {
                taskList.add(n, task);
            }
            return;
        }
        throw new Exception("task is " + task);
    }
    
    public void appendTaskToConfig(final String s, final Task task) throws Exception {
        final ConfigObject configObjectByName = this.getConfigObjectByName(s);
        if (task != null && task.getTaskName() != null) {
            ArrayList taskList = configObjectByName.getTaskList();
            if (taskList == null) {
                taskList = new ArrayList();
            }
            taskList.add(task);
            return;
        }
        throw new Exception("task is " + task);
    }
    
    public Task deleteTaskFromConfig(final String s, final String s2) throws Exception {
        final ConfigObject configObjectByName = this.getConfigObjectByName(s);
        final Task taskFromConfig = this.getTaskFromConfig(s, s2);
        if (configObjectByName.getTaskList() != null) {
            configObjectByName.getTaskList().remove(taskFromConfig);
            return taskFromConfig;
        }
        return taskFromConfig;
    }
    
    public void writeXmlToFile(final String s) throws IOException {
        this.cxw.setConfigEntries(this.configTable);
        this.cxw.setLogLevelEntries(this.logLevelTable);
        if (this.loginIfc != null) {
            this.cxw.setLoginIfcName(this.loginIfc.getClass().getName());
        }
        if (this.dataIfc != null) {
            this.cxw.setDataIfcName(this.dataIfc.getClass().getName());
        }
        this.cxw.writeXmlToFile(s);
    }
}
