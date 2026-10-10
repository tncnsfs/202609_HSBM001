package com.hsbm.backend;

import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

// /api/items 요청을 처리. 데이터는 이제 메모리(store)가 아니라 PostgreSQL(service_item 테이블)
@RestController
@RequestMapping("/api/items")
public class ItemController {

    // DB 담당자. final + 생성자로 받으면 Spring이 MyBatis가 만든 구현체를 자동으로 넣어 줌 (생성자 주입)
    private final ServiceItemMapper mapper;

    public ItemController(ServiceItemMapper mapper) {
        this.mapper = mapper;
    }

    // 목록: SELECT ... ORDER BY id
    @GetMapping
    public List<ServiceItem> list() {
        return mapper.findAll();
    }

    // 1개 조회: 없으면 404
    @GetMapping("/{id}")
    public ServiceItem get(@PathVariable Long id) {
        return find(id);
    }

    // 등록: INSERT 후 DB가 매긴 새 id로 다시 조회해서 돌려줌 (201 Created)
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ServiceItem create(@Valid @RequestBody ServiceItem req) {
        Long newId = mapper.insert(req);
        return find(newId);
    }

    // 수정: 바뀐 줄이 0이면 그 id가 없다는 뜻 → 404
    @PutMapping("/{id}")
    public ServiceItem update(@PathVariable Long id, @Valid @RequestBody ServiceItem req) {
        // 주소의 id를 넣어서 새 ServiceItem을 만듦 (record는 값을 바꿀 수 없어서 새로 만듦)
        ServiceItem item = new ServiceItem(id, req.name(), req.category(), req.price());
        if (mapper.update(item) == 0) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        return find(id);
    }

    // 삭제: 지운 줄이 0이면 404, 성공이면 204 No Content
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        if (mapper.deleteById(id) == 0) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
    }

    // 도우미: DB에서 찾고, 없으면(null) 404
    private ServiceItem find(Long id) {
        ServiceItem item = mapper.findById(id);
        if (item == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        return item;
    }
}