package com.saksham.backend.Controllers;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.saksham.backend.Mappers.HotelMapper;
import com.saksham.backend.Models.Hotel;

@RestController
@RequestMapping("/api/hotel")
public class HotelController {
    @Autowired
    public HotelMapper hotelMapper;

    @GetMapping
    public ResponseEntity<?> search(@RequestParam(required = false) String name,
            @RequestParam(required = false, defaultValue = "0") int id,
            @RequestParam(required = false) String state,
            @RequestParam(required = false, defaultValue = "id asc") String sort,
            @RequestParam(required = false, defaultValue = "0,10") String Limit,
            @RequestParam(required = false, defaultValue = "") String db) {
        try {
            sort = URLDecoder.decode(sort, StandardCharsets.UTF_8);
            db = "_" + db;
            if (id != 0) {
                Hotel hotel;
                hotel = hotelMapper.seachById(id, db);
                if (hotel != null)
                    return ResponseEntity.ok(hotel);
            } else {
                List<Hotel> hotelList;
                if (name != null) {
                    String decoded = URLDecoder.decode(name, StandardCharsets.UTF_8);
                    hotelList = hotelMapper.seachByName("%" + decoded + "%", Limit, sort, db);
                } else if (state != null) {

                    String decoded = URLDecoder.decode(state, StandardCharsets.UTF_8);
                    hotelList = hotelMapper.seachByState(decoded, Limit, sort, db);
                } else {
                    hotelList = hotelMapper.seachAll(Limit, sort, db);
                }
                if (hotelList.size() != 0) {
                    return ResponseEntity.ok(hotelList);
                }
            }
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("hotels not found");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }

    }

    @GetMapping("/advanced")
    public ResponseEntity<?> advanceSearch(@RequestParam String state, @RequestParam String city,
            @RequestParam(required = false) LocalDate checkIn,
            @RequestParam(required = false) LocalDate checkOut,
            @RequestParam(required = false, defaultValue = "id asc") String sort,
            @RequestParam(required = false, defaultValue = "0,10") String Limit,
            @RequestParam(required = false, defaultValue = "") String db) {
        try {
            db = "_" + db;
            state = URLDecoder.decode(state, StandardCharsets.UTF_8);
            city = URLDecoder.decode(city, StandardCharsets.UTF_8);
            List<Hotel> hotelList;
            if (checkOut != null && checkIn != null) {
                hotelList = hotelMapper.advanceSearch(state, city, checkIn, checkOut, Limit, sort, db);
            } else {
                hotelList = hotelMapper.advanceStateSearch(state, city, Limit, sort, db);
            }
            if (hotelList.size() != 0) {
                return ResponseEntity.ok(hotelList);
            }
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No hotels found");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PutMapping
    public ResponseEntity<?> updateHotel(@RequestBody List<Hotel> hotels) {
        try {
            hotelMapper.updateHotel(hotels.get(0), "_en");
            hotelMapper.updateHotel(hotels.get(1), "_hi");
            return ResponseEntity.ok("Updated Successfully");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Plz Try again");
        }
    }

    @PostMapping
    public ResponseEntity<?> addHotel(@RequestBody List<Hotel> hotels) {
        try {
            hotelMapper.addHotel(hotels.get(0), "_en");
            hotelMapper.addHotel(hotels.get(1), "_hi");
            return ResponseEntity.ok(hotels.get(0).getId());
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Plz Try again");
        }
    }
}
