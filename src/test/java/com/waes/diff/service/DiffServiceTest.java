package com.waes.diff.service;

import com.waes.diff.model.Diff;
import com.waes.diff.model.DiffInsight;
import com.waes.diff.model.DiffResult;
import com.waes.diff.repository.DiffRepository;
import com.waes.diff.service.exceptions.DiffNotFoundException;
import com.waes.diff.service.exceptions.IncompleteDiffException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.Base64;
import java.util.Collections;
import java.util.Optional;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;
import static org.mockito.BDDMockito.given;

@RunWith(MockitoJUnitRunner.class)
public class DiffServiceTest {

    static private final String ENCODED_DATA = Base64.getEncoder().encodeToString("encoded Data".getBytes());
    static private final String ENCODED_DATA_DIFF_AT_END = Base64.getEncoder().encodeToString("encoded DatA".getBytes());
    static private final String ENCODED_DATA_3OFFSET = Base64.getEncoder().encodeToString("encodedDATAA".getBytes());
    static private final String ENCODED_DATA_DIFF_AT_BEGINNING = Base64.getEncoder().encodeToString("Encoded Data".getBytes());
    static private final String ENCODED_DATA_DIFF_IN_MIDDLE = Base64.getEncoder().encodeToString("encoded data".getBytes());
    static private final String ENCODED_DATA_2 = Base64.getEncoder().encodeToString("encoded Data 2".getBytes());
    static private final String ENCODED_DATA_LONG_DATA_SET = Base64.getEncoder().encodeToString("encoded Data 2Data 2Data 2Data 2Data 2Data 2Data 2Data 2Data 2Data 2Data 2Data 2Data 2Data 2Data 2Data 2Data 2Data 2Data 2Data 2".getBytes());
    static private final String ENCODED_DATA_LONG_DATA_SET_MANY_DIFFS = Base64.getEncoder().encodeToString("encoded Data 2Data 2Data 2Data 2Data 4Data 2Data 2Data 2Data 3Data 3Data 2Data 2Data 2Data 2Data 2Data 2Data 2Data 2Data 2Data 2".getBytes());


    @Mock private DiffRepository diffRepository;

    @InjectMocks
    private DiffService diffService;


    @Test(expected = DiffNotFoundException.class)
    public void testWhenDiffNotFound_GetDiffResult_ThrowsException() {
        given(diffRepository.getDiff(1L)).willReturn(Optional.empty());

        diffService.getDiffResult(1L);
    }

    @Test(expected = IncompleteDiffException.class)
    public void testWhenDiffIsIncomplete_GetDiffResult_ThrowsException() {
        given(diffRepository.getDiff(1L)).willReturn(Optional.of(new Diff(1L, ENCODED_DATA, null)));

        diffService.getDiffResult(1L);
    }

    @Test(expected = IncompleteDiffException.class)
    public void testWhenDiffIsIncompleteRight_GetDiffResult_ThrowsException() {

        given(diffRepository.getDiff(1L)).willReturn(Optional.of(new Diff(1L, null, ENCODED_DATA)));

        diffService.getDiffResult(1L);
    }

    @Test
    public void testWhenDiffIsEqual_GetDiffResult_ShowsEqual() {

        given(diffRepository.getDiff(1L)).willReturn(Optional.of(new Diff(1L, ENCODED_DATA, ENCODED_DATA)));

        DiffResult diffResult = diffService.getDiffResult(1L);

        assertThat(diffResult.getDiffType(), is(DiffResult.DiffType.EQUAL));
    }

    @Test
    public void testWhenDiffHasDifferentSize_GetDiffResult_ShowsDifferenceInSize() {

        given(diffRepository.getDiff(1L)).willReturn(Optional.of(new Diff(1L, ENCODED_DATA_2, ENCODED_DATA)));

        DiffResult diffResult = diffService.getDiffResult(1L);

        assertThat(diffResult.getDiffType(), is(DiffResult.DiffType.DIFFERENT_SIZE));
    }

