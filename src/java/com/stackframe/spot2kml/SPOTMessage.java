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

/**
 * An encapsulation of the message returned by the SPOT service.
 *
 * @author mcculley
 */
class SPOTMessage {

    final String esn; // The DEVICE ESN field.
    final String esnName;
    final String messageType; // FIXME: Make into an enum.
    final long timeInGMTSecond;
    final double latitude, longitude;

    SPOTMessage(String esn, String esnName, String messageType, long timeInGMTSecond, double latitude, double longitude) {
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
