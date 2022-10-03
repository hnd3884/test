package com.pras.abx;

import java.io.ByteArrayInputStream;
import com.pras.utils.Log;
import com.pras.utils.Utils;
import java.io.InputStream;
import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.util.ArrayList;

public class Android_BX2 implements Resource
{
    byte[] chunk_type_buf;
    byte[] header_size_buf;
    byte[] chunk_size_buf;
    int header_size;
    int chunk_size;
    int package_count;
    byte[] buf_2;
    byte[] buf_4;
    String tag;
    ArrayList<String> stringPool;
    ArrayList<String> resStringPool;
    ArrayList<Integer> resMap;
    int ns_prefix_index;
    int ns_uri_index;
    int ns_linenumber;
    int nodeIndex;
    BXCallback listener;
    
    public Android_BX2(final BXCallback listner) {
        this.chunk_type_buf = new byte[2];
        this.header_size_buf = new byte[2];
        this.chunk_size_buf = new byte[4];
        this.buf_2 = new byte[2];
        this.buf_4 = new byte[4];
        this.tag = "Android_BX2";
        this.stringPool = new ArrayList<String>();
        this.resStringPool = new ArrayList<String>();
        this.resMap = new ArrayList<Integer>();
        this.ns_prefix_index = -1;
        this.ns_uri_index = -1;
        this.ns_linenumber = 0;
        this.nodeIndex = -1;
        this.listener = null;
        this.listener = listner;
    }
    
    public void parse(final String bxFile) throws Exception {
        final BufferedInputStream in = new BufferedInputStream(new FileInputStream(bxFile));
        in.read(this.chunk_type_buf);
        if (Utils.toInt(this.chunk_type_buf, false) != 3) {
            Log.p(this.tag, "It's an invalid BXML file. Exiting!");
            return;
        }
        if (this.listener != null) {
            this.listener.startDoc(bxFile);
        }
        in.read(this.header_size_buf);
        int header_size = Utils.toInt(this.header_size_buf, false);
        in.read(this.chunk_size_buf);
        int chunk_size = Utils.toInt(this.chunk_size_buf, false);
        Log.d(this.tag, "Header Size: " + header_size + " Chunk size: " + chunk_size);
        in.read(this.chunk_type_buf);
        if (Utils.toInt(this.chunk_type_buf, false) == 1) {
            Log.d(this.tag, "String Pool...");
            in.read(this.header_size_buf);
            header_size = Utils.toInt(this.header_size_buf, false);
            in.read(this.chunk_size_buf);
            chunk_size = Utils.toInt(this.chunk_size_buf, false);
            Log.d(this.tag, "String Pool...Header Size: " + header_size + " Chunk Size: " + chunk_size);
            final byte[] spBuf = new byte[chunk_size - 8];
            in.read(spBuf);
            this.parseStringPool(spBuf, header_size, chunk_size);
            in.read(this.chunk_type_buf);
        }
        if (Utils.toInt(this.chunk_type_buf, false) == 384) {
            in.read(this.header_size_buf);
            header_size = Utils.toInt(this.header_size_buf, false);
            in.read(this.chunk_size_buf);
            chunk_size = Utils.toInt(this.chunk_size_buf, false);
            final byte[] rmBuf = new byte[chunk_size - 8];
            in.read(rmBuf);
            this.parseResMapping(rmBuf, header_size, chunk_size);
            in.read(this.chunk_type_buf);
        }
        if (Utils.toInt(this.chunk_type_buf, false) == 256) {
            in.read(this.header_size_buf);
            header_size = Utils.toInt(this.header_size_buf, false);
            in.read(this.chunk_size_buf);
            chunk_size = Utils.toInt(this.chunk_size_buf, false);
            final byte[] nsStartBuf = new byte[chunk_size - 8];
            in.read(nsStartBuf);
            this.parseStartNameSpace(nsStartBuf, header_size, chunk_size);
        }
        in.read(this.chunk_type_buf);
        int chunk_type;
        for (chunk_type = Utils.toInt(this.chunk_type_buf, false); chunk_type != 257; chunk_type = Utils.toInt(this.chunk_type_buf, false)) {
            Log.d(this.tag, "Parsing XML node...Chunk_Type " + chunk_type);
            in.read(this.header_size_buf);
            header_size = Utils.toInt(this.header_size_buf, false);
            in.read(this.chunk_size_buf);
            chunk_size = Utils.toInt(this.chunk_size_buf, false);
            final byte[] elementBuf = new byte[chunk_size - 8];
            in.read(elementBuf);
            if (chunk_type == 258) {
                this.parseXMLStart(elementBuf, header_size, chunk_size);
            }
            else if (chunk_type == 259) {
                this.parseXMLEnd(elementBuf, header_size, chunk_size);
            }
            in.read(this.chunk_type_buf);
        }
        if (chunk_type == 257) {
            in.read(this.header_size_buf);
            header_size = Utils.toInt(this.header_size_buf, false);
            in.read(this.chunk_size_buf);
            chunk_size = Utils.toInt(this.chunk_size_buf, false);
            final byte[] nsEndBuf = new byte[chunk_size - 8];
            in.read(nsEndBuf);
            this.parseEndNameSpace(nsEndBuf, header_size, chunk_size);
        }
        if (this.listener != null) {
            this.listener.endDoc();
        }
    }
    
