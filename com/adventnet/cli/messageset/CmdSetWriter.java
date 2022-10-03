package com.adventnet.cli.messageset;

import java.io.OutputStream;
import java.io.FileOutputStream;
import org.apache.crimson.tree.XmlDocument;
import java.util.Enumeration;
import java.util.Hashtable;
import java.io.IOException;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;

class CmdSetWriter
{
    static final String CMDSetElement = "COMMAND-SET";
    static final String STRCmdSetElement = "STR-COMMAND-SET";
    static final String COMMANDElement = "COMMAND";
    static final String OBJElement = "OBJECT";
    static final String PARElement = "PARAM";
    static final String OPTElement = "OPTIONS";
    static final String LOPTElement = "LONG_OPTS";
    static final String LOPTAElement = "LONG_OPT_ARGS";
    static final String SOPTElement = "SIMPLE_OPTS";
    static final String SOPTAElement = "SIMPLE_OPT_ARGS";
    static final String HELPElement = "HELP";
    static final String DESCRElement = "DESCRIPTION";
    static final String SYNElement = "SYNTAX";
    static final String HOPTElement = "HELP_OPTIONS";
    static final String EXElement = "EXAMPLE";
    static final String REMElement = "REMARKS";
    static final String RELElement = "RELATED_COMMANDS";
    static final String VERAtt = "VERSION";
    static final String NAMEAtt = "NAME";
    static final String DELIMAtt = "DELIMITER";
    static final String DESCAtt = "DESCRIPTION";
    static final String PTYPEAtt = "PARAMTYPE";
    static final String VTYPEAtt = "VALUETYPE";
    static final String OPTFAtt = "OPTION_FLAG";
    static final String OPTCAtt = "OPT_CHAR";
    static final String OPTPAtt = "OPT_PREFIX";
    static final String OPTDAtt = "OPT_DEP";
    static final String OPTNAtt = "OPT_NAME";
    static final String TYPEAtt = "TYPE";
    Document doc;
    
    CmdSetWriter() {
        this.doc = null;
    }
    
    void writeHeaderToFile(final String s) throws IOException {
        try {
            this.doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
        }
        catch (final Exception ex) {
            ex.printStackTrace();
            return;
        }
        final Element element = this.doc.createElement("COMMAND-SET");
        this.doc.appendChild(element);
        element.setAttribute("VERSION", "1.1");
        this.writeToFile(s, this.doc);
    }
    
    void writeXmlToFile(final String s, final Hashtable hashtable) throws IOException {
        try {
            this.doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
        }
        catch (final Exception ex) {
            ex.printStackTrace();
            return;
        }
        final Element element = this.doc.createElement("COMMAND-SET");
        this.doc.appendChild(element);
        element.setAttribute("VERSION", "1.1");
        final Element element2 = this.doc.createElement("STR-COMMAND-SET");
        element.appendChild(element2);
        final Enumeration keys = hashtable.keys();
        while (keys.hasMoreElements()) {
            final String s2 = (String)keys.nextElement();
            this.appendCmdTemplate(element2, s2, (CLICommandTemplate)hashtable.get(s2));
        }
        this.writeToFile(s, this.doc);
    }
    
    void appendCmdTemplate(final Element element, final String s, final CLICommandTemplate cliCommandTemplate) {
        final Element element2 = this.doc.createElement("COMMAND");
        element2.setAttribute("NAME", s);
        final String commandDelimiter = cliCommandTemplate.getCommandDelimiter();
        if (commandDelimiter != null) {
            element2.setAttribute("DELIMITER", commandDelimiter);
        }
        this.appendCmdObjects(element2, cliCommandTemplate);
        final CmdParams[] cmdParamsList = cliCommandTemplate.getCmdParamsList();
        if (cmdParamsList != null) {
            this.appendCmdParams(element2, cmdParamsList);
        }
        final CmdOptions cmdOptionsList = cliCommandTemplate.getCmdOptionsList();
        if (cmdOptionsList != null) {
            this.appendCmdOptions(element2, cmdOptionsList);
        }
        final CmdHelp cmdHelp = cliCommandTemplate.getCmdHelp();
        if (cmdHelp != null) {
            this.appendCmdHelp(element2, cmdHelp);
        }
        element.appendChild(element2);
    }
    
