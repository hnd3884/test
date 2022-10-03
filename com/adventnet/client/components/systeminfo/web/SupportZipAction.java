package com.adventnet.client.components.systeminfo.web;

import java.util.Calendar;
import java.io.InputStream;
import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.util.zip.ZipEntry;
import java.io.OutputStream;
import java.util.zip.ZipOutputStream;
import java.io.FileOutputStream;
import java.util.Arrays;
import java.util.Comparator;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.File;
import com.adventnet.client.view.web.WebViewAPI;
import com.adventnet.iam.xss.IAMEncoder;
import org.apache.struts.action.ActionForward;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletRequest;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.Action;

public class SupportZipAction extends Action
{
    public ActionForward execute(final ActionMapping mapping, final ActionForm form, final HttpServletRequest request, final HttpServletResponse response) throws Exception {
        try {
            final String filename = this.createSupportZip(request);
            if (response.getContentType() == null) {
                response.setContentType("text/html");
            }
            response.getWriter().write("<script>var FEEDBACKFILE='" + IAMEncoder.encodeJavaScript(filename) + "';</script>");
            request.setAttribute("FILENAME", (Object)filename);
        }
        catch (final Exception e) {
            e.printStackTrace();
            response.getWriter().write("<script>var ERROR_MSG='" + IAMEncoder.encodeJavaScript(e.getMessage()) + "';</script>");
        }
        return new ActionForward(WebViewAPI.getRootViewURL(request));
    }
    
    private String createSupportZip(final HttpServletRequest request) throws Exception {
        final File zippedLogDir = new File(request.getRealPath("/logs/supportlogs/"));
        if (!zippedLogDir.exists() && !zippedLogDir.mkdirs()) {
            throw new IOException("Unable to create dir " + zippedLogDir.getPath());
        }
        final String fileName = this.getFileName();
        final String SERVER_HOME = new File("../").getCanonicalPath() + File.separator;
        final String logs = SERVER_HOME + "logs";
        File[] filesList = null;
        final String numberOfFiles = request.getParameter("NUM_FILES");
        if (numberOfFiles != null) {
            final int numOfFiles = Integer.parseInt(numberOfFiles);
            boolean zipServeroutOnly = true;
            final String fileNamesStr = request.getParameter("FILE_NAMES");
            if (fileNamesStr != null) {
                final String[] fileNames = fileNamesStr.split(",");
                if (fileNames.length > 0) {
                    zipServeroutOnly = false;
                    final File logsFolder = new File(logs);
                    final int totalFiles = fileNames.length * numOfFiles;
                    filesList = new File[totalFiles];
                    int fileIndex = 0;
                    for (int fileCount = 0; fileCount < fileNames.length; ++fileCount) {
                        final File[] filteredFileList = logsFolder.listFiles(new LogFileFilter(fileNames[fileCount]));
                        Arrays.sort(filteredFileList, new Comparator() {
                            @Override
                            public int compare(final Object file1, final Object file2) {
                                final File fileObj1 = (File)file1;
                                final File fileObj2 = (File)file2;
                                final long result = fileObj2.lastModified() - fileObj1.lastModified();
                                if (result > 0L) {
                                    return 1;
                                }
                                if (result < 0L) {
                                    return -1;
                                }
                                return 0;
                            }
                        });
                        for (int noOfFilesToAdd = (numOfFiles < filteredFileList.length) ? numOfFiles : filteredFileList.length, count = 0; count < noOfFilesToAdd; ++count) {
                            filesList[fileIndex] = filteredFileList[count];
                            ++fileIndex;
                        }
                    }
                }
            }
            if (zipServeroutOnly) {
                filesList = new File[numOfFiles];
                final File[] filteredList = new File(logs).listFiles(new LogFileFilter("serverout"));
                for (int noOfFilesToAdd2 = (numOfFiles < filteredList.length) ? numOfFiles : filteredList.length, fileIndex2 = 0; fileIndex2 < noOfFilesToAdd2; ++fileIndex2) {
                    filesList[fileIndex2] = filteredList[fileIndex2];
                }
            }
        }
        ZipOutputStream zout = null;
        try {
            zout = new ZipOutputStream(new FileOutputStream(zippedLogDir.getPath() + "/" + fileName));
            final File dirFile = new File(logs);
            File[] tempFls = dirFile.listFiles();
            for (int j = 0; j < tempFls.length; ++j) {
                if (tempFls[j].exists() && tempFls[j].isDirectory()) {
                    this.zipRecursively(zout, tempFls[j].getAbsolutePath());
                }
                else {
                    if (filesList != null) {
                        tempFls = filesList;
                    }
                    for (int i = 0; i < tempFls.length; ++i) {
                        if (tempFls[i].exists() && tempFls[i].isFile() && !tempFls[i].getPath().endsWith(".lck")) {
                            this.addToZip(zout, tempFls[i].getPath());
                        }
                    }
                }
            }
        }
        catch (final Exception e) {
            System.out.println(e);
        }
        finally {
            if (zout != null) {
                zout.close();
            }
        }
        return fileName;
    }
    
