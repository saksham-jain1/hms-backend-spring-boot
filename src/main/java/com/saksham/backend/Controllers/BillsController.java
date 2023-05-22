package com.saksham.backend.Controllers;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import com.saksham.backend.Mappers.BillsMapper;
import com.saksham.backend.Models.Bills;

@RestController
@RequestMapping("/api/bills")
public class BillsController {

    @Autowired
    public BillsMapper billsMapper;

    @GetMapping
    public ResponseEntity<?> getBill(@RequestParam int id) {
        try {
            Bills bill = billsMapper.getBill(id);
            if (bill == null)
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Please try again");
            int subtotal = bill.getRoomRent() + bill.getFoodBeverages() + bill.getOther();
            bill.setSubtotal(subtotal);
            bill.setTaxes((subtotal * 14) / 100);
            bill.setTotal(bill.getSubtotal() + bill.getTaxes() + bill.getCancellation());
            return ResponseEntity.ok(bill);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Some error occured");
        }
    }

    @PostMapping("/create_order")
    public ResponseEntity<?> createOrder(@RequestParam int id) {
        try {
            int amt = getBillAmt(id);

            var client = new RazorpayClient("rzp_test_SJbSE1ULGg8Kqg", "CY3E379plxtIUuQhomelNMVq");

            JSONObject obj = new JSONObject();
            obj.put("amount", amt * 100);
            obj.put("currency", "INR");
            obj.put("receipt", "txn_" + id);

            Order order = client.Orders.create(obj);

            return ResponseEntity.ok(order.toString());
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Some error occured");
        }
    }

    @PutMapping
    public ResponseEntity<?> updateOrder(@RequestParam int id,@RequestParam String trnx) {
        try {
            billsMapper.updateBill(id,trnx);
            return ResponseEntity.ok("done");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Some error occured");
        }
    }

    public boolean addBill(Number id) {
        try {
            billsMapper.addBill(id);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public void updateBills(int id, int roomRent, int foodBeverages, int other, int cancellation) {
        try {
            Bills bill = billsMapper.getBill(id);
            if (bill.getRoomRent() == 0 && bill.getCancellation() == 0)
                bill.setRoomRent(roomRent);
            else
                bill.setRoomRent(0);
            if (bill.getFoodBeverages() == 0 && bill.getCancellation() == 0)
                bill.setFoodBeverages(foodBeverages);
            else
                bill.setFoodBeverages(0);
            if (bill.getOther() == 0 && bill.getCancellation() == 0)
                bill.setOther(other);
            else
                bill.setOther(0);
            if (bill.getCancellation() == 0 && bill.getRoomRent() == 0)
                bill.setCancellation(cancellation);
            else
                bill.setCancellation(0);
            billsMapper.setBill(bill);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public int getBillAmt(int id) {
        Bills bill = billsMapper.getBill(id);
        int subtotal = bill.getRoomRent() + bill.getFoodBeverages() + bill.getOther();
        bill.setSubtotal(subtotal);
        bill.setTaxes((subtotal * 14) / 100);
        bill.setTotal(bill.getSubtotal() + bill.getTaxes() + bill.getCancellation());
        return bill.getTotal();
    }
}
