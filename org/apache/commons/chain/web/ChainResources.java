package org.apache.commons.chain.web;

import java.util.List;
import java.util.ArrayList;
import javax.servlet.ServletContext;
import org.apache.commons.chain.Catalog;
import java.net.URL;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.chain.config.ConfigParser;

final class ChainResources
{
    static void parseClassResources(final String resources, final ConfigParser parser) {
        if (resources == null) {
            return;
        }
        final Log log = LogFactory.getLog(ChainResources.class);
        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        if (loader == null) {
            loader = ChainResources.class.getClassLoader();
        }
        final String[] paths = getResourcePaths(resources);
        String path = null;
        try {
            for (int i = 0; i < paths.length; ++i) {
                path = paths[i];
                final URL url = loader.getResource(path);
                if (url == null) {
                    throw new IllegalStateException("Missing chain config resource '" + path + "'");
                }
                if (log.isDebugEnabled()) {
                    log.debug((Object)("Loading chain config resource '" + path + "'"));
                }
                parser.parse(url);
            }
        }
        catch (final Exception e) {
            throw new RuntimeException("Exception parsing chain config resource '" + path + "': " + e.getMessage());
        }
    }
    
    static void parseClassResources(final Catalog catalog, final String resources, final ConfigParser parser) {
        if (resources == null) {
            return;
        }
        final Log log = LogFactory.getLog(ChainResources.class);
        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        if (loader == null) {
            loader = ChainResources.class.getClassLoader();
        }
        final String[] paths = getResourcePaths(resources);
        String path = null;
        try {
            for (int i = 0; i < paths.length; ++i) {
                path = paths[i];
                final URL url = loader.getResource(path);
                if (url == null) {
                    throw new IllegalStateException("Missing chain config resource '" + path + "'");
                }
                if (log.isDebugEnabled()) {
                    log.debug((Object)("Loading chain config resource '" + path + "'"));
                }
                parser.parse(catalog, url);
            }
        }
        catch (final Exception e) {
            throw new RuntimeException("Exception parsing chain config resource '" + path + "': " + e.getMessage());
        }
    }
    
    static void parseWebResources(final ServletContext context, final String resources, final ConfigParser parser) {
        if (resources == null) {
            return;
        }
        final Log log = LogFactory.getLog(ChainResources.class);
        final String[] paths = getResourcePaths(resources);
        String path = null;
        try {
            for (int i = 0; i < paths.length; ++i) {
                path = paths[i];
                final URL url = context.getResource(path);
                if (url == null) {
                    throw new IllegalStateException("Missing chain config resource '" + path + "'");
                }
                if (log.isDebugEnabled()) {
                    log.debug((Object)("Loading chain config resource '" + path + "'"));
                }
                parser.parse(url);
            }
        }
        catch (final Exception e) {
            throw new RuntimeException("Exception parsing chain config resource '" + path + "': " + e.getMessage());
        }
    }
    
    static void parseWebResources(final Catalog catalog, final ServletContext context, final String resources, final ConfigParser parser) {
        if (resources == null) {
            return;
        }
        final Log log = LogFactory.getLog(ChainResources.class);
        final String[] paths = getResourcePaths(resources);
        String path = null;
        try {
            for (int i = 0; i < paths.length; ++i) {
                path = paths[i];
                final URL url = context.getResource(path);
                if (url == null) {
                    throw new IllegalStateException("Missing chain config resource '" + path + "'");
                }
                if (log.isDebugEnabled()) {
                    log.debug((Object)("Loading chain config resource '" + path + "'"));
                }
                parser.parse(catalog, url);
            }
        }
        catch (final Exception e) {
            throw new RuntimeException("Exception parsing chain config resource '" + path + "': " + e.getMessage());
        }
    }
    
    static String[] getResourcePaths(String resources) {
        final List paths = new ArrayList();
        if (resources != null) {
            int comma;
            while ((comma = resources.indexOf(44)) >= 0) {
                final String path = resources.substring(0, comma).trim();
                if (path.length() > 0) {
                    paths.add(path);
                }
                resources = resources.substring(comma + 1);
            }
            resources = resources.trim();
            if (resources.length() > 0) {
                paths.add(resources);
            }
        }
        return paths.toArray(new String[0]);
    }
}
