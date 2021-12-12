package com.ust.wordmaster.service.filtering;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.util.Arrays;

@Getter
@Setter
public class FilteredHeadline extends Headline implements Serializable {

    private int[] outOfRangeWords;
    private int[] range;

    public FilteredHeadline(Headline headline, int[] indexes, int[] range){
        super(headline);
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
