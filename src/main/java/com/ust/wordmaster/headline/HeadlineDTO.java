package com.ust.wordmaster.headline;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
@Getter
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonPropertyOrder({"version", "description", "size", "source", "rangeStart", "rangeEnd", "rangedTextList"})
public class HeadlineDTO {

    // these are arbitrarily added at the very end
    @JsonProperty("vs")
    private String version;
    @JsonProperty("desc")
    private String description;

    // these come from controller
    private String source;
    private int rangeStart;
    private int rangeEnd;

    // these result from processing
    @JsonProperty("headlines")
    private List<RangedTextDTO> rangedTextDTOS;

    @JsonProperty("noOfHeadlines")
    private int size;

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

    public void setRangedTextDTOS(List<RangedTextDTO> rangedTextDTOS) {
        this.rangedTextDTOS = rangedTextDTOS;
    }

    public void setSize(int size) {
        this.size = size;
    }

}
