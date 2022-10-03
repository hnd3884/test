package sun.net.www;

import java.io.InputStream;
import java.net.URLConnection;
import java.util.StringTokenizer;
import java.io.File;

public class MimeEntry implements Cloneable
{
    private String typeName;
    private String tempFileNameTemplate;
    private int action;
    private String command;
    private String description;
    private String imageFileName;
    private String[] fileExtensions;
    boolean starred;
    public static final int UNKNOWN = 0;
    public static final int LOAD_INTO_BROWSER = 1;
    public static final int SAVE_TO_FILE = 2;
    public static final int LAUNCH_APPLICATION = 3;
    static final String[] actionKeywords;
    
    public MimeEntry(final String s) {
        this(s, 0, null, null, null);
    }
    
    MimeEntry(final String s, final String imageFileName, final String extensions) {
        this.typeName = s.toLowerCase();
        this.action = 0;
        this.command = null;
        this.imageFileName = imageFileName;
        this.setExtensions(extensions);
        this.starred = this.isStarred(this.typeName);
    }
    
    MimeEntry(final String s, final int action, final String command, final String tempFileNameTemplate) {
        this.typeName = s.toLowerCase();
        this.action = action;
        this.command = command;
        this.imageFileName = null;
        this.fileExtensions = null;
        this.tempFileNameTemplate = tempFileNameTemplate;
    }
    
    MimeEntry(final String s, final int action, final String command, final String imageFileName, final String[] fileExtensions) {
        this.typeName = s.toLowerCase();
        this.action = action;
        this.command = command;
        this.imageFileName = imageFileName;
        this.fileExtensions = fileExtensions;
        this.starred = this.isStarred(s);
    }
    
    public synchronized String getType() {
        return this.typeName;
    }
    
    public synchronized void setType(final String s) {
        this.typeName = s.toLowerCase();
    }
    
    public synchronized int getAction() {
        return this.action;
    }
    
    public synchronized void setAction(final int action, final String command) {
        this.action = action;
        this.command = command;
    }
    
    public synchronized void setAction(final int action) {
        this.action = action;
    }
    
    public synchronized String getLaunchString() {
        return this.command;
    }
    
    public synchronized void setCommand(final String command) {
        this.command = command;
    }
    
    public synchronized String getDescription() {
        return (this.description != null) ? this.description : this.typeName;
    }
    
    public synchronized void setDescription(final String description) {
        this.description = description;
    }
    
    public String getImageFileName() {
        return this.imageFileName;
    }
    
    public synchronized void setImageFileName(final String imageFileName) {
        if (new File(imageFileName).getParent() == null) {
            this.imageFileName = System.getProperty("java.net.ftp.imagepath." + imageFileName);
        }
        else {
            this.imageFileName = imageFileName;
        }
        if (imageFileName.lastIndexOf(46) < 0) {
            this.imageFileName += ".gif";
        }
    }
    
    public String getTempFileTemplate() {
        return this.tempFileNameTemplate;
    }
    
    public synchronized String[] getExtensions() {
        return this.fileExtensions;
    }
    
    public synchronized String getExtensionsAsList() {
        String s = "";
        if (this.fileExtensions != null) {
            for (int i = 0; i < this.fileExtensions.length; ++i) {
                s += this.fileExtensions[i];
                if (i < this.fileExtensions.length - 1) {
                    s += ",";
                }
            }
        }
        return s;
    }
    
    public synchronized void setExtensions(final String s) {
        final StringTokenizer stringTokenizer = new StringTokenizer(s, ",");
        final int countTokens = stringTokenizer.countTokens();
        final String[] fileExtensions = new String[countTokens];
        for (int i = 0; i < countTokens; ++i) {
            fileExtensions[i] = ((String)stringTokenizer.nextElement()).trim();
        }
        this.fileExtensions = fileExtensions;
    }
    
    private boolean isStarred(final String s) {
        return s != null && s.length() > 0 && s.endsWith("/*");
    }
    
    public Object launch(final URLConnection urlConnection, final InputStream inputStream, final MimeTable mimeTable) throws ApplicationLaunchException {
        switch (this.action) {
            case 2: {
                try {
                    return inputStream;
                }
                catch (final Exception ex) {
                    return "Load to file failed:\n" + ex;
                }
            }
            case 1: {
                try {
                    return urlConnection.getContent();
                }
                catch (final Exception ex2) {
                    return null;
                }
            }
            case 3: {
                String s = this.command;
                final int index = s.indexOf(32);
                if (index > 0) {
                    s = s.substring(0, index);
                }
                return new MimeLauncher(this, urlConnection, inputStream, mimeTable.getTempFileTemplate(), s);
            }
            case 0: {
                return null;
            }
            default: {
                return null;
            }
        }
    }
    
    public boolean matches(final String s) {
        if (this.starred) {
            return s.startsWith(this.typeName);
        }
        return s.equals(this.typeName);
    }
    
    public Object clone() {
        final MimeEntry mimeEntry = new MimeEntry(this.typeName);
        mimeEntry.action = this.action;
        mimeEntry.command = this.command;
        mimeEntry.description = this.description;
        mimeEntry.imageFileName = this.imageFileName;
        mimeEntry.tempFileNameTemplate = this.tempFileNameTemplate;
        mimeEntry.fileExtensions = this.fileExtensions;
        return mimeEntry;
    }
    
    public synchronized String toProperty() {
        final StringBuffer sb = new StringBuffer();
        final String s = "; ";
        int n = 0;
        final int action = this.getAction();
        if (action != 0) {
            sb.append("action=" + MimeEntry.actionKeywords[action]);
            n = 1;
        }
        final String launchString = this.getLaunchString();
        if (launchString != null && launchString.length() > 0) {
            if (n != 0) {
                sb.append(s);
            }
            sb.append("application=" + launchString);
            n = 1;
        }
        if (this.getImageFileName() != null) {
            if (n != 0) {
                sb.append(s);
            }
            sb.append("icon=" + this.getImageFileName());
            n = 1;
        }
        final String extensionsAsList = this.getExtensionsAsList();
        if (extensionsAsList.length() > 0) {
            if (n != 0) {
                sb.append(s);
            }
            sb.append("file_extensions=" + extensionsAsList);
            n = 1;
        }
        final String description = this.getDescription();
        if (description != null && !description.equals(this.getType())) {
            if (n != 0) {
                sb.append(s);
            }
            sb.append("description=" + description);
        }
        return sb.toString();
    }
    
    @Override
    public String toString() {
        return "MimeEntry[contentType=" + this.typeName + ", image=" + this.imageFileName + ", action=" + this.action + ", command=" + this.command + ", extensions=" + this.getExtensionsAsList() + "]";
    }
    
    static {
        actionKeywords = new String[] { "unknown", "browser", "save", "application" };
    }
}
