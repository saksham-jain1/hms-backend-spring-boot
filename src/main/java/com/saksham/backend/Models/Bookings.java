package com.saksham.backend.Models;

import java.time.LocalDate;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Bookings {
    private int id, userId, rooms, hotelId, amt;
    private String name,hname, email, address, phoneNo, aadharNo;
    private LocalDate checkIn, checkOut;
    private List<Integer> roomNos;
}
