package com.adventnet.cli.messageset;

import java.io.OutputStream;
import java.io.FileOutputStream;
import org.apache.crimson.tree.XmlDocument;
import java.io.IOException;
import java.util.Enumeration;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import javax.xml.parsers.DocumentBuilderFactory;
import java.util.Hashtable;
import org.w3c.dom.Document;

class DataSetWriter
{
    static final String DATASetElement = "DATA-SET";
    static final String STRDataSetElement = "STR-DATA-SET";
    static final String CMDDataElement = "CMDDATA";
    static final String DATAElement = "DATA";
    static final String OBJElement = "OBJECT";
    static final String PARElement = "PARAM";
    static final String OPTElement = "OPTIONS";
    static final String LOPTElement = "LONG_OPTS";
    static final String LOPTAElement = "LONG_OPT_ARGS";
    static final String SOPTElement = "SIMPLE_OPTS";
    static final String SOPTAElement = "SIMPLE_OPT_ARGS";
    static final String VERAtt = "VERSION";
    static final String VALAtt = "VALUE";
    static final String CMDNAMEAtt = "CMDNAME";
    static final String NAMEAtt = "NAME";
    static final String OPTNAtt = "OPT_NAME";
    static final String OPTAAtt = "OPT_ARG";
    static final String OPTNSAtt = "OPT_NAMES";
    static final String OPTVAtt = "OPT_VAL";
    static final String TYPEAtt = "TYPE";
    Document doc;
    
    void writeXmlToFile(final String s, final Hashtable hashtable) throws IOException {
        try {
            this.doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
        }
        catch (final Exception ex) {
            ex.printStackTrace();
            return;
        }
        final Element element = this.doc.createElement("DATA-SET");
        this.doc.appendChild(element);
        element.setAttribute("VERSION", "1.1");
        final Element element2 = this.doc.createElement("STR-DATA-SET");
        element.appendChild(element2);
        final Enumeration keys = hashtable.keys();
        while (keys.hasMoreElements()) {
            final String s2 = (String)keys.nextElement();
            final Element element3 = this.doc.createElement("CMDDATA");
            element3.setAttribute("CMDNAME", s2);
            element2.appendChild(element3);
            final CLIDataInstance[] array = hashtable.get(s2);
            for (int i = 0; i < array.length; ++i) {
                this.appendDataInstance(element3, array[i]);
            }
        }
        this.writeToFile(s, this.doc);
    }
    
    void appendDataInstance(final Element element, final CLIDataInstance cliDataInstance) {
        final Element element2 = this.doc.createElement("DATA");
        element2.setAttribute("NAME", cliDataInstance.getDataName());
        this.appendCmdObjects(element2, cliDataInstance);
        final CmdParams[] cmdParamList = cliDataInstance.getCmdParamList();
        if (cmdParamList != null) {
            this.appendCmdParams(element2, cmdParamList);
        }
        final CmdOptions cmdOption = cliDataInstance.getCmdOption();
        if (cmdOption != null) {
            this.appendCmdOptions(element2, cmdOption);
        }
        element.appendChild(element2);
    }
    
    void appendCmdObjects(final Element element, final CLIDataInstance cliDataInstance) {
        final CmdObject[] cmdObjectList = cliDataInstance.getCmdObjectList();
        if (cmdObjectList == null) {
            return;
        }
        for (int i = 0; i < cmdObjectList.length; ++i) {
            final Element element2 = this.doc.createElement("OBJECT");
            element2.setAttribute("NAME", cmdObjectList[i].getObjectName());
            element2.setAttribute("VALUE", cmdObjectList[i].getObjectValue());
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
            element2.setAttribute("VALUE", array[i].getObjectValue());
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
            element2.setAttribute("VALUE", array[i].getParamValue());
            element.appendChild(element2);
        }
    }
    
    void appendCmdOptions(final Element element, final CmdOptions cmdOptions) {
        final Element element2 = this.doc.createElement("OPTIONS");
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
        element.appendChild(element2);
    }
    
    void appendSimpleOpts(final Element element, final SimpleOpts[] array) {
        for (int i = 0; i < array.length; ++i) {
            final Element element2 = this.doc.createElement("SIMPLE_OPTS");
            element2.setAttribute("OPT_VAL", array[i].getSimpleOptVal());
            element.appendChild(element2);
        }
    }
    
    void appendSimpleOptsArgs(final Element element, final SimpleOptsArgs[] array) {
        for (int i = 0; i < array.length; ++i) {
            final Element element2 = this.doc.createElement("SIMPLE_OPT_ARGS");
            element2.setAttribute("OPT_NAME", array[i].getSimpleOptArgsName());
            element2.setAttribute("OPT_ARG", array[i].getSimpleOptArgsArg());
            element.appendChild(element2);
        }
    }
    
    void appendLongOpts(final Element element, final LongOpts[] array) {
        for (int i = 0; i < array.length; ++i) {
            final Element element2 = this.doc.createElement("LONG_OPTS");
            element2.setAttribute("OPT_VAL", array[i].getLongOptVal());
            element.appendChild(element2);
        }
    }
    
    void appendLongOptsArgs(final Element element, final LongOptsArgs[] array) {
        for (int i = 0; i < array.length; ++i) {
            final Element element2 = this.doc.createElement("LONG_OPT_ARGS");
            element2.setAttribute("OPT_NAME", array[i].getLongOptArgsName());
            element2.setAttribute("OPT_ARG", array[i].getLongOptArgsArg());
            element.appendChild(element2);
        }
    }
    
    void writeToFile(final String s, final Document document) throws IOException {
        ((XmlDocument)document).write((OutputStream)new FileOutputStream(s));
    }
    
    public DataSetWriter() {
        this.doc = null;
    }
}
