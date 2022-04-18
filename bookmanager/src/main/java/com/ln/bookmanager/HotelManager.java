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

    //volatile avoid reading expire data
    private volatile HashMap<String, ArrayList<Booking>> guestCache;

    private volatile HashMap<Date, ArrayList<Integer>> bookingCache;

    //lock room by roomNo to reduce lock contention
    private HashMap<Integer, ReadWriteLock> lockMap;

    private int i = 0;

    HotelManager(int num) {
        roomNum = num;
        storage = new LinkedList<>();
        guestCache = new HashMap<>();
        bookingCache = new HashMap<>();
        lockMap = new HashMap<>();
        for (int i = 0; i < num; i++) {
            lockMap.put(i, new ReentrantReadWriteLock());
        }
    }

    public void storeBooking(Booking book) {
        //validate roomNo
        if (book.getRoomNo() >= roomNum || book.getRoomNo() < 0) {
            return;
        }
        try {
            lockMap.get(book.getRoomNo()).writeLock().lock();
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
            lockMap.get(book.getRoomNo()).writeLock().unlock();
        }
    }

    public List<Integer> findAvailableRoomList(Date date) {
        List<Integer> ret = new ArrayList<>();
        for (Integer i = 1; i <= roomNum; i++) {
            if (!bookingCache.getOrDefault(date, new ArrayList<>()).contains(i)) {
                ret.add(i);
            }
        }
        return ret;
    }

    public List<Booking> findBookList(String guestName) {
        return guestCache.get(guestName);
    }
}
