package com.saksham.backend.Controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.saksham.backend.Mappers.RoomsMapper;
import com.saksham.backend.Models.Rooms;
import java.time.LocalDate;

@RestController
@RequestMapping("/api/rooms")
public class RoomsController {
    @Autowired
    public RoomsMapper roomsMapper;

    @GetMapping
    public ResponseEntity<?> searchAvailability(@RequestParam int hotelId,
            @RequestParam(required = false, defaultValue = "1") int n,
            @RequestParam LocalDate checkIn,@RequestParam LocalDate checkOut,
            @RequestParam String type) {
        try {
            if (n == 1) {
                Rooms room;
                room = roomsMapper.getRoom(hotelId, type, checkIn, checkOut);
                if (room != null)
                    return ResponseEntity.ok(room);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Room not available");
            }
            List<Rooms> rooms;
            rooms = roomsMapper.getRooms(hotelId, n, type, checkIn, checkOut);
            if (rooms.size() == n)
                return ResponseEntity.ok(rooms);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Only " + rooms.size() + " Rooms not available");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }

    }

    @GetMapping("/types")
    public List<String> getAllTypes(@RequestParam int hotelId) {
        return roomsMapper.getAllTypes(hotelId);
    }
}