    void appendCmdObjects(final Element element, final CLICommandTemplate cliCommandTemplate) {
        final CmdObject[] cmdObjectList = cliCommandTemplate.getCmdObjectList();
        if (cmdObjectList == null) {
            return;
        }
        for (int i = 0; i < cmdObjectList.length; ++i) {
            final Element element2 = this.doc.createElement("OBJECT");
            element2.setAttribute("NAME", cmdObjectList[i].getObjectName());
            final String objectDelimiter = cmdObjectList[i].getObjectDelimiter();
            if (objectDelimiter != null) {
                element2.setAttribute("DELIMITER", objectDelimiter);
            }
            final String description = cmdObjectList[i].getDescription();
            if (objectDelimiter != null) {
                element2.setAttribute("DESCRIPTION", description);
            }
            final CmdObject[] childCmdObjectList = cmdObjectList[i].getChildCmdObjectList();
            if (childCmdObjectList != null) {
                this.appendCmdChildObjects(element2, childCmdObjectList);
            }
            final CmdParams[] parameterList = cmdObjectList[i].getParameterList();
            if (parameterList != null) {
                this.appendCmdParams(element2, parameterList);
            }
            final CmdOptions optionsList = cmdObjectList[i].getOptionsList();
            if (optionsList != null) {
                this.appendCmdOptions(element2, optionsList);
            }
            element.appendChild(element2);
        }
    }
    
    void appendCmdChildObjects(final Element element, final CmdObject[] array) {
        for (int i = 0; i < array.length; ++i) {
            final Element element2 = this.doc.createElement("OBJECT");
            element2.setAttribute("NAME", array[i].getObjectName());
            final String objectDelimiter = array[i].getObjectDelimiter();
            if (objectDelimiter != null) {
                element2.setAttribute("DELIMITER", objectDelimiter);
            }
            final String description = array[i].getDescription();
            if (description != null) {
                element2.setAttribute("DESCRIPTION", description);
            }
            element.appendChild(element2);
            final CmdObject[] childCmdObjectList = array[i].getChildCmdObjectList();
            if (childCmdObjectList != null) {
                this.appendCmdChildObjects(element2, childCmdObjectList);
            }
            final CmdParams[] parameterList = array[i].getParameterList();
            if (parameterList != null) {
                this.appendCmdParams(element2, parameterList);
            }
            final CmdOptions optionsList = array[i].getOptionsList();
            if (optionsList != null) {
                this.appendCmdOptions(element2, optionsList);
            }
        }
    }
    
    void appendCmdParams(final Element element, final CmdParams[] array) {
        for (int i = 0; i < array.length; ++i) {
            final Element element2 = this.doc.createElement("PARAM");
            element2.setAttribute("NAME", array[i].getParamName());
            if (array[i].isSendParam()) {
                element2.setAttribute("PARAMTYPE", "NAMEVALUE");
            }
            else {
                element2.setAttribute("PARAMTYPE", "VALUE");
            }
            final byte paramValueType = array[i].getParamValueType();
            if (paramValueType != 0) {
                switch (paramValueType) {
                    case 1: {
                        element2.setAttribute("VALUETYPE", "INTEGER");
                        break;
                    }
                    case 2: {
                        element2.setAttribute("VALUETYPE", "CHAR");
                        break;
                    }
                    case 3: {
                        element2.setAttribute("VALUETYPE", "FLOAT");
                        break;
                    }
                    case 4: {
                        element2.setAttribute("VALUETYPE", "STRING");
                        break;
                    }
                }
            }
            if (array[i].isOptional()) {
                element2.setAttribute("OPTION_FLAG", "OPTIONAL");
            }
            else {
                element2.setAttribute("OPTION_FLAG", "MANDATORY");
            }
            final String paramDescription = array[i].getParamDescription();
            if (paramDescription != null) {
                element2.setAttribute("DESCRIPTION", paramDescription);
            }
            element.appendChild(element2);
        }
    }
    
    void appendCmdOptions(final Element element, final CmdOptions cmdOptions) {
        final Element element2 = this.doc.createElement("OPTIONS");
        final String optionsDescription = cmdOptions.getOptionsDescription();
        if (optionsDescription != null) {
            element2.setAttribute("DESCRIPTION", optionsDescription);
        }
        final SimpleOpts[] simpleOptsList = cmdOptions.getSimpleOptsList();
        if (simpleOptsList != null) {
            this.appendSimpleOpts(element2, simpleOptsList);
        }
        final SimpleOptsArgs[] simpleOptsArgsList = cmdOptions.getSimpleOptsArgsList();
        if (simpleOptsArgsList != null) {
            this.appendSimpleOptsArgs(element2, simpleOptsArgsList);
        }
        final LongOpts[] longOptsList = cmdOptions.getLongOptsList();
        if (longOptsList != null) {
            this.appendLongOpts(element2, longOptsList);
        }
        final LongOptsArgs[] longOptsArgsList = cmdOptions.getLongOptsArgsList();
        if (longOptsArgsList != null) {
            this.appendLongOptsArgs(element2, longOptsArgsList);
        }
        if (simpleOptsList != null || simpleOptsArgsList != null || longOptsList != null || longOptsArgsList != null) {
            element.appendChild(element2);
        }
    }
    
