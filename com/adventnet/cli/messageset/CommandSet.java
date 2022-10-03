package com.adventnet.cli.messageset;

import java.util.Enumeration;
import java.io.IOException;
import org.w3c.dom.NamedNodeMap;
import java.util.Vector;
import org.w3c.dom.Attr;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import java.util.StringTokenizer;
import org.w3c.dom.Document;
import javax.xml.parsers.DocumentBuilder;
import java.io.File;
import com.adventnet.util.parser.ParseException;
import javax.xml.parsers.DocumentBuilderFactory;
import java.util.Hashtable;
import org.w3c.dom.Element;
import java.io.Serializable;

public class CommandSet implements Serializable
{
    private String fileName;
    static final String command = "COMMAND";
    static final String name = "NAME";
    static final String delimiter = "DELIMITER";
    static final String commandObject = "OBJECT";
    static final String description = "DESCRIPTION";
    static final String param = "PARAM";
    static final String paramNameType = "PARAMTYPE";
    static final String paramValueType = "VALUETYPE";
    static final String paramOption = "OPTION_FLAG";
    static final String options = "OPTIONS";
    static final String simpleOpts = "SIMPLE_OPTS";
    static final String optChar = "OPT_CHAR";
    static final String optDep = "OPT_DEP";
    static final String simpleOptsArgs = "SIMPLE_OPT_ARGS";
    static final String optType = "TYPE";
    static final String optPrefix = "OPT_PREFIX";
    static final String longOpts = "LONG_OPTS";
    static final String optName = "OPT_NAME";
    static final String longOptsArgs = "LONG_OPT_ARGS";
    static final String help = "HELP";
    static final String syntax = "SYNTAX";
    static final String helpOptions = "HELP_OPTIONS";
    static final String example = "EXAMPLE";
    static final String remarks = "REMARKS";
    static final String relatedCommands = "RELATED_COMMANDS";
    boolean formCommandWithOptions;
    boolean formCommandWithParams;
    Element rootNode;
    Hashtable commandTable;
    DataSet dataSet;
    boolean objDataParamSet;
    boolean objDataOptsSet;
    private CmdParams[] currDataParams;
    private CmdOptions currDataOpts;
    
    public CommandSet() {
        this.fileName = null;
        this.formCommandWithOptions = true;
        this.formCommandWithParams = true;
        this.rootNode = null;
        this.commandTable = null;
        this.dataSet = null;
        this.objDataParamSet = false;
        this.objDataOptsSet = false;
    }
    
    public CommandSet(final String fileName) throws ParseException {
        this.fileName = null;
        this.formCommandWithOptions = true;
        this.formCommandWithParams = true;
        this.rootNode = null;
        this.commandTable = null;
        this.dataSet = null;
        this.objDataParamSet = false;
        this.objDataOptsSet = false;
        this.fileName = fileName;
        DocumentBuilder documentBuilder;
        try {
            documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        }
        catch (final Exception ex) {
            throw new ParseException(ex.getMessage());
        }
        final File file = new File(fileName);
        Document parse;
        try {
            parse = documentBuilder.parse(file);
        }
        catch (final Exception ex2) {
            throw new ParseException(ex2.getMessage());
        }
        this.rootNode = parse.getDocumentElement();
        this.commandTable = new Hashtable();
        this.getCommandList();
    }
    
    public void setFileName(final String fileName) {
        this.fileName = fileName;
    }
    
    public String getFileName() {
        return this.fileName;
    }
    
    public void setDataSet(final DataSet dataSet) {
        this.dataSet = dataSet;
    }
    
    public DataSet getDataSet() {
        return this.dataSet;
    }
    
    public String assembleCommand(final String s, final String s2) throws InvalidCommandException {
        return this.assembleCommand(s, null, s2);
    }
    
    public String assembleCommand(final String s, final String s2, final String s3) throws InvalidCommandException {
        final StringBuffer sb = new StringBuffer();
        final CLICommandTemplate cliCommandTemplate = this.commandTable.get(s);
        if (cliCommandTemplate == null) {
            throw new InvalidCommandException("Command Not Found");
        }
        String commandDelimiter = " ";
        if (cliCommandTemplate.getCommandDelimiter() != null) {
            commandDelimiter = cliCommandTemplate.getCommandDelimiter();
        }
        if (cliCommandTemplate.getCommandName() != null) {
            sb.append(cliCommandTemplate.getCommandName() + commandDelimiter);
            final StringTokenizer stringTokenizer = new StringTokenizer(s3, ",");
            stringTokenizer.countTokens();
            int countTokens = 0;
            StringTokenizer stringTokenizer2 = null;
            if (s2 != null) {
                stringTokenizer2 = new StringTokenizer(s2, "/");
                countTokens = stringTokenizer2.countTokens();
            }
            final String[] array = new String[countTokens];
            for (int i = 0; i < countTokens; ++i) {
                array[i] = stringTokenizer2.nextToken();
            }
            final CmdObject[] cmdObjectList = cliCommandTemplate.getCmdObjectList();
            final CmdParams[] cmdParamsList = cliCommandTemplate.getCmdParamsList();
            final CmdOptions cmdOptionsList = cliCommandTemplate.getCmdOptionsList();
            if (array.length == 0) {
                if (cmdObjectList != null) {
                    for (int j = 0; j < cmdObjectList.length; ++j) {
                        this.getCommandObjects(sb, stringTokenizer, cmdObjectList[j]);
                    }
                }
            }
            else if (array != null) {
                boolean b = false;
                int k = 0;
                while (k < cmdObjectList.length) {
                    if (cmdObjectList[k].getObjectName().equals(array[0])) {
                        b = true;
                        if (array.length == 1) {
                            this.getCommandForObject(sb, stringTokenizer, cmdObjectList[k]);
                            break;
                        }
                        final String[] array2 = new String[array.length - 1];
                        for (int l = 1; l < array.length; ++l) {
                            array2[l - 1] = array[l];
                        }
                        this.getCommandForObjectList(sb, stringTokenizer, cmdObjectList[k], array2);
                        break;
                    }
                    else {
                        b = false;
                        ++k;
                    }
                }
                if (!b) {
                    throw new InvalidCommandException("Command Path Not Found");
                }
            }
            if (cmdParamsList != null) {
                this.substituteParameters(sb, stringTokenizer, cmdParamsList, commandDelimiter);
            }
            if (cmdOptionsList != null && this.formCommandWithOptions) {
                this.substituteOptions(sb, stringTokenizer, cmdOptionsList, commandDelimiter);
            }
            return new String(sb);
        }
        throw new InvalidCommandException("Command Name Not Found");
    }
    
    public String getCommand(final String s, final String s2) throws InvalidCommandException {
        return this.getCommand(s, s2, null);
    }
    
