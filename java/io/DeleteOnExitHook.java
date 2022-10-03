package java.io;

import sun.misc.SharedSecrets;
import java.util.Iterator;
import java.util.List;
import java.util.Collections;
import java.util.Collection;
import java.util.ArrayList;
import java.util.LinkedHashSet;

class DeleteOnExitHook
{
    private static LinkedHashSet<String> files;
    
    private DeleteOnExitHook() {
    }
    
    static synchronized void add(final String s) {
        if (DeleteOnExitHook.files == null) {
            throw new IllegalStateException("Shutdown in progress");
        }
        DeleteOnExitHook.files.add(s);
    }
    
    static void runHooks() {
        final LinkedHashSet<String> files;
        synchronized (DeleteOnExitHook.class) {
            files = DeleteOnExitHook.files;
            DeleteOnExitHook.files = null;
        }
        final ArrayList list = new ArrayList(files);
        Collections.reverse(list);
        final Iterator iterator = list.iterator();
        while (iterator.hasNext()) {
            new File((String)iterator.next()).delete();
        }
    }
    
    static {
        DeleteOnExitHook.files = new LinkedHashSet<String>();
        SharedSecrets.getJavaLangAccess().registerShutdownHook(2, true, new Runnable() {
            @Override
            public void run() {
                DeleteOnExitHook.runHooks();
            }
        });
    }
}
