package com.adventnet.cli.config;

import java.util.Collection;
import com.adventnet.cli.CLIMessage;
import java.util.StringTokenizer;
import java.util.Properties;
import com.adventnet.cli.transport.CLIProtocolOptions;
import java.util.List;
import java.util.Collections;
import java.util.Vector;
import java.util.ArrayList;
import java.util.Hashtable;

public class ConfigAPI
{
    private ExecutionInterface executionIface;
    private String configFileName;
    private String currentLoginLevel;
    private ConfigXmlParser configParser;
    private Hashtable executeIfaceTable;
    private String executionIfcClassName;
    private LoginInterface loginIfc;
    
    public Hashtable getConfigList() {
        return this.configParser.getConfigTable();
    }
    
    public String getExecutionIfcClassName() {
        return this.executionIfcClassName;
    }
    
    public void setExecutionIfcClassName(final String executionIfcClassName) {
        this.executionIfcClassName = executionIfcClassName;
    }
    
    public ConfigAPI() {
        this.executionIface = null;
        this.configFileName = "ConfigDriver.xml";
        this.currentLoginLevel = null;
        this.configParser = null;
        this.executeIfaceTable = null;
        this.executionIfcClassName = "com.adventnet.cli.config.ExecutionInterfaceImpl";
        this.loginIfc = null;
    }
    
    public ConfigAPI(final String configFileName) throws ConfigException {
        this.executionIface = null;
        this.configFileName = "ConfigDriver.xml";
        this.currentLoginLevel = null;
        this.configParser = null;
        this.executeIfaceTable = null;
        this.executionIfcClassName = "com.adventnet.cli.config.ExecutionInterfaceImpl";
        this.loginIfc = null;
        try {
            if (configFileName != null) {
                this.configFileName = configFileName;
            }
            this.configParser = new ConfigXmlParser(this.configFileName);
            this.executeIfaceTable = new Hashtable();
            this.loginIfc = this.configParser.getLoginInterface();
        }
        catch (final Exception ex) {
            throw new ConfigException(ex.getMessage());
        }
    }
    
    public ConfigObject getConfiguration(final String s) throws ConfigException {
        try {
            return this.configParser.getConfigObjectByName(s);
        }
        catch (final Exception ex) {
            throw new ConfigException(ex.getMessage());
        }
    }
    
    public void loadConfiguration(final String configFileName) throws ConfigException {
        try {
            if (configFileName != null) {
                this.configFileName = configFileName;
                this.configParser.loadFromXmlFile(this.configFileName);
            }
        }
        catch (final Exception ex) {
            throw new ConfigException(ex.getMessage());
        }
    }
    
    public void setDataInterface(final String s, final DataInterface dataInterface) throws ConfigException {
        try {
            final ConfigObject configObjectByName = this.configParser.getConfigObjectByName(s);
            if (configObjectByName != null) {
                configObjectByName.setDataInterface(dataInterface);
            }
        }
        catch (final Exception ex) {
            throw new ConfigException(ex.getMessage());
        }
    }
    
    public ExecutionInterface getExecutionInterface() {
        return this.executionIface;
    }
    
    public void setExecutionInterface(final ExecutionInterface executionIface) {
        this.executionIface = executionIface;
    }
    
    public Hashtable runConfiguration(final String s) throws ConfigException {
        return this.runConfiguration(s, null);
    }
    
    public Hashtable runConfiguration(final String s, final TaskData[] array) throws ConfigException {
        return this.runConfiguration(s, array, null);
    }
    
    public Hashtable runConfiguration(final String s, final TaskData[] array, final Object o) throws ConfigException {
        return this.getResultOfConfig(s, this.getConfigWithData(s, array, o));
    }
    
    public String[] getConfigWithData(final String s, TaskData[] configData, final Object o) throws ConfigException {
        String[] dataForConfiguration;
        try {
            if (o != null) {
                this.executionIface = this.executeIfaceTable.get(o);
                if (this.executionIface == null) {
                    throw new ConfigException("no execution interface available for id " + o);
                }
            }
            final ConfigObject configObjectByName = this.configParser.getConfigObjectByName(s);
            DataInterface dataInterface = configObjectByName.getDataInterface();
            if (dataInterface == null) {
                dataInterface = this.configParser.getDataInterface();
            }
            if (configData == null && dataInterface != null) {
                configData = dataInterface.getConfigData(configObjectByName);
            }
            dataForConfiguration = this.getDataForConfiguration(configObjectByName.getTaskList(), configData);
        }
        catch (final Exception ex) {
            throw new ConfigException(ex.getMessage());
        }
        return dataForConfiguration;
    }
    
