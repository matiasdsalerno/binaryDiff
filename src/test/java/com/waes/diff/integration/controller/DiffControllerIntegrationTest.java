package com.waes.diff.integration.controller;

import com.waes.diff.controller.DiffController;
import com.waes.diff.controller.exceptions.DataNotEncodedException;
import com.waes.diff.model.Diff;
import com.waes.diff.model.DiffData;
import com.waes.diff.model.DiffInsight;
import com.waes.diff.model.DiffResult;
import com.waes.diff.repository.InMemoryDiffRepository;
import com.waes.diff.service.exceptions.DiffNotFoundException;
import com.waes.diff.service.exceptions.IncompleteDiffException;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import static org.hamcrest.CoreMatchers.is;

import java.util.Arrays;
import java.util.Base64;
import java.util.Collections;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertEquals;
import static org.mockito.BDDMockito.given;

@RunWith(SpringRunner.class)
@SpringBootTest
public class DiffControllerIntegrationTest {

    static private final String ENCODED_DATA = Base64.getEncoder().encodeToString("encoded Data".getBytes());
    static private final String ENCODED_DATA_3OFFSET = Base64.getEncoder().encodeToString("encodedDATAA".getBytes());
    static private final String ENCODED_DATA_2 = Base64.getEncoder().encodeToString("encoded Data 2".getBytes());
    static private final String ENCODED_DATA_LONG_DATA_SET = Base64.getEncoder().encodeToString("encoded Data 2Data 2Data 2Data 2Data 2Data 2Data 2Data 2Data 2Data 2Data 2Data 2Data 2Data 2Data 2Data 2Data 2Data 2Data 2Data 2".getBytes());
    static private final String ENCODED_DATA_LONG_DATA_SET_MANY_DIFFS = Base64.getEncoder().encodeToString("encoded Data 2Data 2Data 2Data 2Data 4Data 2Data 2Data 2Data 3Data 3Data 2Data 2Data 2Data 2Data 2Data 2Data 2Data 2Data 2Data 2".getBytes());


    @Autowired
    private DiffController diffController;

    @Autowired
    private InMemoryDiffRepository inMemoryDiffRepository;

    @After
    public void tearDown() {
        inMemoryDiffRepository.clear();
    }

    @Test
    public void testGetDiffResult() {
        diffController.setRightDiff(1L, new DiffData(ENCODED_DATA));
        diffController.setLeftDiff(1L, new DiffData(ENCODED_DATA));

        DiffResult diffResult = diffController.getDiffResult(1L);

        assertThat(diffResult.getDiffType(), is(DiffResult.DiffType.EQUAL));
    }


    @Test(expected = DiffNotFoundException.class)
    public void testWhenDiffNotFound_GetDiffResult_ThrowsException() {
        diffController.getDiffResult(1L);
    }


    @Test(expected = IncompleteDiffException.class)
    public void testWhenDiffIncompleteFromLeft_GetDiffResult_ThrowsException() {
        diffController.setRightDiff(1L, new DiffData(ENCODED_DATA));
        diffController.getDiffResult(1L);
    }


    @Test(expected = DataNotEncodedException.class)
    public void testDiffDataNotEncodedFromRight_GetDiffResult_ThrowsException() {
        diffController.setRightDiff(1L, new DiffData("Hello!"));
    }


    @Test(expected = DataNotEncodedException.class)
    public void testDiffDataNotEncodedFromLeft_GetDiffResult_ThrowsException() {
        diffController.setLeftDiff(1L, new DiffData("Hello!"));
    }

    @Test(expected = IncompleteDiffException.class)
    public void testWhenDiffIncompleteFromRight_GetDiffResult_ThrowsException() {
        diffController.setLeftDiff(1L, new DiffData(ENCODED_DATA));
        diffController.getDiffResult(1L);
    }

    @Test
    public void testWhenDiffNotSameSize_GetDiffResult_ShowsDifferentSize() {
        diffController.setLeftDiff(1L, new DiffData(ENCODED_DATA));
        diffController.setRightDiff(1L, new DiffData(ENCODED_DATA_2));
        DiffResult diffResult = diffController.getDiffResult(1L);

        assertThat(diffResult.getDiffType(), is(DiffResult.DiffType.DIFFERENT_SIZE));
    }

    @Test
    public void testWhenDiffHasSameSizeAndLongDataSetAndManyDiffs_GetDiffResult_ShowsInsightsOnDiff() {

        diffController.setRightDiff(1L, new DiffData(ENCODED_DATA_LONG_DATA_SET));
        diffController.setLeftDiff(1L, new DiffData(ENCODED_DATA_LONG_DATA_SET_MANY_DIFFS));

        DiffResult diffResult = diffController.getDiffResult(1L);

        Assert.assertThat(diffResult.getDiffType(), is(DiffResult.DiffType.SAME_SIZE));
        Assert.assertThat(diffResult.getInsights(), is(
                Arrays.asList(new DiffInsight(50, 1),
                        new DiffInsight(82, 1),
                        new DiffInsight(90, 1))));

    }

    @Test
    public void testWhenDiffHasSameSizeAnd3offset_GetDiffResult_ShowsInsightsOnDiff() {

        diffController.setRightDiff(1L, new DiffData(ENCODED_DATA));
        diffController.setLeftDiff(1L, new DiffData(ENCODED_DATA_3OFFSET));

        DiffResult diffResult = diffController.getDiffResult(1L);

        Assert.assertThat(diffResult.getDiffType(), is(DiffResult.DiffType.SAME_SIZE));
        Assert.assertThat(diffResult.getInsights(), is(Collections.singletonList(new DiffInsight(9, 7))));

    }

}
