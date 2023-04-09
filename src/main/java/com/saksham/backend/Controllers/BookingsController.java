package com.saksham.backend.Controllers;

import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.saksham.backend.Mappers.BookingsMapper;
import com.saksham.backend.Models.Bookings;

@RestController
@RequestMapping("/api/bookings")
public class BookingsController {

    @Autowired
    public BookingsMapper bookingsMapper;
    @Autowired
    private BillsController billsController;

    @GetMapping
    public ResponseEntity<?> getBooking(@RequestParam int bookingId, @RequestParam String db) {
        try {
            Bookings booking = bookingsMapper.getBooking(bookingId);
            if (booking == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No booking found");
            }
            booking.setRoomNos(bookingsMapper.getRoomNo(bookingId));
            booking.setHname(bookingsMapper.getHotelName(booking.getHotelId(), db));
            booking.setAmt(billsController.getBillAmt(bookingId));
            return ResponseEntity.ok(booking);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/all")
    public ResponseEntity<?> getBookings(@RequestParam int userId, @RequestParam String db) {
        try {
            List<Bookings> bookings;
            bookings = bookingsMapper.getBookings(userId);
            for (Bookings b : bookings) {
                b.setAmt(billsController.getBillAmt(b.getId()));
                b.setHname(bookingsMapper.getHotelName(b.getHotelId(), db));
            }
            if (bookings != null)
                return ResponseEntity.ok(bookings);
            return ResponseEntity.ok("No Previous Bookings");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping
    public Number addBooking(@RequestBody Map<String, Object> data) {
        try {
            List<Integer> roomID = new ArrayList<>((Collection<Integer>) data.get("roomID"));
            for (Integer i : roomID) {
                Integer id = bookingsMapper.checkAvailability(i, data);
                if (id != null)
                    return -1;
            }
            bookingsMapper.addBooking(data);
            for (Integer i : roomID) {
                bookingsMapper.addDetails((Number) data.get("id"), i);
            }
            billsController.addBill((Number) data.get("id"));
            return (Number) data.get("id");
        } catch (Exception e) {
            e.printStackTrace();
            bookingsMapper.deleteIncompleteUpdate((Number) data.get("id"));
            bookingsMapper.deleteIncompleteAdd((Number) data.get("id"));
            return -1;
        }
    }

    @GetMapping("/cancellation")
    public ResponseEntity<?> cancellation(@RequestParam int id) {
        try {
            Bookings bookings = bookingsMapper.getBooking(id);
            LocalDate today = LocalDate.now();
            bookingsMapper.cancel(id);
            if (bookings.getCheckIn() != null && bookings.getCheckOut() != null
                    && !today.isBefore(bookings.getCheckIn())) {
                int price = bookingsMapper.getPrice(id) * bookings.getRooms();
                Period period = Period.between(bookings.getCheckIn(), bookings.getCheckOut());
                billsController.updateBills(id, period.getDays() * price, 0, 0, 0);
            } else if (bookings.getCheckIn() != null && bookings.getCheckOut() != null)
                billsController.updateBills(id, 0, 0, 0, 200);
            return ResponseEntity.ok("Booking Cancelled");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/checkin")
    public ResponseEntity<?> checkIn(@RequestParam int id) {
        try {
            Bookings booking = bookingsMapper.getBooking(id);
            if (booking != null && booking.getCheckIn() != null && booking.getCheckOut() != null) {
                int price = bookingsMapper.getPrice(id);
                Period period = Period.between(booking.getCheckIn(), booking.getCheckOut());
                billsController.updateBills(id, period.getDays() * price, 0, 0, 0);
                return ResponseEntity.ok("Check in successfull");
            }
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Error Occured");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/checkout")
    public ResponseEntity<?> checkOut(@RequestParam int id) {
        try {
            Bookings booking = bookingsMapper.getBooking(id);
            Random rand = new Random();
            if (booking != null && booking.getCheckIn() != null && booking.getCheckOut() != null) {
                Period period = Period.between(booking.getCheckIn(), booking.getCheckOut());
                int foodBeverages = (rand.nextInt(201) + 400) * booking.getRooms() * period.getDays(),
                        other = (rand.nextInt(201) + 200) * booking.getRooms() * period.getDays();
                billsController.updateBills(id, 0, foodBeverages, other, 0);
                return ResponseEntity.ok("Check out successfull");
            }
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Error Occured");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

}
