package org.eclipse.jdt.internal.compiler.util;

import java.util.List;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

public class ManifestAnalyzer
{
    private static final int START = 0;
    private static final int IN_CLASSPATH_HEADER = 1;
    private static final int PAST_CLASSPATH_HEADER = 2;
    private static final int SKIPPING_WHITESPACE = 3;
    private static final int READING_JAR = 4;
    private static final int CONTINUING = 5;
    private static final int SKIP_LINE = 6;
    private static final char[] CLASSPATH_HEADER_TOKEN;
    private int classpathSectionsCount;
    private ArrayList calledFilesNames;
    
    static {
        CLASSPATH_HEADER_TOKEN = "Class-Path:".toCharArray();
    }
    
    public boolean analyzeManifestContents(final InputStream inputStream) throws IOException {
        final char[] chars = Util.getInputStreamAsCharArray(inputStream, -1, "UTF-8");
        int state = 0;
        int substate = 0;
        final StringBuffer currentJarToken = new StringBuffer();
        this.classpathSectionsCount = 0;
        this.calledFilesNames = null;
        int i = 0;
        final int max = chars.length;
        while (i < max) {
            int currentChar = chars[i++];
            if (currentChar == 13 && i < max) {
                currentChar = chars[i++];
            }
            switch (state) {
                case 0: {
                    if (currentChar == ManifestAnalyzer.CLASSPATH_HEADER_TOKEN[0]) {
                        state = 1;
                        substate = 1;
                        continue;
                    }
                    state = 6;
                    continue;
                }
                case 1: {
                    if (currentChar == 10) {
                        state = 0;
                        continue;
                    }
                    if (currentChar != ManifestAnalyzer.CLASSPATH_HEADER_TOKEN[substate++]) {
                        state = 6;
                        continue;
                    }
                    if (substate == ManifestAnalyzer.CLASSPATH_HEADER_TOKEN.length) {
                        state = 2;
                        continue;
                    }
                    continue;
                }
                case 2: {
                    if (currentChar == 32) {
                        state = 3;
                        ++this.classpathSectionsCount;
                        continue;
                    }
                    return false;
                }
                case 3: {
                    if (currentChar == 10) {
                        state = 5;
                        continue;
                    }
                    if (currentChar != 32) {
                        currentJarToken.append((char)currentChar);
                        state = 4;
                        continue;
                    }
                    this.addCurrentTokenJarWhenNecessary(currentJarToken);
                    continue;
                }
                case 5: {
                    if (currentChar == 10) {
                        this.addCurrentTokenJarWhenNecessary(currentJarToken);
                        state = 0;
                        continue;
                    }
                    if (currentChar == 32) {
                        state = 3;
                        continue;
                    }
                    if (currentChar == ManifestAnalyzer.CLASSPATH_HEADER_TOKEN[0]) {
                        this.addCurrentTokenJarWhenNecessary(currentJarToken);
                        state = 1;
                        substate = 1;
                        continue;
                    }
                    if (this.calledFilesNames == null) {
                        this.addCurrentTokenJarWhenNecessary(currentJarToken);
                        state = 0;
                        continue;
                    }
                    this.addCurrentTokenJarWhenNecessary(currentJarToken);
                    state = 6;
                    continue;
                }
                case 6: {
                    if (currentChar == 10) {
                        state = 0;
                        continue;
                    }
                    continue;
                }
                case 4: {
                    if (currentChar == 10) {
                        state = 5;
                        continue;
                    }
                    if (currentChar == 32) {
                        state = 3;
                        this.addCurrentTokenJarWhenNecessary(currentJarToken);
                        continue;
                    }
                    currentJarToken.append((char)currentChar);
                    continue;
                }
                default: {
                    continue;
                }
            }
        }
        switch (state) {
            case 0: {
                return true;
            }
            case 1: {
                return true;
            }
            case 2: {
                return false;
            }
            case 3: {
                this.addCurrentTokenJarWhenNecessary(currentJarToken);
                return true;
            }
            case 5: {
                this.addCurrentTokenJarWhenNecessary(currentJarToken);
                return true;
            }
            case 6: {
                return this.classpathSectionsCount == 0 || this.calledFilesNames != null;
            }
            case 4: {
                return false;
            }
            default: {
                return true;
            }
        }
    }
    
    private boolean addCurrentTokenJarWhenNecessary(final StringBuffer currentJarToken) {
        if (currentJarToken != null && currentJarToken.length() > 0) {
            if (this.calledFilesNames == null) {
                this.calledFilesNames = new ArrayList();
            }
            this.calledFilesNames.add(currentJarToken.toString());
            currentJarToken.setLength(0);
            return true;
        }
        return false;
    }
    
    public int getClasspathSectionsCount() {
        return this.classpathSectionsCount;
    }
    
    public List getCalledFileNames() {
        return this.calledFilesNames;
    }
}
