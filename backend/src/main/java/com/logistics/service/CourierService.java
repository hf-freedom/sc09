package com.logistics.service;

import com.logistics.entity.Courier;
import com.logistics.enums.CourierStatusEnum;
import com.logistics.store.DataStore;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CourierService {

    public List<Courier> list() {
        return DataStore.getAllCouriers().stream()
                .sorted(Comparator.comparing(Courier::getId))
                .collect(Collectors.toList());
    }

    public Courier getById(Long id) {
        return DataStore.getCourierMap().get(id);
    }

    public Courier create(Courier courier) {
        courier.setId(DataStore.nextCourierId());
        courier.setCurrentTaskCount(0);
        if (courier.getMaxTaskCount() == null) {
            courier.setMaxTaskCount(5);
        }
        if (courier.getStatus() == null) {
            courier.setStatus(CourierStatusEnum.IDLE.getCode());
        }
        courier.setCreatedAt(LocalDateTime.now());
        courier.setUpdatedAt(LocalDateTime.now());
        DataStore.getCourierMap().put(courier.getId(), courier);
        return courier;
    }

    public Courier update(Long id, Courier courier) {
        Courier existing = DataStore.getCourierMap().get(id);
        if (existing == null) {
            return null;
        }
        if (courier.getName() != null) {
            existing.setName(courier.getName());
        }
        if (courier.getRegion() != null) {
            existing.setRegion(courier.getRegion());
        }
        if (courier.getMaxTaskCount() != null) {
            existing.setMaxTaskCount(courier.getMaxTaskCount());
        }
        if (courier.getStatus() != null) {
            existing.setStatus(courier.getStatus());
        }
        existing.setUpdatedAt(LocalDateTime.now());
        return existing;
    }

    public boolean delete(Long id) {
        return DataStore.getCourierMap().remove(id) != null;
    }

    public boolean incrementTaskCount(Long courierId) {
        Courier courier = DataStore.getCourierMap().get(courierId);
        if (courier == null) {
            return false;
        }
        synchronized (courier) {
            if (courier.getCurrentTaskCount() >= courier.getMaxTaskCount()) {
                return false;
            }
            courier.setCurrentTaskCount(courier.getCurrentTaskCount() + 1);
            if (courier.getCurrentTaskCount() > 0) {
                courier.setStatus(CourierStatusEnum.BUSY.getCode());
            }
            courier.setUpdatedAt(LocalDateTime.now());
        }
        return true;
    }

    public boolean decrementTaskCount(Long courierId) {
        Courier courier = DataStore.getCourierMap().get(courierId);
        if (courier == null) {
            return false;
        }
        synchronized (courier) {
            if (courier.getCurrentTaskCount() <= 0) {
                return false;
            }
            courier.setCurrentTaskCount(courier.getCurrentTaskCount() - 1);
            if (courier.getCurrentTaskCount() == 0) {
                courier.setStatus(CourierStatusEnum.IDLE.getCode());
            }
            courier.setUpdatedAt(LocalDateTime.now());
        }
        return true;
    }

    public Courier findBestCourier(String region, Boolean isUrgent, Long excludedCourierId) {
        List<Courier> availableCouriers = DataStore.getAllCouriers().stream()
                .filter(c -> !CourierStatusEnum.OFFLINE.getCode().equals(c.getStatus()))
                .filter(c -> c.getCurrentTaskCount() < c.getMaxTaskCount())
                .filter(c -> !Objects.equals(c.getId(), excludedCourierId))
                .collect(Collectors.toList());

        if (availableCouriers.isEmpty()) {
            return null;
        }

        List<Courier> sameRegionCouriers = availableCouriers.stream()
                .filter(c -> region.equals(c.getRegion()))
                .collect(Collectors.toList());

        List<Courier> targetCouriers = sameRegionCouriers.isEmpty() ? availableCouriers : sameRegionCouriers;

        if (isUrgent != null && isUrgent) {
            Optional<Courier> idleCourier = targetCouriers.stream()
                    .filter(c -> CourierStatusEnum.IDLE.getCode().equals(c.getStatus()))
                    .min(Comparator.comparing(Courier::getCurrentTaskCount));
            if (idleCourier.isPresent()) {
                return idleCourier.get();
            }
        }

        return targetCouriers.stream()
                .min(Comparator.comparing(Courier::getCurrentTaskCount))
                .orElse(null);
    }
}