    @Test
    public void testWhenDiffHasSameSize_GetDiffResult_ShowsInsightsOnDiff() {

        assertEquals(ENCODED_DATA.length(), ENCODED_DATA_DIFF_IN_MIDDLE.length());

        given(diffRepository.getDiff(1L)).willReturn(Optional.of(new Diff(1L, ENCODED_DATA, ENCODED_DATA_DIFF_IN_MIDDLE)));

        DiffResult diffResult = diffService.getDiffResult(1L);

        assertThat(diffResult.getDiffType(), is(DiffResult.DiffType.SAME_SIZE));

        assertThat(diffResult.getInsights(), is(Collections.singletonList(new DiffInsight(11, 1))));
    }

    @Test
    public void testWhenDiffHasSameSizeAndDiffAtBegining_GetDiffResult_ShowsInsightsOnDiff() {

        assertEquals(ENCODED_DATA.length(), ENCODED_DATA_DIFF_AT_BEGINNING.length());

        given(diffRepository.getDiff(1L)).willReturn(Optional.of(new Diff(1L, ENCODED_DATA, ENCODED_DATA_DIFF_AT_BEGINNING)));

        DiffResult diffResult = diffService.getDiffResult(1L);

        assertThat(diffResult.getDiffType(), is(DiffResult.DiffType.SAME_SIZE));

        assertThat(diffResult.getInsights(), is(Collections.singletonList(new DiffInsight(0, 1))));
    }

    @Test
    public void testWhenDiffHasSameSizeAndDiffAtTheEnd_GetDiffResult_ShowsInsightsOnDiff() {

        assertEquals(ENCODED_DATA.length(), ENCODED_DATA_DIFF_AT_END.length());

        given(diffRepository.getDiff(1L)).willReturn(Optional.of(new Diff(1L, ENCODED_DATA, ENCODED_DATA_DIFF_AT_END)));

        DiffResult diffResult = diffService.getDiffResult(1L);

        assertThat(diffResult.getDiffType(), is(DiffResult.DiffType.SAME_SIZE));

        assertThat(diffResult.getInsights(), is(Collections.singletonList(new DiffInsight(15, 1))));
    }

    @Test
    public void testWhenDiffHasSameSizeAndLongDataSetAndManyDiffs_GetDiffResult_ShowsInsightsOnDiff() {

        assertEquals(ENCODED_DATA_LONG_DATA_SET.length(), ENCODED_DATA_LONG_DATA_SET_MANY_DIFFS.length());

        given(diffRepository.getDiff(1L)).willReturn(Optional.of(new Diff(1L, ENCODED_DATA_LONG_DATA_SET, ENCODED_DATA_LONG_DATA_SET_MANY_DIFFS)));

        DiffResult diffResult = diffService.getDiffResult(1L);

        assertThat(diffResult.getDiffType(), is(DiffResult.DiffType.SAME_SIZE));
        assertThat(diffResult.getInsights(), is(
                Arrays.asList(new DiffInsight(50, 1),
                              new DiffInsight(82, 1),
                              new DiffInsight(90, 1))));

    }

    @Test
    public void testWhenDiffHasSameSizeAnd3offset_GetDiffResult_ShowsInsightsOnDiff() {

        assertEquals(ENCODED_DATA.length(), ENCODED_DATA_3OFFSET.length());

        given(diffRepository.getDiff(1L)).willReturn(Optional.of(new Diff(1L, ENCODED_DATA, ENCODED_DATA_3OFFSET)));

        DiffResult diffResult = diffService.getDiffResult(1L);

        assertThat(diffResult.getDiffType(), is(DiffResult.DiffType.SAME_SIZE));
        assertThat(diffResult.getInsights(), is(Collections.singletonList(new DiffInsight(9, 7))));

    }
}