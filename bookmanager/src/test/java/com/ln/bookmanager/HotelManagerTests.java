package com.ln.bookmanager;

import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.junit.Assert;
import org.junit.Test;

public class HotelManagerTests {

    @Test
    public void test() {
        HotelManager hotelManager = new HotelManager(10);
        String[] namePools = new String[]{"Anna", "Timmy", "Reeves", "Richard", "Job"};
        Date[] datePools = new Date[]{Date.valueOf(LocalDate.of(2022, 3, 1)),
                Date.valueOf(LocalDate.of(2022, 3, 2)),
                Date.valueOf(LocalDate.of(2022, 3, 3)),
                Date.valueOf(LocalDate.of(2022, 3, 4)),
                Date.valueOf(LocalDate.of(2022, 3, 5))};
        ExecutorService executorService = Executors.newFixedThreadPool(5);
        Random random = new Random();
        int times = 100;
        CountDownLatch countDownLatch = new CountDownLatch(times);
        int i = 0;
        while (i++ < times) {
            Integer num1 = random.nextInt(5);
            Integer num2 = random.nextInt(10);
            Integer num3 = random.nextInt(5);
            executorService.execute(() -> {
                try {
                    Booking randomBooking = new Booking(namePools[num1], num2, datePools[num3]);
                    hotelManager.storeBooking(randomBooking);
                } finally {
                    countDownLatch.countDown();
                }
            });
        }
        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        executorService.shutdown();
        System.out.println(hotelManager.findAvailableRoomList(Date.valueOf(LocalDate.of(2022, 3, 2))));
        System.out.println("Anna reserved success times: " + hotelManager.findBookList("Anna").size());
        System.out.println(hotelManager.findBookList("Anna"));
        System.out.println("Timmy reserved success times: " + hotelManager.findBookList("Timmy").size());
        System.out.println(hotelManager.findBookList("Timmy"));
        System.out.println("Reeves reserved success times: " + hotelManager.findBookList("Reeves").size());
        System.out.println(hotelManager.findBookList("Reeves"));
        System.out.println("Richard reserved success times: " + hotelManager.findBookList("Richard").size());
        System.out.println(hotelManager.findBookList("Richard"));
        System.out.println("Job reserved success times: " + hotelManager.findBookList("Job").size());
        System.out.println(hotelManager.findBookList("Job"));
    }

    @Test
    public void testBookingRepeat() {
        HotelManager hotelManager = new HotelManager(10);
        Booking testBooking = new Booking("Anna", 1, Date.valueOf(LocalDate.of(2022, 3, 2)));
        hotelManager.storeBooking(testBooking);
        hotelManager.storeBooking(testBooking);
        ArrayList<Booking> expect = new ArrayList<>();
        expect.add(testBooking);
        List<Booking> actual = hotelManager.findBookList("Anna");
        System.out.println(actual);
        Assert.assertTrue(expect.equals(actual));
    }

    @Test
    public void testBookingRoomOutOfRange() {
        HotelManager hotelManager = new HotelManager(10);
        Booking testBooking1 = new Booking("Anna", -1, Date.valueOf(LocalDate.of(2022, 3, 2)));
        Booking testBooking2 = new Booking("Anna", 10, Date.valueOf(LocalDate.of(2022, 3, 2)));
        hotelManager.storeBooking(testBooking1);
        hotelManager.storeBooking(testBooking2);
        List<Booking> actual = hotelManager.findBookList("Anna");
        Assert.assertNull(actual);
    }
}