    private String[] getDataForConfiguration(final ArrayList list, final TaskData[] array) throws Exception {
        Object[] array2 = null;
        final Vector vector = new Vector();
        for (int i = 0; i < list.size(); ++i) {
            final Task task = list.get(i);
            TaskData taskData = null;
            if (array != null) {
                if (task.getExecutionCount() > 1) {
                    for (int j = 0; j < task.getExecutionCount(); ++j) {
                        taskData = null;
                        for (int k = 0; k < array.length; ++k) {
                            if (array[k].getTaskName().equals(task.getTaskName() + String.valueOf(j + 1))) {
                                taskData = array[k];
                                break;
                            }
                        }
                        if (j + 1 == task.getExecutionCount()) {
                            break;
                        }
                        vector.addElement(this.getDataForTask(task, taskData));
                    }
                }
                else {
                    for (int l = 0; l < array.length; ++l) {
                        if (array[l].getTaskName().equals(task.getTaskName())) {
                            taskData = array[l];
                            break;
                        }
                    }
                }
            }
            vector.addElement(this.getDataForTask(task, taskData));
        }
        if (vector.size() > 0) {
            array2 = new String[vector.size()];
            vector.copyInto(array2);
        }
        return (String[])array2;
    }
    
    public Hashtable getResultOfConfig(final String s, final String[] array) throws ConfigException {
        final Hashtable hashtable = new Hashtable();
        try {
            final ConfigObject configuration = this.getConfiguration(s);
            final ArrayList taskList = configuration.getTaskList();
            this.loginIfc = this.configParser.getLoginInterface();
            final ArrayList list = new ArrayList();
            final boolean switchLoginLevel = this.switchLoginLevel(this.currentLoginLevel, configuration.getLoginLevel(), list);
            int n = 0;
            for (int i = 0; i < taskList.size(); ++i) {
                final Task task = taskList.get(i);
                if (task.getExecutionCount() > 1) {
                    for (int j = 0; j < task.getExecutionCount(); ++j) {
                        final String executeTheTask = this.executeTheTask(task, array[n], configuration);
                        if (executeTheTask != null) {
                            hashtable.put(task.getTaskName() + String.valueOf(j + 1), executeTheTask);
                        }
                        ++n;
                    }
                }
                else {
                    final String executeTheTask2 = this.executeTheTask(task, array[n], configuration);
                    if (executeTheTask2 != null) {
                        hashtable.put(task.getTaskName(), executeTheTask2);
                    }
                    ++n;
                }
            }
            if (switchLoginLevel) {
                Collections.reverse(list);
                this.switchLoginLevel(configuration.getLoginLevel(), this.currentLoginLevel, list);
            }
        }
        catch (final Exception ex) {
            throw new ConfigException(ex.getMessage());
        }
        return hashtable;
    }
    
    public void addConfiguration(final ConfigObject configObject) throws ConfigException {
        try {
            this.configParser.addConfiguration(configObject);
        }
        catch (final Exception ex) {
            throw new ConfigException(ex.getMessage());
        }
    }
    
    public ConfigObject deleteConfiguration(final String s) throws ConfigException {
        try {
            return this.configParser.deleteConfiguration(s);
        }
        catch (final Exception ex) {
            throw new ConfigException(ex.getMessage());
        }
    }
    
    public void insertTask(final String s, final Task task, final String s2) throws ConfigException {
        try {
            this.configParser.addTaskToConfig(s, task, s2);
        }
        catch (final Exception ex) {
            throw new ConfigException(ex.getMessage());
        }
    }
    
    public void appendTask(final String s, final Task task) throws ConfigException {
        try {
            this.configParser.appendTaskToConfig(s, task);
        }
        catch (final Exception ex) {
            throw new ConfigException(ex.getMessage());
        }
    }
    
    public Task deleteTask(final String s, final String s2) throws ConfigException {
        try {
            return this.configParser.deleteTaskFromConfig(s, s2);
        }
        catch (final Exception ex) {
            throw new ConfigException(ex.getMessage());
        }
    }
    