    private void parseStringPool(final byte[] spBuf, final int header_size, final int chunk_size) throws Exception {
        final ByteArrayInputStream in = new ByteArrayInputStream(spBuf);
        final byte[] int_buf = new byte[4];
        in.read(int_buf);
        final int string_count = Utils.toInt(int_buf, false);
        in.read(int_buf);
        final int style_count = Utils.toInt(int_buf, false);
        in.read(int_buf);
        final int flag = Utils.toInt(int_buf, false);
        in.read(int_buf);
        final int string_start = Utils.toInt(int_buf, false);
        in.read(int_buf);
        final int style_start = Utils.toInt(int_buf, false);
        Log.d(this.tag, "String Count: " + string_count + " Style Count: " + style_count + " Flag: " + flag + " String Start: " + string_start + " Style Start: " + style_start);
        final int[] string_indices = new int[string_count];
        if (string_count > 0) {
            for (int i = 0; i < string_count; ++i) {
                in.read(int_buf);
                string_indices[i] = Utils.toInt(int_buf, false);
            }
        }
        if (style_count > 0) {
            in.skip(style_count * 4);
        }
        for (int i = 0; i < string_count; ++i) {
            int string_len = 0;
            if (i == string_count - 1) {
                if (style_start == 0) {
                    string_len = chunk_size - string_indices[i] - header_size - 4 * string_count;
                    Log.d(this.tag, "Last String size: " + string_len + " Chunk_Size: " + chunk_size + " Index: " + string_indices[i]);
                }
                else {
                    string_len = style_start - string_indices[i];
                }
            }
            else {
                string_len = string_indices[i + 1] - string_indices[i];
            }
            final byte[] short_buf = new byte[2];
            in.read(short_buf);
            int actual_str_len = 0;
            if (short_buf[0] == short_buf[1]) {
                actual_str_len = short_buf[0];
            }
            else {
                actual_str_len = Utils.toInt(short_buf, false);
            }
            final byte[] str_buf = new byte[actual_str_len];
            final byte[] buf = new byte[string_len - 2];
            in.read(buf);
            int j = 0;
            for (int k = 0; k < buf.length; ++k) {
                if (buf[k] != 0) {
                    str_buf[j++] = buf[k];
                }
            }
            this.stringPool.add(new String(str_buf));
        }
        Log.d(this.tag, "[String Pool] Size: " + this.stringPool.size());
        Log.d(this.tag, "[String Pool] " + this.stringPool);
    }
    
    private void parseResMapping(final byte[] rmBuf, final int header_size, final int chunk_size) throws Exception {
        final ByteArrayInputStream in = new ByteArrayInputStream(rmBuf);
        final int num_of_res_ids = rmBuf.length / 4;
        final byte[] int_buf = new byte[4];
        for (int i = 0; i < num_of_res_ids; ++i) {
            in.read(int_buf);
            this.resMap.add(Utils.toInt(int_buf, false));
        }
        Log.d(this.tag, "[Res Mapping] Resource Mapping " + this.resMap);
    }
    
    private void parseStartNameSpace(final byte[] nsStartBuf, final int header_size, final int chunk_size) throws Exception {
        this.nodeIndex = 0;
        final ByteArrayInputStream in = new ByteArrayInputStream(nsStartBuf);
        final byte[] int_buf = new byte[4];
        in.read(int_buf);
        this.ns_linenumber = Utils.toInt(int_buf, false);
        in.read(int_buf);
        final int comment = Utils.toInt(int_buf, false);
        in.read(int_buf);
        this.ns_prefix_index = Utils.toInt(int_buf, false);
        in.read(int_buf);
        this.ns_uri_index = Utils.toInt(int_buf, false);
        Log.d(this.tag, "[Namespace Start]Line Number: " + this.ns_linenumber + " Prefix: " + this.stringPool.get(this.ns_prefix_index) + " URI: " + this.stringPool.get(this.ns_uri_index));
    }
    
