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
//        if (termList == null || termList.isEmpty()) return;
//
//        // 合并原始 term 为完整字符串
//        StringBuilder sb = new StringBuilder();
//        for (Term t : termList) sb.append(t.word);
//        String fullText = sb.toString();
//
//        List<Span> spans = new ArrayList<>();
//
//        // 匹配时间、金额、百分比
//        findSpans(DATE_PATTERN, fullText, spans, Nature.t);
//        findSpans(MONEY_PATTERN, fullText, spans, Nature.m);
//        findSpans(PERCENT_PATTERN, fullText, spans, Nature.m);
//        findSpans(ENGLISH_PHRASE, fullText, spans, Nature.nz);
//
//        if (spans.isEmpty()) return;
//
//        // 按开始位置排序
//        spans.sort(Comparator.comparingInt(s -> s.start));
//
//        // 重建 term 列表
//        List<Term> newList = new ArrayList<>();
//        int index = 0;
//        for (Span span : spans) {
//            if (index < span.start)
//                newList.add(new Term(fullText.substring(index, span.start), Nature.nz));
//            newList.add(new Term(fullText.substring(span.start, span.end), span.nature));
//            index = span.end;
//        }
//        if (index < fullText.length())
//            newList.add(new Term(fullText.substring(index), Nature.nz));
//
//        // 替换原列表
//        termList.clear();
//        termList.addAll(newList);
//    }
//
//    private static void findSpans(Pattern pattern, String text, List<Span> spans, Nature nature) {
//        Matcher matcher = pattern.matcher(text);
//        while (matcher.find()) {
//            spans.add(new Span(matcher.start(), matcher.end(), nature));
//        }
//    }
//
//    static class Span {
//        int start, end;
//        Nature nature;
//        Span(int s, int e, Nature n) { start = s; end = e; nature = n; }
//    }
//}


