package com.ust.wordmaster.service.filtering;


import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface FilteringService {

    List<FilteredHeadline> filter(List<String> headlines, int rangeStart, int rangeEnd);
}
