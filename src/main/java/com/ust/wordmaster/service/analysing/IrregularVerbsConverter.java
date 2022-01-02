package com.ust.wordmaster.service.analysing;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class IrregularVerbsConverter {

    private final Map<String, String> conversionMap;

    public IrregularVerbsConverter() {
        conversionMap = createConversionMap();
    }

    private Map<String, String> createConversionMap() {
        List<IrregularVerb> irregularVerbList = getIrregularVerbsSortedByBaseForm();

        Map<String, String> baseFormPastForms = new HashMap<>();

        irregularVerbList.forEach(verb -> {
            Arrays.stream(verb.getPastSimple().split("/"))
                    .forEach(pastSimple -> baseFormPastForms.put(pastSimple, verb.getBase()));

            Arrays.stream(verb.getPastParticiple().split("/"))
                    .forEach(pastParticiple -> baseFormPastForms.put(pastParticiple, verb.getBase()));
        });

        return baseFormPastForms;
    }

    private List<IrregularVerb> getIrregularVerbsSortedByBaseForm() {
        List<String> lines = null;
        try {
            lines = Files.readAllLines(Paths.get("list-of-irregular-verbs.csv"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        List<IrregularVerb> irregularVerbList = new ArrayList<>(200);

        Objects.requireNonNull(lines).stream()
                .map(String::trim)
                .map(line -> line.split("\\s+"))
                .forEach(arr -> {
                    if (arr.length == 3 || arr.length == 6) {
                        irregularVerbList.add(new IrregularVerb(arr[0], arr[1], arr[2]));

                        if (arr.length == 6)
                            irregularVerbList.add(new IrregularVerb(arr[3], arr[4], arr[5]));
                    }

                });

        irregularVerbList.add(new IrregularVerb("can", "could", "could"));
        irregularVerbList.add(new IrregularVerb("hold", "held", "held"));
        Collections.sort(irregularVerbList);
        return irregularVerbList;
    }

    public String convertToBaseForm(String word) {
        return conversionMap.getOrDefault(word.toLowerCase(), word);
    }

    static class IrregularVerb implements Comparable<IrregularVerb> {
        private final String base;
        private final String pastSimple;
        private final String pastParticiple;

        public IrregularVerb(String base, String pastSimple, String pastParticiple) {
            this.base = base;
            this.pastSimple = pastSimple;
            this.pastParticiple = pastParticiple;
        }

        public String getBase() {
            return base;
        }

        public String getPastSimple() {
            return pastSimple;
        }

        public String getPastParticiple() {
            return pastParticiple;
        }

        @Override
        public String toString() {
            return base + " " + pastSimple + " " + pastParticiple;
        }

        @Override
        public int compareTo(IrregularVerb o) {

            return this.base.compareTo(o.base);
        }
    }


}
