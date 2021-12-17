package com.ust.wordmaster.service.filtering;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Arrays;

@Getter
@Setter
public class ParsedTextUnit extends TextUnit implements Serializable {

    private int[] outOfRangeWords;
    private int[] range;

    public ParsedTextUnit(TextUnit textUnit, int[] indexes, int[] range){
        super(textUnit);
        this.outOfRangeWords = indexes;
        this.range = range;
    }

    @Override
    public String toString() {
        return "FilteredHeadline{" + super.toString() + "," +
                "outOfRangeWords=" + Arrays.toString(outOfRangeWords) +
                ", range=" + Arrays.toString(range) +
                '}';
    }
}