    private void parseXMLStart(final byte[] xmlStartBuf, final int header_size, final int chunk_size) throws Exception {
        ++this.nodeIndex;
        final Node node = new Node();
        node.setIndex(this.nodeIndex);
        final ByteArrayInputStream in = new ByteArrayInputStream(xmlStartBuf);
        final byte[] int_buf = new byte[4];
        in.read(int_buf);
        final int lineNumber = Utils.toInt(int_buf, false);
        node.setLinenumber(lineNumber);
        in.read(int_buf);
        final int comment = Utils.toInt(int_buf, false);
        in.read(int_buf);
        final int ns_index = Utils.toInt(int_buf, false);
        in.read(int_buf);
        final int name_index = Utils.toInt(int_buf, false);
        final byte[] short_buf = new byte[2];
        in.read(short_buf);
        final int attributeStart = Utils.toInt(short_buf, false);
        in.read(short_buf);
        final int attributeSize = Utils.toInt(short_buf, false);
        in.read(short_buf);
        final int attributeCount = Utils.toInt(short_buf, false);
        in.skip(6L);
        Log.d(this.tag, "[XML Node] Name: " + ((name_index == -1) ? "-1" : this.stringPool.get(name_index)) + " Attr count: " + attributeCount);
        if (name_index != -1) {
            node.setName(this.stringPool.get(name_index));
            if (this.ns_prefix_index != -1 && this.ns_uri_index != -1) {
                node.setNamespacePrefix(this.stringPool.get(this.ns_prefix_index));
                node.setNamespaceURI(this.stringPool.get(this.ns_uri_index));
            }
        }
        if (attributeCount == 0) {
            if (this.listener != null) {
                this.listener.startNode(node);
            }
            return;
        }
        for (int i = 0; i < attributeCount; ++i) {
            final Attribute attr = new Attribute();
            in.read(int_buf);
            final int attr_ns_index = Utils.toInt(int_buf, false);
            in.read(int_buf);
            final int attr_name_index = Utils.toInt(int_buf, false);
            in.read(int_buf);
            final int attr_raw_value = Utils.toInt(int_buf, false);
            String attr_value = "";
            if (attr_raw_value == -1) {
                in.read(short_buf);
                final int data_size = Utils.toInt(short_buf, false);
                in.skip(1L);
                final int data_type = in.read();
                in.read(int_buf);
                final int data = Utils.toInt(int_buf, false);
                attr_value = new StringBuilder().append(data).toString();
            }
            else {
                attr_value = this.stringPool.get(attr_raw_value);
                in.skip(8L);
            }
            if (attr_name_index != -1) {
                attr.setName(this.stringPool.get(attr_name_index));
                attr.setValue(attr_value);
                attr.setIndex(i);
                node.addAttribute(attr);
            }
        }
        if (this.listener != null) {
            this.listener.startNode(node);
        }
    }
    
    private void parseXMLEnd(final byte[] xmlEndBuf, final int header_size, final int chunk_size) throws Exception {
        final ByteArrayInputStream in = new ByteArrayInputStream(xmlEndBuf);
        final byte[] int_buf = new byte[4];
        in.read(int_buf);
        final int lineNumber = Utils.toInt(int_buf, false);
        in.read(int_buf);
        final int comment = Utils.toInt(int_buf, false);
        in.read(int_buf);
        final int ns_index = Utils.toInt(int_buf, false);
        in.read(int_buf);
        final int name_index = Utils.toInt(int_buf, false);
        Log.d(this.tag, "[XML_END] Line Number: " + lineNumber + " Namespace: " + ns_index + " Name: " + ((name_index == -1) ? "-1" : this.stringPool.get(name_index)));
        if (name_index != -1) {
            final Node node = new Node();
            node.setName(this.stringPool.get(name_index));
            node.setLinenumber(lineNumber);
            node.setNamespacePrefix(this.stringPool.get(this.ns_prefix_index));
            node.setNamespaceURI(this.stringPool.get(this.ns_uri_index));
            if (this.listener != null) {
                this.listener.endNode(node);
            }
        }
    }
    
    private void parseEndNameSpace(final byte[] nsStartBuf, final int header_size, final int chunk_size) throws Exception {
        final ByteArrayInputStream in = new ByteArrayInputStream(nsStartBuf);
        final byte[] int_buf = new byte[4];
        in.read(int_buf);
        final int lineNumber = Utils.toInt(int_buf, false);
        in.read(int_buf);
        final int comment = Utils.toInt(int_buf, false);
        in.read(int_buf);
        final int prefix_index = Utils.toInt(int_buf, false);
        in.read(int_buf);
        final int uri_index = Utils.toInt(int_buf, false);
        Log.d(this.tag, "[Namespace END]Line Number: " + lineNumber + " Prefix: " + prefix_index + " URI: " + uri_index);
    }
    
