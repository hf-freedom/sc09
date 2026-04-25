package com.logistics.controller;

import com.logistics.common.Result;
import com.logistics.entity.DispatchLog;
import com.logistics.entity.StatusLog;
import com.logistics.service.LogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/logs")
public class LogController {

    @Autowired
    private LogService logService;

    @GetMapping("/dispatch")
    public Result<List<DispatchLog>> listDispatchLogs() {
        return Result.success(logService.listDispatchLogs());
    }

    @GetMapping("/status")
    public Result<List<StatusLog>> listStatusLogs() {
        return Result.success(logService.listStatusLogs());
    }

    @GetMapping
    public Result<Map<String, Object>> listAllLogs() {
        Map<String, Object> result = new HashMap<>();
        result.put("dispatchLogs", logService.listDispatchLogs());
        result.put("statusLogs", logService.listStatusLogs());
        return Result.success(result);
    }
}
