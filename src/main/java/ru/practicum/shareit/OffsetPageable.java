package ru.practicum.shareit;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

public class OffsetPageable extends PageRequest {
    private final int offset;
    private final int limit;

    public OffsetPageable(int offset, int limit, Sort sort) {
        super(0, limit, sort);
        this.offset = offset;
        this.limit = limit;
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
}
