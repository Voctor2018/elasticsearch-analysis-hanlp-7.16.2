package com.hankcs.utility;

import com.hankcs.hanlp.seg.Segment;
import com.hankcs.hanlp.seg.common.Term;
import java.util.List;

public class RuleBasedSegment extends Segment {

    private final Segment base;   // 被代理的原始 Segment
    private boolean enableRule;   // 规则识别开关

    public RuleBasedSegment(Segment base) {
        this.base = base;
    }

    public RuleBasedSegment enableRuleBasedSegment(boolean enable) {
        this.enableRule = enable;
        return this;
    }

    @Override
    protected List<Term> segSentence(char[] sentence) {
        // 这里调用原始 segment 的分词
        List<Term> termList = base.seg(sentence);

        // 增加自定义规则识别逻辑
        if (enableRule && termList != null && !termList.isEmpty()) {
            SimpleRuleRecognition.apply(termList);
        }
        return termList;
    }
}
