package com.saksham.backend.Mappers;

import java.time.LocalDate;
import java.util.List;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import com.saksham.backend.Models.Hotel;

@Mapper
public interface HotelMapper {

    @Select("SELECT h.*,r.price FROM hotels${db} as h,(SELECT `hotelId`,min(price) as price from rooms group BY `hotelId`) as r where h.id=r.`hotelId` and name like #{name} order by #{sort} LIMIT ${Limit}")
    List<Hotel> seachByName(String name, String Limit, String sort, String db);

    @Select("SELECT h.*,r.price FROM hotels${db} as h,(SELECT `hotelId`,min(price) as price from rooms group BY `hotelId`) as r where h.id=r.`hotelId` and state = #{state} order by #{sort} LIMIT ${Limit}")
    List<Hotel> seachByState(String state, String Limit, String sort, String db);

    @Select("SELECT h.*,r.price FROM hotels${db} as h,(SELECT `hotelId`,min(price) as price from rooms group BY `hotelId`) as r where h.id=r.`hotelId` and h.id = #{id}")
    Hotel seachById(int id, String db);

    @Select("SELECT h.*,r.price FROM hotels${db} as h,(SELECT `hotelId`,min(price) as price from rooms group BY `hotelId`) as r where h.id=r.`hotelId` order by ${sort} LIMIT ${Limit}")
    List<Hotel> seachAll(String Limit, String sort, String db);

    @Select("SELECT h.*,r.price FROM hotels${db} as h,(SELECT `hotelId`,min(price) as price from rooms group BY `hotelId`) as r where h.id=r.`hotelId` and state = #{state} and city =#{city} order by #{sort} LIMIT ${Limit}")
    List<Hotel> advanceStateSearch(String state, String city, String Limit, String sort, String db);

    @Select("SELECT DISTINCT(h.id),h.*,min(price) as price from hotels{db} h,rooms r where h.state = #{state} and h.city = #{city} and r.`hotelId` = h.id and r.id not in (Select r.id from rooms as r,bookings as b,roombooked as rb WHERE r.id=rb.`roomID` and rb.`bookingId`=b.id and (b.`checkIn` BETWEEN CAST(#{checkIn} as Date) and #{checkOut} or b.`checkOut` BETWEEN CAST(#{checkIn} as Date) and #{checkOut}) and (`checkIn` is not null AND `checkOut` is not null)) GROUP BY(h.id) order by ${sort} LIMIT ${Limit}")
    List<Hotel> advanceSearch(String state, String city, LocalDate checkIn, LocalDate checkOut, String Limit,
            String sort,
            String db);

    @Insert("INSERT INTO hotels${db} (name,description,city,state,address,location,rating,imageUrl) VALUES(#{hotel.name},#{hotel.description},#{hotel.city},#{hotel.state},#{hotel.address},#{hotel.location},#{hotel.rating},#{hotel.imageUrl})")
    @Options(useGeneratedKeys = true, keyProperty = "hotel.id")
    int addHotel(Hotel hotel, String db);

    @Update("Update hotels${db} set name=#{hotel.name},description=#{hotel.description},city = #{hotel.city},state=#{hotel.state},address=#{hotel.address},location=#{hotel.location},imageUrl=#{hotel.imageUrl} where id = #{hotel.id}")
    void updateHotel(Hotel hotel, String db);
}
