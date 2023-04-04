package com.saksham.backend.Controllers;

import java.time.LocalDate;
import java.time.Period;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
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

    @GetMapping
    public ResponseEntity<?> getBooking(@RequestParam int bookingId) {
        try {
            Bookings booking = bookingsMapper.getBooking(bookingId);
            booking.setRoomNos(bookingsMapper.getRoomNo(bookingId));
            return ResponseEntity.ok(booking);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/all")
    public ResponseEntity<?> getBookings(@RequestParam int userId) {
        try {
            List<Bookings> bookings;
            LocalDate now = LocalDate.now();
            bookings = bookingsMapper.getBookings(userId);
            for (Bookings b : bookings) {
                BillsController billsController = new BillsController();
                if (b.getCheckIn() != null && b.getCheckOut() != null && now.isAfter(b.getCheckOut()))
                    b.setAmt(billsController.getBillAmt(b.getId()));
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
    public boolean addBooking(@RequestParam Map<String, Object> data, @RequestParam List<Integer> roomID) {
        try {
            for (Integer i : roomID) {
                Integer id = bookingsMapper.checkAvailability(i, data);
                if (id != null)
                    return false;
            }
            bookingsMapper.addBooking(data);
            for (Integer i : roomID) {
                bookingsMapper.addDetails((Number) data.get("id"), i);
            }
            BillsController billController = new BillsController();
            billController.addBill((Number) data.get("id"));
            return true;
        } catch (Exception e) {
            bookingsMapper.deleteIncompleteUpdate((Number) data.get("id"));
            bookingsMapper.deleteIncompleteAdd((Number) data.get("id"));
            e.printStackTrace();
            return false;
        }
    }

    @GetMapping("/cancellation")
    public ResponseEntity<?> cancellation(@RequestParam int id) {
        try {
            bookingsMapper.cancel(id);
            BillsController billController = new BillsController();
            billController.updateBills(id);
            return ResponseEntity.ok("Booking Cancelled");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/checkin")
    public ResponseEntity<?> checkIn(@RequestParam int id, @RequestParam int hotelId) {
        try {
            Bookings booking = bookingsMapper.getBooking(id);
            if (booking != null) {
                int price = bookingsMapper.getPrice(id) * booking.getRooms();
                Period period = Period.between(booking.getCheckIn(), booking.getCheckOut());
                BillsController billController = new BillsController();
                billController.updateBills(id, period.getDays() * price);
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
            if (booking != null) {
                Period period = Period.between(booking.getCheckIn(), booking.getCheckOut());
                BillsController billController = new BillsController();
                int foodBeverages = (rand.nextInt(201) + 400) * booking.getRooms() * period.getDays(),
                        other = (rand.nextInt(201) + 200) * booking.getRooms() * period.getDays();
                billController.updateBills(id, foodBeverages, other);
                return ResponseEntity.ok("Check out successfull");
            }
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Error Occured");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

}
