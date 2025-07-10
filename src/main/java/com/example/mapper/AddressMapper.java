package com.example.mapper;

import com.example.entity.Address;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
 * 地址表的数据库操作接口
 * <p>
 * 通过继承 {@link BaseMapper<Address>}，该接口自动拥有了对 Address 表的常用 CRUD 操作能力，
 * 例如 insert, delete, update, selectById, selectList 等。
 * 无需在 XML 文件中编写对应的 SQL 语句。
 * </p>
 */
public interface AddressMapper extends BaseMapper<Address> {

}
