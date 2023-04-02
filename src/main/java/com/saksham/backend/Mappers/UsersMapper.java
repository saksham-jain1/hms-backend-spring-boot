package com.saksham.backend.Mappers;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;

import com.saksham.backend.Models.Users;

@Mapper
public interface UsersMapper {
    @Select("SELECT * FROM users WHERE email = #{email}")
    Users findByEmail(String email);

    @Insert("INSERT INTO users(name,email,password,type) VALUES(#{name},#{email},#{password},#{type})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void addUser(Users user);
  }
