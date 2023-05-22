package com.saksham.backend.Mappers;

import java.time.LocalDateTime;
import java.util.List;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import com.saksham.backend.Models.Otp;
import com.saksham.backend.Models.Users;

@Mapper
public interface UsersMapper {
  @Select("SELECT * FROM users WHERE email = #{email}")
  Users findByEmail(String email);

  @Insert("INSERT INTO users(name,email,password,type,img) VALUES(#{name},#{email},#{password},#{type},#{img})")
  @Options(useGeneratedKeys = true, keyProperty = "id")
  void addUser(Users user);

  @Update("UPDATE users set password = #{password} where email = #{email}")
  void changePassword(String password, String email);

  @Insert("INSERT INTO otp(email,create_time,otp) VALUES(#{email},#{time},#{otp})")
  void setOtp(String email, String otp, String time);

  @Update("UPDATE otp set otp = #{otp},create_time=#{time} where email = #{email}")
  void updateOtp(String email, String otp, String time);

  @Select("Select * FROM otp where email = #{email}")
  Otp getOtp(String email);

  @Update("UPDATE users set ${field} = #{value} where email = #{email}")
  void update(String email, String field, String value);

  @Delete("DELETE FROM users where email = #{email}")
  void deleteUser(String email);

  @Select("SELECT * FROM users where type = #{hotelID}")
  List<Users> getAllStaf(int hotelID);

}
