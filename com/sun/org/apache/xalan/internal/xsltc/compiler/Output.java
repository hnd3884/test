package com.sun.org.apache.xalan.internal.xsltc.compiler;

import com.sun.org.apache.bcel.internal.generic.InstructionList;
import com.sun.org.apache.bcel.internal.generic.ConstantPoolGen;
import com.sun.org.apache.bcel.internal.generic.INVOKEVIRTUAL;
import com.sun.org.apache.bcel.internal.generic.PUTFIELD;
import com.sun.org.apache.bcel.internal.generic.CompoundInstruction;
import com.sun.org.apache.bcel.internal.generic.PUSH;
import com.sun.org.apache.bcel.internal.generic.Instruction;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.MethodGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.ClassGenerator;
import java.util.StringTokenizer;
import java.io.UnsupportedEncodingException;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.ErrorMsg;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import com.sun.org.apache.xml.internal.serializer.Encodings;
import com.sun.org.apache.xml.internal.utils.XML11Char;
import java.util.Properties;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.Util;

final class Output extends TopLevelElement
{
    private String _version;
    private String _method;
    private String _encoding;
    private boolean _omitHeader;
    private String _standalone;
    private String _doctypePublic;
    private String _doctypeSystem;
    private String _cdata;
    private boolean _indent;
    private String _mediaType;
    private String _indentamount;
    private boolean _disabled;
    private static final String STRING_SIG = "Ljava/lang/String;";
    private static final String XML_VERSION = "1.0";
    private static final String HTML_VERSION = "4.0";
    
    Output() {
        this._omitHeader = false;
        this._indent = false;
        this._disabled = false;
    }
    
    @Override
    public void display(final int indent) {
        this.indent(indent);
        Util.println("Output " + this._method);
    }
    
    public void disable() {
        this._disabled = true;
    }
    
    public boolean enabled() {
        return !this._disabled;
    }
    
    public String getCdata() {
        return this._cdata;
    }
    
    public String getOutputMethod() {
        return this._method;
    }
    
    private void transferAttribute(final Output previous, final String qname) {
        if (!this.hasAttribute(qname) && previous.hasAttribute(qname)) {
            this.addAttribute(qname, previous.getAttribute(qname));
        }
    }
    
    public void mergeOutput(final Output previous) {
        this.transferAttribute(previous, "version");
        this.transferAttribute(previous, "method");
        this.transferAttribute(previous, "encoding");
        this.transferAttribute(previous, "doctype-system");
        this.transferAttribute(previous, "doctype-public");
        this.transferAttribute(previous, "media-type");
        this.transferAttribute(previous, "indent");
        this.transferAttribute(previous, "omit-xml-declaration");
        this.transferAttribute(previous, "standalone");
        if (previous.hasAttribute("cdata-section-elements")) {
            this.addAttribute("cdata-section-elements", previous.getAttribute("cdata-section-elements") + ' ' + this.getAttribute("cdata-section-elements"));
        }
        String prefix = this.lookupPrefix("http://xml.apache.org/xalan");
        if (prefix != null) {
            this.transferAttribute(previous, prefix + ':' + "indent-amount");
        }
        prefix = this.lookupPrefix("http://xml.apache.org/xslt");
        if (prefix != null) {
            this.transferAttribute(previous, prefix + ':' + "indent-amount");
        }
    }
    
