//package com.hankcs.utility;
//
//import com.hankcs.hanlp.corpus.tag.Nature;
//import com.hankcs.hanlp.seg.common.Term;
//import java.util.*;
//import java.util.regex.Pattern;
//import java.util.regex.Matcher;
//
//public class SimpleRuleRecognition {
//
//    private static final Pattern DATE_PATTERN = Pattern.compile("(\\d{2,4}年\\d{1,2}月\\d{1,2}日)");
//    private static final Pattern MONEY_PATTERN = Pattern.compile("\\d+(\\.\\d+)?(元|块|人民币)");
//    private static final Pattern PERCENT_PATTERN = Pattern.compile("\\d+(\\.\\d+)?%");
//    private static final Pattern ENGLISH_PHRASE = Pattern.compile("[A-Za-z]+(?:[A-Z][a-z0-9]+)*");
//
//    public static void apply(List<Term> termList) {
//        if (termList == null || termList.size() < 2) return;
//
//        StringBuilder sentence = new StringBuilder();
//        for (Term t : termList) sentence.append(t.word);
//        String fullText = sentence.toString();
//
//        // 查找时间
//        Matcher dateMatcher = DATE_PATTERN.matcher(fullText);
//        List<Span> spans = new ArrayList<>();
//        while (dateMatcher.find()) {
//            spans.add(new Span(dateMatcher.start(), dateMatcher.end(), "t"));
//        }
//
//        // 查找金额
//        Matcher moneyMatcher = MONEY_PATTERN.matcher(fullText);
//        while (moneyMatcher.find()) {
//            spans.add(new Span(moneyMatcher.start(), moneyMatcher.end(), "m"));
//        }
//
//        // 查找百分比
//        Matcher percentMatcher = PERCENT_PATTERN.matcher(fullText);
//        while (percentMatcher.find()) {
//            spans.add(new Span(percentMatcher.start(), percentMatcher.end(), "m"));
//        }
//
//        if (spans.isEmpty()) return;
//
//        // 按位置合并 term
//        List<Term> newList = new ArrayList<>();
//        int charIndex = 0;
//        for (Span span : spans) {
//            for (Term term : termList) {
//                int len = term.word.length();
//                if (charIndex == span.start) {
//                    newList.add(new Term(fullText.substring(span.start, span.end), Nature.create(span.nature)));
//                }
//                charIndex += len;
//            }
//        }
//
//        // 替换原列表
//        // 简单做法：清空并重新添加
//        termList.clear();
//        termList.addAll(rebuild(termList, fullText, spans));
//    }
//
//    private static List<Term> rebuild(List<Term> oldList, String fullText, List<Span> spans) {
//        List<Term> result = new ArrayList<>();
//        int index = 0;
//        for (Span span : spans) {
//            if (index < span.start)
//                result.add(new Term(fullText.substring(index, span.start), null));
//            result.add(new Term(fullText.substring(span.start, span.end), Nature.create(span.nature)));
//            index = span.end;
//        }
//        if (index < fullText.length())
//            result.add(new Term(fullText.substring(index), null));
//        return result;
//    }
//
//
//
//    static class Span {
//        int start, end;
//        String nature;
//        Span(int s, int e, String n) { start = s; end = e; nature = n; }
//    }
//}


package com.hankcs.utility;

import com.hankcs.hanlp.corpus.tag.Nature;
import com.hankcs.hanlp.seg.common.Term;
import java.util.*;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

public class SimpleRuleRecognition {

    private static final Pattern DATE_PATTERN = Pattern.compile("(\\d{2,4}年\\d{1,2}月\\d{1,2}日)");
    private static final Pattern MONEY_PATTERN = Pattern.compile("\\d+(\\.\\d+)?(元|块|人民币)");
    private static final Pattern PERCENT_PATTERN = Pattern.compile("\\d+(\\.\\d+)?%");
    private static final Pattern ENGLISH_PHRASE = Pattern.compile("[A-Za-z]+(?:[A-Z][a-z0-9]+)*");

    public static void apply(List<Term> termList) {
        if (termList == null || termList.isEmpty()) return;

        // 合并原始 term 为完整字符串
        StringBuilder sb = new StringBuilder();
        for (Term t : termList) sb.append(t.word);
        String fullText = sb.toString();

        List<Span> spans = new ArrayList<>();

        // 匹配时间、金额、百分比
        findSpans(DATE_PATTERN, fullText, spans, Nature.t);
        findSpans(MONEY_PATTERN, fullText, spans, Nature.m);
        findSpans(PERCENT_PATTERN, fullText, spans, Nature.m);
        findSpans(ENGLISH_PHRASE, fullText, spans, Nature.nz);

        if (spans.isEmpty()) return;

        // 按开始位置排序
        spans.sort(Comparator.comparingInt(s -> s.start));

        // 重建 term 列表
        List<Term> newList = new ArrayList<>();
        int index = 0;
        for (Span span : spans) {
            if (index < span.start)
                newList.add(new Term(fullText.substring(index, span.start), Nature.nz));
            newList.add(new Term(fullText.substring(span.start, span.end), span.nature));
            index = span.end;
        }
        if (index < fullText.length())
            newList.add(new Term(fullText.substring(index), Nature.nz));

        // 替换原列表
        termList.clear();
        termList.addAll(newList);
    }

    private static void findSpans(Pattern pattern, String text, List<Span> spans, Nature nature) {
        Matcher matcher = pattern.matcher(text);
        while (matcher.find()) {
            spans.add(new Span(matcher.start(), matcher.end(), nature));
        }
    }

    static class Span {
        int start, end;
        Nature nature;
        Span(int s, int e, Nature n) { start = s; end = e; nature = n; }
    }
}