    public void saveChangesToXmlFile() throws ConfigException {
        try {
            this.configParser.writeXmlToFile(this.configFileName);
        }
        catch (final Exception ex) {
            throw new ConfigException(ex.getMessage());
        }
    }
    
    public String getCurrentLoginLevel() {
        return this.currentLoginLevel;
    }
    
    public void setCurrentLoginLevel(final String currentLoginLevel) throws ConfigException {
        try {
            if (this.currentLoginLevel == null) {
                final LoginLevel loginLevel = this.getLoginLevel(currentLoginLevel);
                if (loginLevel != null) {
                    this.executionIface.setLoginLevel(loginLevel);
                    this.currentLoginLevel = loginLevel.getLoginLevel();
                }
            }
            else if (this.switchLoginLevel(this.currentLoginLevel, currentLoginLevel, null)) {
                this.currentLoginLevel = currentLoginLevel;
            }
        }
        catch (final Exception ex) {
            throw new ConfigException(ex.getMessage());
        }
    }
    
    public void openSession(final CLIProtocolOptions cliProtocolOptions) throws ConfigException {
        try {
            if (cliProtocolOptions == null) {
                throw new Exception("cannot open session, CLIProtocolOptions is null");
            }
            if (this.getExecutionInterface() == null) {
                this.setExecutionInterface(this.getExecutionIfcInstance());
            }
            if (this.getCurrentLoginLevel() == null) {
                this.setCurrentLoginLevel(this.configParser.getDefaultLoginLevel());
            }
            else {
                this.executionIface.setLoginLevel(this.getLoginLevel(this.currentLoginLevel));
            }
            this.executionIface.login(cliProtocolOptions);
            this.executeIfaceTable.put(cliProtocolOptions.getID(), this.executionIface);
        }
        catch (final Exception ex) {
            throw new ConfigException(ex.getMessage());
        }
    }
    
    public void closeSession(final Object o) throws ConfigException {
        try {
            if (o == null) {
                throw new Exception("cannot close session, id is null");
            }
            this.executeIfaceTable.remove(o).close();
        }
        catch (final Exception ex) {
            throw new ConfigException(ex.getMessage());
        }
    }
    
    public ExecutionInterface getExecutionIfcInstance() throws Exception {
        try {
            return (ExecutionInterface)Class.forName(this.executionIfcClassName).newInstance();
        }
        catch (final Exception ex) {
            throw new Exception("cannot create ExecutionInterface implementation instance " + ex.getMessage());
        }
    }
    
    private String parseAndGetCommand(final String s, final Properties properties) throws Exception {
        final String[] paramsOfCommand = this.getParamsOfCommand(s);
        String string;
        if (paramsOfCommand != null) {
            final StringBuffer sb = new StringBuffer();
            int n = 0;
            for (int i = 0; i < paramsOfCommand.length; ++i) {
                final int index = s.indexOf(paramsOfCommand[i]);
                if (i > 0) {
                    n = s.indexOf(paramsOfCommand[i - 1]) + paramsOfCommand[i - 1].length();
                }
                sb.append(s.substring(n, index));
                String s2 = null;
                if (s.charAt(index) == '$') {
                    s2 = properties.getProperty(paramsOfCommand[i]);
                    if (s2 == null) {
                        throw new Exception("no data present for mandatory task");
                    }
                }
                else if (s.charAt(index) == '#') {
                    s2 = properties.getProperty(paramsOfCommand[i]);
                    if (s2 == null) {
                        continue;
                    }
                }
                sb.append(s2);
                if (i == paramsOfCommand.length - 1) {
                    sb.append(s.substring(index + paramsOfCommand[i].length(), s.length()));
                }
            }
            string = sb.toString();
        }
        else {
            string = s;
        }
        return string;
    }
    
    private String removeExtraSpecialCharacter(final String s) {
        final StringTokenizer stringTokenizer = new StringTokenizer(s);
        StringBuffer sb = new StringBuffer(s);
        while (stringTokenizer.hasMoreTokens()) {
            final String nextToken = stringTokenizer.nextToken();
            if (nextToken.startsWith("$$")) {
                sb = sb.deleteCharAt(sb.toString().indexOf(nextToken));
            }
            else {
                if (!nextToken.startsWith("##")) {
                    continue;
                }
                sb = sb.deleteCharAt(sb.toString().indexOf(nextToken));
            }
        }
        return sb.toString();
    }
    
