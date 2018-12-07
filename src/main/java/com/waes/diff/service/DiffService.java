package com.waes.diff.service;

import com.waes.diff.model.Diff;
import com.waes.diff.model.DiffData;
import com.waes.diff.model.DiffInsight;
import com.waes.diff.model.DiffResult;
import com.waes.diff.repository.DiffRepository;
import com.waes.diff.service.exceptions.IncompleteDiffException;
import org.springframework.stereotype.Service;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
public class DiffService {

    private DiffRepository diffRepository;
    private static final int BUCKET_SIZE  = 10;

    public DiffService(DiffRepository diffRepository) {
        this.diffRepository = diffRepository;
    }

    public void saveRightDiff(Long id, DiffData diffData) {
        diffRepository.saveRightDiff(id, diffData);
    }


    public void saveLeftDiff(Long id, DiffData diffData) {
        diffRepository.saveLeftDiff(id, diffData);
    }

    public DiffResult getDiffResult(Long diffId) {
        Diff diff = diffRepository.getDiff(diffId);

        if(Objects.isNull(diff.getRight())) {
            throw new IncompleteDiffException("Right part of the diff is null");
        }

        if(Objects.isNull(diff.getLeft())) {
            throw new IncompleteDiffException("Left part of the diff is null");
        }
        if(diff.getLeft().equals(diff.getRight())) {
            return DiffResult.equal(diffId);
        }
        if(diff.getLeft().length() != diff.getRight().length()) {
            return DiffResult.differentSize(diffId, diff.getRight().length() - diff.getLeft().length());
        }

        char[] leftData = diff.getLeft().toCharArray();
        char[] rightData = diff.getRight().toCharArray();
        // To improve performance of the diff, divide diff into buckets, so that each bucket can be compared in a different Thread
        int buckets = calculateBuckets(leftData);
        int lastBucketSize = calculateLastBucketSize(leftData);

        // Create completable futures that will execute in parallel all the diffs
        List<Integer> insights = IntStream.range(0, buckets)
                .mapToObj(i -> CompletableFuture.supplyAsync(() -> getDiffIndexes(i, leftData, rightData, BUCKET_SIZE)))
                .map(CompletableFuture::join)
                .flatMap(List::stream)
                .collect(Collectors.toList());

        // Execute last diff
        List<Integer> lastBucketInsights = getDiffIndexes(buckets, leftData, rightData, lastBucketSize);
        insights.addAll(lastBucketInsights);

        // Calculate diff insights (indexes + offsets)
        List<DiffInsight> diffInsights = calculateDiffInsights(insights);

        return DiffResult.sameSize(diffId, diffInsights);
    }

    private int calculateBuckets(char[] leftData) {
        return leftData.length / BUCKET_SIZE;
    }

    private int calculateLastBucketSize(char[] leftData) {
        return leftData.length % BUCKET_SIZE;
    }

    /**
     * Given the list of indexes where there are differences, calculates the offset for all the indexes
     *
     * @param insights, a list of all the indexes where there are differences
     * @return a list of DiffInsight which represents indexes + offsets
     */
    private List<DiffInsight> calculateDiffInsights(List<Integer> insights) {
        List<DiffInsight> diffInsights = new LinkedList<>();
        int offset = 1;
        int index = insights.get(0);
        for(int i = 0; i < insights.size() - 1; i++) {
            if(insights.get(i + 1) == insights.get(i) + 1) {
                offset++;
            } else {
                diffInsights.add(new DiffInsight(index, offset));
                index = insights.get(i+1);
                offset = 1;
            }
        }
        diffInsights.add(new DiffInsight(index, offset));
        return diffInsights;
    }

    private List<Integer> getDiffIndexes(int bucket, char[] leftData, char[] rightData, int bucketSize) {
        List<Integer> diffInsights = new LinkedList<>();
        for(int i = 0; i < bucketSize; i++) {
            if(leftData[i + bucket*BUCKET_SIZE] != rightData[i + bucket*BUCKET_SIZE]) {
                diffInsights.add(i + bucket*BUCKET_SIZE);
            }
        }
        return diffInsights;
    }
}