    public String getCommand(final String s, final String s2, final String s3) throws InvalidCommandException {
        final CLICommandTemplate cliCommandTemplate = this.commandTable.get(s);
        if (cliCommandTemplate == null) {
            throw new InvalidCommandException("Command Not Found");
        }
        final CLIDataInstance dataByName = this.dataSet.getDataByName(s, s2);
        if (dataByName == null) {
            throw new InvalidCommandException("Command Data Not Found");
        }
        final StringBuffer sb = new StringBuffer();
        String commandDelimiter = " ";
        if (cliCommandTemplate.getCommandDelimiter() != null) {
            commandDelimiter = cliCommandTemplate.getCommandDelimiter();
        }
        if (cliCommandTemplate.getCommandName() != null) {
            sb.append(cliCommandTemplate.getCommandName() + commandDelimiter);
            int countTokens = 0;
            StringTokenizer stringTokenizer = null;
            if (s3 != null) {
                stringTokenizer = new StringTokenizer(s3, "/");
                countTokens = stringTokenizer.countTokens();
            }
            final String[] array = new String[countTokens];
            for (int i = 0; i < countTokens; ++i) {
                array[i] = stringTokenizer.nextToken();
            }
            final CmdObject[] cmdObjectList = cliCommandTemplate.getCmdObjectList();
            final CmdObject[] cmdObjectList2 = dataByName.getCmdObjectList();
            final CmdParams[] cmdParamsList = cliCommandTemplate.getCmdParamsList();
            final CmdParams[] cmdParamList = dataByName.getCmdParamList();
            final CmdOptions cmdOptionsList = cliCommandTemplate.getCmdOptionsList();
            final CmdOptions cmdOption = dataByName.getCmdOption();
            this.objDataParamSet = false;
            this.objDataOptsSet = false;
            if (cmdObjectList != null && cmdObjectList2 != null && (s3 == null || s3.length() != 0)) {
                this.getCommand(cmdObjectList, cmdObjectList2, sb, array);
            }
            if (!this.objDataParamSet) {
                this.currDataParams = cmdParamList;
            }
            if (cmdParamsList != null && cmdParamList != null && this.formCommandWithParams) {
                this.substituteCmdParams(cmdParamsList, cmdParamList, sb, commandDelimiter);
            }
            if (!this.objDataOptsSet) {
                this.currDataOpts = cmdOption;
            }
            if (cmdOptionsList != null && cmdOption != null) {
                final SimpleOpts[] simpleOptsList = cmdOptionsList.getSimpleOptsList();
                final SimpleOpts[] simpleOptsList2 = cmdOption.getSimpleOptsList();
                if (simpleOptsList != null && simpleOptsList2 != null) {
                    this.substituteSimpleOpts(simpleOptsList, simpleOptsList2, sb, commandDelimiter);
                }
                final SimpleOptsArgs[] simpleOptsArgsList = cmdOptionsList.getSimpleOptsArgsList();
                final SimpleOptsArgs[] simpleOptsArgsList2 = cmdOption.getSimpleOptsArgsList();
                if (simpleOptsArgsList != null && simpleOptsArgsList2 != null) {
                    this.substituteSimpleOptsArgs(simpleOptsArgsList, simpleOptsArgsList2, sb, commandDelimiter);
                }
                final LongOpts[] longOptsList = cmdOptionsList.getLongOptsList();
                final LongOpts[] longOptsList2 = cmdOption.getLongOptsList();
                if (longOptsList != null && longOptsList2 != null) {
                    this.substituteLongOpts(longOptsList, longOptsList2, sb, commandDelimiter);
                }
                final LongOptsArgs[] longOptsArgsList = cmdOptionsList.getLongOptsArgsList();
                final LongOptsArgs[] longOptsArgsList2 = cmdOption.getLongOptsArgsList();
                if (longOptsArgsList != null && longOptsArgsList2 != null) {
                    this.substituteLongOptsArgs(longOptsArgsList, longOptsArgsList2, sb, commandDelimiter);
                }
            }
            return new String(sb);
        }
        throw new InvalidCommandException("Command Name Not Found");
    }
    
    void substituteCmdParams(final CmdParams[] array, final CmdParams[] array2, final StringBuffer sb, final String s) throws InvalidCommandException {
        int n;
        if (array.length >= array2.length) {
            n = array.length;
        }
        else {
            n = array2.length;
        }
        for (int i = 0; i < n; ++i) {
            if (!array[i].getParamName().equals(array2[i].getParamName())) {
                throw new InvalidCommandException("Parameter name mismatch between DataSet and CommandSet");
            }
            if (array[i].isSendParam()) {
                sb.append(array[i].getParamName());
                sb.append("=");
            }
            final String paramValue = array[i].getParamValue();
            if (paramValue != null) {
                array2[i].setParamValue(paramValue);
                array[i].setParamValue(null);
            }
            final String paramValue2 = array2[i].getParamValue();
            this.checkValueType(paramValue2, array[i].getParamValueType());
            sb.append(paramValue2 + s);
        }
    }
    