    public String[] getParamsOfCommand(final String s) {
        if (s != null) {
            final StringTokenizer stringTokenizer = new StringTokenizer(s);
            stringTokenizer.nextToken();
            final Vector vector = new Vector();
            while (stringTokenizer.hasMoreTokens()) {
                final String nextToken = stringTokenizer.nextToken();
                if (!nextToken.startsWith("$$")) {
                    if (nextToken.startsWith("##")) {
                        continue;
                    }
                    if (!nextToken.startsWith("$") && !nextToken.startsWith("#")) {
                        continue;
                    }
                    vector.add(nextToken);
                }
            }
            if (vector.size() > 0) {
                final String[] array = new String[vector.size()];
                vector.copyInto(array);
                return array;
            }
        }
        return null;
    }
    
    public LoginLevel getLoginLevel(final String s) throws ConfigException {
        try {
            return this.configParser.getLoginLevelByName(s);
        }
        catch (final Exception ex) {
            throw new ConfigException(ex.getMessage());
        }
    }
    
    public Task getTaskOfConfig(final String s, final String s2) throws ConfigException {
        try {
            return this.configParser.getTaskFromConfig(s, s2);
        }
        catch (final Exception ex) {
            throw new ConfigException(ex.getMessage());
        }
    }
    
    public String runTask(final String s, final String s2, final TaskData taskData) throws ConfigException {
        return this.runTask(s, s2, taskData, null);
    }
    
    public String runTask(final String s, final String s2, final TaskData taskData, final Object o) throws ConfigException {
        return this.getResultOfTask(s, this.getTaskCmd(s, s2, taskData, o), s2);
    }
    
    public String getTaskCmd(final String s, final String s2, TaskData taskData, final Object o) throws ConfigException {
        String dataForTask;
        try {
            if (o != null) {
                this.executionIface = this.executeIfaceTable.get(o);
                if (this.executionIface == null) {
                    throw new ConfigException("no execution interface available for id " + o);
                }
            }
            DataInterface dataInterface = this.getConfiguration(s2).getDataInterface();
            if (dataInterface == null) {
                dataInterface = this.configParser.getDataInterface();
            }
            final Task taskOfConfig = this.getTaskOfConfig(s2, s);
            if (taskData == null && dataInterface != null) {
                if (taskOfConfig.getTaskType() == 2) {
                    final Properties configCmdData = dataInterface.getConfigCmdData(s2, taskOfConfig.getCommand());
                    if (configCmdData != null) {
                        taskData = new TaskData();
                        taskData.setTaskName(taskOfConfig.getTaskName());
                        taskData.setCmdParams(configCmdData);
                    }
                }
                else {
                    final String[] configScriptData = dataInterface.getConfigScriptData(s2, taskOfConfig.getScriptName());
                    if (configScriptData != null) {
                        taskData = new TaskData();
                        taskData.setTaskName(taskOfConfig.getTaskName());
                        taskData.setScriptArgs(configScriptData);
                    }
                }
            }
            dataForTask = this.getDataForTask(taskOfConfig, taskData);
        }
        catch (final Exception ex) {
            throw new ConfigException(ex.getMessage());
        }
        return dataForTask;
    }
    
    public String getResultOfTask(final String s, final String s2, final String s3) throws ConfigException {
        String executeTheTask;
        try {
            final ConfigObject configuration = this.getConfiguration(s3);
            final Task taskOfConfig = this.getTaskOfConfig(s3, s);
            this.loginIfc = this.configParser.getLoginInterface();
            final ArrayList list = new ArrayList();
            final boolean switchLoginLevel = this.switchLoginLevel(this.currentLoginLevel, configuration.getLoginLevel(), list);
            executeTheTask = this.executeTheTask(taskOfConfig, s2, configuration);
            if (switchLoginLevel) {
                Collections.reverse(list);
                this.switchLoginLevel(configuration.getLoginLevel(), this.currentLoginLevel, list);
            }
        }
        catch (final Exception ex) {
            throw new ConfigException(ex.getMessage());
        }
        return executeTheTask;
    }
    