    private void addToZip(final ZipOutputStream zout, final String fileName) throws Exception {
        final File f = new File(fileName);
        if (f.exists()) {
            try {
                zout.putNextEntry(new ZipEntry(f.getName()));
            }
            catch (final IOException ioe) {
                System.err.println(ioe);
            }
            this.addFileContents(fileName, zout);
        }
    }
    
    private void zipRecursively(final ZipOutputStream jos, final String directory) throws Exception {
        final File dir = new File(directory);
        final String server = new File("../").getCanonicalPath() + File.separator + "logs";
        final File[] files = dir.listFiles();
        ZipEntry je = null;
        String fi = null;
        File file = null;
        for (int i = 0; i < files.length; ++i) {
            file = files[i];
            fi = file.getAbsolutePath();
            if (file.isDirectory()) {
                je = new ZipEntry(fi.substring(server.length(), fi.length()) + File.separator);
                try {
                    jos.putNextEntry(je);
                }
                catch (final IOException ioe) {
                    System.err.println(ioe);
                }
                this.zipRecursively(jos, fi);
            }
            else {
                je = new ZipEntry(fi.substring(server.length(), fi.length()));
                try {
                    jos.putNextEntry(je);
                }
                catch (final IOException ioe) {
                    System.err.println(ioe);
                }
                this.addFileContents(fi, jos);
            }
        }
    }
    
    private void addFileContents(final String fileName, final OutputStream ous) throws Exception {
        final File fi = new File(fileName);
        FileInputStream fis = null;
        BufferedInputStream bis = null;
        try {
            fis = new FileInputStream(fi);
            bis = new BufferedInputStream(fis);
            int i;
            while ((i = bis.read()) != -1) {
                ous.write(i);
            }
        }
        catch (final IOException ioe) {
            System.err.println(ioe);
        }
        finally {
            if (fis != null) {
                fis.close();
            }
            if (bis != null) {
                bis.close();
            }
        }
    }
    
    private long getFileSize(final File file, long totalsize) {
        if (file.isDirectory()) {
            final String[] entries = file.list();
            for (int maxlen = (entries == null) ? 0 : entries.length, i = 0; i < maxlen && totalsize <= 52428800L; totalsize = this.getFileSize(new File(file, entries[i]), totalsize), ++i) {}
        }
        else if (file.isFile()) {
            totalsize += file.length();
        }
        return totalsize;
    }
    
    private String getFileName() {
        final String[] Months = { "Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec" };
        final Calendar rightNow = Calendar.getInstance();
        final String uniqueTimeStr = Months[rightNow.get(2)] + "_" + rightNow.get(5) + "_" + rightNow.get(1) + "_" + rightNow.get(11) + "_" + rightNow.get(12) + "_" + rightNow.get(13);
        final String fileName = "logs_" + uniqueTimeStr + ".zip";
        return fileName;
    }
    
    class LogFileFilter implements FilenameFilter
    {
        public String pattern;
        
        public LogFileFilter(final String pattern) {
            this.pattern = pattern;
        }
        
        @Override
        public boolean accept(final File dir, final String fileName) {
            return fileName.startsWith(this.pattern) && !fileName.endsWith(".lck");
        }
    }
}
