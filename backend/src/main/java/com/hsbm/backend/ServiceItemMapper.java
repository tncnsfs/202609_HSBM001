package com.hsbm.backend;

import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

// @Mapper = MyBatis가 이 인터페이스의 구현체를 자동으로 만들어 줌
// 실제 SQL은 resources/mapper/ServiceItemMapper.xml에 있고, 메서드 이름 = XML의 id로 연결됨
@Mapper
public interface ServiceItemMapper {

    // 전체 목록 (GET /api/items)
    List<ServiceItem> findAll();

    // 1개 조회. 없으면 null (GET /api/items/{id})
    ServiceItem findById(@Param("id") Long id);

    // 등록 후 DB가 매긴 새 id를 돌려줌 (POST)
    Long insert(ServiceItem item);

    // 수정. 바뀐 줄 수를 돌려줌 (PUT)
    int update(ServiceItem item);

    // 삭제. 지운 줄 수를 돌려줌 (DELETE)
    int deleteById(@Param("id") Long id);
}