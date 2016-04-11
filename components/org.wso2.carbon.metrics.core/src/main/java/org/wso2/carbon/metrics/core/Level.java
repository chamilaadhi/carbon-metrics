/*
 * Copyright 2014 WSO2 Inc. (http://wso2.org)
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.wso2.carbon.metrics.core;

import java.util.Locale;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * <p> This class defines a set of standard levels to be used in Metrics. This class is similar to
 * org.apache.logging.log4j.Level in Apache Log4j 2 project. </p> <p> Levels are organized from most specific to least:
 * </p> <ul> <li>{@link #OFF} (most specific, no metrics)</li> <li>{@link #INFO}</li> <li>{@link #DEBUG}</li> <li>{@link
 * #TRACE} (least specific, a lot of data)</li> <li>{@link #ALL} (least specific, all data)</li> </ul>
 */
public class Level implements Comparable<Level> {

    private final String name;

    private final int intLevel;

    private static final ConcurrentMap<String, Level> levels = new ConcurrentHashMap<String, Level>();

    /**
     * No events will be used for metrics.
     */
    public static final Level OFF;

    /**
     * A metric for informational purposes.
     */
    public static final Level INFO;

    /**
     * A general debugging metric.
     */
    public static final Level DEBUG;

    /**
     * A fine-grained metric
     */
    public static final Level TRACE;

    /**
     * All metrics should be enabled.
     */
    public static final Level ALL;

    static {
        OFF = new Level("OFF", 0);
        INFO = new Level("INFO", 400);
        DEBUG = new Level("DEBUG", 500);
        TRACE = new Level("TRACE", 600);
        ALL = new Level("ALL", Integer.MAX_VALUE);
    }

    private Level(final String name, final int intLevel) {
        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException("Illegal null Level constant");
        }
        if (intLevel < 0) {
            throw new IllegalArgumentException("Illegal Level int less than zero.");
        }
        this.name = name;
        this.intLevel = intLevel;
        if (levels.putIfAbsent(name, this) != null) {
            throw new IllegalStateException("Level " + name + " has already been defined.");
        }
    }

    @Override
    public int compareTo(final Level other) {
        return intLevel < other.intLevel ? -1 : (intLevel > other.intLevel ? 1 : 0);
    }

    @Override
    public boolean equals(final Object other) {
        return other instanceof Level && other == this;
    }

    @Override
    public int hashCode() {
        return this.name.hashCode();
    }

    /**
     * Gets the symbolic name of this Level. Equivalent to calling {@link #toString()}.
     *
     * @return the name of this Level.
     */
    public String name() {
        return this.name;
    }

    @Override
    public String toString() {
        return this.name;
    }

    /**
     * Return the Level associated with the name or null if the Level cannot be found.
     *
     * @param name The name of the Level.
     * @return The Level or null.
     */
    public static Level getLevel(final String name) {
        return levels.get(name);
    }

    /**
     * Converts the string passed as argument to a level. If the conversion fails, then this method returns {@link
     * #DEBUG}.
     *
     * @param sArg The name of the desired Level.
     * @return The Level associated with the String.
     */
    public static Level toLevel(final String sArg) {
        return toLevel(sArg, Level.DEBUG);
    }

    /**
     * Converts the string passed as argument to a level. If the conversion fails, then this method returns the value of
     * <code>defaultLevel</code>.
     *
     * @param name         The name of the desired Level.
     * @param defaultLevel The Level to use if the String is invalid.
     * @return The Level associated with the String.
     */
    public static Level toLevel(final String name, final Level defaultLevel) {
        if (name == null) {
            return defaultLevel;
        }
        final Level level = levels.get(name.toUpperCase(Locale.ENGLISH));
        return level == null ? defaultLevel : level;
    }

    /**
     * Return the Level associated with the name.
     *
     * @param name The name of the Level to return.
     * @return The Level.
     * @throws java.lang.NullPointerException     if the Level name is {@code null}.
     * @throws java.lang.IllegalArgumentException if the Level name is not registered.
     */
    public static Level valueOf(final String name) {
        if (name == null) {
            throw new NullPointerException("No level name given.");
        }
        final String levelName = name.toUpperCase();
        if (levels.containsKey(levelName)) {
            return levels.get(levelName);
        }
        throw new IllegalArgumentException("Unknown level constant [" + levelName + "].");
    }
}