    private void getCommand(final CmdObject[] array, final CmdObject[] array2, final StringBuffer sb, final String[] array3) throws InvalidCommandException {
        if (array2.length < array.length) {
            final int length = array2.length;
        }
        if (array3.length == 0) {
            for (int i = 0; i < array.length; ++i) {
                final CmdObject cmdObject = array[i];
                final CmdObject cmdObject2 = array2[i];
                if (!cmdObject.getObjectName().equals(cmdObject2.getObjectName())) {
                    throw new InvalidCommandException("Invalid data in dataSet");
                }
                String objectDelimiter = " ";
                if (cmdObject.getObjectDelimiter() != null) {
                    objectDelimiter = cmdObject.getObjectDelimiter();
                }
                sb.append(cmdObject2.getObjectValue() + objectDelimiter);
                final CmdParams[] parameterList = cmdObject.getParameterList();
                final CmdParams[] parameterList2 = cmdObject2.getParameterList();
                int n = 0;
                if (parameterList != null) {
                    n = parameterList.length;
                }
                if (parameterList2 != null && parameterList2.length <= n) {
                    n = parameterList2.length;
                }
                if (this.formCommandWithParams) {
                    for (int j = 0; j < n; ++j) {
                        if (!parameterList[j].getParamName().equals(parameterList2[j].getParamName())) {
                            throw new InvalidCommandException("Parameter name mismatch between DataSet and CommandSet");
                        }
                        if (parameterList[j].isSendParam()) {
                            sb.append(parameterList[j].getParamName());
                            sb.append("=");
                        }
                        final String paramValue = parameterList[j].getParamValue();
                        if (paramValue != null) {
                            parameterList2[j].setParamValue(paramValue);
                            parameterList[j].setParamValue(null);
                        }
                        this.checkValueType(parameterList2[j].getParamValue(), parameterList[j].getParamValueType());
                        sb.append(parameterList2[j].getParamValue() + objectDelimiter);
                    }
                }
                final CmdOptions optionsList = cmdObject.getOptionsList();
                final CmdOptions optionsList2 = cmdObject2.getOptionsList();
                if (optionsList != null && optionsList2 != null) {
                    final SimpleOpts[] simpleOptsList = optionsList.getSimpleOptsList();
                    final SimpleOpts[] simpleOptsList2 = optionsList2.getSimpleOptsList();
                    if (simpleOptsList != null && simpleOptsList2 != null) {
                        this.substituteSimpleOpts(simpleOptsList, simpleOptsList2, sb, objectDelimiter);
                    }
                    final SimpleOptsArgs[] simpleOptsArgsList = optionsList.getSimpleOptsArgsList();
                    final SimpleOptsArgs[] simpleOptsArgsList2 = optionsList2.getSimpleOptsArgsList();
                    if (simpleOptsArgsList != null && simpleOptsArgsList2 != null) {
                        this.substituteSimpleOptsArgs(simpleOptsArgsList, simpleOptsArgsList2, sb, objectDelimiter);
                    }
                    final LongOpts[] longOptsList = optionsList.getLongOptsList();
                    final LongOpts[] longOptsList2 = optionsList2.getLongOptsList();
                    if (longOptsList != null && longOptsList2 != null) {
                        this.substituteLongOpts(longOptsList, longOptsList2, sb, objectDelimiter);
                    }
                    final LongOptsArgs[] longOptsArgsList = optionsList.getLongOptsArgsList();
                    final LongOptsArgs[] longOptsArgsList2 = optionsList2.getLongOptsArgsList();
                    if (longOptsArgsList != null && longOptsArgsList2 != null) {
                        this.substituteLongOptsArgs(longOptsArgsList, longOptsArgsList2, sb, objectDelimiter);
                    }
                }
                final CmdObject[] childCmdObjectList = cmdObject.getChildCmdObjectList();
                final CmdObject[] childCmdObjectList2 = cmdObject2.getChildCmdObjectList();
                if (childCmdObjectList != null) {
                    if (childCmdObjectList2 != null) {
                        this.getCommand(childCmdObjectList, childCmdObjectList2, sb, new String[0]);
                    }
                }
            }
        }
        else {
            boolean b = false;
            int k = 0;
            while (k < array.length) {
                if (!array[k].getObjectName().equals(array3[0])) {
                    b = false;
                    ++k;
                }
                else {
                    b = true;
                    final CmdObject cmdObject3 = array[k];
                    final CmdObject cmdObject4 = array2[k];
                    if (!cmdObject3.getObjectName().equals(cmdObject4.getObjectName())) {
                        throw new InvalidCommandException("Invalid data in dataSet");
                    }
                    String objectDelimiter2 = " ";
                    if (cmdObject3.getObjectDelimiter() != null) {
                        objectDelimiter2 = cmdObject3.getObjectDelimiter();
                    }
                    sb.append(cmdObject4.getObjectValue() + objectDelimiter2);
                    final CmdParams[] parameterList3 = cmdObject3.getParameterList();
                    final CmdParams[] parameterList4 = cmdObject4.getParameterList();
                    int n2 = 0;
                    if (parameterList3 != null) {
                        n2 = parameterList3.length;
                    }
                    if (parameterList4 != null && parameterList4.length <= n2) {
                        n2 = parameterList4.length;
                    }
                    if (this.formCommandWithParams) {
                        for (int l = 0; l < n2; ++l) {
                            if (!parameterList3[l].getParamName().equals(parameterList4[l].getParamName())) {
                                throw new InvalidCommandException("Parameter name mismatch between DataSet and CommandSet");
                            }
                            if (parameterList3[l].isSendParam()) {
                                sb.append(parameterList3[l].getParamName());
                                sb.append("=");
                            }
                            final String paramValue2 = parameterList3[l].getParamValue();
                            if (paramValue2 != null) {
                                parameterList4[l].setParamValue(paramValue2);
                                parameterList3[l].setParamValue(null);
                            }
                            this.checkValueType(parameterList4[l].getParamValue(), parameterList3[l].getParamValueType());
                            sb.append(parameterList4[l].getParamValue() + objectDelimiter2);
                        }
                    }
                    final CmdOptions optionsList3 = cmdObject3.getOptionsList();
                    final CmdOptions optionsList4 = cmdObject4.getOptionsList();
                    if (optionsList3 != null && optionsList4 != null) {
                        final SimpleOpts[] simpleOptsList3 = optionsList3.getSimpleOptsList();
                        final SimpleOpts[] simpleOptsList4 = optionsList4.getSimpleOptsList();
                        if (simpleOptsList3 != null && simpleOptsList4 != null) {
                            this.substituteSimpleOpts(simpleOptsList3, simpleOptsList4, sb, objectDelimiter2);
                        }
                        final SimpleOptsArgs[] simpleOptsArgsList3 = optionsList3.getSimpleOptsArgsList();
                        final SimpleOptsArgs[] simpleOptsArgsList4 = optionsList4.getSimpleOptsArgsList();
                        if (simpleOptsArgsList3 != null && simpleOptsArgsList4 != null) {
                            this.substituteSimpleOptsArgs(simpleOptsArgsList3, simpleOptsArgsList4, sb, objectDelimiter2);
                        }
                        final LongOpts[] longOptsList3 = optionsList3.getLongOptsList();
                        final LongOpts[] longOptsList4 = optionsList4.getLongOptsList();
                        if (longOptsList3 != null && longOptsList4 != null) {
                            this.substituteLongOpts(longOptsList3, longOptsList4, sb, objectDelimiter2);
                        }
                        final LongOptsArgs[] longOptsArgsList3 = optionsList3.getLongOptsArgsList();
                        final LongOptsArgs[] longOptsArgsList4 = optionsList4.getLongOptsArgsList();
                        if (longOptsArgsList3 != null && longOptsArgsList4 != null) {
                            this.substituteLongOptsArgs(longOptsArgsList3, longOptsArgsList4, sb, objectDelimiter2);
                        }
                    }
                    if (array3.length == 1) {
                        this.currDataParams = parameterList4;
                        this.objDataParamSet = true;
                        this.currDataOpts = optionsList4;
                        this.objDataOptsSet = true;
                        return;
                    }
                    final CmdObject[] childCmdObjectList3 = cmdObject3.getChildCmdObjectList();
                    final CmdObject[] childCmdObjectList4 = cmdObject4.getChildCmdObjectList();
                    if ((childCmdObjectList3 == null || childCmdObjectList4 == null) && array3.length > 1) {
                        throw new InvalidCommandException("Command Path Not Found");
                    }
                    final String[] array4 = new String[array3.length - 1];
                    for (int n3 = 1; n3 < array3.length; ++n3) {
                        array4[n3 - 1] = array3[n3];
                    }
                    this.getCommand(childCmdObjectList3, childCmdObjectList4, sb, array4);
                    break;
                }
            }
            if (!b) {
                throw new InvalidCommandException("Command Path Not Found");
            }
        }
    }
    
