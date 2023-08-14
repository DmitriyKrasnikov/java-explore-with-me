package ru.practicum.ewm.pagination;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

public class FromSizePage extends PageRequest {
    protected FromSizePage(int from, int size, Sort sort) {
        super(from, size, sort);
    }

    public static PageRequest ofFromSize(int from, int size) {
        return PageRequest.of(from / size, size);
    }
}
