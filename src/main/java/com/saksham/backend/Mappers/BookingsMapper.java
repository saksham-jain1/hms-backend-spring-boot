package com.saksham.backend.Mappers;

import java.util.List;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;

import com.saksham.backend.Models.Bookings;

@Mapper
public interface BookingsMapper {
    
    @Select("SELECT * from bookings where userId=#{userId}")
    List<Bookings> getBookings(int userId);

    @Insert("Insert into bookings(name,userId,email,address,phoneNo,rooms,checkIn,checkOut)")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void addBooking();

    @Insert("Insert into roombooked(bookingId,roomID) values(#{bookingId},#{roomID})")
    void addDetails(int bookingId,int roomID);
}
