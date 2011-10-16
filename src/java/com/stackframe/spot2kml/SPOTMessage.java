/*
 * Copyright 2011 StackFrame, LLC
 * All rights reserved.
 */
package com.stackframe.spot2kml;

/**
 *
 * @author mcculley
 */
public class SPOTMessage {

    public final String esn; // The DEVICE ESN field.
    public final String esnName;
    public final String messageType; // FIXME: Make into an enum.
    public final long timeInGMTSecond;
    public final double latitude, longitude;

    public SPOTMessage(String esn, String esnName, String messageType, long timeInGMTSecond, double latitude, double longitude) {
        this.esn = esn;
        this.esnName = esnName;
        this.messageType = messageType;
        this.timeInGMTSecond = timeInGMTSecond;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    @Override
    public String toString() {
        return "SPOTMessage{" + "esn=" + esn + ", esnName=" + esnName + ", messageType=" + messageType + ", timeInGMTSecond=" + timeInGMTSecond + ", latitude=" + latitude + ", longitude=" + longitude + '}';
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final SPOTMessage other = (SPOTMessage) obj;
        if ((this.esn == null) ? (other.esn != null) : !this.esn.equals(other.esn)) {
            return false;
        }
        if ((this.messageType == null) ? (other.messageType != null) : !this.messageType.equals(other.messageType)) {
            return false;
        }
        if (this.timeInGMTSecond != other.timeInGMTSecond) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 17 * hash + (this.esn != null ? this.esn.hashCode() : 0);
        hash = 17 * hash + (this.messageType != null ? this.messageType.hashCode() : 0);
        hash = 17 * hash + (int) (this.timeInGMTSecond ^ (this.timeInGMTSecond >>> 32));
        return hash;
    }

}
