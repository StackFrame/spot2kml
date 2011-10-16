/*
 * Copyright 2011 StackFrame, LLC
 * All rights reserved.
 */
package com.stackframe.spot2kml;

import java.util.Collection;
import java.util.Comparator;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 *
 * @author mcculley
 */
public class CachedMessages {

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
    public synchronized void addAll(Collection<SPOTMessage> m) {
        messages.addAll(m);
        lastUpdate = System.currentTimeMillis();
    }

    /**
     * Returns a SortedSet of SPOTMessage objects. The messages are sorted such that the latest message is first and the oldest is last.
     *
     * @return the sorted messages
     */
    public synchronized SortedSet<SPOTMessage> getMessages() {
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
    public long getLastRetrieve() {
        return lastRetrieve;
    }

    /**
     * Gets the last time the messages were updated, in milliseconds since midnight, January 1, 1970 UTC.
     *
     * @return the last time the messages were updated
     */
    public long getLastUpdate() {
        return lastUpdate;
    }
}
