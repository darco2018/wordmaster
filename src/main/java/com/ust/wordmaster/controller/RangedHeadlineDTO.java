package com.ust.wordmaster.controller;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Getter;

import java.util.List;
import java.util.Objects;

@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonPropertyOrder({"version", "description", "size", "source", "rangeStart", "rangeEnd", "rangedTextList"})
public class RangedHeadlineDTO {

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
    private List<RangedTextDTO> rangedTextDTOList;
    @JsonProperty("dataSize")
    private int size;

    public RangedHeadlineDTO() {
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

    public void setRangedTextList(List<RangedTextDTO> rangedTextDTOList) {
        Objects.requireNonNull(rangedTextDTOList, "List of ranged texts cannot be null");
        this.rangedTextDTOList = rangedTextDTOList;
        this.size = this.rangedTextDTOList.size();
    }

}
