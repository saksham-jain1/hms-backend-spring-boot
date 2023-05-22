package com.saksham.backend.Mappers;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import com.saksham.backend.Models.Bills;

@Mapper
public interface BillsMapper {

    @Select("SELECT * from bills where id = #{id}")
    Bills getBill(int id);

    @Insert("INSERT into bills (id) values (#{id})")
    void addBill(Number id);

    @Update("UPDATE bills SET roomRent = roomRent + #{roomRent},foodBeverages = foodBeverages + #{foodBeverages}, Other = Other + #{other}, cancellation = cancellation + #{cancellation} WHERE id = #{id}")
    void setBill(Bills data);

    @Update("UPDATE bills SET status = 'paid', trnx_id=#{trnx_id} where id = #{id}")
    void updateBill(int id,String trnx_id);
}
