package javax.mail.event;

public abstract class FolderAdapter implements FolderListener
{
    @Override
    public void folderCreated(final FolderEvent e) {
    }
    
    @Override
    public void folderRenamed(final FolderEvent e) {
    }
    
    @Override
    public void folderDeleted(final FolderEvent e) {
    }
}
