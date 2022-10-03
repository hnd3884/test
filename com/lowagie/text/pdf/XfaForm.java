package com.lowagie.text.pdf;

import java.util.Iterator;
import java.util.EmptyStackException;
import org.w3c.dom.NodeList;
import java.io.FileInputStream;
import java.io.File;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.Collection;
import java.io.OutputStream;
import com.lowagie.text.xml.XmlDomWriter;
import org.xml.sax.SAXException;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import javax.xml.parsers.DocumentBuilder;
import java.io.InputStream;
import java.io.ByteArrayInputStream;
import java.io.Reader;
import java.io.StringReader;
import org.xml.sax.InputSource;
import org.xml.sax.EntityResolver;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.ByteArrayOutputStream;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

public class XfaForm
{
    private Xml2SomTemplate templateSom;
    private Node templateNode;
    private Xml2SomDatasets datasetsSom;
    private Node datasetsNode;
    private AcroFieldsSearch acroFieldsSom;
    private PdfReader reader;
    private boolean xfaPresent;
    private Document domDocument;
    private boolean changed;
    public static final String XFA_DATA_SCHEMA = "http://www.xfa.org/schema/xfa-data/1.0/";
    
    public XfaForm() {
    }
    
    public static PdfObject getXfaObject(final PdfReader reader) {
        final PdfDictionary af = (PdfDictionary)PdfReader.getPdfObjectRelease(reader.getCatalog().get(PdfName.ACROFORM));
        if (af == null) {
            return null;
        }
        return PdfReader.getPdfObjectRelease(af.get(PdfName.XFA));
    }
    
    public XfaForm(final PdfReader reader) throws IOException, ParserConfigurationException, SAXException {
        this.reader = reader;
        final PdfObject xfa = getXfaObject(reader);
        if (xfa == null) {
            this.xfaPresent = false;
            return;
        }
        this.xfaPresent = true;
        final ByteArrayOutputStream bout = new ByteArrayOutputStream();
        if (xfa.isArray()) {
            final PdfArray ar = (PdfArray)xfa;
            for (int k = 1; k < ar.size(); k += 2) {
                final PdfObject ob = ar.getDirectObject(k);
                if (ob instanceof PRStream) {
                    final byte[] b = PdfReader.getStreamBytes((PRStream)ob);
                    bout.write(b);
                }
            }
        }
        else if (xfa instanceof PRStream) {
            final byte[] b2 = PdfReader.getStreamBytes((PRStream)xfa);
            bout.write(b2);
        }
        bout.close();
        final DocumentBuilderFactory fact = DocumentBuilderFactory.newInstance();
        fact.setNamespaceAware(true);
        final DocumentBuilder db = fact.newDocumentBuilder();
        db.setEntityResolver(new EntityResolver() {
            @Override
            public InputSource resolveEntity(final String publicId, final String systemId) {
                return new InputSource(new StringReader(""));
            }
        });
        this.domDocument = db.parse(new ByteArrayInputStream(bout.toByteArray()));
        this.extractNodes();
    }
    
    private void extractNodes() {
        Node n;
        for (n = this.domDocument.getFirstChild(); n.getChildNodes().getLength() == 0; n = n.getNextSibling()) {}
        for (n = n.getFirstChild(); n != null; n = n.getNextSibling()) {
            if (n.getNodeType() == 1) {
                final String s = n.getLocalName();
                if (s.equals("template")) {
                    this.templateNode = n;
                    this.templateSom = new Xml2SomTemplate(n);
                }
                else if (s.equals("datasets")) {
                    this.datasetsNode = n;
                    this.datasetsSom = new Xml2SomDatasets(n.getFirstChild());
                }
            }
        }
    }
    
