package com.saksham.backend.Models;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Rooms {
    private int id,roomNo,hotelId,price;
    private String type;
}