    void substituteSimpleOpts(final SimpleOpts[] array, final SimpleOpts[] array2, final StringBuffer sb, final String s) {
        int n;
        if (array.length >= array2.length) {
            n = array.length;
        }
        else {
            n = array2.length;
        }
        for (int i = 0; i < n; ++i) {
            final String simpleOptVal = array[i].getSimpleOptVal();
            if (this.formCommandWithOptions && simpleOptVal != null) {
                if (array[i].getSimpleOptPrefix() != null) {
                    sb.append(array[i].getSimpleOptPrefix());
                }
                else {
                    sb.append("-");
                }
                array2[i].setSimpleOptVal(simpleOptVal);
                array[i].setSimpleOptVal(null);
                sb.append(array2[i].getSimpleOptVal() + s);
            }
        }
    }
    
    void substituteSimpleOptsArgs(final SimpleOptsArgs[] array, final SimpleOptsArgs[] array2, final StringBuffer sb, final String s) throws InvalidCommandException {
        int n;
        if (array.length >= array2.length) {
            n = array.length;
        }
        else {
            n = array2.length;
        }
        for (int i = 0; i < n; ++i) {
            if (this.formCommandWithOptions) {
                final String simpleOptArgsArg = array[i].getSimpleOptArgsArg();
                if (simpleOptArgsArg != null) {
                    if (array[i].getSimpleOptArgsPrefix() != null) {
                        sb.append(array[i].getSimpleOptArgsPrefix());
                    }
                    else {
                        sb.append("-");
                    }
                    array2[i].setSimpleOptArgsArg(simpleOptArgsArg);
                    array[i].setSimpleOptArgsArg(null);
                    this.checkValueType(array2[i].getSimpleOptArgsArg(), array[i].getOptionType());
                    sb.append(array2[i].getSimpleOptArgsName() + " " + array2[i].getSimpleOptArgsArg() + s);
                }
            }
        }
    }
    
    void substituteLongOpts(final LongOpts[] array, final LongOpts[] array2, final StringBuffer sb, final String s) {
        int n;
        if (array.length >= array2.length) {
            n = array.length;
        }
        else {
            n = array2.length;
        }
        for (int i = 0; i < n; ++i) {
            final String longOptVal = array[i].getLongOptVal();
            if (this.formCommandWithOptions && longOptVal != null) {
                if (array[i].getLongOptPrefix() != null) {
                    sb.append(array[i].getLongOptPrefix());
                }
                else {
                    sb.append("--");
                }
                array2[i].setLongOptVal(longOptVal);
                array[i].setLongOptVal(null);
                sb.append(array2[i].getLongOptVal() + s);
            }
        }
    }
    
    void substituteLongOptsArgs(final LongOptsArgs[] array, final LongOptsArgs[] array2, final StringBuffer sb, final String s) throws InvalidCommandException {
        int n;
        if (array.length >= array2.length) {
            n = array.length;
        }
        else {
            n = array2.length;
        }
        for (int i = 0; i < n; ++i) {
            if (this.formCommandWithOptions) {
                final String longOptArgsArg = array[i].getLongOptArgsArg();
                if (longOptArgsArg != null) {
                    if (array[i].getLongOptArgsPrefix() != null) {
                        sb.append(array[i].getLongOptArgsPrefix());
                    }
                    else {
                        sb.append("--");
                    }
                    array2[i].setLongOptArgsArg(longOptArgsArg);
                    array[i].setLongOptArgsArg(null);
                    this.checkValueType(array2[i].getLongOptArgsArg(), array[i].getOptionType());
                    sb.append(array2[i].getLongOptArgsName() + " " + array2[i].getLongOptArgsArg() + s);
                }
            }
        }
    }
    
    void getCommandObjects(final StringBuffer sb, final StringTokenizer stringTokenizer, final CmdObject cmdObject) throws InvalidCommandException {
        String objectDelimiter = " ";
        if (cmdObject.getObjectDelimiter() != null) {
            objectDelimiter = cmdObject.getObjectDelimiter();
        }
        if (stringTokenizer.hasMoreTokens()) {
            sb.append(stringTokenizer.nextToken() + objectDelimiter);
        }
        else {
            sb.append(cmdObject.getObjectName() + objectDelimiter);
        }
        final CmdParams[] parameterList = cmdObject.getParameterList();
        final CmdOptions optionsList = cmdObject.getOptionsList();
        if (parameterList != null && optionsList != null) {
            this.substituteParamsOptions(sb, stringTokenizer, parameterList, optionsList, objectDelimiter);
        }
        else if (parameterList != null && optionsList == null) {
            this.substituteParameters(sb, stringTokenizer, parameterList, objectDelimiter);
        }
        else if (parameterList == null && optionsList != null && this.formCommandWithOptions) {
            this.substituteOptions(sb, stringTokenizer, optionsList, objectDelimiter);
        }
        final CmdObject[] childCmdObjectList = cmdObject.getChildCmdObjectList();
        if (childCmdObjectList == null) {
            return;
        }
        for (int i = 0; i < childCmdObjectList.length; ++i) {
            this.getCommandObjects(sb, stringTokenizer, childCmdObjectList[i]);
        }
    }
    
    void getCommandForObject(final StringBuffer sb, final StringTokenizer stringTokenizer, final CmdObject cmdObject) throws InvalidCommandException {
        String objectDelimiter = " ";
        if (cmdObject.getObjectDelimiter() != null) {
            objectDelimiter = cmdObject.getObjectDelimiter();
        }
        if (stringTokenizer.hasMoreTokens()) {
            sb.append(stringTokenizer.nextToken() + objectDelimiter);
        }
        else {
            sb.append(cmdObject.getObjectName() + objectDelimiter);
        }
        final CmdParams[] parameterList = cmdObject.getParameterList();
        final CmdOptions optionsList = cmdObject.getOptionsList();
        if (parameterList != null && optionsList != null) {
            this.substituteParamsOptions(sb, stringTokenizer, parameterList, optionsList, objectDelimiter);
        }
        else if (parameterList != null && optionsList == null) {
            this.substituteParameters(sb, stringTokenizer, parameterList, objectDelimiter);
        }
        else if (parameterList == null && optionsList != null && this.formCommandWithOptions) {
            this.substituteOptions(sb, stringTokenizer, optionsList, objectDelimiter);
        }
    }
    
