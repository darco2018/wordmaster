package com.ust.wordmaster.service.filtering;


import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface TextUnitsCreator {

    List<ParsedTextUnit> parseIntoTextUnits(List<String> charSequences, int rangeStart, int rangeEnd);
}