//package com.hankcs.utility;
//
//import com.hankcs.hanlp.corpus.tag.Nature;
//import com.hankcs.hanlp.seg.common.Term;
//import java.util.*;
//import java.util.regex.*;
//
//public class SimpleRuleRecognition {
//
//    // 结构化识别正则
//    private static final Pattern DATE_PATTERN = Pattern.compile("(\\d{2,4}年\\d{1,2}月\\d{1,2}日)");
//    private static final Pattern MONEY_PATTERN = Pattern.compile("\\d+(\\.\\d+)?(元|块|人民币)");
//    private static final Pattern PERCENT_PATTERN = Pattern.compile("\\d+(\\.\\d+)?%");
//    private static final Pattern ENGLISH_PHRASE = Pattern.compile("[A-Za-z]+(?:[A-Z][a-z0-9]+)*");
//    private static final Pattern CAMEL_CASE = Pattern.compile("[A-Z][a-z0-9]+(?:[A-Z][a-z0-9]+)+");
//    private static final Pattern LOCATION_SPLIT = Pattern.compile("([^\\s]{2,})(县|市|镇|区|乡|村|店|厂|路|园|房|屯|社|部|局)");
//
//    public static void apply(List<Term> termList) {
//        if (termList == null || termList.isEmpty()) return;
//
//        // 1️⃣ 合并为文本
//        StringBuilder sb = new StringBuilder();
//        for (Term t : termList) sb.append(t.word);
//        String fullText = sb.toString();
//
////        // 2️⃣ 干预同义词替换
////        for (Map.Entry<String, String> entry : InterventionDictionary.interventionMap.entrySet()) {
////            fullText = fullText.replace(entry.getKey(), entry.getValue());
////        }
//
//        // 3️⃣ 地名自动拆分（例如“苏州市区” → “苏州 市区”）
//        Matcher locMatcher = LOCATION_SPLIT.matcher(fullText);
//        StringBuffer locBuffer = new StringBuffer();
//        while (locMatcher.find()) {
//            locMatcher.appendReplacement(locBuffer, locMatcher.group(1) + " " + locMatcher.group(2));
//        }
//        locMatcher.appendTail(locBuffer);
//        fullText = locBuffer.toString();
//
//        // 4️⃣ 应用结构化规则识别
//        List<Span> spans = new ArrayList<>();
//        findSpans(DATE_PATTERN, fullText, spans, Nature.t);
//        findSpans(MONEY_PATTERN, fullText, spans, Nature.m);
//        findSpans(PERCENT_PATTERN, fullText, spans, Nature.m);
//        findSpans(ENGLISH_PHRASE, fullText, spans, Nature.nz);
//        findSpans(CAMEL_CASE, fullText, spans, Nature.nz);
//
//        if (spans.isEmpty()) {
////            termList.clear();
////            for (String word : fullText.split("\\s+")) {
////                termList.add(new Term(word, Nature.nz));
////            }
//            return;
//        }
//
//        // 5️⃣ 合并结果
//        spans.sort(Comparator.comparingInt(s -> s.start));
//        List<Term> newList = new ArrayList<>();
//        int index = 0;
//        for (Span span : spans) {
//            if (index < span.start)
//                newList.add(new Term(fullText.substring(index, span.start), Nature.nz));
//            newList.add(new Term(fullText.substring(span.start, span.end), span.nature));
//            index = span.end;
//        }
//        if (index < fullText.length())
//            newList.add(new Term(fullText.substring(index), Nature.nz));
//
//        termList.clear();
//        termList.addAll(newList);
//    }
//
//    private static void findSpans(Pattern pattern, String text, List<Span> spans, Nature nature) {
//        Matcher matcher = pattern.matcher(text);
//        while (matcher.find()) {
//            spans.add(new Span(matcher.start(), matcher.end(), nature));
//        }
//    }
//
//    static class Span {
//        int start, end;
//        Nature nature;
//        Span(int s, int e, Nature n) { start = s; end = e; nature = n; }
//    }
//
//    private static String applyIntervention(String text) {
////        for (Map.Entry<String, String> entry : InterventionDictionary.interventionMap.entrySet()) {
////            text = text.replace(entry.getKey(), entry.getValue());
////        }
//        text = text.replaceAll("([^\\s]{2,})(县|市|镇|区|乡|村|店|厂|路|园|房|屯|社|部|局)", "$1 $2");
//        return text;
//    }
//}



package com.hankcs.utility;

import com.hankcs.dic.InterventionDictionary;
import com.hankcs.hanlp.corpus.tag.Nature;
import com.hankcs.hanlp.seg.common.Term;
import java.util.*;
import java.util.regex.*;

public class SimpleRuleRecognition {

    public static class RuleConfig {
        /**
         * 是否识别日期
         */
        public boolean enableDateRule = false;
        /**
         * 是否识别金额
         */
        public boolean enableMoneyRule = false;
        /**
         * 是否识别百分比
         */
        public boolean enablePercentRule = false;
        /**
         * 是否识别英文词
         */
        public boolean enableEnglishRule = false;
        /**
         * 是否开启同义词替换
         */
        public boolean enableInterventionRule = false;
    }

    // 结构化识别正则
    private static final Pattern DATE_PATTERN = Pattern.compile("(\\d{2,4}年\\d{1,2}月\\d{1,2}日)");
    private static final Pattern MONEY_PATTERN = Pattern.compile("\\d+(\\.\\d+)?(元|块|人民币)");
    private static final Pattern PERCENT_PATTERN = Pattern.compile("\\d+(\\.\\d+)?%");
    private static final Pattern ENGLISH_PHRASE = Pattern.compile("[A-Za-z]+(?:[A-Z][a-z0-9]+)*");

