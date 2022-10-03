package com.zoho.clustering.filerepl;

import java.util.Date;
import java.util.Iterator;
import java.io.File;
import java.util.logging.Level;
import java.util.Map;
import com.zoho.clustering.util.FileUtil;
import java.text.SimpleDateFormat;
import java.util.logging.Logger;

public class SnapshotUtil
{
    private static Logger logger;
    private static final SimpleDateFormat DATE_FOMATTER;
    private DirectoryList directoryList;
    private String snapshotDir;
    
    public SnapshotUtil(final DirectoryList directoryList, final String snapshotDir) {
        this.directoryList = directoryList;
        this.snapshotDir = snapshotDir;
    }
    
    public DirectoryList getDirectoryList() {
        return this.directoryList;
    }
    
    public String getSnapshotDir() {
        return this.snapshotDir;
    }
    
    public String takeSnapshot() {
        final File tmp = this.generateSnapshotTmpDir();
        FileUtil.assertOrCreateDir(tmp);
        final long start = System.currentTimeMillis();
        for (final Map.Entry<Integer, String> entry : this.directoryList.entries()) {
            final Integer dirId = entry.getKey();
            final String dirName = entry.getValue();
            FileUtil.zipDirectory(dirName, tmp.getPath() + '/' + dirId + ".zip");
            SnapshotUtil.logger.log(Level.INFO, "Time taken(cummulative) to zip the directory [{0}] = {1} ms", new Object[] { dirName, System.currentTimeMillis() - start });
        }
        final String snapshotFileName = tmp.getName() + ".zip";
        FileUtil.zipDirectory(tmp.getPath(), this.snapshotDir + '/' + snapshotFileName);
        if (!FileUtil.deleteDirectory(tmp)) {
            SnapshotUtil.logger.log(Level.WARNING, "Couldn't delete the 'snapshot' dir [" + tmp.getAbsolutePath() + "]");
        }
        SnapshotUtil.logger.log(Level.INFO, "Total time taken to create snapshot zip = {0} ms", System.currentTimeMillis() - start);
        return snapshotFileName;
    }
    
    public File getFilePath(final String snapshotName) {
        return new File(this.snapshotDir, snapshotName);
    }
    
    private synchronized File generateSnapshotTmpDir() {
        final String dateStr = SnapshotUtil.DATE_FOMATTER.format(new Date(System.currentTimeMillis()));
        int i = 1;
        File file;
        while (true) {
            file = new File(this.snapshotDir, dateStr + "_" + i);
            final File zipFile = new File(this.snapshotDir, dateStr + "_" + i + ".zip");
            if (!zipFile.exists() && !file.exists()) {
                break;
            }
            ++i;
        }
        return file;
    }
    
    static {
        SnapshotUtil.logger = Logger.getLogger(SnapshotUtil.class.getName());
        DATE_FOMATTER = new SimpleDateFormat("MM-dd-yyyy");
    }
}
