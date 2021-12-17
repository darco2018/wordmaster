package com.ust.wordmaster.service.filteringOLD;


import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface TextUnitsCreatorOLD {

    List<ParsedTextUnitOLD> parseIntoTextUnits(List<String> charSequences, int rangeStart, int rangeEnd);
}
