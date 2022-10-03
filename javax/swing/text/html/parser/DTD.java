package javax.swing.text.html.parser;

import java.io.DataInputStream;
import sun.awt.AppContext;
import java.io.IOException;
import java.util.StringTokenizer;
import java.util.BitSet;
import java.util.Hashtable;
import java.util.Vector;

public class DTD implements DTDConstants
{
    public String name;
    public Vector<Element> elements;
    public Hashtable<String, Element> elementHash;
    public Hashtable<Object, Entity> entityHash;
    public final Element pcdata;
    public final Element html;
    public final Element meta;
    public final Element base;
    public final Element isindex;
    public final Element head;
    public final Element body;
    public final Element applet;
    public final Element param;
    public final Element p;
    public final Element title;
    final Element style;
    final Element link;
    final Element script;
    public static final int FILE_VERSION = 1;
    private static final Object DTD_HASH_KEY;
    
    protected DTD(final String name) {
        this.elements = new Vector<Element>();
        this.elementHash = new Hashtable<String, Element>();
        this.entityHash = new Hashtable<Object, Entity>();
        this.pcdata = this.getElement("#pcdata");
        this.html = this.getElement("html");
        this.meta = this.getElement("meta");
        this.base = this.getElement("base");
        this.isindex = this.getElement("isindex");
        this.head = this.getElement("head");
        this.body = this.getElement("body");
        this.applet = this.getElement("applet");
        this.param = this.getElement("param");
        this.p = this.getElement("p");
        this.title = this.getElement("title");
        this.style = this.getElement("style");
        this.link = this.getElement("link");
        this.script = this.getElement("script");
        this.name = name;
        this.defEntity("#RE", 65536, 13);
        this.defEntity("#RS", 65536, 10);
        this.defEntity("#SPACE", 65536, 32);
        this.defineElement("unknown", 17, false, true, null, null, null, null);
    }
    
    public String getName() {
        return this.name;
    }
    
    public Entity getEntity(final String s) {
        return this.entityHash.get(s);
    }
    
    public Entity getEntity(final int n) {
        return this.entityHash.get(n);
    }
    
    boolean elementExists(final String s) {
        return !"unknown".equals(s) && this.elementHash.get(s) != null;
    }
    
    public Element getElement(final String s) {
        Element element = this.elementHash.get(s);
        if (element == null) {
            element = new Element(s, this.elements.size());
            this.elements.addElement(element);
            this.elementHash.put(s, element);
        }
        return element;
    }
    
    public Element getElement(final int n) {
        return this.elements.elementAt(n);
    }
    
    public Entity defineEntity(final String s, final int n, final char[] array) {
        Entity entity = this.entityHash.get(s);
        if (entity == null) {
            entity = new Entity(s, n, array);
            this.entityHash.put(s, entity);
            if ((n & 0x10000) != 0x0 && array.length == 1) {
                switch (n & 0xFFFEFFFF) {
                    case 1:
                    case 11: {
                        this.entityHash.put((int)array[0], entity);
                        break;
                    }
                }
            }
        }
        return entity;
    }
    
    public Element defineElement(final String s, final int type, final boolean oStart, final boolean oEnd, final ContentModel content, final BitSet exclusions, final BitSet inclusions, final AttributeList atts) {
        final Element element = this.getElement(s);
        element.type = type;
        element.oStart = oStart;
        element.oEnd = oEnd;
        element.content = content;
        element.exclusions = exclusions;
        element.inclusions = inclusions;
        element.atts = atts;
        return element;
    }
    
    public void defineAttributes(final String s, final AttributeList atts) {
        this.getElement(s).atts = atts;
    }
    
    public Entity defEntity(final String s, final int n, final int n2) {
        return this.defineEntity(s, n, new char[] { (char)n2 });
    }
    
    protected Entity defEntity(final String s, final int n, final String s2) {
        final int length = s2.length();
        final char[] array = new char[length];
        s2.getChars(0, length, array, 0);
        return this.defineEntity(s, n, array);
    }
    
    protected Element defElement(final String s, final int n, final boolean b, final boolean b2, final ContentModel contentModel, final String[] array, final String[] array2, final AttributeList list) {
        BitSet set = null;
        if (array != null && array.length > 0) {
            set = new BitSet();
            for (final String s2 : array) {
                if (s2.length() > 0) {
                    set.set(this.getElement(s2).getIndex());
                }
            }
        }
        BitSet set2 = null;
        if (array2 != null && array2.length > 0) {
            set2 = new BitSet();
            for (final String s3 : array2) {
                if (s3.length() > 0) {
                    set2.set(this.getElement(s3).getIndex());
                }
            }
        }
        return this.defineElement(s, n, b, b2, contentModel, set, set2, list);
    }
    