    public static void setXfa(final XfaForm form, final PdfReader reader, final PdfWriter writer) throws IOException {
        final PdfDictionary af = (PdfDictionary)PdfReader.getPdfObjectRelease(reader.getCatalog().get(PdfName.ACROFORM));
        if (af == null) {
            return;
        }
        final PdfObject xfa = getXfaObject(reader);
        if (xfa.isArray()) {
            final PdfArray ar = (PdfArray)xfa;
            int t = -1;
            int d = -1;
            for (int k = 0; k < ar.size(); k += 2) {
                final PdfString s = ar.getAsString(k);
                if ("template".equals(s.toString())) {
                    t = k + 1;
                }
                if ("datasets".equals(s.toString())) {
                    d = k + 1;
                }
            }
            if (t > -1 && d > -1) {
                reader.killXref(ar.getAsIndirectObject(t));
                reader.killXref(ar.getAsIndirectObject(d));
                final PdfStream tStream = new PdfStream(serializeDoc(form.templateNode));
                tStream.flateCompress(writer.getCompressionLevel());
                ar.set(t, writer.addToBody(tStream).getIndirectReference());
                final PdfStream dStream = new PdfStream(serializeDoc(form.datasetsNode));
                dStream.flateCompress(writer.getCompressionLevel());
                ar.set(d, writer.addToBody(dStream).getIndirectReference());
                af.put(PdfName.XFA, new PdfArray(ar));
                return;
            }
        }
        reader.killXref(af.get(PdfName.XFA));
        final PdfStream str = new PdfStream(serializeDoc(form.domDocument));
        str.flateCompress(writer.getCompressionLevel());
        final PdfIndirectReference ref = writer.addToBody(str).getIndirectReference();
        af.put(PdfName.XFA, ref);
    }
    
    public void setXfa(final PdfWriter writer) throws IOException {
        setXfa(this, this.reader, writer);
    }
    
    public static byte[] serializeDoc(final Node n) throws IOException {
        final XmlDomWriter xw = new XmlDomWriter();
        final ByteArrayOutputStream fout = new ByteArrayOutputStream();
        xw.setOutput(fout, null);
        xw.setCanonical(false);
        xw.write(n);
        fout.close();
        return fout.toByteArray();
    }
    
    public boolean isXfaPresent() {
        return this.xfaPresent;
    }
    
    public Document getDomDocument() {
        return this.domDocument;
    }
    
    public String findFieldName(final String name, final AcroFields af) {
        final HashMap items = af.getFields();
        if (items.containsKey(name)) {
            return name;
        }
        if (this.acroFieldsSom == null) {
            if (items.isEmpty() && this.xfaPresent) {
                this.acroFieldsSom = new AcroFieldsSearch(this.datasetsSom.getName2Node().keySet());
            }
            else {
                this.acroFieldsSom = new AcroFieldsSearch(items.keySet());
            }
        }
        if (this.acroFieldsSom.getAcroShort2LongName().containsKey(name)) {
            return this.acroFieldsSom.getAcroShort2LongName().get(name);
        }
        return this.acroFieldsSom.inverseSearchGlobal(Xml2Som.splitParts(name));
    }
    
    public String findDatasetsName(final String name) {
        if (this.datasetsSom.getName2Node().containsKey(name)) {
            return name;
        }
        return this.datasetsSom.inverseSearchGlobal(Xml2Som.splitParts(name));
    }
    
    public Node findDatasetsNode(String name) {
        if (name == null) {
            return null;
        }
        name = this.findDatasetsName(name);
        if (name == null) {
            return null;
        }
        return this.datasetsSom.getName2Node().get(name);
    }
    
    public static String getNodeText(final Node n) {
        if (n == null) {
            return "";
        }
        return getNodeText(n, "");
    }
    
    private static String getNodeText(final Node n, String name) {
        for (Node n2 = n.getFirstChild(); n2 != null; n2 = n2.getNextSibling()) {
            if (n2.getNodeType() == 1) {
                name = getNodeText(n2, name);
            }
            else if (n2.getNodeType() == 3) {
                name += n2.getNodeValue();
            }
        }
        return name;
    }
    
    public void setNodeText(final Node n, final String text) {
        if (n == null) {
            return;
        }
        Node nc = null;
        while ((nc = n.getFirstChild()) != null) {
            n.removeChild(nc);
        }
        if (n.getAttributes().getNamedItemNS("http://www.xfa.org/schema/xfa-data/1.0/", "dataNode") != null) {
            n.getAttributes().removeNamedItemNS("http://www.xfa.org/schema/xfa-data/1.0/", "dataNode");
        }
        n.appendChild(this.domDocument.createTextNode(text));
        this.changed = true;
    }
    
    public void setXfaPresent(final boolean xfaPresent) {
        this.xfaPresent = xfaPresent;
    }
    
    public void setDomDocument(final Document domDocument) {
        this.domDocument = domDocument;
        this.extractNodes();
    }
    
