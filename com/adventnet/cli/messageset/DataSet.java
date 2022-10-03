package com.adventnet.cli.messageset;

import java.io.IOException;
import java.io.FileNotFoundException;
import org.w3c.dom.NamedNodeMap;
import java.util.Vector;
import org.w3c.dom.Attr;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Document;
import javax.xml.parsers.DocumentBuilder;
import java.io.File;
import com.adventnet.util.parser.ParseException;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.FileOutputStream;
import java.util.Hashtable;
import org.w3c.dom.Element;
import java.io.Serializable;

public class DataSet implements Serializable
{
    static final String cmdData = "CMDDATA";
    static final String cmdName = "CMDNAME";
    static final String dataNode = "DATA";
    static final String name = "NAME";
    static final String object = "OBJECT";
    static final String value = "VALUE";
    static final String param = "PARAM";
    static final String options = "OPTIONS";
    static final String simpleOpts = "SIMPLE_OPTS";
    static final String simpleOptsArgs = "SIMPLE_OPT_ARGS";
    static final String optName = "OPT_NAME";
    static final String optArg = "OPT_ARG";
    static final String longOpts = "LONG_OPTS";
    static final String optVal = "OPT_VAL";
    static final String longOptsArgs = "LONG_OPT_ARGS";
    static final String header = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>";
    Element rootNode;
    Hashtable dataTable;
    private String commandName;
    private FileOutputStream fos;
    private int tabCount;
    
    public DataSet(final String s) throws ParseException {
        this.rootNode = null;
        this.dataTable = null;
        this.commandName = null;
        this.tabCount = 0;
        DocumentBuilder documentBuilder;
        try {
            documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        }
        catch (final Exception ex) {
            throw new ParseException(ex.getMessage());
        }
        final File file = new File(s);
        Document parse;
        try {
            parse = documentBuilder.parse(file);
        }
        catch (final Exception ex2) {
            throw new ParseException(ex2.getMessage());
        }
        this.rootNode = parse.getDocumentElement();
        this.dataTable = new Hashtable();
        this.getDataList();
    }
    
    private void getDataList() {
        final NodeList elementsByTagName = this.rootNode.getElementsByTagName("CMDDATA");
        for (int length = elementsByTagName.getLength(), i = 0; i < length; ++i) {
            final CLIDataInstance[] cliDataInstanceList = this.getCLIDataInstanceList(elementsByTagName.item(i));
            if (cliDataInstanceList != null) {
                this.dataTable.put(this.commandName, cliDataInstanceList);
            }
        }
    }
    
    private CLIDataInstance[] getCLIDataInstanceList(final Node node) {
        this.commandName = ((Attr)node.getAttributes().getNamedItem("CMDNAME")).getValue();
        final Vector tokensByName = this.getTokensByName(node, "DATA");
        final int size = tokensByName.size();
        if (size == 0) {
            return null;
        }
        final CLIDataInstance[] array = new CLIDataInstance[size];
        for (int i = 0; i < size; ++i) {
            final Node node2 = tokensByName.elementAt(i);
            final CLIDataInstance cliDataInstance = new CLIDataInstance();
            final NamedNodeMap attributes = node2.getAttributes();
            if (attributes.getNamedItem("NAME") != null) {
                cliDataInstance.setDataName(((Attr)attributes.getNamedItem("NAME")).getValue());
            }
            final CmdObject[] cmdObjectList = this.getCmdObjectList(node2);
            if (cmdObjectList != null) {
                cliDataInstance.setCmdObjectList(cmdObjectList);
            }
            final CmdParams[] parameterList = this.getParameterList(node2);
            if (parameterList != null) {
                cliDataInstance.setCmdParamList(parameterList);
            }
            final Vector tokensByName2 = this.getTokensByName(node2, "OPTIONS");
            if (tokensByName2.size() == 1) {
                final CmdOptions cmdOptions = this.getCmdOptions(tokensByName2.elementAt(0));
                if (cmdOptions != null) {
                    cliDataInstance.setCmdOption(cmdOptions);
                }
            }
            array[i] = cliDataInstance;
        }
        return array;
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
        final String value = ((Attr)attributes.getNamedItem("NAME")).getValue();
        final String value2 = ((Attr)attributes.getNamedItem("VALUE")).getValue();
        cmdObject.setObjectName(value);
        cmdObject.setObjectValue(value2);
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
    
    private CmdParams getCmdParams(final Node node) {
        final CmdParams cmdParams = new CmdParams();
        final NamedNodeMap attributes = node.getAttributes();
        final Attr attr = (Attr)attributes.getNamedItem("NAME");
        final Attr attr2 = (Attr)attributes.getNamedItem("VALUE");
        if (attr2 != null) {
            cmdParams.setParamValue(attr2.getValue());
        }
        if (attr != null) {
            cmdParams.setParamName(attr.getValue());
        }
        return cmdParams;
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
        final Attr attr = (Attr)node.getAttributes().getNamedItem("OPT_VAL");
        if (attr != null) {
            simpleOpts.setSimpleOptVal(attr.getValue());
        }
        return simpleOpts;
    }
    
    private SimpleOptsArgs getSimpleOptsArgs(final Node node) {
        final SimpleOptsArgs simpleOptsArgs = new SimpleOptsArgs();
        final NamedNodeMap attributes = node.getAttributes();
        final Attr attr = (Attr)attributes.getNamedItem("OPT_NAME");
        final Attr attr2 = (Attr)attributes.getNamedItem("OPT_ARG");
        if (attr != null) {
            simpleOptsArgs.setSimpleOptArgsName(attr.getValue());
        }
        if (attr2 != null) {
            simpleOptsArgs.setSimpleOptArgsArg(attr2.getValue());
        }
        return simpleOptsArgs;
    }
    
    private LongOpts getLongOpts(final Node node) {
        final LongOpts longOpts = new LongOpts();
        final Attr attr = (Attr)node.getAttributes().getNamedItem("OPT_VAL");
        if (attr != null) {
            longOpts.setLongOptVal(attr.getValue());
        }
        return longOpts;
    }
    
    private LongOptsArgs getLongOptsArgs(final Node node) {
        final LongOptsArgs longOptsArgs = new LongOptsArgs();
        final NamedNodeMap attributes = node.getAttributes();
        final Attr attr = (Attr)attributes.getNamedItem("OPT_NAME");
        final Attr attr2 = (Attr)attributes.getNamedItem("OPT_ARG");
        if (attr != null) {
            longOptsArgs.setLongOptArgsName(attr.getValue());
        }
        if (attr2 != null) {
            longOptsArgs.setLongOptArgsArg(attr2.getValue());
        }
        return longOptsArgs;
    }
    
    public CLIDataInstance[] getData(final String s) {
        return this.dataTable.get(s);
    }
    
    public Hashtable getDataSetEntries() {
        return this.dataTable;
    }
    
    public CLIDataInstance getDataByName(final String s, final String s2) {
        final CLIDataInstance[] data = this.getData(s);
        if (data != null) {
            for (int length = data.length, i = 0; i < length; ++i) {
                if (data[i].getDataName().equals(s2)) {
                    return data[i];
                }
            }
        }
        return null;
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
    
    public void writeToFile(final String s) throws FileNotFoundException, IOException {
        new DataSetWriter().writeXmlToFile(s, this.getDataSetEntries());
    }
}
