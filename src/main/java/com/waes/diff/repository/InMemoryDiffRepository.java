package com.waes.diff.repository;

import com.waes.diff.model.Diff;
import com.waes.diff.model.DiffData;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class InMemoryDiffRepository implements DiffRepository {

    private Map<Long, Diff> diffs = new ConcurrentHashMap<>();

    @Override
    public void saveRightDiff(Long id, DiffData diffData) {
        diffs.compute(id, (aLong, diff) -> {
            if(Objects.nonNull(diff)) {
                diff.setRight(diffData.getEncodedData());
                return diff;
            } else {
                return new Diff(id, diffData.getEncodedData(), null);
            }
        });
    }

    @Override
    public void saveLeftDiff(Long id, DiffData diffData) {
        diffs.compute(id, (aLong, diff) -> {
            if(Objects.nonNull(diff)) {
                diff.setLeft(diffData.getEncodedData());
                return diff;
            } else {
                return new Diff(id, null, diffData.getEncodedData());
            }
        });
    }

    @Override
    public Optional<Diff> getDiff(long l) {
        return Optional.ofNullable(diffs.get(l));
    }

    public void clear() {
        diffs.clear();
    }
}
