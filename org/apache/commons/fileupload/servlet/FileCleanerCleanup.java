package org.apache.commons.fileupload.servlet;

import javax.servlet.ServletContextEvent;
import org.apache.commons.io.FileCleaningTracker;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextListener;

public class FileCleanerCleanup implements ServletContextListener
{
    public static final String FILE_CLEANING_TRACKER_ATTRIBUTE;
    
    public static FileCleaningTracker getFileCleaningTracker(final ServletContext pServletContext) {
        return (FileCleaningTracker)pServletContext.getAttribute(FileCleanerCleanup.FILE_CLEANING_TRACKER_ATTRIBUTE);
    }
    
    public static void setFileCleaningTracker(final ServletContext pServletContext, final FileCleaningTracker pTracker) {
        pServletContext.setAttribute(FileCleanerCleanup.FILE_CLEANING_TRACKER_ATTRIBUTE, (Object)pTracker);
    }
    
    public void contextInitialized(final ServletContextEvent sce) {
        setFileCleaningTracker(sce.getServletContext(), new FileCleaningTracker());
    }
    
    public void contextDestroyed(final ServletContextEvent sce) {
        getFileCleaningTracker(sce.getServletContext()).exitWhenFinished();
    }
    
    static {
        FILE_CLEANING_TRACKER_ATTRIBUTE = FileCleanerCleanup.class.getName() + ".FileCleaningTracker";
    }
}
