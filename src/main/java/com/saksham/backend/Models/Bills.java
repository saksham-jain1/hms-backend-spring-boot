package com.saksham.backend.Models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Bills {
    private int id,roomRent=0,foodBeverages=0,other=0,cancellation=0,total,subtotal,taxes;
}