    @Override
    public void parseContents(final Parser parser) {
        final Properties outputProperties = new Properties();
        parser.setOutput(this);
        if (this._disabled) {
            return;
        }
        String attrib = null;
        this._version = this.getAttribute("version");
        if (this._version.equals("")) {
            this._version = null;
        }
        else {
            outputProperties.setProperty("version", this._version);
        }
        this._method = this.getAttribute("method");
        if (this._method.equals("")) {
            this._method = null;
        }
        if (this._method != null) {
            this._method = this._method.toLowerCase();
            if (this._method.equals("xml") || this._method.equals("html") || this._method.equals("text") || (XML11Char.isXML11ValidQName(this._method) && this._method.indexOf(":") > 0)) {
                outputProperties.setProperty("method", this._method);
            }
            else {
                this.reportError(this, parser, "INVALID_METHOD_IN_OUTPUT", this._method);
            }
        }
        this._encoding = this.getAttribute("encoding");
        if (this._encoding.equals("")) {
            this._encoding = null;
        }
        else {
            try {
                final String canonicalEncoding = Encodings.convertMime2JavaEncoding(this._encoding);
                final OutputStreamWriter outputStreamWriter = new OutputStreamWriter(System.out, canonicalEncoding);
            }
            catch (final UnsupportedEncodingException e) {
                final ErrorMsg msg = new ErrorMsg("UNSUPPORTED_ENCODING", this._encoding, this);
                parser.reportError(4, msg);
            }
            outputProperties.setProperty("encoding", this._encoding);
        }
        attrib = this.getAttribute("omit-xml-declaration");
        if (!attrib.equals("")) {
            if (attrib.equals("yes")) {
                this._omitHeader = true;
            }
            outputProperties.setProperty("omit-xml-declaration", attrib);
        }
        this._standalone = this.getAttribute("standalone");
        if (this._standalone.equals("")) {
            this._standalone = null;
        }
        else {
            outputProperties.setProperty("standalone", this._standalone);
        }
        this._doctypeSystem = this.getAttribute("doctype-system");
        if (this._doctypeSystem.equals("")) {
            this._doctypeSystem = null;
        }
        else {
            outputProperties.setProperty("doctype-system", this._doctypeSystem);
        }
        this._doctypePublic = this.getAttribute("doctype-public");
        if (this._doctypePublic.equals("")) {
            this._doctypePublic = null;
        }
        else {
            outputProperties.setProperty("doctype-public", this._doctypePublic);
        }
        this._cdata = this.getAttribute("cdata-section-elements");
        if (this._cdata.equals("")) {
            this._cdata = null;
        }
        else {
            final StringBuffer expandedNames = new StringBuffer();
            final StringTokenizer tokens = new StringTokenizer(this._cdata);
            while (tokens.hasMoreTokens()) {
                final String qname = tokens.nextToken();
                if (!XML11Char.isXML11ValidQName(qname)) {
                    final ErrorMsg err = new ErrorMsg("INVALID_QNAME_ERR", qname, this);
                    parser.reportError(3, err);
                }
                expandedNames.append(parser.getQName(qname).toString()).append(' ');
            }
            outputProperties.setProperty("cdata-section-elements", this._cdata = expandedNames.toString());
        }
        attrib = this.getAttribute("indent");
        if (!attrib.equals("")) {
            if (attrib.equals("yes")) {
                this._indent = true;
            }
            outputProperties.setProperty("indent", attrib);
        }
        else if (this._method != null && this._method.equals("html")) {
            this._indent = true;
        }
        this._indentamount = this.getAttribute(this.lookupPrefix("http://xml.apache.org/xalan"), "indent-amount");
        if (this._indentamount.equals("")) {
            this._indentamount = this.getAttribute(this.lookupPrefix("http://xml.apache.org/xslt"), "indent-amount");
        }
        if (!this._indentamount.equals("")) {
            outputProperties.setProperty("indent_amount", this._indentamount);
        }
        this._mediaType = this.getAttribute("media-type");
        if (this._mediaType.equals("")) {
            this._mediaType = null;
        }
        else {
            outputProperties.setProperty("media-type", this._mediaType);
        }
        if (this._method != null) {
            if (this._method.equals("html")) {
                if (this._version == null) {
                    this._version = "4.0";
                }
                if (this._mediaType == null) {
                    this._mediaType = "text/html";
                }
            }
            else if (this._method.equals("text") && this._mediaType == null) {
                this._mediaType = "text/plain";
            }
        }
        parser.getCurrentStylesheet().setOutputProperties(outputProperties);
    }
    
