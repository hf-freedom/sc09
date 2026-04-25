package com.logistics.controller;

import com.logistics.common.Result;
import com.logistics.entity.Branch;
import com.logistics.service.BranchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/branches")
public class BranchController {

    @Autowired
    private BranchService branchService;

    @GetMapping
    public Result<List<Branch>> list() {
        return Result.success(branchService.list());
    }

    @GetMapping("/{id}")
    public Result<Branch> getById(@PathVariable Long id) {
        Branch branch = branchService.getById(id);
        if (branch == null) {
            return Result.error("网点不存在");
        }
        return Result.success(branch);
    }

    @PostMapping
    public Result<Branch> create(@RequestBody Branch branch) {
        return Result.success(branchService.create(branch));
    }

    @PutMapping("/{id}")
    public Result<Branch> update(@PathVariable Long id, @RequestBody Branch branch) {
        Branch updated = branchService.update(id, branch);
        if (updated == null) {
            return Result.error("网点不存在");
        }
        return Result.success(updated);
    }

    @DeleteMapping("/{id}")
    public Result<Boolean> delete(@PathVariable Long id) {
        boolean success = branchService.delete(id);
        if (!success) {
            return Result.error("网点不存在");
        }
        return Result.success(true);
    }
}