    void getCommandForObjectList(final StringBuffer sb, final StringTokenizer stringTokenizer, final CmdObject cmdObject, final String[] array) throws InvalidCommandException {
        String objectDelimiter = " ";
        if (cmdObject.getObjectDelimiter() != null) {
            objectDelimiter = cmdObject.getObjectDelimiter();
        }
        if (stringTokenizer.hasMoreTokens()) {
            sb.append(stringTokenizer.nextToken() + objectDelimiter);
        }
        else {
            sb.append(cmdObject.getObjectName() + objectDelimiter);
        }
        final CmdParams[] parameterList = cmdObject.getParameterList();
        final CmdOptions optionsList = cmdObject.getOptionsList();
        if (parameterList != null && optionsList != null) {
            this.substituteParamsOptions(sb, stringTokenizer, parameterList, optionsList, objectDelimiter);
        }
        else if (parameterList != null && optionsList == null) {
            this.substituteParameters(sb, stringTokenizer, parameterList, objectDelimiter);
        }
        else if (parameterList == null && optionsList != null && this.formCommandWithOptions) {
            this.substituteOptions(sb, stringTokenizer, optionsList, objectDelimiter);
        }
        final CmdObject[] childCmdObjectList = cmdObject.getChildCmdObjectList();
        if (array.length == 0) {
            return;
        }
        if (childCmdObjectList == null && array.length > 0) {
            throw new InvalidCommandException("Command Path Not Found");
        }
        boolean b = false;
        for (int i = 0; i < childCmdObjectList.length; ++i) {
            if (childCmdObjectList[i].getObjectName().equals(array[0])) {
                b = true;
                final String[] array2 = new String[array.length - 1];
                for (int j = 1; j < array.length; ++j) {
                    array2[j - 1] = array[j];
                }
                this.getCommandForObjectList(sb, stringTokenizer, childCmdObjectList[i], array2);
                break;
            }
            b = false;
        }
        if (!b) {
            throw new InvalidCommandException("Command Path Not Found");
        }
    }
    
    void substituteParamsOptions(final StringBuffer sb, final StringTokenizer stringTokenizer, final CmdParams[] array, final CmdOptions cmdOptions, final String s) throws InvalidCommandException {
        this.substituteParameters(sb, stringTokenizer, array, s);
        if (this.formCommandWithOptions) {
            this.substituteOptions(sb, stringTokenizer, cmdOptions, s);
        }
    }
    
    void substituteParameters(final StringBuffer sb, final StringTokenizer stringTokenizer, final CmdParams[] array, final String s) throws InvalidCommandException {
        int i = 0;
        if (this.formCommandWithParams) {
            while (i < array.length) {
                String nextToken = null;
                if (stringTokenizer.hasMoreTokens()) {
                    nextToken = stringTokenizer.nextToken();
                }
                if (nextToken != null) {
                    if (array[i].isSendParam()) {
                        sb.append(array[i].getParamName());
                        sb.append("=");
                    }
                    this.checkValueType(nextToken, array[i].getParamValueType());
                    sb.append(nextToken + s);
                }
                else if (array[i] != null) {
                    sb.append(array[i].getParamName() + s);
                }
                ++i;
            }
        }
    }
    
    void substituteOptions(final StringBuffer sb, final StringTokenizer stringTokenizer, final CmdOptions cmdOptions, final String s) throws InvalidCommandException {
        if (cmdOptions.getSimpleOptsList() != null) {
            final SimpleOpts[] simpleOptsList = cmdOptions.getSimpleOptsList();
            for (int i = 0; i < simpleOptsList.length; ++i) {
                String nextToken = null;
                if (stringTokenizer.hasMoreTokens()) {
                    nextToken = stringTokenizer.nextToken();
                }
                if (nextToken != null) {
                    if (simpleOptsList[i].getSimpleOptPrefix() != null) {
                        sb.append(simpleOptsList[i].getSimpleOptPrefix());
                    }
                    else {
                        sb.append("-");
                    }
                    sb.append(nextToken);
                    if (s != null) {
                        sb.append(s);
                    }
                }
                else if (simpleOptsList[i] != null && simpleOptsList[i].getSimpleOptChar() != null) {
                    if (simpleOptsList[i].getSimpleOptPrefix() != null) {
                        sb.append(simpleOptsList[i].getSimpleOptPrefix());
                    }
                    else {
                        sb.append("-");
                    }
                    sb.append(simpleOptsList[i].getSimpleOptChar());
                    if (s != null) {
                        sb.append(s);
                    }
                }
            }
        }
        if (cmdOptions.getSimpleOptsArgsList() != null) {
            final SimpleOptsArgs[] simpleOptsArgsList = cmdOptions.getSimpleOptsArgsList();
            for (int j = 0; j < simpleOptsArgsList.length; ++j) {
                String nextToken2 = null;
                if (stringTokenizer.hasMoreTokens()) {
                    nextToken2 = stringTokenizer.nextToken();
                }
                if (nextToken2 != null) {
                    if (simpleOptsArgsList[j].getSimpleOptArgsPrefix() != null) {
                        sb.append(simpleOptsArgsList[j].getSimpleOptArgsPrefix());
                    }
                    else {
                        sb.append("-");
                    }
                    this.checkValueType(nextToken2, simpleOptsArgsList[j].getOptionType());
                    sb.append(nextToken2);
                    if (s != null) {
                        sb.append(s);
                    }
                }
                else if (simpleOptsArgsList[j] != null) {
                    if (simpleOptsArgsList[j].getSimpleOptArgsPrefix() != null) {
                        sb.append(simpleOptsArgsList[j].getSimpleOptArgsPrefix());
                    }
                    else {
                        sb.append("-");
                    }
                    if (simpleOptsArgsList[j].getSimpleOptArgsChar() != null) {
                        sb.append(simpleOptsArgsList[j].getSimpleOptArgsChar());
                    }
                    if (s != null) {
                        sb.append(s);
                    }
                }
            }
        }
        if (cmdOptions.getLongOptsList() != null) {
            final LongOpts[] longOptsList = cmdOptions.getLongOptsList();
            for (int k = 0; k < longOptsList.length; ++k) {
                String nextToken3 = null;
                if (stringTokenizer.hasMoreTokens()) {
                    nextToken3 = stringTokenizer.nextToken();
                }
                if (nextToken3 != null) {
                    if (longOptsList[k].getLongOptPrefix() != null) {
                        sb.append(longOptsList[k].getLongOptPrefix());
                    }
                    else {
                        sb.append("--");
                    }
                    sb.append(nextToken3);
                    if (s != null) {
                        sb.append(s);
                    }
                }
                else if (longOptsList[k] != null && longOptsList[k].getLongOptNames() != null) {
                    if (longOptsList[k].getLongOptPrefix() != null) {
                        sb.append(longOptsList[k].getLongOptPrefix());
                    }
                    else {
                        sb.append("--");
                    }
                    sb.append(longOptsList[k].getLongOptNames());
                    if (s != null) {
                        sb.append(s);
                    }
                }
            }
        }
        if (cmdOptions.getLongOptsArgsList() != null) {
            final LongOptsArgs[] longOptsArgsList = cmdOptions.getLongOptsArgsList();
            for (int l = 0; l < longOptsArgsList.length; ++l) {
                String nextToken4 = null;
                if (stringTokenizer.hasMoreTokens()) {
                    nextToken4 = stringTokenizer.nextToken();
                }
                if (nextToken4 != null) {
                    if (longOptsArgsList[l].getLongOptArgsPrefix() != null) {
                        sb.append(longOptsArgsList[l].getLongOptArgsPrefix());
                    }
                    else {
                        sb.append("--");
                    }
                    this.checkValueType(nextToken4, longOptsArgsList[l].getOptionType());
                    sb.append(nextToken4);
                    if (s != null) {
                        sb.append(s);
                    }
                }
                else if (longOptsArgsList[l] != null) {
                    if (longOptsArgsList[l].getLongOptArgsPrefix() != null) {
                        sb.append(longOptsArgsList[l].getLongOptArgsPrefix());
                    }
                    else {
                        sb.append("--");
                    }
                    if (longOptsArgsList[l].getLongOptArgsName() != null) {
                        sb.append(longOptsArgsList[l].getLongOptArgsName());
                    }
                    if (s != null) {
                        sb.append(s);
                    }
                }
            }
        }
    }
    