    public static void apply(List<Term> termList, RuleConfig config) {
        if (termList == null || termList.size() < 2) return;
        if (config == null) config = new RuleConfig(); // 默认全开

        StringBuilder sentence = new StringBuilder();
        for (Term t : termList) sentence.append(t.word);
        String fullText = sentence.toString();

        List<Span> spans = new ArrayList<>();

        if (config.enableDateRule) {
            Matcher dateMatcher = DATE_PATTERN.matcher(fullText);
            while (dateMatcher.find()) spans.add(new Span(dateMatcher.start(), dateMatcher.end(), Nature.t));
        }
        if (config.enableMoneyRule) {
            Matcher moneyMatcher = MONEY_PATTERN.matcher(fullText);
            while (moneyMatcher.find()) spans.add(new Span(moneyMatcher.start(), moneyMatcher.end(), Nature.m));
        }
        if (config.enablePercentRule) {
            Matcher percentMatcher = PERCENT_PATTERN.matcher(fullText);
            while (percentMatcher.find()) spans.add(new Span(percentMatcher.start(), percentMatcher.end(), Nature.m));
        }
        if (config.enableEnglishRule) {
            Matcher englishMatcher = ENGLISH_PHRASE.matcher(fullText);
            while (englishMatcher.find()) spans.add(new Span(englishMatcher.start(), englishMatcher.end(), Nature.nz));
        }

        // 自定义干预规则
        if (config.enableInterventionRule) {
            spans.addAll(buildInterventionSpans(fullText));
        }

        if (!spans.isEmpty()) {
            List<Term> newList = rebuildSelective(termList, fullText, spans);
            termList.clear();
            termList.addAll(newList);
        }
    }

//    private static List<Term> rebuildSelective(List<Term> oldList, String fullText, List<Span> spans) {
//        List<Term> result = new ArrayList<>();
//        if (oldList == null || oldList.isEmpty()) return result;
//
//        // 1. 合并重叠或相邻的 spans（按字符索引）
//        if (spans == null) spans = new ArrayList<>();
//        spans.sort(Comparator.comparingInt(s -> s.start));
//        List<Span> mergedSpans = new ArrayList<>();
//
//        // 交叉取并集
//        for (Span s : spans) {
//            if (mergedSpans.isEmpty()) {
//                mergedSpans.add(new Span(s.start, s.end, s.nature));
//                continue;
//            }
//            Span last = mergedSpans.get(mergedSpans.size() - 1);
//            if (s.start < last.end) {
//                // overlap or contiguous -> merge; when natures differ, prefer s.nature (规则优先)
//                last.end = Math.max(last.end, s.end);
//                last.nature = s.nature != null ? s.nature : last.nature;
//            } else {
//                mergedSpans.add(new Span(s.start, s.end, s.nature));
//            }
//        }
//
//        // 2. 计算每个原始 term 在 fullText 中的字符区间（start, end）
//        int cursor = 0;
//        class TokenSpan { int start, end; Term term; }
//        List<TokenSpan> tokenSpans = new ArrayList<>();
//        for (Term t : oldList) {
//            int len = t.word == null ? 0 : t.word.length();
//            TokenSpan ts = new TokenSpan();
//            ts.start = cursor;
//            ts.end = cursor + len;
//            ts.term = t;
//            tokenSpans.add(ts);
//            cursor += len;
//        }
//
//        // 3. 遍历 tokenSpans，用 mergedSpans 覆盖重叠片段
//        int tokenIdx = 0;
//        int spanIdx = 0;
//        while (tokenIdx < tokenSpans.size()) {
//            TokenSpan ts = tokenSpans.get(tokenIdx);
//
//            // 如果没有更多 span，直接把剩余 tokens 全部加入
//            if (spanIdx >= mergedSpans.size()) {
//                result.add(ts.term);
//                tokenIdx++;
//                continue;
//            }
//
//            Span span = mergedSpans.get(spanIdx);
//
//            // 如果当前 token 在 span 之前（不重叠）
//            if (ts.end <= span.start) {
//                result.add(ts.term);
//                tokenIdx++;
//                continue;
//            }
//
//            // 如果当前 token 在 span 之后（说明某些 span 没覆盖任何 token -> skip span）
//            if (ts.start >= span.end) {
//                spanIdx++;
//                continue;
//            }
//
//            // 否则 token 与 span 有重叠：我们需要把所有与该 span 有重叠的 token 合并成一个新 Term
//            int mergeStartChar = span.start;
//            int mergeEndChar = span.end;
//            Nature mergeNature = span.nature;
//
//            // advance tokenIdx 消耗所有与 span 重叠的 token
//            int consumeIdx = tokenIdx;
//            while (consumeIdx < tokenSpans.size() && tokenSpans.get(consumeIdx).start < mergeEndChar) {
//                mergeEndChar = Math.max(mergeEndChar, tokenSpans.get(consumeIdx).end); // 保证覆盖
//                consumeIdx++;
//            }
//
//            // 构造合并字符串（注意边界安全）
//            String mergedWord;
//            int textLen = fullText.length();
//            int s = Math.max(0, Math.min(mergeStartChar, textLen));
//            int e = Math.max(0, Math.min(mergeEndChar, textLen));
//            mergedWord = fullText.substring(s, e);
//
//            // 创建新 Term，使用规则提供的 nature（若为 null 则回退为 nz）
//            Nature nat = mergeNature != null ? mergeNature : Nature.nz;
//            Term mergedTerm = new Term(mergedWord, nat);
//
//            result.add(mergedTerm);
//
//            // 将 tokenIdx 移动到已消费位置
//            tokenIdx = consumeIdx;
//
//            // span 处理完毕，移动到下一个 span
//            spanIdx++;
//        }
//
//        return result;
//    }

