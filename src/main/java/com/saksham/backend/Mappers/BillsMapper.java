package com.saksham.backend.Mappers;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import com.saksham.backend.Models.Bills;

@Mapper
public interface BillsMapper {

    @Select("SELECT * form bills where id = #{id}")
    Bills getBill(int id);

    @Insert("INSERT into bills (id) values (#{id})")
    void addBill(Number id);

    @Update("UPDATE bills SET roomRent += #{data.roomRent},foodBeverages += #{data.foodBeverages}, Other +=#{data.other}, cancellation +=#{data.cancellation} WHERE id = #{data.id}")
    void setBill(Bills data);
}
