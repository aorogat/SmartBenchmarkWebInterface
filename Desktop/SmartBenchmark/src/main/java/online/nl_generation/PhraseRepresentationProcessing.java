package online.nl_generation;

import system.components.Predicate_Representation_Extractor;

/**
 *
 * @author aorogat
 */
public class PhraseRepresentationProcessing {
    public static String NP_without_verb(String NP)
    {
        if(NP==null)
            return NP;
        return NP.replaceAll("(\\bis/are\\b|\\bis\\b|\\bare\\b|\\bwas\\b|\\bwere\\b) ", "").trim();
    }
    
    public static String NP_only(String NP)
    {
        if(NP==null)
            return NP;
        return NP_without_verb(NP).replaceAll("(\\ba\\b|\\ban\\b|\\bthe\\b)", "")
                .replaceAll("(" + Predicate_Representation_Extractor.getVerbPrepositionsConcatenated("\\b|\\b") + ")", "").trim();
    }
    
    public static String NP_without_Preposition(String NP)
    {
        if(NP==null)
            return NP;
        return NP.replaceAll("(" + Predicate_Representation_Extractor.getVerbPrepositionsConcatenated("\\b|\\b") + ")", "").trim();
    }
    
    public static String NP_without_verb___first(String NP)
    {
        if(NP==null)
            return NP;
        return NP.replaceFirst("(\\bis/are\\b|\\bis\\b|\\bare\\b|\\bwas\\b|\\bwere\\b) ", "").trim();
    }
    
    public static String NP_only___first(String NP)
    {
        if(NP==null)
            return NP;
        return NP_without_verb(NP).replaceFirst("(\\ba\\b|\\ban\\b|\\bthe\\b)", "")
                .replaceFirst("(" + Predicate_Representation_Extractor.getVerbPrepositionsConcatenated("\\b|\\b") + ")", "").trim();
    }
}
