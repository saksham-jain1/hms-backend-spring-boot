package com.saksham.backend.Models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Hotel {
    private int id,price;
    private String name, description, address, city, state, location, imageUrl;
    private float rating;
}