    public PdfReader getReader() {
        return this.reader;
    }
    
    public void setReader(final PdfReader reader) {
        this.reader = reader;
    }
    
    public boolean isChanged() {
        return this.changed;
    }
    
    public void setChanged(final boolean changed) {
        this.changed = changed;
    }
    
    public Xml2SomTemplate getTemplateSom() {
        return this.templateSom;
    }
    
    public void setTemplateSom(final Xml2SomTemplate templateSom) {
        this.templateSom = templateSom;
    }
    
    public Xml2SomDatasets getDatasetsSom() {
        return this.datasetsSom;
    }
    
    public void setDatasetsSom(final Xml2SomDatasets datasetsSom) {
        this.datasetsSom = datasetsSom;
    }
    
    public AcroFieldsSearch getAcroFieldsSom() {
        return this.acroFieldsSom;
    }
    
    public void setAcroFieldsSom(final AcroFieldsSearch acroFieldsSom) {
        this.acroFieldsSom = acroFieldsSom;
    }
    
    public Node getDatasetsNode() {
        return this.datasetsNode;
    }
    
    public void fillXfaForm(final File file) throws ParserConfigurationException, SAXException, IOException {
        this.fillXfaForm(new FileInputStream(file));
    }
    
    public void fillXfaForm(final InputStream is) throws ParserConfigurationException, SAXException, IOException {
        this.fillXfaForm(new InputSource(is));
    }
    
    public void fillXfaForm(final InputSource is) throws ParserConfigurationException, SAXException, IOException {
        final DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        final DocumentBuilder db = dbf.newDocumentBuilder();
        db.setEntityResolver(new EntityResolver() {
            @Override
            public InputSource resolveEntity(final String publicId, final String systemId) {
                return new InputSource(new StringReader(""));
            }
        });
        final Document newdoc = db.parse(is);
        this.fillXfaForm(newdoc.getDocumentElement());
    }
    
    public void fillXfaForm(final Node node) {
        final Node data = this.datasetsNode.getFirstChild();
        final NodeList list = data.getChildNodes();
        if (list.getLength() == 0) {
            data.appendChild(this.domDocument.importNode(node, true));
        }
        else {
            data.replaceChild(this.domDocument.importNode(node, true), data.getFirstChild());
        }
        this.extractNodes();
        this.setChanged(true);
    }
    
    public static class InverseStore
    {
        protected ArrayList part;
        protected ArrayList follow;
        
        public InverseStore() {
            this.part = new ArrayList();
            this.follow = new ArrayList();
        }
        
        public String getDefaultName() {
            InverseStore store = this;
            Object obj;
            while (true) {
                obj = store.follow.get(0);
                if (obj instanceof String) {
                    break;
                }
                store = (InverseStore)obj;
            }
            return (String)obj;
        }
        
        public boolean isSimilar(String name) {
            final int idx = name.indexOf(91);
            name = name.substring(0, idx + 1);
            for (int k = 0; k < this.part.size(); ++k) {
                if (this.part.get(k).startsWith(name)) {
                    return true;
                }
            }
            return false;
        }
    }
    
    public static class Stack2 extends ArrayList
    {
        private static final long serialVersionUID = -7451476576174095212L;
        
        public Object peek() {
            if (this.size() == 0) {
                throw new EmptyStackException();
            }
            return this.get(this.size() - 1);
        }
        
        public Object pop() {
            if (this.size() == 0) {
                throw new EmptyStackException();
            }
            final Object ret = this.get(this.size() - 1);
            this.remove(this.size() - 1);
            return ret;
        }
        
        public Object push(final Object item) {
            this.add(item);
            return item;
        }
        
        public boolean empty() {
            return this.size() == 0;
        }
    }
    
    public static class Xml2Som
    {
        protected ArrayList order;
        protected HashMap name2Node;
        protected HashMap inverseSearch;
        protected Stack2 stack;
        protected int anform;
        
        public static String escapeSom(final String s) {
            if (s == null) {
                return "";
            }
            int idx = s.indexOf(46);
            if (idx < 0) {
                return s;
            }
            final StringBuffer sb = new StringBuffer();
            int last = 0;
            while (idx >= 0) {
                sb.append(s, last, idx);
                sb.append('\\');
                last = idx;
                idx = s.indexOf(46, idx + 1);
            }
            sb.append(s.substring(last));
            return sb.toString();
        }
        
