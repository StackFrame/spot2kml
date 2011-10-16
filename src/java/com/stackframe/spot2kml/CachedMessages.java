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

    private long lastRetrieve;
    private long lastUpdate;
    private static final Comparator<SPOTMessage> messageTimeComparator = new Comparator<SPOTMessage>() {

        public int compare(SPOTMessage t, SPOTMessage t1) {
            return (int) (t1.timeInGMTSecond - t.timeInGMTSecond);
        }
    };
    private final SortedSet<SPOTMessage> messages = new TreeSet<SPOTMessage>(messageTimeComparator);

    public synchronized void addAll(Collection<SPOTMessage> m) {
        messages.addAll(m);
        lastUpdate = System.currentTimeMillis();
    }

    public synchronized SortedSet<SPOTMessage> getMessages() {
        SortedSet<SPOTMessage> copy = new TreeSet<SPOTMessage>(messageTimeComparator);
        copy.addAll(messages);
        lastRetrieve = System.currentTimeMillis();
        return copy;
    }

    public long getLastRetrieve() {
        return lastRetrieve;
    }

    public long getLastUpdate() {
        return lastUpdate;
    }
}
