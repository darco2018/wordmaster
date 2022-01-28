package com.ust.wordmaster.service.range;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class IrregularVerbsConverter {

    private final Map<String, String> conversionMap;
    private final String irregularVerbsFile = "list-of-irregular-verbs.csv";

    public IrregularVerbsConverter() {
        conversionMap = createConversionMap();
    }

    private Map<String, String> createConversionMap() {
        List<IrregularVerb> irregularVerbList = getIrregularVerbsSortedByBaseForm();

        Map<String, String> baseFormPastForms = new HashMap<>();

        irregularVerbList.forEach(verb -> {
            Arrays.stream(verb.pastSimple().split("/"))
                    .forEach(pastSimple -> baseFormPastForms.put(pastSimple, verb.base()));

            Arrays.stream(verb.pastParticiple().split("/"))
                    .forEach(pastParticiple -> baseFormPastForms.put(pastParticiple, verb.base()));
        });

        return baseFormPastForms;
    }

    private List<IrregularVerb> getIrregularVerbsSortedByBaseForm() {
        List<String> lines = null;
        try {
            lines = Files.readAllLines(Paths.get(this.irregularVerbsFile));
        } catch (IOException e) {
            e.printStackTrace();
        }
        List<IrregularVerb> irregularVerbList = new ArrayList<>(200);

        Objects.requireNonNull(lines).stream()
                .map(String::strip)
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
        return word != null ? conversionMap.getOrDefault(word.toLowerCase(), null) : null;
    }

    record IrregularVerb(String base, String pastSimple,
                         String pastParticiple) implements Comparable<IrregularVerb> {

        @Override
        public int compareTo(IrregularVerb o) {
            return this.base.compareTo(o.base);
        }
    }


}
