package com.ust.wordmaster.headline;

import com.ust.wordmaster.service.range.RangedText;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
public class HeadlineMapper {

    public HeadlineDTO toHeadlineDTO(int rangeStart, int rangeEnd, String websiteURL, List<RangedText> rangedTexts) {

        HeadlineDTO headlineDTO = new HeadlineDTO();
        headlineDTO.setSource(websiteURL);
        headlineDTO.setRangeStart(rangeStart);
        headlineDTO.setRangeEnd(rangeEnd);
        headlineDTO.setDescription("Headlines processed against 5000 dictionary to show words out of the requested range");
        headlineDTO.setVersion("1.0");
        headlineDTO.setSize(rangedTexts.size());
        headlineDTO.setRangedTextDTOS(toRangeTextDTOs(rangedTexts));

        return headlineDTO;
    }

    private List<RangedTextDTO> toRangeTextDTOs(List<RangedText> rangedTexts) {
        Objects.requireNonNull(rangedTexts, "List of ranged texts cannot be null");

        return rangedTexts.stream()
                .map(text -> new RangedTextDTO(text.getText(), text.getOutOfRangeWords()))
                .toList();

    }



}
