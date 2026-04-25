package com.logistics.controller;

import com.logistics.common.Result;
import com.logistics.entity.Courier;
import com.logistics.service.CourierService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/couriers")
public class CourierController {

    @Autowired
    private CourierService courierService;

    @GetMapping
    public Result<List<Courier>> list() {
        return Result.success(courierService.list());
    }

    @GetMapping("/{id}")
    public Result<Courier> getById(@PathVariable Long id) {
        Courier courier = courierService.getById(id);
        if (courier == null) {
            return Result.error("快递员不存在");
        }
        return Result.success(courier);
    }

    @PostMapping
    public Result<Courier> create(@RequestBody Courier courier) {
        return Result.success(courierService.create(courier));
    }

    @PutMapping("/{id}")
    public Result<Courier> update(@PathVariable Long id, @RequestBody Courier courier) {
        Courier updated = courierService.update(id, courier);
        if (updated == null) {
            return Result.error("快递员不存在");
        }
        return Result.success(updated);
    }

    @DeleteMapping("/{id}")
    public Result<Boolean> delete(@PathVariable Long id) {
        boolean success = courierService.delete(id);
        if (!success) {
            return Result.error("快递员不存在");
        }
        return Result.success(true);
    }
}