    private void checkValueType(final String s, final byte b) throws InvalidCommandException {
        if (b == 1) {
            try {
                Integer.parseInt(s);
                return;
            }
            catch (final Exception ex) {
                throw new InvalidCommandException("Wrong type for parameter value : " + ex.getMessage());
            }
        }
        if (b != 2) {
            if (b == 3) {
                try {
                    Float.parseFloat(s);
                }
                catch (final Exception ex2) {
                    throw new InvalidCommandException("Wrong type for parameter value : " + ex2.getMessage());
                }
            }
        }
    }
    
    private void getCommandList() {
        final NodeList elementsByTagName = this.rootNode.getElementsByTagName("COMMAND");
        for (int length = elementsByTagName.getLength(), i = 0; i < length; ++i) {
            final CLICommandTemplate cliCommandTemplate = this.getCLICommandTemplate(elementsByTagName.item(i));
            if (cliCommandTemplate != null) {
                this.commandTable.put(cliCommandTemplate.getCommandName(), cliCommandTemplate);
            }
        }
    }
    
    private CLICommandTemplate getCLICommandTemplate(final Node node) {
        final CLICommandTemplate cliCommandTemplate = new CLICommandTemplate();
        final NamedNodeMap attributes = node.getAttributes();
        final Attr attr = (Attr)attributes.getNamedItem("NAME");
        final Attr attr2 = (Attr)attributes.getNamedItem("DELIMITER");
        if (attr != null) {
            cliCommandTemplate.setCommandName(attr.getValue());
        }
        if (attr2 != null) {
            cliCommandTemplate.setCommandDelimiter(attr2.getValue());
        }
        final CmdObject[] cmdObjectList = this.getCmdObjectList(node);
        if (cmdObjectList != null) {
            cliCommandTemplate.setCmdObjectList(cmdObjectList);
        }
        final CmdParams[] parameterList = this.getParameterList(node);
        if (parameterList != null) {
            cliCommandTemplate.setCmdParamsList(parameterList);
        }
        final CmdOptions optionsList = this.getOptionsList(node);
        if (optionsList != null) {
            cliCommandTemplate.setCmdOptionsList(optionsList);
        }
        final Vector tokensByName = this.getTokensByName(node, "HELP");
        if (tokensByName.size() == 1) {
            final CmdHelp cmdHelp = new CmdHelp();
            final NodeList childNodes = tokensByName.elementAt(0).getChildNodes();
            final Vector vector = new Vector();
            for (int length = childNodes.getLength(), i = 0; i < length; ++i) {
                if (childNodes.item(i).getNodeType() == 1) {
                    vector.addElement(childNodes.item(i));
                }
            }
            for (int j = 0; j < vector.size(); ++j) {
                final Node node2 = vector.elementAt(j);
                final String trim = node2.getNodeName().trim();
                if (node2.getChildNodes().getLength() != 0) {
                    final String nodeValue = node2.getChildNodes().item(0).getNodeValue();
                    if (trim.equals("DESCRIPTION")) {
                        cmdHelp.setHelpDescription(nodeValue);
                    }
                    else if (trim.equals("SYNTAX")) {
                        cmdHelp.setHelpSyntax(nodeValue);
                    }
                    else if (trim.equals("HELP_OPTIONS")) {
                        cmdHelp.setHelpOptions(nodeValue);
                    }
                    else if (trim.equals("EXAMPLE")) {
                        cmdHelp.setHelpExample(nodeValue);
                    }
                    else if (trim.equals("REMARKS")) {
                        cmdHelp.setHelpRemarks(nodeValue);
                    }
                    else if (trim.equals("RELATED_COMMANDS")) {
                        cmdHelp.setHelpRelatedCommands(nodeValue);
                    }
                }
            }
            if (cmdHelp != null) {
                cliCommandTemplate.setCmdHelp(cmdHelp);
            }
        }
        return cliCommandTemplate;
    }
    
    private CmdObject[] getCmdObjectList(final Node node) {
        final CmdObject[] array = null;
        final Vector tokensByName = this.getTokensByName(node, "OBJECT");
        final int size = tokensByName.size();
        if (size == 0) {
            return array;
        }
        final CmdObject[] array2 = new CmdObject[size];
        for (int i = 0; i < size; ++i) {
            array2[i] = this.getCmdObject((Node)tokensByName.elementAt(i));
        }
        return array2;
    }
    
    private CmdObject getCmdObject(final Node node) {
        final CmdObject cmdObject = new CmdObject();
        final NamedNodeMap attributes = node.getAttributes();
        cmdObject.setObjectName(((Attr)attributes.getNamedItem("NAME")).getValue());
        if (attributes.getNamedItem("DELIMITER") != null) {
            cmdObject.setObjectDelimiter(((Attr)attributes.getNamedItem("DELIMITER")).getValue());
        }
        if (attributes.getNamedItem("DESCRIPTION") != null) {
            cmdObject.setDescription(((Attr)attributes.getNamedItem("DESCRIPTION")).getValue());
        }
        cmdObject.setParameterList(this.getParameterList(node));
        cmdObject.setChildCmdObjectList(this.getCmdObjectList(node));
        cmdObject.setOptionsList(this.getOptionsList(node));
        return cmdObject;
    }
    
