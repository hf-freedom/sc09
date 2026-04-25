package com.logistics.service;

import com.logistics.entity.Branch;
import com.logistics.store.DataStore;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class BranchService {

    public List<Branch> list() {
        return DataStore.getAllBranches().stream()
                .sorted(Comparator.comparing(Branch::getId))
                .collect(Collectors.toList());
    }

    public Branch getById(Long id) {
        return DataStore.getBranchMap().get(id);
    }

    public Branch create(Branch branch) {
        branch.setId(DataStore.nextBranchId());
        if (branch.getStatus() == null) {
            branch.setStatus("active");
        }
        branch.setCreatedAt(LocalDateTime.now());
        branch.setUpdatedAt(LocalDateTime.now());
        DataStore.getBranchMap().put(branch.getId(), branch);
        return branch;
    }

    public Branch update(Long id, Branch branch) {
        Branch existing = DataStore.getBranchMap().get(id);
        if (existing == null) {
            return null;
        }
        if (branch.getName() != null) {
            existing.setName(branch.getName());
        }
        if (branch.getCoverageRegions() != null) {
            existing.setCoverageRegions(branch.getCoverageRegions());
        }
        if (branch.getStatus() != null) {
            existing.setStatus(branch.getStatus());
        }
        existing.setUpdatedAt(LocalDateTime.now());
        return existing;
    }

    public boolean delete(Long id) {
        return DataStore.getBranchMap().remove(id) != null;
    }
}