    void appendSimpleOpts(final Element element, final SimpleOpts[] array) {
        for (int i = 0; i < array.length; ++i) {
            final Element element2 = this.doc.createElement("SIMPLE_OPTS");
            element2.setAttribute("OPT_CHAR", array[i].getSimpleOptChar());
            final String simpleOptPrefix = array[i].getSimpleOptPrefix();
            if (simpleOptPrefix != null) {
                element2.setAttribute("OPT_PREFIX", simpleOptPrefix);
            }
            final String simpleOptDep = array[i].getSimpleOptDep();
            if (simpleOptDep != null) {
                element2.setAttribute("OPT_DEP", simpleOptDep);
            }
            element.appendChild(element2);
        }
    }
    
    void appendSimpleOptsArgs(final Element element, final SimpleOptsArgs[] array) {
        for (int i = 0; i < array.length; ++i) {
            final Element element2 = this.doc.createElement("SIMPLE_OPT_ARGS");
            element2.setAttribute("OPT_CHAR", array[i].getSimpleOptArgsChar());
            final String simpleOptArgsPrefix = array[i].getSimpleOptArgsPrefix();
            if (simpleOptArgsPrefix != null) {
                element2.setAttribute("OPT_PREFIX", simpleOptArgsPrefix);
            }
            final byte optionType = array[i].getOptionType();
            if (optionType != 0) {
                switch (optionType) {
                    case 1: {
                        element2.setAttribute("TYPE", "INTEGER");
                        break;
                    }
                    case 2: {
                        element2.setAttribute("TYPE", "CHAR");
                        break;
                    }
                    case 3: {
                        element2.setAttribute("TYPE", "FLOAT");
                        break;
                    }
                    case 4: {
                        element2.setAttribute("TYPE", "STRING");
                        break;
                    }
                }
            }
            final String simpleOptArgsDep = array[i].getSimpleOptArgsDep();
            if (simpleOptArgsDep != null) {
                element2.setAttribute("OPT_DEP", simpleOptArgsDep);
            }
            element.appendChild(element2);
        }
    }
    
    void appendLongOpts(final Element element, final LongOpts[] array) {
        for (int i = 0; i < array.length; ++i) {
            final Element element2 = this.doc.createElement("LONG_OPTS");
            element2.setAttribute("OPT_NAME", array[i].getLongOptNames());
            final String longOptPrefix = array[i].getLongOptPrefix();
            if (longOptPrefix != null) {
                element2.setAttribute("OPT_PREFIX", longOptPrefix);
            }
            final String longOptDep = array[i].getLongOptDep();
            if (longOptDep != null) {
                element2.setAttribute("OPT_DEP", longOptDep);
            }
            element.appendChild(element2);
        }
    }
    
    void appendLongOptsArgs(final Element element, final LongOptsArgs[] array) {
        for (int i = 0; i < array.length; ++i) {
            final Element element2 = this.doc.createElement("LONG_OPT_ARGS");
            element2.setAttribute("OPT_NAME", array[i].getLongOptArgsName());
            final String longOptArgsPrefix = array[i].getLongOptArgsPrefix();
            if (longOptArgsPrefix != null) {
                element2.setAttribute("OPT_PREFIX", longOptArgsPrefix);
            }
            final byte optionType = array[i].getOptionType();
            if (optionType != 0) {
                switch (optionType) {
                    case 1: {
                        element2.setAttribute("TYPE", "INTEGER");
                        break;
                    }
                    case 2: {
                        element2.setAttribute("TYPE", "CHAR");
                        break;
                    }
                    case 3: {
                        element2.setAttribute("TYPE", "FLOAT");
                        break;
                    }
                    case 4: {
                        element2.setAttribute("TYPE", "STRING");
                        break;
                    }
                }
            }
            final String longOptArgsDep = array[i].getLongOptArgsDep();
            if (longOptArgsDep != null) {
                element2.setAttribute("OPT_DEP", longOptArgsDep);
            }
            element.appendChild(element2);
        }
    }
    
    void appendCmdHelp(final Element element, final CmdHelp cmdHelp) {
        final Element element2 = this.doc.createElement("HELP");
        this.appendHelpData(element2, cmdHelp.getHelpDescription(), "DESCRIPTION");
        this.appendHelpData(element2, cmdHelp.getHelpSyntax(), "SYNTAX");
        this.appendHelpData(element2, cmdHelp.getHelpOptions(), "HELP_OPTIONS");
        this.appendHelpData(element2, cmdHelp.getHelpExample(), "EXAMPLE");
        this.appendHelpData(element2, cmdHelp.getHelpRemarks(), "REMARKS");
        this.appendHelpData(element2, cmdHelp.getHelpRelatedCommands(), "RELATED_COMMANDS");
        element.appendChild(element2);
    }
    
    void appendHelpData(final Element element, final String s, final String s2) {
        if (s != null) {
            final Element element2 = this.doc.createElement(s2);
            element2.appendChild(this.doc.createTextNode(s));
            element.appendChild(element2);
        }
    }
    
    void writeToFile(final String s, final Document document) throws IOException {
        ((XmlDocument)document).write((OutputStream)new FileOutputStream(s));
    }
}