    private CmdParams[] getParameterList(final Node node) {
        final Vector tokensByName = this.getTokensByName(node, "PARAM");
        final int size = tokensByName.size();
        if (size == 0) {
            return null;
        }
        final CmdParams[] array = new CmdParams[size];
        for (int i = 0; i < size; ++i) {
            array[i] = this.getCmdParams((Node)tokensByName.elementAt(i));
        }
        return array;
    }
    
    private CmdOptions getOptionsList(final Node node) {
        final Vector tokensByName = this.getTokensByName(node, "OPTIONS");
        if (tokensByName.size() == 0) {
            return null;
        }
        final CmdOptions cmdOptions = new CmdOptions();
        return this.getCmdOptions((Node)tokensByName.elementAt(0));
    }
    
    private CmdOptions getCmdOptions(final Node node) {
        final CmdOptions cmdOptions = new CmdOptions();
        final Attr attr = (Attr)node.getAttributes().getNamedItem("DESCRIPTION");
        if (attr != null) {
            cmdOptions.setOptionsDescription(attr.getValue());
        }
        final Vector tokensByName = this.getTokensByName(node, "SIMPLE_OPTS");
        final int size = tokensByName.size();
        if (size > 0) {
            final SimpleOpts[] simpleOptsList = new SimpleOpts[size];
            for (int i = 0; i < size; ++i) {
                simpleOptsList[i] = this.getSimpleOpts((Node)tokensByName.elementAt(i));
            }
            cmdOptions.setSimpleOptsList(simpleOptsList);
        }
        final Vector tokensByName2 = this.getTokensByName(node, "SIMPLE_OPT_ARGS");
        final int size2 = tokensByName2.size();
        if (size2 > 0) {
            final SimpleOptsArgs[] simpleOptsArgsList = new SimpleOptsArgs[size2];
            for (int j = 0; j < size2; ++j) {
                simpleOptsArgsList[j] = this.getSimpleOptsArgs((Node)tokensByName2.elementAt(j));
            }
            cmdOptions.setSimpleOptsArgsList(simpleOptsArgsList);
        }
        final Vector tokensByName3 = this.getTokensByName(node, "LONG_OPTS");
        final int size3 = tokensByName3.size();
        if (size3 > 0) {
            final LongOpts[] longOptsList = new LongOpts[size3];
            for (int k = 0; k < size3; ++k) {
                longOptsList[k] = this.getLongOpts((Node)tokensByName3.elementAt(k));
            }
            cmdOptions.setLongOptsList(longOptsList);
        }
        final Vector tokensByName4 = this.getTokensByName(node, "LONG_OPT_ARGS");
        final int size4 = tokensByName4.size();
        if (size4 > 0) {
            final LongOptsArgs[] longOptsArgsList = new LongOptsArgs[size4];
            for (int l = 0; l < size4; ++l) {
                longOptsArgsList[l] = this.getLongOptsArgs((Node)tokensByName4.elementAt(l));
            }
            cmdOptions.setLongOptsArgsList(longOptsArgsList);
        }
        return cmdOptions;
    }
    
    private SimpleOpts getSimpleOpts(final Node node) {
        final SimpleOpts simpleOpts = new SimpleOpts();
        final NamedNodeMap attributes = node.getAttributes();
        final Attr attr = (Attr)attributes.getNamedItem("OPT_CHAR");
        final Attr attr2 = (Attr)attributes.getNamedItem("OPT_DEP");
        final Attr attr3 = (Attr)attributes.getNamedItem("OPT_PREFIX");
        if (attr != null) {
            simpleOpts.setSimpleOptChar(attr.getValue());
        }
        if (attr2 != null) {
            simpleOpts.setSimpleOptDep(attr2.getValue());
        }
        if (attr3 != null) {
            simpleOpts.setSimpleOptPrefix(attr3.getValue());
        }
        return simpleOpts;
    }
    
    private SimpleOptsArgs getSimpleOptsArgs(final Node node) {
        final SimpleOptsArgs simpleOptsArgs = new SimpleOptsArgs();
        final NamedNodeMap attributes = node.getAttributes();
        final Attr attr = (Attr)attributes.getNamedItem("OPT_CHAR");
        final Attr attr2 = (Attr)attributes.getNamedItem("TYPE");
        final Attr attr3 = (Attr)attributes.getNamedItem("OPT_DEP");
        final Attr attr4 = (Attr)attributes.getNamedItem("OPT_PREFIX");
        if (attr != null) {
            simpleOptsArgs.setSimpleOptArgsChar(attr.getValue());
        }
        if (attr2 != null) {
            final String upperCase = attr2.getValue().toUpperCase();
            if (upperCase.equals("INTEGER")) {
                simpleOptsArgs.setOptionType((byte)1);
            }
            else if (upperCase.equals("CHAR")) {
                simpleOptsArgs.setOptionType((byte)2);
            }
            else if (upperCase.equals("FLOAT")) {
                simpleOptsArgs.setOptionType((byte)3);
            }
            else if (upperCase.equals("STRING")) {
                simpleOptsArgs.setOptionType((byte)4);
            }
        }
        if (attr3 != null) {
            simpleOptsArgs.setSimpleOptArgsDep(attr3.getValue());
        }
        if (attr4 != null) {
            simpleOptsArgs.setSimpleOptArgsPrefix(attr4.getValue());
        }
        return simpleOptsArgs;
    }
    
    private LongOpts getLongOpts(final Node node) {
        final LongOpts longOpts = new LongOpts();
        final NamedNodeMap attributes = node.getAttributes();
        final Attr attr = (Attr)attributes.getNamedItem("OPT_NAME");
        final Attr attr2 = (Attr)attributes.getNamedItem("OPT_DEP");
        final Attr attr3 = (Attr)attributes.getNamedItem("OPT_PREFIX");
        if (attr != null) {
            longOpts.setLongOptNames(attr.getValue());
        }
        if (attr2 != null) {
            longOpts.setLongOptDep(attr2.getValue());
        }
        if (attr3 != null) {
            longOpts.setLongOptPrefix(attr3.getValue());
        }
        return longOpts;
    }
    
    private LongOptsArgs getLongOptsArgs(final Node node) {
        final LongOptsArgs longOptsArgs = new LongOptsArgs();
        final NamedNodeMap attributes = node.getAttributes();
        final Attr attr = (Attr)attributes.getNamedItem("OPT_NAME");
        final Attr attr2 = (Attr)attributes.getNamedItem("TYPE");
        final Attr attr3 = (Attr)attributes.getNamedItem("OPT_DEP");
        final Attr attr4 = (Attr)attributes.getNamedItem("OPT_PREFIX");
        if (attr != null) {
            longOptsArgs.setLongOptArgsName(attr.getValue());
        }
        if (attr2 != null) {
            final String upperCase = attr2.getValue().toUpperCase();
            if (upperCase.equals("INTEGER")) {
                longOptsArgs.setOptionType((byte)1);
            }
            else if (upperCase.equals("CHAR")) {
                longOptsArgs.setOptionType((byte)2);
            }
            else if (upperCase.equals("FLOAT")) {
                longOptsArgs.setOptionType((byte)3);
            }
            else if (upperCase.equals("STRING")) {
                longOptsArgs.setOptionType((byte)4);
            }
        }
        if (attr3 != null) {
            longOptsArgs.setLongOptArgsDep(attr3.getValue());
        }
        if (attr4 != null) {
            longOptsArgs.setLongOptArgsPrefix(attr4.getValue());
        }
        return longOptsArgs;
    }
    
