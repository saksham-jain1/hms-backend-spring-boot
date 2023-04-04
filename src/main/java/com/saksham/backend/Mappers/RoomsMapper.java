package com.saksham.backend.Mappers;

import java.time.LocalDate;
import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import com.saksham.backend.Models.Rooms;

@Mapper
public interface RoomsMapper {

    @Select("Select * from rooms where type=#{type} and hotelId=#{hotelId} and id not in (Select r.id from rooms as r,bookings as b,roombooked as rb WHERE r.id=rb.`roomID` and rb.`bookingId`=b.id and (b.`checkIn` BETWEEN CAST(#{checkIn} as Date) and #{checkOut} or b.`checkOut` BETWEEN CAST(#{checkIn} as Date) and #{checkOut}) and (`checkIn` is not null AND `checkOut` is not null)) LIMIT 1")
    Rooms getRoom(int hotelId, String type, LocalDate checkIn, LocalDate checkOut);

    @Select("Select * from rooms where type=#{type} and hotelId=#{hotelId} and id not in (Select r.id from rooms as r,bookings as b,roombooked as rb WHERE r.id=rb.`roomID` and rb.`bookingId`=b.id and (b.`checkIn` BETWEEN CAST(#{checkIn} as Date) and #{checkOut} or b.`checkOut` BETWEEN CAST(#{checkIn} as Date) and #{checkOut}) and (`checkIn` is not null AND `checkOut` is not null)) LIMIT ${n}")
    List<Rooms> getRooms(int hotelId, int n, String type, LocalDate checkIn, LocalDate checkOut);

    @Select("Select distinct type from rooms where hotelId = #{hotelId}")
    List<String> getAllTypes(int hotelId);
}
