package com.waes.diff.controller;


import com.waes.diff.controller.exceptions.DataNotEncodedException;
import com.waes.diff.model.DiffData;
import com.waes.diff.model.DiffResult;
import com.waes.diff.service.DiffService;
import org.springframework.web.bind.annotation.*;

import java.util.regex.Pattern;

@RestController
@RequestMapping("/diff/{id}")
public class DiffController {

    private DiffService diffService;
    private static final Pattern BASE64_VALIDATION = Pattern.compile("^([A-Za-z0-9+/]{4})*([A-Za-z0-9+/]{3}=|[A-Za-z0-9+/]{2}==)?$");
    public DiffController(DiffService diffService) {
        this.diffService = diffService;
    }

    @PostMapping("/right")
    public void setRightDiff(@PathVariable("id") Long id,
            @RequestBody DiffData diffData) {
        validateBase64Data(diffData);
        diffService.saveRightDiff(id, diffData);
    }

    @PostMapping(value = "/left", consumes = {"application/json"})
    public void setLeftDiff(@PathVariable("id") Long id,
                             @RequestBody DiffData diffData) {
        validateBase64Data(diffData);
        diffService.saveLeftDiff(id, diffData);
    }

    private void validateBase64Data(@RequestBody DiffData diffData) {
        if(!BASE64_VALIDATION.matcher(diffData.getEncodedData()).matches()) {
            throw new DataNotEncodedException("Diff Data must be encoded in base64");
        }
    }

    @GetMapping(produces = {"application/json"})
    public DiffResult getDiffResult(@PathVariable("id") Long id) {
        return diffService.getDiffResult(id);
    }

}
