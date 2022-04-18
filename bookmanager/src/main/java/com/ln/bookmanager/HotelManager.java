package com.ln.bookmanager;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * @author lining
 * @date 2022/4/18
 **/
public class HotelManager {

    private int roomNum;

    private LinkedList<Booking> storage;

    private HashMap<String, ArrayList<Booking>> guestCache;

    private HashMap<Date, ArrayList<Integer>> bookingCache;

    private ReadWriteLock lock;

    private int i = 0;

    HotelManager(int num) {
        roomNum = num;
        storage = new LinkedList<>();
        guestCache = new HashMap<>();
        bookingCache = new HashMap<>();
        lock = new ReentrantReadWriteLock();
    }

    public void storeBooking(Booking book) {
        try {
            lock.writeLock().lock();
            //validate roomNo
            if (book.getRoomNo() > roomNum) {
                return;
            }
            //validate date
            ArrayList<Integer> rooms = bookingCache.get(book.getDate());
            if (rooms != null && rooms.contains(book.getRoomNo())) {
                return;
            }
            //store
            storage.add(book);
            //update cache
            ArrayList<Integer> roomList = bookingCache.getOrDefault(book.getDate(), new ArrayList<>());
            roomList.add(book.getRoomNo());
            bookingCache.put(book.getDate(), roomList);
            ArrayList<Booking> bookingList = guestCache.getOrDefault(book.getGuestName(), new ArrayList<>());
            bookingList.add(book);
            guestCache.put(book.getGuestName(), bookingList);
        } finally {
            lock.writeLock().unlock();
        }
    }

    public List<Integer> findAvailableRoomList(Date date) {
        try {
            lock.readLock().lock();
            List<Integer> ret = new ArrayList<>();
            for (Integer i = 1; i <= roomNum; i++) {
                if (!bookingCache.getOrDefault(date, new ArrayList<>()).contains(i)) {
                    ret.add(i);
                }
            }
            return ret;
        } finally {
            lock.readLock().unlock();
        }
    }

    public List<Booking> findBookList(String guestName) {
        try {
            lock.readLock().lock();
            return guestCache.get(guestName);
        } finally {
            lock.readLock().unlock();
        }
    }
}
