package ru.practicum.shareit;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.exception.PageableException;

import javax.persistence.criteria.CriteriaBuilder;

import static org.mockito.ArgumentMatchers.any;

public class OffsetLimitPageableTest {

    private OffsetLimitPageable offsetLimitPageable;

    private Integer offset;
    private Integer limit;
    private Sort sort;

    @Test
    void ofTest() {
        offset = 0;
        limit = 5;

        Assertions.assertEquals(OffsetLimitPageable.of(offset, limit).getOffset(), 0);
        Assertions.assertEquals(OffsetLimitPageable.of(offset, limit).getPageSize(), 5);
        Assertions.assertEquals(OffsetLimitPageable.of(offset, limit).getPageNumber(), 0);
        Assertions.assertEquals(OffsetLimitPageable.of(offset, limit).getSort(), Sort.unsorted());

        offset = null;
        limit = null;
        Assertions.assertEquals(OffsetLimitPageable.of(offset, limit).getOffset(), 0);
        Assertions.assertEquals(OffsetLimitPageable.of(offset, limit).getPageSize(), 100);

        offset = -1;
        Assertions.assertThrows(PageableException.class, () -> OffsetLimitPageable.of(offset, limit));
    }

    @Test
    void methodsShouldReturnNullTest() {
        offset = 0;
        limit = 5;
        Assertions.assertNull(OffsetLimitPageable.of(offset, limit).next());
        Assertions.assertNull(OffsetLimitPageable.of(offset, limit).previousOrFirst());
        Assertions.assertNull(OffsetLimitPageable.of(offset, limit).first());
        Assertions.assertNull(OffsetLimitPageable.of(offset, limit).withPage(0));
    }

}
