package com.adventnet.client.components.tpl.service;

import com.adventnet.i18n.I18N;
import org.htmlparser.util.SimpleNodeIterator;
import org.htmlparser.util.NodeList;
import com.adventnet.client.components.table.template.FillTable;
import org.htmlparser.nodes.TagNode;
import org.htmlparser.filters.HasAttributeFilter;
import org.htmlparser.NodeFilter;
import org.htmlparser.Parser;
import com.adventnet.client.tpl.TemplateAPI;
import java.io.RandomAccessFile;
import java.io.File;
import java.util.Iterator;
import com.adventnet.ds.query.SelectQuery;
import java.io.IOException;
import java.util.logging.Level;
import com.adventnet.persistence.Row;
import com.adventnet.mfw.bean.BeanUtil;
import com.adventnet.persistence.Persistence;
import java.util.List;
import java.util.ArrayList;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import com.adventnet.persistence.DataObject;
import java.util.logging.Logger;
import com.adventnet.client.tpl.service.TemplateService;
import com.adventnet.mfw.service.Service;

public class TemplateTablePopulator implements Service, TemplateService
{
    protected Logger logger;
    
    public TemplateTablePopulator() {
        this.logger = Logger.getLogger(TemplateTablePopulator.class.getName());
    }
    
    public void start() throws Exception {
    }
    
    public void readDbAndCache(final DataObject serviceDO, final boolean checkModifiedTime) throws Exception {
        final Table table = new Table("TableTemplateFiles");
        final SelectQuery sq = (SelectQuery)new SelectQueryImpl(table);
        final Column c1 = new Column("TableTemplateFiles", "FILE_NO");
        final Column c2 = new Column("TableTemplateFiles", "FILE_NAME");
        final ArrayList colList = new ArrayList();
        colList.add(c1);
        colList.add(c2);
        sq.addSelectColumns((List)colList);
        final Persistence per = (Persistence)BeanUtil.lookup("Persistence");
        final DataObject d = per.get(sq);
        final Iterator it = d.getRows("TableTemplateFiles");
        String data = null;
        while (it.hasNext()) {
            final Row row = it.next();
            data = (String)row.get("FILE_NAME");
            try {
                this.read(data, checkModifiedTime);
            }
            catch (final IOException e) {
                this.logger.log(Level.FINEST, e.getMessage());
            }
        }
    }
    
    public static String getFileAsString(final File fileName) throws IOException {
        final RandomAccessFile rd = new RandomAccessFile(fileName, "r");
        final byte[] arr = new byte[(int)rd.length()];
        rd.readFully(arr);
        rd.close();
        return new String(arr);
    }
    
    public void read(final String s, final boolean checkModifiedTime) throws Exception {
        String filepath = "";
        if (System.getProperty("contextDIR") != null) {
            filepath = System.getProperty("server.dir") + "/webapps/" + System.getProperty("contextDIR") + "/" + s;
            final File testfile = new File(filepath);
            if (!testfile.exists() || !testfile.canRead()) {
                filepath = System.getProperty("server.dir") + s;
            }
        }
        else {
            filepath = System.getProperty("server.dir") + s;
        }
        final File file = new File(filepath);
        if (!file.exists() || !file.canRead()) {
            this.logger.log(Level.WARNING, "this " + filepath + "  File can't be read");
            return;
        }
        if (checkModifiedTime && TemplateAPI.lastReadTime.containsKey(s) && file.lastModified() < Integer.parseInt(TemplateAPI.lastReadTime.get(s))) {
            return;
        }
        TemplateAPI.lastReadTime.put(s, (int)System.currentTimeMillis() + "");
        final Parser parser = new Parser(filepath);
        final NodeList nl = parser.parse((NodeFilter)null);
        final HasAttributeFilter filter = new HasAttributeFilter();
        filter.setAttributeName("viewname");
        final NodeList sublist = nl.extractAllNodesThatMatch((NodeFilter)filter, true);
        final SimpleNodeIterator iter = sublist.elements();
        while (iter.hasMoreNodes()) {
            final TagNode temp = (TagNode)iter.nextNode();
            final NodeList tempList = temp.getChildren();
            final String tempViewName = temp.getAttribute("viewname");
            final String componentType = temp.getAttribute("componentname");
            String templateEntry = temp.toHtml();
            templateEntry = checkDollarProperties(templateEntry);
            if ("ACTab".equals(componentType)) {
                FillTable.tabhtmlmap.put(tempViewName, templateEntry);
            }
            else {
                FillTable.tablehtmlmap.put(tempViewName, templateEntry);
            }
        }
    }
    
    public void updateCache() throws Exception {
        this.readDbAndCache(null, true);
    }
    
    public void create(final DataObject serviceDO) throws Exception {
        this.readDbAndCache(serviceDO, false);
    }
    
    public void stop() throws Exception {
    }
    
    public void destroy() throws Exception {
    }
    
    public static String checkDollarProperties(String string) throws Exception {
        StringBuffer buff = new StringBuffer();
        while (string.indexOf("${I18N||") != -1) {
            final int index = string.indexOf("${I18N||");
            final int index2 = string.indexOf("}", index);
            buff.append(string.substring(0, index));
            final String dollarstuff = string.substring(index, index2 + 1);
            string = string.substring(index2 + 1);
            buff.append(getDollarI18N(dollarstuff));
        }
        buff.append(string);
        string = buff.toString();
        buff = new StringBuffer();
        while (string.indexOf("${SP||") != -1) {
            final int index = string.indexOf("${SP||");
            final int index2 = string.indexOf("}", index);
            buff.append(string.substring(0, index));
            final String dollarstuff = string.substring(index, index2 + 1);
            string = string.substring(index2 + 1);
            buff.append(getDollarSystemPropery(dollarstuff));
        }
        buff.append(string);
        return buff.toString();
    }
    
    private static String getDollarSystemPropery(final String str) {
        final String data = str.substring(2, str.length() - 1);
        final String[] datas = data.split("\\|\\|", 3);
        if (System.getProperty(datas[1]) != null && !"".equals(System.getProperty(datas[1]))) {
            return System.getProperty(datas[1]);
        }
        if ("LOCALHOST".equals(datas[2])) {
            return "${REQ||LOCAL}";
        }
        return datas[2];
    }
    
    private static String getDollarI18N(final String str) throws Exception {
        final String data = str.substring(2, str.length() - 1);
        final String[] datas = data.split("\\|\\|", 2);
        return I18N.getMsg(datas[1], new Object[0]);
    }
}
