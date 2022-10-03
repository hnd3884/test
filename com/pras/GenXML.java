package com.pras;

import java.io.FileOutputStream;
import java.util.ArrayList;
import com.pras.abx.Attribute;
import com.pras.utils.Log;
import com.pras.abx.Node;
import com.pras.abx.BXCallback;

public class GenXML implements BXCallback
{
    StringBuffer xml;
    int cl;
    Node currentNode;
    String xmlFile;
    String tag;
    
    public GenXML() {
        this.xml = new StringBuffer();
        this.cl = 1;
        this.currentNode = null;
        this.tag = this.getClass().getSimpleName();
    }
    
    public void startDoc(final String xmlFile) {
        this.xmlFile = xmlFile;
        this.xml.append("<?xml version=\"1.0\" encoding=\"utf-8\"?>");
    }
    
    public void startNode(final Node node) {
        if (node == null) {
            return;
        }
        this.currentNode = node;
        Log.d(this.tag, "[GenXML] Node LN: " + node.getLinenumber() + " CL: " + this.cl);
        if (this.cl == node.getLinenumber()) {
            this.xml.append("\n");
        }
        else {
            while (this.cl < node.getLinenumber()) {
                this.ln();
            }
        }
        this.xml.append("<" + node.getName());
        if (node.getIndex() == Node.ROOT) {
            this.xml.append(" xmlns:" + node.getNamespacePrefix() + "=\"" + node.getNamespaceURI() + "\"");
        }
        final ArrayList<Attribute> attrs = node.getAttrs();
        Log.d(this.tag, "[GenXML] Number of Attributes " + attrs.size());
        if (attrs.size() == 0) {
            this.xml.append(">");
            this.ln();
            return;
        }
        for (int i = 0; i < attrs.size(); ++i) {
            final Attribute attr = attrs.get(i);
            this.ln();
            this.xml.append(" " + attr.getName() + "=\"" + attr.getValue() + "\"");
        }
        this.xml.append(">");
    }
    
    public void nodeValue(final int lineNumber, final String name, final String value) {
    }
    
    public void endNode(final Node node) {
        if (this.cl == node.getLinenumber()) {
            this.xml.append("\n");
        }
        else if (this.cl < node.getLinenumber()) {
            this.ln();
        }
        if (this.currentNode.getName().equals(node.getName())) {
            final int index = this.xml.lastIndexOf("\"");
            if (index != -1) {
                this.xml.delete(index + 1, this.xml.length());
                this.xml.append("/>");
            }
            else {
                this.xml.append("</" + node.getName() + ">");
            }
        }
        else {
            this.xml.append("</" + node.getName() + ">");
        }
    }
    
    public void endDoc() throws Exception {
        final FileOutputStream out = new FileOutputStream(String.valueOf(this.xmlFile) + ".xml");
        out.write(this.xml.toString().getBytes());
        out.flush();
        out.close();
    }
    
    private void ln() {
        ++this.cl;
        this.xml.append("\n");
    }
}
