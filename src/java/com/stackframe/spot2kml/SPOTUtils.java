/*
 * Copyright 2011 StackFrame, LLC
 * All rights reserved.
 */
package com.stackframe.spot2kml;

import java.util.concurrent.TimeUnit;

/**
 *
 * @author mcculley
 */
class SPOTUtils {

    static final long refreshLimit = TimeUnit.MILLISECONDS.convert(15, TimeUnit.MINUTES); // Minimum time in minutes between updates.

    private SPOTUtils() {
        // inhibit construction
    }
}