        public static String unescapeSom(final String s) {
            int idx = s.indexOf(92);
            if (idx < 0) {
                return s;
            }
            final StringBuffer sb = new StringBuffer();
            int last = 0;
            while (idx >= 0) {
                sb.append(s, last, idx);
                last = idx + 1;
                idx = s.indexOf(92, idx + 1);
            }
            sb.append(s.substring(last));
            return sb.toString();
        }
        
        protected String printStack() {
            if (this.stack.empty()) {
                return "";
            }
            final StringBuffer s = new StringBuffer();
            for (int k = 0; k < this.stack.size(); ++k) {
                s.append('.').append(this.stack.get(k));
            }
            return s.substring(1);
        }
        
        public static String getShortName(final String s) {
            int idx = s.indexOf(".#subform[");
            if (idx < 0) {
                return s;
            }
            int last = 0;
            final StringBuffer sb = new StringBuffer();
            while (idx >= 0) {
                sb.append(s, last, idx);
                idx = s.indexOf("]", idx + 10);
                if (idx < 0) {
                    return sb.toString();
                }
                last = idx + 1;
                idx = s.indexOf(".#subform[", last);
            }
            sb.append(s.substring(last));
            return sb.toString();
        }
        
        public void inverseSearchAdd(final String unstack) {
            inverseSearchAdd(this.inverseSearch, this.stack, unstack);
        }
        
        public static void inverseSearchAdd(final HashMap inverseSearch, final Stack2 stack, final String unstack) {
            String last = (String)stack.peek();
            InverseStore store = inverseSearch.get(last);
            if (store == null) {
                store = new InverseStore();
                inverseSearch.put(last, store);
            }
            for (int k = stack.size() - 2; k >= 0; --k) {
                last = stack.get(k);
                final int idx = store.part.indexOf(last);
                InverseStore store2;
                if (idx < 0) {
                    store.part.add(last);
                    store2 = new InverseStore();
                    store.follow.add(store2);
                }
                else {
                    store2 = store.follow.get(idx);
                }
                store = store2;
            }
            store.part.add("");
            store.follow.add(unstack);
        }
        
        public String inverseSearchGlobal(final ArrayList parts) {
            if (parts.isEmpty()) {
                return null;
            }
            InverseStore store = this.inverseSearch.get(parts.get(parts.size() - 1));
            if (store == null) {
                return null;
            }
            int k = parts.size() - 2;
            while (k >= 0) {
                final String part = parts.get(k);
                final int idx = store.part.indexOf(part);
                if (idx < 0) {
                    if (store.isSimilar(part)) {
                        return null;
                    }
                    return store.getDefaultName();
                }
                else {
                    store = store.follow.get(idx);
                    --k;
                }
            }
            return store.getDefaultName();
        }
        
        public static Stack2 splitParts(String name) {
            while (name.startsWith(".")) {
                name = name.substring(1);
            }
            final Stack2 parts = new Stack2();
            int last = 0;
            int pos = 0;
            while (true) {
                pos = last;
                while (true) {
                    pos = name.indexOf(46, pos);
                    if (pos < 0) {
                        break;
                    }
                    if (name.charAt(pos - 1) != '\\') {
                        break;
                    }
                    ++pos;
                }
                if (pos < 0) {
                    break;
                }
                String part = name.substring(last, pos);
                if (!part.endsWith("]")) {
                    part += "[0]";
                }
                parts.add(part);
                last = pos + 1;
            }
            String part = name.substring(last);
            if (!part.endsWith("]")) {
                part += "[0]";
            }
            parts.add(part);
            return parts;
        }
        
        public ArrayList getOrder() {
            return this.order;
        }
        
        public void setOrder(final ArrayList order) {
            this.order = order;
        }
        
        public HashMap getName2Node() {
            return this.name2Node;
        }
        
        public void setName2Node(final HashMap name2Node) {
            this.name2Node = name2Node;
        }
        
        public HashMap getInverseSearch() {
            return this.inverseSearch;
        }
        
        public void setInverseSearch(final HashMap inverseSearch) {
            this.inverseSearch = inverseSearch;
        }
    }
    