    protected AttributeList defAttributeList(final String s, final int n, final int n2, final String s2, final String s3, final AttributeList list) {
        Vector<String> vector = null;
        if (s3 != null) {
            vector = new Vector<String>();
            final StringTokenizer stringTokenizer = new StringTokenizer(s3, "|");
            while (stringTokenizer.hasMoreTokens()) {
                final String nextToken = stringTokenizer.nextToken();
                if (nextToken.length() > 0) {
                    vector.addElement(nextToken);
                }
            }
        }
        return new AttributeList(s, n, n2, s2, vector, list);
    }
    
    protected ContentModel defContentModel(final int n, final Object o, final ContentModel contentModel) {
        return new ContentModel(n, o, contentModel);
    }
    
    @Override
    public String toString() {
        return this.name;
    }
    
    public static void putDTDHash(final String s, final DTD dtd) {
        getDtdHash().put(s, dtd);
    }
    
    public static DTD getDTD(String lowerCase) throws IOException {
        lowerCase = lowerCase.toLowerCase();
        DTD dtd = getDtdHash().get(lowerCase);
        if (dtd == null) {
            dtd = new DTD(lowerCase);
        }
        return dtd;
    }
    
    private static Hashtable<String, DTD> getDtdHash() {
        final AppContext appContext = AppContext.getAppContext();
        Hashtable hashtable = (Hashtable)appContext.get(DTD.DTD_HASH_KEY);
        if (hashtable == null) {
            hashtable = new Hashtable();
            appContext.put(DTD.DTD_HASH_KEY, hashtable);
        }
        return hashtable;
    }
    
    public void read(final DataInputStream dataInputStream) throws IOException {
        if (dataInputStream.readInt() != 1) {}
        final String[] array = new String[dataInputStream.readShort()];
        for (int i = 0; i < array.length; ++i) {
            array[i] = dataInputStream.readUTF();
        }
        for (short short1 = dataInputStream.readShort(), n = 0; n < short1; ++n) {
            this.defEntity(array[dataInputStream.readShort()], dataInputStream.readByte() | 0x10000, dataInputStream.readUTF());
        }
        for (short short2 = dataInputStream.readShort(), n2 = 0; n2 < short2; ++n2) {
            final short short3 = dataInputStream.readShort();
            final byte byte1 = dataInputStream.readByte();
            final byte byte2 = dataInputStream.readByte();
            this.defElement(array[short3], byte1, (byte2 & 0x1) != 0x0, (byte2 & 0x2) != 0x0, this.readContentModel(dataInputStream, array), this.readNameArray(dataInputStream, array), this.readNameArray(dataInputStream, array), this.readAttributeList(dataInputStream, array));
        }
    }
    
    private ContentModel readContentModel(final DataInputStream dataInputStream, final String[] array) throws IOException {
        switch (dataInputStream.readByte()) {
            case 0: {
                return null;
            }
            case 1: {
                return this.defContentModel(dataInputStream.readByte(), this.readContentModel(dataInputStream, array), this.readContentModel(dataInputStream, array));
            }
            case 2: {
                return this.defContentModel(dataInputStream.readByte(), this.getElement(array[dataInputStream.readShort()]), this.readContentModel(dataInputStream, array));
            }
            default: {
                throw new IOException("bad bdtd");
            }
        }
    }
    
    private String[] readNameArray(final DataInputStream dataInputStream, final String[] array) throws IOException {
        final short short1 = dataInputStream.readShort();
        if (short1 == 0) {
            return null;
        }
        final String[] array2 = new String[short1];
        for (short n = 0; n < short1; ++n) {
            array2[n] = array[dataInputStream.readShort()];
        }
        return array2;
    }
    
    private AttributeList readAttributeList(final DataInputStream dataInputStream, final String[] array) throws IOException {
        AttributeList list = null;
        for (int i = dataInputStream.readByte(); i > 0; --i) {
            final short short1 = dataInputStream.readShort();
            final byte byte1 = dataInputStream.readByte();
            final byte byte2 = dataInputStream.readByte();
            final short short2 = dataInputStream.readShort();
            final String s = (short2 == -1) ? null : array[short2];
            Vector<String> vector = null;
            final short short3 = dataInputStream.readShort();
            if (short3 > 0) {
                vector = new Vector<String>(short3);
                for (short n = 0; n < short3; ++n) {
                    vector.addElement(array[dataInputStream.readShort()]);
                }
            }
            list = new AttributeList(array[short1], byte1, byte2, s, vector, list);
        }
        return list;
    }
    
    static {
        DTD_HASH_KEY = new Object();
    }
}
