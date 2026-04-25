package com.logistics.service;

import com.logistics.entity.Courier;
import com.logistics.entity.DispatchLog;
import com.logistics.entity.Order;
import com.logistics.entity.StatusLog;
import com.logistics.enums.OrderStatusEnum;
import com.logistics.store.DataStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class OrderService {

    @Autowired
    private CourierService courierService;

    @Autowired
    private LogService logService;

    public List<Order> list() {
        return DataStore.getAllOrders().stream()
                .sorted(Comparator.comparing(Order::getId).reversed())
                .collect(Collectors.toList());
    }

    public Order getById(Long id) {
        return DataStore.getOrderMap().get(id);
    }

    public Order create(Order order) {
        order.setId(DataStore.nextOrderId());
        order.setOrderNo(generateOrderNo());
        order.setStatus(OrderStatusEnum.PENDING_ASSIGN.getCode());
        order.setCreatedAt(LocalDateTime.now());
        order.setUpdatedAt(LocalDateTime.now());
        DataStore.getOrderMap().put(order.getId(), order);

        logService.createStatusLog(order, null, OrderStatusEnum.PENDING_ASSIGN.getCode(), "系统", "订单创建");

        tryDispatch(order);

        return order;
    }

    private String generateOrderNo() {
        return "OD" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"))
                + String.format("%04d", (int) (Math.random() * 10000));
    }

    private void tryDispatch(Order order) {
        Courier bestCourier = courierService.findBestCourier(
                order.getRegion(),
                order.getUrgent(),
                order.getLastFailedCourierId()
        );

        if (bestCourier != null) {
            assignOrder(order, bestCourier);
        } else {
            logService.createDispatchLog(order, null, "待分配", "无可用快递员，订单进入待分配状态");
        }
    }

    private void assignOrder(Order order, Courier courier) {
        boolean success = courierService.incrementTaskCount(courier.getId());
        if (!success) {
            return;
        }

        String oldStatus = order.getStatus();
        order.setCourierId(courier.getId());
        order.setCourierName(courier.getName());
        order.setStatus(OrderStatusEnum.ASSIGNED.getCode());
        order.setUpdatedAt(LocalDateTime.now());

        logService.createStatusLog(order, oldStatus, OrderStatusEnum.ASSIGNED.getCode(), "系统", "分配给快递员: " + courier.getName());
        logService.createDispatchLog(order, courier, "派单", "自动派单成功");
    }

    public Order acceptOrder(Long orderId) {
        Order order = DataStore.getOrderMap().get(orderId);
        if (order == null) {
            return null;
        }
        if (!OrderStatusEnum.ASSIGNED.getCode().equals(order.getStatus())) {
            return null;
        }

        String oldStatus = order.getStatus();
        order.setStatus(OrderStatusEnum.ACCEPTED.getCode());
        order.setUpdatedAt(LocalDateTime.now());

        logService.createStatusLog(order, oldStatus, OrderStatusEnum.ACCEPTED.getCode(), order.getCourierName(), "快递员接单");
        logService.createDispatchLog(order, courierService.getById(order.getCourierId()), "接单", "快递员确认接单");

        return order;
    }

    public Order pickUp(Long orderId) {
        Order order = DataStore.getOrderMap().get(orderId);
        if (order == null) {
            return null;
        }
        if (!OrderStatusEnum.ACCEPTED.getCode().equals(order.getStatus())) {
            return null;
        }

        String oldStatus = order.getStatus();
        order.setStatus(OrderStatusEnum.PICKING_UP.getCode());
        order.setUpdatedAt(LocalDateTime.now());

        logService.createStatusLog(order, oldStatus, OrderStatusEnum.PICKING_UP.getCode(), order.getCourierName(), "快递员取件");
        logService.createDispatchLog(order, courierService.getById(order.getCourierId()), "取件", "快递员取件中");

        return order;
    }

    public Order startDelivery(Long orderId) {
        Order order = DataStore.getOrderMap().get(orderId);
        if (order == null) {
            return null;
        }
        if (!OrderStatusEnum.PICKING_UP.getCode().equals(order.getStatus())) {
            return null;
        }

        String oldStatus = order.getStatus();
        order.setStatus(OrderStatusEnum.DELIVERING.getCode());
        order.setUpdatedAt(LocalDateTime.now());

        logService.createStatusLog(order, oldStatus, OrderStatusEnum.DELIVERING.getCode(), order.getCourierName(), "开始配送");
        logService.createDispatchLog(order, courierService.getById(order.getCourierId()), "配送", "快递员开始配送");

        return order;
    }

    public Order sign(Long orderId) {
        Order order = DataStore.getOrderMap().get(orderId);
        if (order == null) {
            return null;
        }
        if (!OrderStatusEnum.DELIVERING.getCode().equals(order.getStatus())) {
            return null;
        }

        String oldStatus = order.getStatus();
        order.setStatus(OrderStatusEnum.SIGNED.getCode());
        order.setUpdatedAt(LocalDateTime.now());

        if (order.getCourierId() != null) {
            courierService.decrementTaskCount(order.getCourierId());
        }

        logService.createStatusLog(order, oldStatus, OrderStatusEnum.SIGNED.getCode(), order.getCourierName(), "订单签收");
        logService.createDispatchLog(order, courierService.getById(order.getCourierId()), "签收", "订单已签收");

        return order;
    }

    public Order fail(Long orderId, String failReason) {
        Order order = DataStore.getOrderMap().get(orderId);
        if (order == null) {
            return null;
        }
        if (!OrderStatusEnum.DELIVERING.getCode().equals(order.getStatus())
                && !OrderStatusEnum.PICKING_UP.getCode().equals(order.getStatus())) {
            return null;
        }

        String oldStatus = order.getStatus();
        order.setStatus(OrderStatusEnum.FAILED.getCode());
        order.setFailReason(failReason);
        order.setLastFailedCourierId(order.getCourierId());
        order.setUpdatedAt(LocalDateTime.now());

        if (order.getCourierId() != null) {
            courierService.decrementTaskCount(order.getCourierId());
        }

        logService.createStatusLog(order, oldStatus, OrderStatusEnum.FAILED.getCode(), order.getCourierName(), "配送失败: " + failReason);
        logService.createDispatchLog(order, courierService.getById(order.getCourierId()), "失败", "配送失败，原因: " + failReason);

        return order;
    }

    public Order redispatch(Long orderId) {
        Order order = DataStore.getOrderMap().get(orderId);
        if (order == null) {
            return null;
        }
        if (!OrderStatusEnum.FAILED.getCode().equals(order.getStatus())
                && !OrderStatusEnum.PENDING_ASSIGN.getCode().equals(order.getStatus())) {
            return null;
        }

        String oldStatus = order.getStatus();
        order.setStatus(OrderStatusEnum.PENDING_ASSIGN.getCode());
        order.setCourierId(null);
        order.setCourierName(null);
        order.setFailReason(null);
        order.setUpdatedAt(LocalDateTime.now());

        logService.createStatusLog(order, oldStatus, OrderStatusEnum.PENDING_ASSIGN.getCode(), "系统", "重新派单");
        logService.createDispatchLog(order, null, "重新派单", "订单重新进入派单队列，排除快递员ID: " + order.getLastFailedCourierId());

        tryDispatch(order);

        return order;
    }
}
