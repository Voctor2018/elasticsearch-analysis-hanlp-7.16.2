package com.hankcs.lucene;

import com.hankcs.cfg.Configuration;
import com.hankcs.hanlp.model.crf.CRFLexicalAnalyzer;
import com.hankcs.hanlp.seg.Segment;
import com.hankcs.model.CRFNERecognizerInstance;
import com.hankcs.model.CRFPOSTaggerInstance;
import com.hankcs.model.CRFSegmenterInstance;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.lucene.analysis.Analyzer;

import java.security.AccessController;
import java.security.PrivilegedAction;

/**
 * Project: elasticsearch-analysis-hanlp
 * Description: CRF分析器
 * Author: Kenn
 * Create: 2021-01-30 01:22
 */
public class HanLPCRFAnalyzer extends Analyzer {
    private static final Logger logger = LogManager.getLogger(HanLPCRFAnalyzer.class);
    /**
     * 分词配置
     */
    private final Configuration configuration;

    public HanLPCRFAnalyzer(Configuration configuration) {
        super();
        this.configuration = configuration;
        logger.info("HanLPCRFAnalyzer start");
    }

    @Override
    protected TokenStreamComponents createComponents(String fieldName) {
        if (CRFPOSTaggerInstance.getInstance().getTagger() == null) {
            return new TokenStreamComponents(
                    TokenizerBuilder.tokenizer(
                            AccessController.doPrivileged((PrivilegedAction<Segment>) () ->
                                    new CRFLexicalAnalyzer(
                                            CRFSegmenterInstance.getInstance().getSegmenter()
                                    )),
                            configuration));
        } else if (CRFNERecognizerInstance.getInstance().getRecognizer() == null) {
            return new TokenStreamComponents(
                    TokenizerBuilder.tokenizer(
                            AccessController.doPrivileged((PrivilegedAction<Segment>) () ->
                                    new CRFLexicalAnalyzer(
                                            CRFSegmenterInstance.getInstance().getSegmenter(),
                                            CRFPOSTaggerInstance.getInstance().getTagger()
                                    )),
                            configuration));
        } else {
            return new TokenStreamComponents(
                    TokenizerBuilder.tokenizer(
                            AccessController.doPrivileged((PrivilegedAction<Segment>) () ->
                                    new CRFLexicalAnalyzer(
                                            CRFSegmenterInstance.getInstance().getSegmenter(),
                                            CRFPOSTaggerInstance.getInstance().getTagger(),
                                            CRFNERecognizerInstance.getInstance().getRecognizer()
                                    )),
                            configuration));
        }
    }
}
