package com.logistics.controller;

import com.logistics.common.Result;
import com.logistics.entity.Order;
import com.logistics.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @GetMapping
    public Result<List<Order>> list() {
        return Result.success(orderService.list());
    }

    @GetMapping("/{id}")
    public Result<Order> getById(@PathVariable Long id) {
        Order order = orderService.getById(id);
        if (order == null) {
            return Result.error("订单不存在");
        }
        return Result.success(order);
    }

    @PostMapping
    public Result<Order> create(@RequestBody Order order) {
        if (order.getReceiverAddress() == null || order.getReceiverAddress().isEmpty()) {
            return Result.error("收货地址不能为空");
        }
        if (order.getRegion() == null || order.getRegion().isEmpty()) {
            return Result.error("区域不能为空");
        }
        return Result.success(orderService.create(order));
    }

    @PostMapping("/{id}/accept")
    public Result<Order> accept(@PathVariable Long id) {
        Order order = orderService.acceptOrder(id);
        if (order == null) {
            return Result.error("订单状态不支持接单");
        }
        return Result.success(order);
    }

    @PostMapping("/{id}/pick-up")
    public Result<Order> pickUp(@PathVariable Long id) {
        Order order = orderService.pickUp(id);
        if (order == null) {
            return Result.error("订单状态不支持取件");
        }
        return Result.success(order);
    }

    @PostMapping("/{id}/start-delivery")
    public Result<Order> startDelivery(@PathVariable Long id) {
        Order order = orderService.startDelivery(id);
        if (order == null) {
            return Result.error("订单状态不支持开始配送");
        }
        return Result.success(order);
    }

    @PostMapping("/{id}/sign")
    public Result<Order> sign(@PathVariable Long id) {
        Order order = orderService.sign(id);
        if (order == null) {
            return Result.error("订单状态不支持签收");
        }
        return Result.success(order);
    }

    @PostMapping("/{id}/fail")
    public Result<Order> fail(@PathVariable Long id, @RequestBody Map<String, String> body) {
        String reason = body.get("reason");
        if (reason == null || reason.isEmpty()) {
            return Result.error("失败原因不能为空");
        }
        Order order = orderService.fail(id, reason);
        if (order == null) {
            return Result.error("订单状态不支持标记失败");
        }
        return Result.success(order);
    }

    @PostMapping("/{id}/redispatch")
    public Result<Order> redispatch(@PathVariable Long id) {
        Order order = orderService.redispatch(id);
        if (order == null) {
            return Result.error("订单状态不支持重新派单");
        }
        return Result.success(order);
    }
}