    private static List<Term> rebuildSelective(List<Term> oldList, String fullText, List<Span> spans) {
        List<Term> result = new ArrayList<>();
        if (oldList == null || oldList.isEmpty()) return result;

        // 1. 合并重叠的 spans（仅合并真正重叠，不合并相邻）
        if (spans == null) spans = new ArrayList<>();
        spans.sort(Comparator.comparingInt(s -> s.start));
        List<Span> mergedSpans = new ArrayList<>();
        for (Span s : spans) {
            if (mergedSpans.isEmpty()) {
                mergedSpans.add(new Span(s.start, s.end, s.nature));
                continue;
            }
            Span last = mergedSpans.get(mergedSpans.size() - 1);
            // 这里使用严格小于：只有真正重叠才合并；相邻（last.end == s.start）不合并
            if (s.start < last.end) {
                last.end = Math.max(last.end, s.end);
                if (s.nature != null) last.nature = s.nature;
            } else {
                mergedSpans.add(new Span(s.start, s.end, s.nature));
            }
        }

        // 2. 计算原始 token 的字符区间
        int cursor = 0;
        class TokenSpan { int start, end; Term term; }
        List<TokenSpan> tokenSpans = new ArrayList<>();
        for (Term t : oldList) {
            int len = t.word == null ? 0 : t.word.length();
            TokenSpan ts = new TokenSpan();
            ts.start = cursor;
            ts.end = cursor + len;
            ts.term = t;
            tokenSpans.add(ts);
            cursor += len;
        }

        // 3. 遍历 tokenSpans 与 mergedSpans，进行选择性合并/拆分
        int tokenIdx = 0;
        int spanIdx = 0;
        while (tokenIdx < tokenSpans.size()) {
            TokenSpan ts = tokenSpans.get(tokenIdx);

            // 无更多 span：直接追加剩余 tokens
            if (spanIdx >= mergedSpans.size()) {
                result.add(ts.term);
                tokenIdx++;
                continue;
            }

            Span span = mergedSpans.get(spanIdx);

            // token 在 span 之前（不重叠）
            if (ts.end <= span.start) {
                result.add(ts.term);
                tokenIdx++;
                continue;
            }

            // span 在 token 之前（span 未覆盖任何 token）
            if (ts.start >= span.end) {
                spanIdx++;
                continue;
            }

            // --- 处理重叠（部分或完全） ---
            // 若 token 在 span 左侧有前缀（token.start < span.start），先保留前缀
            if (ts.start < span.start) {
                String prefix = safeSubstring(fullText, ts.start, span.start);
                if (!prefix.isEmpty()) {
                    result.add(new Term(prefix, ts.term.nature != null ? ts.term.nature : Nature.nz));
                }
            }

            // 计算被 span 覆盖并消费的 token（注意部分覆盖的后缀处理）
            int mergeEndChar = span.end;
            int consumeIdx = tokenIdx;
            boolean lastTokenPartiallyCovered = false;
            while (consumeIdx < tokenSpans.size()) {
                TokenSpan cur = tokenSpans.get(consumeIdx);
                if (cur.start >= mergeEndChar) break;
                if (cur.end > mergeEndChar) {
                    lastTokenPartiallyCovered = true;
                }
                mergeEndChar = Math.max(mergeEndChar, cur.end);
                consumeIdx++;
                if (lastTokenPartiallyCovered) break;
            }

            // 生成 mergedWord：如果有替换文本用 replacement，否则用原文 substring(span.start, span.end)
            String mergedWord = safeSubstring(fullText, span.start, span.end);
            Nature nat = span.nature != null ? span.nature : Nature.nz;
            result.add(new Term(mergedWord, nat));

            // 如果最后被部分覆盖的 token 有后缀，需要将后缀作为新的 token 插入到 tokenSpans 的当前位置
            if (lastTokenPartiallyCovered) {
                TokenSpan cur = tokenSpans.get(consumeIdx - 1);
                int suffixStart = span.end;
                int suffixEnd = cur.end;
                String suffixWord = safeSubstring(fullText, suffixStart, suffixEnd);
                if (!suffixWord.isEmpty()) {
                    TokenSpan suffixTs = new TokenSpan();
                    suffixTs.start = suffixStart;
                    suffixTs.end = suffixEnd;
                    suffixTs.term = new Term(suffixWord, cur.term.nature != null ? cur.term.nature : Nature.nz);
                    tokenSpans.add(consumeIdx, suffixTs);
                }
            }

            // 把 tokenIdx 移到 consumeIdx（已消费的 tokens 跳过）
            tokenIdx = consumeIdx;
            spanIdx++;
        }

        return result;
    }