    private CmdParams getCmdParams(final Node node) {
        final CmdParams cmdParams = new CmdParams();
        final NamedNodeMap attributes = node.getAttributes();
        final Attr attr = (Attr)attributes.getNamedItem("NAME");
        final Attr attr2 = (Attr)attributes.getNamedItem("PARAMTYPE");
        final Attr attr3 = (Attr)attributes.getNamedItem("VALUETYPE");
        final Attr attr4 = (Attr)attributes.getNamedItem("OPTION_FLAG");
        final Attr attr5 = (Attr)attributes.getNamedItem("DESCRIPTION");
        if (attr != null) {
            cmdParams.setParamName(attr.getValue());
        }
        if (attr2 != null) {
            final String upperCase = attr2.getValue().toUpperCase();
            if (upperCase.equals("NAMEVALUE")) {
                cmdParams.sendParam = true;
            }
            else if (upperCase.equals("VALUE")) {
                cmdParams.sendParam = false;
            }
        }
        if (attr3 != null) {
            final String upperCase2 = attr3.getValue().toUpperCase();
            if (upperCase2.equals("INTEGER")) {
                cmdParams.setParamValueType((byte)1);
            }
            else if (upperCase2.equals("CHAR")) {
                cmdParams.setParamValueType((byte)2);
            }
            else if (upperCase2.equals("FLOAT")) {
                cmdParams.setParamValueType((byte)3);
            }
            else if (upperCase2.equals("STRING")) {
                cmdParams.setParamValueType((byte)4);
            }
        }
        if (attr4 != null) {
            final String upperCase3 = attr4.getValue().toUpperCase();
            if (upperCase3.equals("MANDATORY")) {
                cmdParams.optionFlag = false;
            }
            else if (upperCase3.equals("OPTIONAL")) {
                cmdParams.optionFlag = true;
            }
        }
        if (attr5 != null) {
            cmdParams.setParamDescription(attr5.getValue());
        }
        return cmdParams;
    }
    
    private CLICommandTemplate getCommand(final String s) {
        return this.commandTable.get(s);
    }
    
    public Hashtable getCommandSetEntries() {
        return this.commandTable;
    }
    
    public Hashtable getDataSetEntries() {
        if (this.dataSet != null) {
            return this.dataSet.getDataSetEntries();
        }
        return null;
    }
    
    public void writeHeaderToFile(final String s) throws IOException {
        new CmdSetWriter().writeHeaderToFile(s);
    }
    
    public void writeToFile(final String s) throws IOException {
        if (this.getCommandSetEntries() != null) {
            new CmdSetWriter().writeXmlToFile(s, this.getCommandSetEntries());
        }
    }
    
    private Vector getTokensByName(final Node node, final String s) {
        final NodeList childNodes = node.getChildNodes();
        final Vector vector = new Vector();
        for (int length = childNodes.getLength(), i = 0; i < length; ++i) {
            final Node item = childNodes.item(i);
            if (item.getNodeType() == 1 && item.getNodeName().equals(s)) {
                vector.addElement(childNodes.item(i));
            }
        }
        return vector;
    }
    
    public CmdParams[] getCurrDataParams() {
        return this.currDataParams;
    }
    
    public CmdOptions getCurrDataOptions() {
        return this.currDataOpts;
    }
    
    public void formCommandWithOptions(final boolean formCommandWithOptions) {
        this.formCommandWithOptions = formCommandWithOptions;
    }
    
    public boolean isSetFormCommandWithOptions() {
        return this.formCommandWithOptions;
    }
    
    public void formCommandWithParams(final boolean formCommandWithParams) {
        this.formCommandWithParams = formCommandWithParams;
    }
    
    public boolean isSetFormCommandWithParams() {
        return this.formCommandWithParams;
    }
    
    public CLICommandTemplate[] getCLICommandTemplateList() {
        if (this.commandTable != null) {
            final CLICommandTemplate[] array = new CLICommandTemplate[this.commandTable.size()];
            int n = 0;
            final Enumeration elements = this.commandTable.elements();
            while (elements.hasMoreElements()) {
                array[n] = (CLICommandTemplate)elements.nextElement();
                ++n;
            }
            return array;
        }
        return null;
    }
    
    public void addCLICommandTemplate(final CLICommandTemplate cliCommandTemplate) throws Exception {
        String commandName = null;
        if (cliCommandTemplate != null) {
            commandName = cliCommandTemplate.getCommandName();
        }
        if (cliCommandTemplate != null && commandName != null) {
            if (this.commandTable != null) {
                if (this.commandTable.containsKey(commandName)) {
                    throw new Exception("The command name " + commandName + " already exists.");
                }
                this.commandTable.put(commandName, cliCommandTemplate);
            }
            else {
                (this.commandTable = new Hashtable()).put(commandName, cliCommandTemplate);
            }
            return;
        }
        throw new Exception("Exception while trying to add command " + cliCommandTemplate);
    }
    
    public void removeCLICommandTemplate(final CLICommandTemplate cliCommandTemplate) throws Exception {
        String commandName = null;
        if (cliCommandTemplate != null) {
            commandName = cliCommandTemplate.getCommandName();
        }
        if (cliCommandTemplate == null || commandName == null) {
            throw new Exception("Exception while trying to remove command " + cliCommandTemplate);
        }
        if (this.commandTable != null && this.commandTable.containsKey(commandName)) {
            this.commandTable.remove(commandName);
            return;
        }
        throw new Exception("The command name " + commandName + " does already exist.");
    }
    
    public void modifyCLICommandTemplate(final String s, final CLICommandTemplate cliCommandTemplate) throws Exception {
        String s2 = null;
        if (s != null && !s.equals("")) {
            s2 = s;
        }
        if (s2 == null) {
            throw new Exception("Exception while trying to modify command " + s);
        }
        if (!this.commandTable.containsKey(s2)) {
            throw new Exception("The command name " + s2 + " does not exist.");
        }
        if (cliCommandTemplate != null && cliCommandTemplate.getCommandName() != null) {
            this.commandTable.remove(s2);
            this.commandTable.put(cliCommandTemplate.getCommandName(), cliCommandTemplate);
            return;
        }
        throw new Exception("Can not modify command. Please enter a valid CLICommandTemplate object.");
    }
}
