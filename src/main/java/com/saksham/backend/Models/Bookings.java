package com.saksham.backend.Models;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Bookings {
    private int id,userId,rooms;
    private String name,email,address,phoneNo,aadharNo;
    private LocalDate checkIn,checkOut;
}
