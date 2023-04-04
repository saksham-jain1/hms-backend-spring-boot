package com.saksham.backend.Mappers;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import com.saksham.backend.Models.Bookings;

@Mapper
public interface BookingsMapper {

    @Select("SELECT * from bookings where userId=#{userId}")
    List<Bookings> getBookings(int userId);

    @Select("SELECT * from bookings where id=#{bookingId}")
    Bookings getBooking(int bookingId);

    @Insert("Insert into bookings(name,userId,email,address,phoneNo,rooms,hotelId,checkIn,checkOut,aadharNo) VALUES (#{data.name},#{data.userID},#{data.email},#{data.address},#{data.phoneNo},#{data.rooms},#{data.hotelId},#{data.checkIn},#{data.checkOut},#{data.aadharNo})")
    @Options(useGeneratedKeys = true, keyProperty = "data.id")
    void addBooking(@Param("data") Map<String, Object> data);

    @Insert("Insert into roombooked(bookingId,roomID) values(#{bookingId},#{roomID})")
    void addDetails(Number bookingId, int roomID);

    @Delete("Delete From roombooked where bookingId = #{id}")
    void deleteIncompleteUpdate(Number id);

    @Delete("Delete From bookings where id = #{id}")
    void deleteIncompleteAdd(Number id);

    @Select("select id from bookings WHERE id in (SELECT `bookingId` FROM roombooked where `roomID` = #{i}) and `checkIn` NOT BETWEEN CAST(#{data.checkIn} as Date) and #{data.checkout} and `checkOut` NOT BETWEEN CAST(#{data.checkIn} as Date) and #{data.checkout} LIMIT 1")
    Integer checkAvailability(Integer i, Map<String, Object> data);

    @Update("Update bookings set checkIn = null,checkOut = null where id = #{id}")
    void cancel(int id);

    @Select("Select roomNo from rooms where id in (Select roomID from roombooked where bookingId == #{bookingId})")
    List<Integer> getRoomNo(int bookingId);

    @Select("Select roomNo from rooms where id in (Select roomID from roombooked where bookingId == #{bookingId}) group by price")
    int getPrice(int bookingId);
}