    public static class Xml2SomDatasets extends Xml2Som
    {
        public Xml2SomDatasets(final Node n) {
            this.order = new ArrayList();
            this.name2Node = new HashMap();
            this.stack = new Stack2();
            this.anform = 0;
            this.inverseSearch = new HashMap();
            this.processDatasetsInternal(n);
        }
        
        public Node insertNode(Node n, final String shortName) {
            final Stack2 stack = Xml2Som.splitParts(shortName);
            final Document doc = n.getOwnerDocument();
            Node n2 = null;
            n = n.getFirstChild();
            for (int k = 0; k < stack.size(); ++k) {
                final String part = stack.get(k);
                int idx = part.lastIndexOf(91);
                final String name = part.substring(0, idx);
                idx = Integer.parseInt(part.substring(idx + 1, part.length() - 1));
                int found = -1;
                for (n2 = n.getFirstChild(); n2 != null; n2 = n2.getNextSibling()) {
                    if (n2.getNodeType() == 1) {
                        final String s = Xml2Som.escapeSom(n2.getLocalName());
                        if (s.equals(name) && ++found == idx) {
                            break;
                        }
                    }
                }
                while (found < idx) {
                    n2 = doc.createElementNS(null, name);
                    n2 = n.appendChild(n2);
                    final Node attr = doc.createAttributeNS("http://www.xfa.org/schema/xfa-data/1.0/", "dataNode");
                    attr.setNodeValue("dataGroup");
                    n2.getAttributes().setNamedItemNS(attr);
                    ++found;
                }
                n = n2;
            }
            Xml2Som.inverseSearchAdd(this.inverseSearch, stack, shortName);
            this.name2Node.put(shortName, n2);
            this.order.add(shortName);
            return n2;
        }
        
        private static boolean hasChildren(final Node n) {
            final Node dataNodeN = n.getAttributes().getNamedItemNS("http://www.xfa.org/schema/xfa-data/1.0/", "dataNode");
            if (dataNodeN != null) {
                final String dataNode = dataNodeN.getNodeValue();
                if ("dataGroup".equals(dataNode)) {
                    return true;
                }
                if ("dataValue".equals(dataNode)) {
                    return false;
                }
            }
            if (!n.hasChildNodes()) {
                return false;
            }
            for (Node n2 = n.getFirstChild(); n2 != null; n2 = n2.getNextSibling()) {
                if (n2.getNodeType() == 1) {
                    return true;
                }
            }
            return false;
        }
        
        private void processDatasetsInternal(final Node n) {
            final HashMap ss = new HashMap();
            for (Node n2 = n.getFirstChild(); n2 != null; n2 = n2.getNextSibling()) {
                if (n2.getNodeType() == 1) {
                    final String s = Xml2Som.escapeSom(n2.getLocalName());
                    Integer i = ss.get(s);
                    if (i == null) {
                        i = new Integer(0);
                    }
                    else {
                        i = new Integer(i + 1);
                    }
                    ss.put(s, i);
                    if (hasChildren(n2)) {
                        this.stack.push(s + "[" + i.toString() + "]");
                        this.processDatasetsInternal(n2);
                        this.stack.pop();
                    }
                    else {
                        this.stack.push(s + "[" + i.toString() + "]");
                        final String unstack = this.printStack();
                        this.order.add(unstack);
                        this.inverseSearchAdd(unstack);
                        this.name2Node.put(unstack, n2);
                        this.stack.pop();
                    }
                }
            }
        }
    }
    
    public static class AcroFieldsSearch extends Xml2Som
    {
        private HashMap acroShort2LongName;
        
        public AcroFieldsSearch(final Collection items) {
            this.inverseSearch = new HashMap();
            this.acroShort2LongName = new HashMap();
            for (final String itemName : items) {
                final String itemShort = Xml2Som.getShortName(itemName);
                this.acroShort2LongName.put(itemShort, itemName);
                Xml2Som.inverseSearchAdd(this.inverseSearch, Xml2Som.splitParts(itemShort), itemName);
            }
        }
        
        public HashMap getAcroShort2LongName() {
            return this.acroShort2LongName;
        }
        
        public void setAcroShort2LongName(final HashMap acroShort2LongName) {
            this.acroShort2LongName = acroShort2LongName;
        }
    }
    
