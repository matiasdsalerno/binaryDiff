package com.waes.diff.repository;

import com.waes.diff.model.Diff;
import com.waes.diff.model.DiffData;
import org.junit.Before;
import org.junit.Test;

import java.util.Optional;

import static org.junit.Assert.*;

public class InMemoryDiffRepositoryTest {

    private InMemoryDiffRepository diffRepository;

    @Before
    public void setUp() {
        diffRepository = new InMemoryDiffRepository();
    }

    @Test
    public void saveRightDiff() {
        assertFalse(diffRepository.getDiff(1L).isPresent());

        diffRepository.saveRightDiff(1L, new DiffData("data"));

        Optional<Diff> diff = diffRepository.getDiff(1L);
        assertTrue(diff.isPresent());

        assertFalse(diff.get().getRight().isEmpty());
    }

    @Test
    public void saveLeftDiff() {
        assertFalse(diffRepository.getDiff(1L).isPresent());

        diffRepository.saveLeftDiff(1L, new DiffData("data"));

        Optional<Diff> diff = diffRepository.getDiff(1L);
        assertTrue(diff.isPresent());

        assertFalse(diff.get().getLeft().isEmpty());
    }

    @Test
    public void clear() {
        assertFalse(diffRepository.getDiff(1L).isPresent());

        diffRepository.saveLeftDiff(1L, new DiffData("data"));

        assertTrue(diffRepository.getDiff(1L).isPresent());

        diffRepository.clear();

        assertFalse(diffRepository.getDiff(1L).isPresent());
    }
}