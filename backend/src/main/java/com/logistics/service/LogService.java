package com.logistics.service;

import com.logistics.entity.Courier;
import com.logistics.entity.DispatchLog;
import com.logistics.entity.Order;
import com.logistics.entity.StatusLog;
import com.logistics.store.DataStore;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class LogService {

    public List<DispatchLog> listDispatchLogs() {
        return DataStore.getAllDispatchLogs().stream()
                .sorted(Comparator.comparing(DispatchLog::getCreatedAt).reversed())
                .collect(Collectors.toList());
    }

    public List<StatusLog> listStatusLogs() {
        return DataStore.getAllStatusLogs().stream()
                .sorted(Comparator.comparing(StatusLog::getCreatedAt).reversed())
                .collect(Collectors.toList());
    }

    public void createDispatchLog(Order order, Courier courier, String action, String reason) {
        DispatchLog log = new DispatchLog();
        log.setId(DataStore.nextDispatchLogId());
        log.setOrderId(order.getId());
        log.setOrderNo(order.getOrderNo());
        if (courier != null) {
            log.setCourierId(courier.getId());
            log.setCourierName(courier.getName());
        }
        log.setAction(action);
        log.setReason(reason);
        log.setCreatedAt(LocalDateTime.now());
        DataStore.getDispatchLogMap().put(log.getId(), log);
    }

    public void createStatusLog(Order order, String fromStatus, String toStatus, String operator, String remark) {
        StatusLog log = new StatusLog();
        log.setId(DataStore.nextStatusLogId());
        log.setOrderId(order.getId());
        log.setOrderNo(order.getOrderNo());
        log.setFromStatus(fromStatus);
        log.setToStatus(toStatus);
        log.setOperator(operator);
        log.setRemark(remark);
        log.setCreatedAt(LocalDateTime.now());
        DataStore.getStatusLogMap().put(log.getId(), log);
    }
}
