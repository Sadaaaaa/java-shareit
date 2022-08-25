package ru.practicum.shareit;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.exception.PageableException;

import java.util.Optional;

public class OffsetLimitPageable implements Pageable {
    private final int offset;
    private final int limit;
    private Sort sort;
    private static int DEFAULT_PAGE_SIZE = 100;

    public OffsetLimitPageable(int offset, int limit, Sort sort) {
        this.offset = offset;
        this.limit = limit;
        this.sort = sort;
    }

    public static Pageable of(Integer from, Integer size) {
        if (from == null && size == null) {
            from = 0;
            size = DEFAULT_PAGE_SIZE;
        }

        if (saveUnboxing(size) < 1 || saveUnboxing(from) < 0) {
            throw new PageableException("FROM < 0 OR  SIZE < 1");
        }

        return new OffsetLimitPageable(saveUnboxing(from), saveUnboxing(size), Sort.unsorted());
    }

    public static Pageable of(Integer from, Integer size, Sort sort) {
        if (from == null && size == null) {
            from = 0;
            size = DEFAULT_PAGE_SIZE;
        }

        if (saveUnboxing(size) < 1 || saveUnboxing(from) < 0) {
            throw new PageableException("FROM < 0 OR  SIZE < 1");
        }

        return new OffsetLimitPageable(saveUnboxing(from), saveUnboxing(size), sort);
    }

    @Override
    public int getPageNumber() {
        return 0;
    }

    @Override
    public int getPageSize() {
        return limit;
    }

    @Override
    public long getOffset() {
        return offset;
    }

    @Override
    public Sort getSort() {
        return sort;
    }

    @Override
    public Pageable next() {
        return null;
    }

    @Override
    public Pageable previousOrFirst() {
        return null;
    }

    @Override
    public Pageable first() {
        return null;
    }

    @Override
    public Pageable withPage(int pageNumber) {
        return null;
    }

    @Override
    public boolean hasPrevious() {
        return false;
    }

    public static int saveUnboxing(Integer value) {
        return Optional.ofNullable(value).orElse(0);
    }
}