    private String getDataForTask(final Task task, final TaskData taskData) throws Exception {
        final String s = new String();
        String s2;
        if (task.getTaskType() == 2) {
            if (!task.getDataRequired()) {
                if (task.getMandatory()) {
                    return this.removeExtraSpecialCharacter(task.getCommand());
                }
                return s;
            }
            else {
                Properties cmdParams = null;
                if (taskData != null) {
                    cmdParams = taskData.getCmdParams();
                }
                if (cmdParams == null) {
                    if (task.getMandatory()) {
                        throw new Exception(" no data for mandatory task " + task.getTaskName());
                    }
                    return s;
                }
                else {
                    s2 = this.removeExtraSpecialCharacter(this.parseAndGetCommand(task.getCommand(), cmdParams));
                }
            }
        }
        else if (!task.getDataRequired()) {
            if (task.getMandatory()) {
                return task.getScriptName();
            }
            return s;
        }
        else {
            String[] scriptArgs = null;
            if (taskData != null) {
                scriptArgs = taskData.getScriptArgs();
            }
            if (scriptArgs == null) {
                if (task.getMandatory()) {
                    throw new Exception(" no data for mandatory task");
                }
                return s;
            }
            else {
                s2 = task.getScriptName();
                for (int i = 0; i < scriptArgs.length; ++i) {
                    s2 = s2 + " " + scriptArgs[i];
                }
            }
        }
        return s2;
    }
    
    private String executeTheTask(final Task task, final String s, final ConfigObject configObject) throws Exception {
        String executeCommand = null;
        final ArrayList list = new ArrayList();
        final boolean switchLoginLevel = this.switchLoginLevel(configObject.getLoginLevel(), task.getLoginLevel(), list);
        if (task.getTaskType() == 2) {
            if (s.length() == 0) {
                return null;
            }
            executeCommand = this.executionIface.executeCommand(new CLIMessage(s));
        }
        else {
            if (s.length() == 0) {
                return null;
            }
            String[] array = null;
            final StringTokenizer stringTokenizer = new StringTokenizer(s);
            final String nextToken = stringTokenizer.nextToken();
            if (task.getDataRequired()) {
                array = new String[stringTokenizer.countTokens()];
                int n = 0;
                while (stringTokenizer.hasMoreTokens()) {
                    array[n++] = stringTokenizer.nextToken();
                }
            }
            this.executionIface.executeScript(nextToken, array, task.getScriptType());
        }
        if (switchLoginLevel) {
            Collections.reverse(list);
            this.switchLoginLevel(task.getLoginLevel(), configObject.getLoginLevel(), list);
        }
        return executeCommand;
    }
    
    private boolean switchLoginLevel(final String s, final String s2, ArrayList path) throws Exception {
        if (s2 == null || s.equals(s2)) {
            return false;
        }
        if (path == null || path.size() == 0) {
            if (!this.configParser.searchTree(s, s2)) {
                throw new Exception("login level " + s2 + " doesn't exist");
            }
            if (path != null) {
                path.addAll(this.configParser.getPath());
            }
            else {
                path = this.configParser.getPath();
            }
        }
        for (int i = 1; i < path.size(); ++i) {
            final LoginLevel loginLevel = path.get(i);
            if (loginLevel.isUserNameRequired() && loginLevel.getLoginName().length() == 0) {
                if (this.loginIfc == null) {
                    throw new Exception("no userName defined for the login level " + loginLevel.getLoginLevel());
                }
                final String loginName = this.loginIfc.getLoginName(loginLevel.getLoginLevel());
                if (loginName == null) {
                    if (i > 0) {
                        this.setCurrentLoginLevel(((LoginLevel)path.get(i - 1)).getLoginLevel());
                    }
                    return false;
                }
                loginLevel.setLoginName(loginName);
            }
            if (loginLevel.isPasswordRequired() && loginLevel.getLoginPassword().length() == 0) {
                if (this.loginIfc == null) {
                    throw new Exception("no password defined for the login level " + loginLevel.getLoginLevel());
                }
                final String loginPassword = this.loginIfc.getLoginPassword(loginLevel.getLoginLevel());
                if (loginPassword == null) {
                    if (i > 0) {
                        this.setCurrentLoginLevel(((LoginLevel)path.get(i - 1)).getLoginLevel());
                    }
                    return false;
                }
                loginLevel.setLoginPassword(loginPassword);
            }
            this.executionIface.setLoginLevel(loginLevel);
        }
        return true;
    }
}
