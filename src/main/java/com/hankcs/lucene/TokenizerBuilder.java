package com.hankcs.lucene;

import com.hankcs.cfg.Configuration;
import com.hankcs.dic.CustomDictionaryCheck;
import com.hankcs.hanlp.HanLP;
import com.hankcs.hanlp.seg.Segment;
import com.hankcs.hanlp.seg.common.Term;
import com.hankcs.utility.RuleBasedSegment;
import com.hankcs.utility.SimpleRuleRecognition;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.lucene.analysis.Tokenizer;

import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.List;

/**
 * Project: elasticsearch-analysis-hanlp
 * Description:
 * Author: Kenn
 * Create: 2019-04-25 09:47
 */
public class TokenizerBuilder {

    private static final Logger logger = LogManager.getLogger(TokenizerBuilder.class);

    /**
     * 构建Tokenizer
     *
     * @param segment       原始segment
     * @param configuration 配置信息
     * @return 返回tokenizer
     */
    public static Tokenizer tokenizer(Segment segment, Configuration configuration) {
        logger.info("===========tokenizer start===========");
        Segment seg = segment(segment, configuration);
        logger.info("===========Segment end===========");

        return AccessController.doPrivileged((PrivilegedAction<HanLPTokenizer>)() -> new HanLPTokenizer(seg, configuration));
    }

    /**
     * 根据配置信息配置segment
     *
     * @param segment       原始segment
     * @param configuration 配置信息
     * @return 新segment
     */
    private static Segment segment(Segment segment, Configuration configuration) {
        if (!configuration.isEnableCustomConfig()) {
            segment.enableOffset(true);
        } else {
            segment.enableIndexMode(configuration.isEnableIndexMode())
                    .enableNumberQuantifierRecognize(configuration.isEnableNumberQuantifierRecognize())
                    .enableCustomDictionary(configuration.isEnableCustomDictionary())
                    .enableTranslatedNameRecognize(configuration.isEnableTranslatedNameRecognize())
                    .enableJapaneseNameRecognize(configuration.isEnableJapaneseNameRecognize())
                    .enableOrganizationRecognize(configuration.isEnableOrganizationRecognize())
                    .enablePlaceRecognize(configuration.isEnablePlaceRecognize())
                    .enableNameRecognize(configuration.isEnableNameRecognize())
                    .enablePartOfSpeechTagging(configuration.isEnablePartOfSpeechTagging())
                    .enableOffset(configuration.isEnableOffset())
                    .enableCustomDictionaryForcing(configuration.isEnableCustomDictionaryForcing());
        }

        SimpleRuleRecognition.RuleConfig ruleConfig = new SimpleRuleRecognition.RuleConfig();
        ruleConfig.enableMoneyRule = configuration.isEnableMoneyRuleBasedSegment(); // 识别金额
        ruleConfig.enableDateRule = configuration.isEnableDateRuleBasedSegment();   // 识别日期
        ruleConfig.enableEnglishRule = configuration.isEnableEnglishRuleBasedSegment(); // 识别英文词
        ruleConfig.enablePercentRule = configuration.isEnablePercentRuleBasedSegment(); // 识别百分比
        ruleConfig.enableInterventionRule = configuration.isEnableInterventionRuleBasedSegment(); // 启用自定义替换
        ruleConfig.enablePlaceRule = configuration.isEnablePlaceRuleBasedSegment(); // 启用地区分词

        Segment wrapped = new RuleBasedSegment(segment)
                .BasedSegmentRuleConfig(ruleConfig)
                .enableRuleBasedSegment(configuration.isEnableRuleBasedSegment());


        if (configuration.isEnableTraditionalChineseMode()) {
            return new Segment() {
                @Override
                protected List<Term> segSentence(char[] sentence) {
                    return segment.seg(HanLP.convertToSimplifiedChinese(new String(sentence)));
                }
            };
        }

        return wrapped;
    }
}
