package com.ust.wordmaster.headline;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.ust.wordmaster.service.range.RangedText;
import lombok.Getter;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonPropertyOrder({"version", "description", "size", "source", "rangeStart", "rangeEnd", "rangedTextList"})
public class HeadlineResponseDTO {

    // these are arbitrarily added at the very end
    @JsonProperty("vs")
    private String version;
    private String description;

    // these come from controller
    private String source;
    private int rangeStart;
    private int rangeEnd;

    // these result from processing
    @JsonProperty("headlines")
    private List<RangedTextJSON> rangedTextJSONList;

    @JsonProperty("dataSize")
    private int size;

    public HeadlineResponseDTO() {
    }

    /////////// SETTERS /////////////////////////////////////

    public void setVersion(String version) {
        this.version = Objects.requireNonNullElse(version, "");
    }

    public void setDescription(String description) {
        this.description = Objects.requireNonNullElse(description, "");
    }

    public void setSource(String source) {
        this.source = Objects.requireNonNullElse(source, "");
    }

    public void setRangeStart(int rangeStart) {
        this.rangeStart = rangeStart;
    }

    public void setRangeEnd(int rangeEnd) {
        this.rangeEnd = rangeEnd;
    }


    public void setRangedTexts(List<RangedText> rangedTexts) {
        Objects.requireNonNull(rangedTexts, "List of ranged texts cannot be null");

        List<RangedTextJSON> rangedTextJSONList = rangedTexts.stream()
                .map(text -> new RangedTextJSON(text.getText(), text.getOutOfRangeWords()))
                .collect(Collectors.toList());

        this.rangedTextJSONList = rangedTextJSONList;
        this.size = this.rangedTextJSONList.size();
    }
}
