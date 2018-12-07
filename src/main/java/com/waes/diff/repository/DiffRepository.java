package com.waes.diff.repository;

import com.waes.diff.model.Diff;
import com.waes.diff.model.DiffData;

public interface DiffRepository {
    void saveRightDiff(Long id, DiffData diffData);

    void saveLeftDiff(Long id, DiffData diffData);

    Diff getDiff(long l);
}