    private static String safeSubstring(String s, int start, int end) {
        if (s == null || start >= end) return "";
        int len = s.length();
        int a = Math.max(0, Math.min(start, len));
        int b = Math.max(0, Math.min(end, len));
        if (a >= b) return "";
        return s.substring(a, b);
    }



    static class Span {
        int start, end;
        Nature nature;

        Span(int s, int e, Nature n) {
            this.start = s;
            this.end = e;
            this.nature = n;
        }
    }

    private static List<Span>  applyIntervention(String text) {
        List<Span> spans = new ArrayList<>();
        // 地名拆分规则（不做替换，只做断词提示）
        Pattern splitPattern = Pattern.compile("([^\\s]{2,})(县|市|镇|区|乡|村|店|厂|路|园|房|屯|社|部|局|园区|市区)");
        Matcher matcher = splitPattern.matcher(text);
        while (matcher.find()) {
            spans.add(new Span(matcher.start(), matcher.end(), null)); // 标记为split-only span
        }
        return spans;
    }


    /**
     * 基于 Span 模型的干预规则：
     * 1. 同义词替换（InterventionDictionary.interventionMap）
     * 2. 地名等自动断词
     *
     * @param fullText 原始句子（与分词拼接一致）
     * @return List<Span> 规则命中的位置与替换结果（不修改原文）
     */
    private static List<Span> buildInterventionSpans(String fullText) {
        List<Span> spans = new ArrayList<>();

        // 1️⃣ 同义词替换规则
        if (InterventionDictionary.interventionMap != null && !InterventionDictionary.interventionMap.isEmpty()) {
            for (Map.Entry<String, String> entry : InterventionDictionary.interventionMap.entrySet()) {
                String key = entry.getKey();
                String value = entry.getValue();

                // 多次匹配（不区分大小写，可根据需要修改）
                int index = 0;
                while ((index = fullText.indexOf(key, index)) != -1) {
                    for (String v : value.split(" ")){
                        int keyIndex = index + key.indexOf(v);
                        spans.add(new Span(keyIndex, keyIndex + v.length(), Nature.nz)); // nature 可自定义
                    }
                    index += key.length();
                }
            }
        }

        return spans;
    }
}


