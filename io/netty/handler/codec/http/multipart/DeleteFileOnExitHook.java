package io.netty.handler.codec.http.multipart;

import java.util.Map;
import java.util.Collections;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Iterator;
import java.io.File;
import java.util.Set;

final class DeleteFileOnExitHook
{
    private static final Set<String> FILES;
    
    private DeleteFileOnExitHook() {
    }
    
    public static void remove(final String file) {
        DeleteFileOnExitHook.FILES.remove(file);
    }
    
    public static void add(final String file) {
        DeleteFileOnExitHook.FILES.add(file);
    }
    
    public static boolean checkFileExist(final String file) {
        return DeleteFileOnExitHook.FILES.contains(file);
    }
    
    static void runHook() {
        for (final String filename : DeleteFileOnExitHook.FILES) {
            new File(filename).delete();
        }
    }
    
    static {
        FILES = Collections.newSetFromMap(new ConcurrentHashMap<String, Boolean>());
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                DeleteFileOnExitHook.runHook();
            }
        });
    }
}
