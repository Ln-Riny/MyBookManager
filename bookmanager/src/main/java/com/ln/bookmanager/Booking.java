package com.ln.bookmanager;

import java.util.Date;
import java.util.Objects;

/**
 * @author lining
 * @date 2022/4/18
 **/
public class Booking {
    private String guestName;
    private Integer roomNo;
    private Date date;

    public Booking(String guestName, Integer roomNo, Date date) {
        this.guestName = guestName;
        this.roomNo = roomNo;
        this.date = date;
    }

    public String getGuestName() {
        return guestName;
    }

    public Integer getRoomNo() {
        return roomNo;
    }

    public Date getDate() {
        return date;
    }

    @Override
    public int hashCode() {
        return Objects.hash(guestName, roomNo, date);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        Booking that = (Booking) obj;
        return guestName == that.guestName && roomNo.equals(that.roomNo) && date == that.date;
    }

    @Override
    public String toString() {
        return "guestName: " + guestName + ", "
                + "roomNo: " + roomNo.toString() + ", "
                + "date: " + date.toString() + "\n";
    }
}

