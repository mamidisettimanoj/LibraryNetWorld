package com.librarynet.controller;

import com.librarynet.dto.ApiMessage;
import com.librarynet.service.SampleDataService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin")
public class AdminController {
    private final SampleDataService data;

    public AdminController(SampleDataService data) { this.data = data; }

    @PostMapping("/reset-sample-data")
    public ApiMessage reset() {
        data.reset();
        return new ApiMessage("Sample data restored successfully");
    }
}