    public static class Xml2SomTemplate extends Xml2Som
    {
        private boolean dynamicForm;
        private int templateLevel;
        
        public Xml2SomTemplate(final Node n) {
            this.order = new ArrayList();
            this.name2Node = new HashMap();
            this.stack = new Stack2();
            this.anform = 0;
            this.templateLevel = 0;
            this.inverseSearch = new HashMap();
            this.processTemplate(n, null);
        }
        
        public String getFieldType(final String s) {
            final Node n = this.name2Node.get(s);
            if (n == null) {
                return null;
            }
            if (n.getLocalName().equals("exclGroup")) {
                return "exclGroup";
            }
            Node ui;
            for (ui = n.getFirstChild(); ui != null && (ui.getNodeType() != 1 || !ui.getLocalName().equals("ui")); ui = ui.getNextSibling()) {}
            if (ui == null) {
                return null;
            }
            for (Node type = ui.getFirstChild(); type != null; type = type.getNextSibling()) {
                if (type.getNodeType() == 1 && (!type.getLocalName().equals("extras") || !type.getLocalName().equals("picture"))) {
                    return type.getLocalName();
                }
            }
            return null;
        }
        
        private void processTemplate(final Node n, HashMap ff) {
            if (ff == null) {
                ff = new HashMap();
            }
            final HashMap ss = new HashMap();
            for (Node n2 = n.getFirstChild(); n2 != null; n2 = n2.getNextSibling()) {
                if (n2.getNodeType() == 1) {
                    final String s = n2.getLocalName();
                    if (s.equals("subform")) {
                        final Node name = n2.getAttributes().getNamedItem("name");
                        String nn = "#subform";
                        boolean annon = true;
                        if (name != null) {
                            nn = Xml2Som.escapeSom(name.getNodeValue());
                            annon = false;
                        }
                        Integer i;
                        if (annon) {
                            i = new Integer(this.anform);
                            ++this.anform;
                        }
                        else {
                            i = ss.get(nn);
                            if (i == null) {
                                i = new Integer(0);
                            }
                            else {
                                i = new Integer(i + 1);
                            }
                            ss.put(nn, i);
                        }
                        this.stack.push(nn + "[" + i.toString() + "]");
                        ++this.templateLevel;
                        if (annon) {
                            this.processTemplate(n2, ff);
                        }
                        else {
                            this.processTemplate(n2, null);
                        }
                        --this.templateLevel;
                        this.stack.pop();
                    }
                    else if (s.equals("field") || s.equals("exclGroup")) {
                        final Node name = n2.getAttributes().getNamedItem("name");
                        if (name != null) {
                            final String nn = Xml2Som.escapeSom(name.getNodeValue());
                            Integer j = (Integer)ff.get(nn);
                            if (j == null) {
                                j = new Integer(0);
                            }
                            else {
                                j = new Integer(j + 1);
                            }
                            ff.put(nn, j);
                            this.stack.push(nn + "[" + j.toString() + "]");
                            final String unstack = this.printStack();
                            this.order.add(unstack);
                            this.inverseSearchAdd(unstack);
                            this.name2Node.put(unstack, n2);
                            this.stack.pop();
                        }
                    }
                    else if (!this.dynamicForm && this.templateLevel > 0 && s.equals("occur")) {
                        int initial = 1;
                        int min = 1;
                        int max = 1;
                        Node a = n2.getAttributes().getNamedItem("initial");
                        if (a != null) {
                            try {
                                initial = Integer.parseInt(a.getNodeValue().trim());
                            }
                            catch (final Exception ex) {}
                        }
                        a = n2.getAttributes().getNamedItem("min");
                        if (a != null) {
                            try {
                                min = Integer.parseInt(a.getNodeValue().trim());
                            }
                            catch (final Exception ex2) {}
                        }
                        a = n2.getAttributes().getNamedItem("max");
                        if (a != null) {
                            try {
                                max = Integer.parseInt(a.getNodeValue().trim());
                            }
                            catch (final Exception ex3) {}
                        }
                        if (initial != min || min != max) {
                            this.dynamicForm = true;
                        }
                    }
                }
            }
        }
        
        public boolean isDynamicForm() {
            return this.dynamicForm;
        }
        
        public void setDynamicForm(final boolean dynamicForm) {
            this.dynamicForm = dynamicForm;
        }
    }
}
