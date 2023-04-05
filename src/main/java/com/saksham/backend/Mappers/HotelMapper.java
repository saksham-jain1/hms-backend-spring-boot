package com.saksham.backend.Mappers;

import java.time.LocalDate;
import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import com.saksham.backend.Models.Hotel;

@Mapper
public interface HotelMapper {

    @Select("SELECT h.*,r.price FROM hotels${db} as h,(SELECT `hotelId`,min(price) as price from rooms group BY `hotelId`) as r where h.id=r.`hotelId` and name like #{name} order by #{sort} LIMIT #{Limit}")
    List<Hotel> seachByName(String name, int Limit, String sort,String db);

    @Select("SELECT h.*,r.price FROM hotels${db} as h,(SELECT `hotelId`,min(price) as price from rooms group BY `hotelId`) as r where h.id=r.`hotelId` and state = #{state} order by #{sort} LIMIT #{Limit}")
    List<Hotel> seachByState(String state, int Limit, String sort,String db);

    @Select("SELECT h.*,r.price FROM hotels${db} as h,(SELECT `hotelId`,min(price) as price from rooms group BY `hotelId`) as r where h.id=r.`hotelId` and h.id = #{id}")
    Hotel seachById(int id,String db);

    @Select("SELECT h.*,r.price FROM hotels${db} as h,(SELECT `hotelId`,min(price) as price from rooms group BY `hotelId`) as r where h.id=r.`hotelId` order by ${sort} LIMIT #{Limit}")
    List<Hotel> seachAll(int Limit, String sort,String db);

    @Select("SELECT h.*,r.price FROM hotels${db} as h,(SELECT `hotelId`,min(price) as price from rooms group BY `hotelId`) as r where h.id=r.`hotelId` and state = #{state} and city =#{city} order by #{sort} LIMIT #{Limit}")
    List<Hotel> advanceStateSearch(String state, String city, int Limit, String sort,String db);

    @Select("SELECT DISTINCT(h.id),h.*,min(price) as price from hotels{db} h,rooms r where h.state = #{state} and h.city = #{city} and r.`hotelId` = h.id and r.id not in (Select r.id from rooms as r,bookings as b,roombooked as rb WHERE r.id=rb.`roomID` and rb.`bookingId`=b.id and (b.`checkIn` BETWEEN CAST(#{checkIn} as Date) and #{checkOut} or b.`checkOut` BETWEEN CAST(#{checkIn} as Date) and #{checkOut}) and (`checkIn` is not null AND `checkOut` is not null)) GROUP BY(h.id) order by ${sort} LIMIT ${Limit}")
    List<Hotel> advanceSearch(String state, String city, LocalDate checkIn, LocalDate checkOut, int Limit, String sort,String db);
}