    @Override
    public void translate(final ClassGenerator classGen, final MethodGenerator methodGen) {
        if (this._disabled) {
            return;
        }
        final ConstantPoolGen cpg = classGen.getConstantPool();
        final InstructionList il = methodGen.getInstructionList();
        int field = 0;
        il.append(classGen.loadTranslet());
        if (this._version != null && !this._version.equals("1.0")) {
            field = cpg.addFieldref("com.sun.org.apache.xalan.internal.xsltc.runtime.AbstractTranslet", "_version", "Ljava/lang/String;");
            il.append(Output.DUP);
            il.append(new PUSH(cpg, this._version));
            il.append(new PUTFIELD(field));
        }
        if (this._method != null) {
            field = cpg.addFieldref("com.sun.org.apache.xalan.internal.xsltc.runtime.AbstractTranslet", "_method", "Ljava/lang/String;");
            il.append(Output.DUP);
            il.append(new PUSH(cpg, this._method));
            il.append(new PUTFIELD(field));
        }
        if (this._encoding != null) {
            field = cpg.addFieldref("com.sun.org.apache.xalan.internal.xsltc.runtime.AbstractTranslet", "_encoding", "Ljava/lang/String;");
            il.append(Output.DUP);
            il.append(new PUSH(cpg, this._encoding));
            il.append(new PUTFIELD(field));
        }
        if (this._omitHeader) {
            field = cpg.addFieldref("com.sun.org.apache.xalan.internal.xsltc.runtime.AbstractTranslet", "_omitHeader", "Z");
            il.append(Output.DUP);
            il.append(new PUSH(cpg, this._omitHeader));
            il.append(new PUTFIELD(field));
        }
        if (this._standalone != null) {
            field = cpg.addFieldref("com.sun.org.apache.xalan.internal.xsltc.runtime.AbstractTranslet", "_standalone", "Ljava/lang/String;");
            il.append(Output.DUP);
            il.append(new PUSH(cpg, this._standalone));
            il.append(new PUTFIELD(field));
        }
        field = cpg.addFieldref("com.sun.org.apache.xalan.internal.xsltc.runtime.AbstractTranslet", "_doctypeSystem", "Ljava/lang/String;");
        il.append(Output.DUP);
        il.append(new PUSH(cpg, this._doctypeSystem));
        il.append(new PUTFIELD(field));
        field = cpg.addFieldref("com.sun.org.apache.xalan.internal.xsltc.runtime.AbstractTranslet", "_doctypePublic", "Ljava/lang/String;");
        il.append(Output.DUP);
        il.append(new PUSH(cpg, this._doctypePublic));
        il.append(new PUTFIELD(field));
        if (this._mediaType != null) {
            field = cpg.addFieldref("com.sun.org.apache.xalan.internal.xsltc.runtime.AbstractTranslet", "_mediaType", "Ljava/lang/String;");
            il.append(Output.DUP);
            il.append(new PUSH(cpg, this._mediaType));
            il.append(new PUTFIELD(field));
        }
        if (this._indent) {
            field = cpg.addFieldref("com.sun.org.apache.xalan.internal.xsltc.runtime.AbstractTranslet", "_indent", "Z");
            il.append(Output.DUP);
            il.append(new PUSH(cpg, this._indent));
            il.append(new PUTFIELD(field));
        }
        if (this._indentamount != null && !this._indentamount.equals("")) {
            field = cpg.addFieldref("com.sun.org.apache.xalan.internal.xsltc.runtime.AbstractTranslet", "_indentamount", "I");
            il.append(Output.DUP);
            il.append(new PUSH(cpg, Integer.parseInt(this._indentamount)));
            il.append(new PUTFIELD(field));
        }
        if (this._cdata != null) {
            final int index = cpg.addMethodref("com.sun.org.apache.xalan.internal.xsltc.runtime.AbstractTranslet", "addCdataElement", "(Ljava/lang/String;)V");
            final StringTokenizer tokens = new StringTokenizer(this._cdata);
            while (tokens.hasMoreTokens()) {
                il.append(Output.DUP);
                il.append(new PUSH(cpg, tokens.nextToken()));
                il.append(new INVOKEVIRTUAL(index));
            }
        }
        il.append(Output.POP);
    }
}
