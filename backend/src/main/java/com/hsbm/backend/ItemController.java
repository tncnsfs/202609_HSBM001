package com.hsbm.backend;

import jakarta.validation.Valid;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicLong;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

// ItemController.java : 설계표 그대로
// 재시작: 서버 터미널에서 Ctrl+C → ./mvnw spring-boot:run
// ④ 테스트: 다른 터미널에서 : curl localhost:8080/api/items

@RestController
@RequestMapping("/api/items")
public class ItemController {

    private final Map<Long, ServiceItem> store = new ConcurrentSkipListMap<>();
    private final AtomicLong seq = new AtomicLong();

    public ItemController() {
        add("에어컨 청소", "가전 청소", 50000);
        add("욕실 청소", "청소", 80000);
        add("이사 청소", "청소", 90000);
    }

    @GetMapping
    public List<ServiceItem> list() {
        return new ArrayList<>(store.values());
    }

    @GetMapping("/{id}")
    public ServiceItem get(@PathVariable Long id) {
        return find(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ServiceItem create(@Valid @RequestBody ServiceItem req) {
        return add(req.name(), req.category(), req.price());
    }

    @PutMapping("/{id}")
    public ServiceItem update(@PathVariable Long id, @Valid @RequestBody ServiceItem req) {
        find(id);
        ServiceItem item = new ServiceItem(id, req.name(), req.category(), req.price());
        store.put(id, item);
        return item;
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        find(id);
        store.remove(id);
    }

    private ServiceItem add(String name, String category, int price) {
        long id = seq.incrementAndGet();
        ServiceItem item = new ServiceItem(id, name, category, price);
        store.put(id, item);
        return item;
    }

    private ServiceItem find(Long id) {
        ServiceItem item = store.get(id);
        if (item == null) throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        return item;
    }
}