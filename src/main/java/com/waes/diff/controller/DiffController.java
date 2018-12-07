package com.waes.diff.controller;


import com.waes.diff.model.DiffData;
import com.waes.diff.model.DiffResult;
import com.waes.diff.service.DiffService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/diff/{id}")
public class DiffController {

    private DiffService diffService;

    public DiffController(DiffService diffService) {
        this.diffService = diffService;
    }

    @PostMapping("/right")
    public void setRightDiff(@PathVariable("id") Long id,
            @RequestBody DiffData diffData) {
        diffService.saveRightDiff(id, diffData);
    }

    @PostMapping("/left")
    public void setLeftDiff(@PathVariable("id") Long id,
                             @RequestBody DiffData diffData) {
        diffService.saveLeftDiff(id, diffData);
    }

    @GetMapping
    public DiffResult getDiffResult(@PathVariable("id") Long id) {
        return diffService.getDiffResult(id);
    }

}
