package com.adventnet.client.components.tpl.service;

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

public class TplTablePopulator implements Service, TemplateService
{
    protected Logger logger;
    
    public TplTablePopulator() {
        this.logger = Logger.getLogger(TplTablePopulator.class.getName());
    }
    
    public void start() throws Exception {
    }
    
    public void readDbAndCache(final DataObject serviceDO, final boolean checkModifiedTime) throws Exception {
        final Table table1 = new Table("TemplateFiles");
        final SelectQuery sq = (SelectQuery)new SelectQueryImpl(table1);
        final Column c1 = new Column("TemplateFiles", "FILE_NO");
        final Column c2 = new Column("TemplateFiles", "FILE_NAME");
        final ArrayList colList = new ArrayList();
        colList.add(c1);
        colList.add(c2);
        sq.addSelectColumns((List)colList);
        final Persistence per = (Persistence)BeanUtil.lookup("Persistence");
        final DataObject d = per.get(sq);
        final Iterator it = d.getRows("TemplateFiles");
        String data = null;
        while (it.hasNext()) {
            final Row row = it.next();
            data = (String)row.get("FILE_NAME");
            try {
                this.read(data, false);
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
        int a = 0;
        int b = 0;
        int i = 1;
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
        final String fullfile = new String(getFileAsString(file));
        TemplateAPI.lastReadTime.put(s, (int)System.currentTimeMillis() + "");
        final int len = fullfile.length();
        while (i < len) {
            if (fullfile.indexOf("start-", i) != -1 && fullfile.indexOf("-end-", i) != -1) {
                a = fullfile.indexOf("!--TPL-start-", i);
                b = fullfile.indexOf("-->", a);
                if (a > b) {
                    this.error();
                }
                final String key1 = fullfile.substring(a + 13, b);
                a = fullfile.indexOf("<!--TPL-end-", b);
                if (a < b) {
                    this.error();
                }
                final int firstnewlinepos = fullfile.indexOf(10, b);
                final String value = fullfile.substring(firstnewlinepos + 1, a - 1);
                b = fullfile.indexOf("-->", a);
                final String key2 = fullfile.substring(a + 12, b);
                if (key1.equals(key2)) {
                    TemplateAPI.setHtmlMap(key1, value);
                }
                else {
                    this.error();
                }
                i = b + 3;
            }
            else {
                ++i;
            }
        }
    }
    
    public void updateCache() throws Exception {
        this.readDbAndCache(null, true);
    }
    
    public void create(final DataObject serviceDO) throws Exception {
        this.readDbAndCache(serviceDO, false);
    }
    
    public void error() {
        this.logger.log(Level.FINEST, "File format error");
    }
    
    public void stop() throws Exception {
    }
    
    public void destroy() throws Exception {
    }
}
