//
// $Id: ConfigUtil.java,v 1.1 2001/02/15 01:09:57 mdb Exp $

package com.samskivert.util;

import java.io.InputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * The config util class provides routines for loading configuration
 * information out of a file that lives somewhere in the classpath.
 */
public class ConfigUtil
{
    /**
     * Loads a properties file from the named file that exists somewhere
     * in the classpath. A full path should be supplied, but variations
     * including and not including a leading slash will be used because
     * JVMs differ on their opinion of whether this is necessary.
     *
     * <p> The classloader that loaded the <code>ConfigUtil</code> class
     * is searched first, followed by the system classpath. If you wish to
     * provide an additional classloader, use the version of this function
     * that takes a classloader as an argument.
     *
     * @param path The path to the properties file, relative to the root
     * of the classpath entry from which it will be loaded
     * (e.g. /conf/foo.properties or perhaps just bar.properties).
     */
    public static Properties loadProperties (String path)
	throws IOException
    {
	return loadProperties(path, ConfigUtil.class.getClassLoader());
    }

    /**
     * Loads a properties file from the named file that exists somewhere
     * in the classpath. A full path should be supplied, but variations
     * including and not including a leading slash will be used because
     * JVMs differ on their opinion of whether this is necessary.
     *
     * <p> The supplied classloader is searched first, followed by the
     * system classloader.
     *
     * @param path The path to the properties file, relative to the root
     * of the classpath entry from which it will be loaded
     * (e.g. /conf/foo.properties or perhaps just bar.properties).
     */
    public static Properties loadProperties (String name, ClassLoader loader)
	throws IOException
    {
	return null;
    }

    /**
     * Returns an input stream referencing a file that exists somewhere in
     * the classpath. A full path (relative to the classpath directories)
     * should be supplied, but variations including and not including a
     * leading slash will be used because JVMs differ on their opinion of
     * whether this is necessary.
     *
     * <p> The classloader that loaded the <code>ConfigUtil</code> class
     * is searched first, followed by the system classpath. If you wish to
     * provide an additional classloader, use the version of this function
     * that takes a classloader as an argument.
     *
     * @param path The path to the file, relative to the root of the
     * classpath directory from which it will be loaded
     * (e.g. /conf/foo.gif or perhaps just bar.gif if the file is at the
     * top level).
     */
    public static InputStream getStream (String path)
    {
	return getStream(path, ConfigUtil.class.getClassLoader());
    }

    /**
     * Returns an input stream referencing a file that exists somewhere in
     * the classpath. A full path (relative to the classpath directories)
     * should be supplied, but variations including and not including a
     * leading slash will be used because JVMs differ on their opinion of
     * whether this is necessary.
     *
     * <p> The supplied classloader is searched first, followed by the
     * system classloader.
     *
     * @param path The path to the file, relative to the root of the
     * classpath directory from which it will be loaded
     * (e.g. /conf/foo.gif or perhaps just bar.gif if the file is at the
     * top level).
     */
    public static InputStream getStream (String path, ClassLoader loader)
    {
	// first try the supplied class loader
	InputStream in = loader.getResourceAsStream(path);
	if (in == null) {
	    // if that didn't work, try the system classloader
            in = Class.class.getResourceAsStream(path);
	}
	return in;
    }
}
