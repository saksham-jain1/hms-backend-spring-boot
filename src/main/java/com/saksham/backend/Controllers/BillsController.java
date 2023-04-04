package com.saksham.backend.Controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.saksham.backend.Mappers.BillsMapper;
import com.saksham.backend.Models.Bills;

@RestController
@RequestMapping("/api/bills")
public class BillsController {

    @Autowired
    BillsMapper billsMapper;

    @GetMapping
    public ResponseEntity<?> getBill(@RequestParam int id) {
        try {
            Bills bill = billsMapper.getBill(id);
            if(bill==null)
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Please try again");
            int subtotal = bill.getRoomRent() + bill.getFoodBeverages() + bill.getOther() + bill.getCancellation();
            bill.setSubtotal(subtotal);
            bill.setTaxes((subtotal*14)/100);
            bill.setTotal(bill.getSubtotal()+bill.getTaxes());
            return ResponseEntity.ok(bill);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Some error occured");
        }
    }

    public boolean addBill(Number id)
    {
        try {
            billsMapper.addBill(id);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public void updateBills(int id){
        try {
            Bills bill = new Bills();
            bill.setCancellation(200);
            bill.setId(id);
            billsMapper.setBill(bill);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void updateBills(int id,int roomRent){
        try {
            Bills bill = new Bills();
            bill.setRoomRent(roomRent);
            bill.setId(id);
            billsMapper.setBill(bill);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void updateBills(int id,int foodBeverages,int other){
        try {
            Bills bill = new Bills();
            bill.setFoodBeverages(foodBeverages);
            bill.setOther(other);
            bill.setId(id);
            billsMapper.setBill(bill);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public int getBillAmt(int id){
        Bills bill = billsMapper.getBill(id);
            int subtotal = bill.getRoomRent() + bill.getFoodBeverages() + bill.getOther() + bill.getCancellation();
            bill.setSubtotal(subtotal);
            bill.setTaxes((subtotal*14)/100);
            bill.setTotal(bill.getSubtotal()+bill.getTaxes());
            return bill.getTotal();
    }
}
