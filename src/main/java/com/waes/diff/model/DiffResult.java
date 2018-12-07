package com.waes.diff.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Collections;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DiffResult {

    private Long id;
    private DiffType diffType;
    private Integer differenceInSize;
    private List<DiffInsight> insights;

    public static DiffResult equal(Long diffId) {
        return new DiffResult(diffId, DiffType.EQUAL, 0, Collections.emptyList());
    }

    public static DiffResult differentSize(Long diffId, int sizeDifference) {
        return new DiffResult(diffId, DiffType.DIFFERENT_SIZE, sizeDifference, Collections.emptyList());
    }

    public static DiffResult sameSize(Long diffId, List<DiffInsight> insights) {
        return new DiffResult(diffId, DiffType.SAME_SIZE, 0, insights);
    }

    public enum DiffType {
        EQUAL, SAME_SIZE, DIFFERENT_SIZE
    }
}
