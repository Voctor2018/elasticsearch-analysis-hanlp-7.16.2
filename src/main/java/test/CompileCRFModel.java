package test;

import com.hankcs.dic.RemoteDictLoader;
import com.hankcs.hanlp.corpus.io.ByteArray;

import java.util.List;

public class CompileCRFModel {
    public static void main(String[] args) throws Exception {

        // 输入路径：你训练好的 txt 格式模型
        String input = "src/main/java/test/final_model.txt";
        String output = "src/main/java/test/final_model.txt.bin";      // 输出的目标 bin 文件


        // 转换模型
//        CRFLexicalAnalyzer analyzer = new CRFLexicalAnalyzer(input);
//        IOUtil.saveObjectTo(analyzer, output);

        // 加载模型测试
        // LinearModel model4 = new LinearModel(output);
        // 打印模型前20行
//        prinent(output);

        RemoteDictLoader loader = new RemoteDictLoader();
        List<String> dictWords = loader.getRemoteExtWords("DICT");

        System.out.println("=== 从数据库加载的词条 ===");
        dictWords.forEach(System.out::println);


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
