package test;

import com.hankcs.cfg.HanlpConfig;
import com.hankcs.dic.RemoteDictLoader;
import com.hankcs.hanlp.HanLP;
import com.hankcs.hanlp.corpus.io.ByteArray;
import com.hankcs.hanlp.model.crf.CRFLexicalAnalyzer;
import com.hankcs.hanlp.seg.Segment;
import com.hankcs.hanlp.seg.common.Term;
import com.hankcs.model.CRFSegmenterInstance;
import com.hankcs.utility.RuleBasedSegment;
import com.hankcs.utility.SimpleRuleRecognition;

import java.util.List;

public class CompileCRFModel {
    public static void main(String[] args) throws Exception {

        // 输入路径：你训练好的 txt 格式模型
//        String input = "src/main/java/test/final_model.txt";
//        String output = "src/main/java/test/final_model.txt.bin";      // 输出的目标 bin 文件


        // 转换模型
//        CRFLexicalAnalyzer analyzer = new CRFLexicalAnalyzer(input);
//        IOUtil.saveObjectTo(analyzer, output);

        // 加载模型测试
        // LinearModel model4 = new LinearModel(output);
        // 打印模型前20行
//        prinent(output);

//        RemoteDictLoader loader = new RemoteDictLoader();
//        List<String> dictWords = loader.getRemoteExtWords("DICT");
//
//        System.out.println("=== 从数据库加载的词条 ===");
//        dictWords.forEach(System.out::println);


        String[] texts = {
//                "广西壮族自治区南宁市江南区沙井街道邕津村村民委员会",
                "苏州市区人民西路",
                "南京市鼓楼区中山路",
                "北京市海淀区中关村南大街",
                "香港特别行政区",
                "新疆维吾尔族自治区",
                "江苏省苏州市工业园区",
//                    "NovaCode是位于江苏省苏州市工业园区的一家上市公司，在2023年12月12日销售部在苏州市区人民西路观前街收入500元,导致OpenAIChatGPT、Nova-Code股价大涨。"
        };

        // 分词调用方案一
        HanLP.Config.CRFCWSModelPath = "C:/Users/qichacha/Desktop/wsl/final_model.txt.bin";
        HanLP.Config.CRFPOSModelPath = "C:/Users/qichacha/Desktop/wsl/data-for-1.7.5/data/model/crf/pku199801/pos.txt.bin";
        HanLP.Config.CRFNERModelPath = "C:/Users/qichacha/Desktop/wsl/data-for-1.7.5/data/model/crf/pku199801/ner.txt.bin";
        Segment baseSegment =  HanLP.newSegment("crf");

        baseSegment.enableCustomDictionary(false);  // 关闭用户词典（与 analyzer 默认一致）
        baseSegment.enableNameRecognize(false);    // 禁止人名识别
        baseSegment.enablePlaceRecognize(false);   // 禁止地名识别
        baseSegment.enableOrganizationRecognize(false); // 禁止机构识别
        baseSegment.enableTranslatedNameRecognize(false); // 是否启用音译人名识别
        baseSegment.enableJapaneseNameRecognize(false); // 是否启用日本人名识别
        baseSegment.enableNumberQuantifierRecognize(false); // 禁止数词+量词识别


        SimpleRuleRecognition.RuleConfig ruleConfig = new SimpleRuleRecognition.RuleConfig();
        ruleConfig.enableMoneyRule = true; // 识别金额
        ruleConfig.enableDateRule = true;
        ruleConfig.enableEnglishRule = true;
        ruleConfig.enablePercentRule = true;
        ruleConfig.enableInterventionRule = true; // 是否开启同义词替换
        ruleConfig.enablePlaceRule = true; // 启用自定义替换

        RuleBasedSegment segment = new RuleBasedSegment(baseSegment)
                .enableRuleBasedSegment(true)
                .BasedSegmentRuleConfig(ruleConfig);

        for(String s : texts){
            List<Term> terms = segment.seg(s);
            for (Term term : terms) {
                System.out.println(term.word + "/" + term.nature);
            }
            System.out.println("===========分割线===========");
        }

        // 分词调用方案二
//        CRFLexicalAnalyzer analyzer = new CRFLexicalAnalyzer(HanLP.Config.CRFCWSModelPath);
//        analyzer.enableCustomDictionary(false);
//        analyzer.enableRuleBasedSegment(false);
//
//        for(String s : texts){
//            System.out.println(analyzer.seg(s));
//        }


        System.out.println("测试完毕");

    }

    public static void  prinent(String path){
        ByteArray byteArray = ByteArray.createByteArray(path);

        for (int i = 0; i < 20; i++) {
            System.out.printf("offset=%d int=%d%n", byteArray.getOffset(), byteArray.nextInt());
        }
        System.out.println("模型加载完毕");

    };

}
