/*
 * Copyright 2011 StackFrame, LLC
 *
 * This is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License version 3
 * as published by the Free Software Foundation.
 *
 * You should have received a copy of the GNU General Public License
 * along with this file.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.stackframe.spot2kml;

import java.util.Collection;
import java.util.Comparator;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * A cache of SPOT messages.
 *
 * @author mcculley
 */
class CachedMessages {

    // FIXME: Need to cap the maximum number of messages kept.
    private long lastRetrieve;
    private long lastUpdate;
    private static final Comparator<SPOTMessage> messageTimeComparator = new Comparator<SPOTMessage>() {

        public int compare(SPOTMessage t, SPOTMessage t1) {
            return (int) (t1.timeInGMTSecond - t.timeInGMTSecond);
        }
    };
    private final SortedSet<SPOTMessage> messages = new TreeSet<SPOTMessage>(messageTimeComparator);

    /**
     * Add a Collection of SPOTMessage objects to this cache.
     *
     * @param m a Collection of SPOTMessage objects
     */
    synchronized void addAll(Collection<SPOTMessage> m) {
        messages.addAll(m);
        lastUpdate = System.currentTimeMillis();
    }

    /**
     * Returns a SortedSet of SPOTMessage objects. The messages are sorted such that the latest message is first and the oldest is last.
     *
     * @return the sorted messages
     */
    synchronized SortedSet<SPOTMessage> getMessages() {
        SortedSet<SPOTMessage> copy = new TreeSet<SPOTMessage>(messageTimeComparator);
        copy.addAll(messages);
        lastRetrieve = System.currentTimeMillis();
        return copy;
    }

    /**
     * Gets the last time the messages were retrieved, in milliseconds since midnight, January 1, 1970 UTC.
     *
     * @return the last time the messages were retrieved
     */
    long getLastRetrieve() {
        return lastRetrieve;
    }

    /**
     * Gets the last time the messages were updated, in milliseconds since midnight, January 1, 1970 UTC.
     *
     * @return the last time the messages were updated
     */
    long getLastUpdate() {
        return lastUpdate;
    }
}
