package jdk.jfr.internal.tool;

import java.util.Iterator;
import java.nio.file.Path;
import java.io.IOException;
import jdk.jfr.internal.Type;
import java.util.Comparator;
import jdk.jfr.internal.consumer.RecordingInternals;
import jdk.jfr.consumer.RecordingFile;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.Deque;
import java.util.Collections;
import java.util.List;

final class Metadata extends Command
{
    @Override
    public String getName() {
        return "metadata";
    }
    
    @Override
    public List<String> getOptionSyntax() {
        return Collections.singletonList("<file>");
    }
    
    @Override
    public String getDescription() {
        return "Display event metadata, such as labels, descriptions and field layout";
    }
    
    @Override
    public void execute(final Deque<String> deque) throws UserSyntaxException, UserDataException {
        final Path jfrInputFile = this.getJFRInputFile(deque);
        boolean showIds = false;
        for (int i = deque.size(); i > 0; i = deque.size()) {
            if (this.acceptOption(deque, "--ids")) {
                showIds = true;
            }
            if (i == deque.size()) {
                throw new UserSyntaxException("unknown option " + (String)deque.peek());
            }
        }
        try (final PrintWriter printWriter = new PrintWriter(System.out)) {
            final PrettyWriter prettyWriter = new PrettyWriter(printWriter);
            prettyWriter.setShowIds(showIds);
            try (final RecordingFile recordingFile = new RecordingFile(jfrInputFile)) {
                final List<Type> types = RecordingInternals.INSTANCE.readTypes(recordingFile);
                Collections.sort((List<Object>)types, (Comparator<? super Object>)new TypeComparator());
                final Iterator<Type> iterator = types.iterator();
                while (iterator.hasNext()) {
                    prettyWriter.printType(iterator.next());
                }
                prettyWriter.flush(true);
            }
            catch (final IOException ex) {
                this.couldNotReadError(jfrInputFile, ex);
            }
        }
    }
    
    private static class TypeComparator implements Comparator<Type>
    {
        @Override
        public int compare(final Type type, final Type type2) {
            if (this.groupValue(type) != this.groupValue(type2)) {
                return Integer.compare(this.groupValue(type), this.groupValue(type2));
            }
            final String name = type.getName();
            final String name2 = type2.getName();
            final String substring = name.substring(0, name.lastIndexOf(46) + 1);
            final String substring2 = name2.substring(0, name2.lastIndexOf(46) + 1);
            if (substring.equals(substring2)) {
                return name.compareTo(name2);
            }
            if (Type.SUPER_TYPE_EVENT.equals(type.getSuperType()) && !substring.equals(substring2)) {
                if (substring.equals("jdk.jfr")) {
                    return -1;
                }
                if (substring2.equals("jdk.jfr")) {
                    return 1;
                }
            }
            return substring.compareTo(substring2);
        }
        
        int groupValue(final Type type) {
            final String superType = type.getSuperType();
            if (superType == null) {
                return 1;
            }
            if (Type.SUPER_TYPE_ANNOTATION.equals(superType)) {
                return 3;
            }
            if (Type.SUPER_TYPE_SETTING.equals(superType)) {
                return 4;
            }
            if (Type.SUPER_TYPE_EVENT.equals(superType)) {
                return 5;
            }
            return 2;
        }
    }
}