    public void parseResourceTable(final String arscFile) throws Exception {
        this.stringPool.clear();
        Log.d(this.tag, "[Res_Table] File: " + arscFile);
        final BufferedInputStream in = new BufferedInputStream(new FileInputStream(arscFile));
        in.read(this.buf_2);
        Log.d(this.tag, "[Res_Table] Chunk type: " + Utils.toInt(this.buf_2, false));
        if (Utils.toInt(this.buf_2, false) != 2) {
            Log.d(this.tag, "It's an invalid Resources.arsc file. Exiting!");
            return;
        }
        in.read(this.buf_2);
        this.header_size = Utils.toInt(this.buf_2, false);
        in.read(this.buf_4);
        this.chunk_size = Utils.toInt(this.buf_4, false);
        in.read(this.buf_4);
        this.package_count = Utils.toInt(this.buf_4, false);
        Log.d(this.tag, "[Res_Table] Header Size: " + this.header_size + " Chunk size: " + this.chunk_size + " Package_count: " + this.package_count);
        in.read(this.buf_2);
        Log.d(this.tag, "[Res_Table] Chunk type: " + Utils.toInt(this.buf_2, false) + " -->" + this.buf_2[0] + " " + this.buf_2[1]);
        if (Utils.toInt(this.buf_2, false) == 1) {
            Log.d(this.tag, "String Pool...");
            in.read(this.buf_2);
            this.header_size = Utils.toInt(this.buf_2, false);
            in.read(this.buf_4);
            this.chunk_size = Utils.toInt(this.buf_4, false);
            Log.d(this.tag, "String Pool...Header Size: " + this.header_size + " Chunk Size: " + this.chunk_size);
            final byte[] spBuf = new byte[this.chunk_size - 8];
            in.read(spBuf);
            this.parseStringPool(spBuf, this.header_size, this.chunk_size);
            in.read(this.buf_2);
        }
        Log.d(this.tag, "[Res_Table] Chunk type: " + Utils.toInt(this.buf_2, false));
        if (Utils.toInt(this.buf_2, false) == 512) {
            this.parseResPackage(in);
        }
        Log.d(this.tag, "Resource.arsc parsing done!!");
    }
    
    private void parseResPackage(final BufferedInputStream in) throws Exception {
        in.read(this.buf_2);
        this.header_size = Utils.toInt(this.buf_2, false);
        in.read(this.buf_4);
        this.chunk_size = Utils.toInt(this.buf_4, false);
        in.read(this.buf_4);
        final int packg_id = Utils.toInt(this.buf_4, false);
        Log.d(this.tag, "String Pool...Header Size: " + this.header_size + " Chunk Size: " + this.chunk_size + " Packg_ID: " + packg_id);
        final byte[] packg_name_buf = new byte[256];
        in.read(packg_name_buf);
        final String packg_name = Utils.toString(packg_name_buf, false);
        Log.d(this.tag, "Package Name: " + new String(packg_name));
        in.read(this.buf_4);
        final int typeStrings = Utils.toInt(this.buf_4, false);
        in.read(this.buf_4);
        final int lastPublicType = Utils.toInt(this.buf_4, false);
        in.read(this.buf_4);
        final int keyString = Utils.toInt(this.buf_4, false);
        in.read(this.buf_4);
        final int lastPublicKey = Utils.toInt(this.buf_4, false);
        Log.d(this.tag, "[Res_Table] typeStrings=" + typeStrings + " lastPublicType=" + lastPublicType + " keyString=" + keyString + " lastPublicKey=" + lastPublicKey);
        in.read(this.buf_2);
        if (Utils.toInt(this.buf_2, false) == 1) {
            Log.d(this.tag, "String Pool...");
            in.read(this.buf_2);
            this.header_size = Utils.toInt(this.buf_2, false);
            in.read(this.buf_4);
            this.chunk_size = Utils.toInt(this.buf_4, false);
            Log.d(this.tag, "String Pool...Header Size: " + this.header_size + " Chunk Size: " + this.chunk_size);
            final byte[] spBuf = new byte[this.chunk_size - 8];
            in.read(spBuf);
            this.parseStringPool(spBuf, this.header_size, this.chunk_size);
            in.read(this.buf_2);
        }
        if (Utils.toInt(this.buf_2, false) == 1) {
            Log.d(this.tag, "String Pool...");
            in.read(this.buf_2);
            this.header_size = Utils.toInt(this.buf_2, false);
            in.read(this.buf_4);
            this.chunk_size = Utils.toInt(this.buf_4, false);
            Log.d(this.tag, "String Pool...Header Size: " + this.header_size + " Chunk Size: " + this.chunk_size);
            final byte[] spBuf = new byte[this.chunk_size - 8];
            in.read(spBuf);
            this.parseStringPool(spBuf, this.header_size, this.chunk_size);
            in.read(this.buf_2);
        }
    }
    
    private void parseResType() {
    }
    
    private void parseResTypeSpec() {
    }
}
