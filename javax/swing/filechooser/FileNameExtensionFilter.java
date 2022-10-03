package javax.swing.filechooser;

import java.util.Arrays;
import java.io.File;
import java.util.Locale;

public final class FileNameExtensionFilter extends FileFilter
{
    private final String description;
    private final String[] extensions;
    private final String[] lowerCaseExtensions;
    
    public FileNameExtensionFilter(final String description, final String... array) {
        if (array == null || array.length == 0) {
            throw new IllegalArgumentException("Extensions must be non-null and not empty");
        }
        this.description = description;
        this.extensions = new String[array.length];
        this.lowerCaseExtensions = new String[array.length];
        for (int i = 0; i < array.length; ++i) {
            if (array[i] == null || array[i].length() == 0) {
                throw new IllegalArgumentException("Each extension must be non-null and not empty");
            }
            this.extensions[i] = array[i];
            this.lowerCaseExtensions[i] = array[i].toLowerCase(Locale.ENGLISH);
        }
    }
    
    @Override
    public boolean accept(final File file) {
        if (file != null) {
            if (file.isDirectory()) {
                return true;
            }
            final String name = file.getName();
            final int lastIndex = name.lastIndexOf(46);
            if (lastIndex > 0 && lastIndex < name.length() - 1) {
                final String lowerCase = name.substring(lastIndex + 1).toLowerCase(Locale.ENGLISH);
                final String[] lowerCaseExtensions = this.lowerCaseExtensions;
                for (int length = lowerCaseExtensions.length, i = 0; i < length; ++i) {
                    if (lowerCase.equals(lowerCaseExtensions[i])) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
    
    @Override
    public String getDescription() {
        return this.description;
    }
    
    public String[] getExtensions() {
        final String[] array = new String[this.extensions.length];
        System.arraycopy(this.extensions, 0, array, 0, this.extensions.length);
        return array;
    }
    
    @Override
    public String toString() {
        return super.toString() + "[description=" + this.getDescription() + " extensions=" + Arrays.asList(this.getExtensions()) + "]";
    }
}
