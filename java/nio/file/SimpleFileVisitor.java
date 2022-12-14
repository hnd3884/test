package java.nio.file;

import java.io.IOException;
import java.util.Objects;
import java.nio.file.attribute.BasicFileAttributes;

public class SimpleFileVisitor<T> implements FileVisitor<T>
{
    protected SimpleFileVisitor() {
    }
    
    @Override
    public FileVisitResult preVisitDirectory(final T t, final BasicFileAttributes basicFileAttributes) throws IOException {
        Objects.requireNonNull(t);
        Objects.requireNonNull(basicFileAttributes);
        return FileVisitResult.CONTINUE;
    }
    
    @Override
    public FileVisitResult visitFile(final T t, final BasicFileAttributes basicFileAttributes) throws IOException {
        Objects.requireNonNull(t);
        Objects.requireNonNull(basicFileAttributes);
        return FileVisitResult.CONTINUE;
    }
    
    @Override
    public FileVisitResult visitFileFailed(final T t, final IOException ex) throws IOException {
        Objects.requireNonNull(t);
        throw ex;
    }
    
    @Override
    public FileVisitResult postVisitDirectory(final T t, final IOException ex) throws IOException {
        Objects.requireNonNull(t);
        if (ex != null) {
            throw ex;
        }
        return FileVisitResult.CONTINUE;
    }
}
