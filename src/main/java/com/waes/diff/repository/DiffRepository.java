package com.waes.diff.repository;

import com.waes.diff.model.Diff;
import com.waes.diff.model.DiffData;

import java.util.Optional;

public interface DiffRepository {
    void saveRightDiff(Long id, DiffData diffData);

    void saveLeftDiff(Long id, DiffData diffData);

    Optional<Diff> getDiff(long l);
}
