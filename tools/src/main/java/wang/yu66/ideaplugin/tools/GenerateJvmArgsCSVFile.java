package wang.yu66.ideaplugin.tools;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by wangming on 2017/6/29.
 */
public class GenerateJvmArgsCSVFile {

    private static final String FILE_NAME = "d://jvm.csv";

    private static final String[] header = {"Type", "Argument", "Comment"};

    private static final CSVFormat CSV_FORMAT = CSVFormat.DEFAULT.withRecordSeparator("{|}");
    private static final List<String> writable = new ArrayList();

    private static final String[] types = {"Standard Options",
            "Non-Standard Options",
            "Advanced Runtime Options",
            "Advanced JIT Compiler Options",
            "Advanced Serviceability Options",
            "Advanced Garbage Collection Options"
            , ""};
    private static FileWriter fileWriter = null;
    private static CSVPrinter csvPrinter = null;

    static {
        File file = new File(FILE_NAME);
        try {
            if (file.exists()) {
                file.delete();
            }
            file = new File(FILE_NAME);
            file.createNewFile();
            fileWriter = new FileWriter(FILE_NAME, true);  // 创建test.csv的字符输出流
            csvPrinter = new CSVPrinter(fileWriter, CSV_FORMAT);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {

        try {
            Document doc = Jsoup.connect("http://docs.oracle.com/javase/8/docs/technotes/tools/unix/java.html").get();
            for (int start = 0; start <= 6; start++) {
                int typeIdx = start + 10;
                String type = types[start];

                for (int i = 1; i < 300; ) {
                    String title = "#JSSOR624 > div:nth-child(6) > div:nth-child(" + typeIdx + ") > dl > dt:nth-child(" + (i++) + ")";
                    String content = "#JSSOR624 > div:nth-child(6) > div:nth-child(" + typeIdx + ") > dl > dd:nth-child(" + (i++) + ")";
                    Elements titleElements = doc.select(title);
                    Elements contentElements = doc.select(content);
                    if (titleElements.text().equals("")) {
                        continue;
                    }
                    System.out.println(title + " [" + typeIdx + "," + i + "] ---> " + " ---> " + titleElements.text());
                    StringBuffer contentBuffer = new StringBuffer();
                    for (int j = 0; j < contentElements.size(); j++) {
                        Element c = contentElements.get(j);
                        Elements children = c.children();
                        for (Element element : children) {
                            contentBuffer.append(element.text()).append("\n");
                        }
                    }

                    System.out.println(content + " [" + typeIdx + "," + i + "] ---> " + contentBuffer);
                    write(type, titleElements.text(), contentBuffer.toString());

                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                csvPrinter.flush();
                fileWriter.flush();
                fileWriter.close();
                csvPrinter.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static void write(String type, String argument, String comment) {

        try {
            writable.clear();
            writable.add(type);
            writable.add(argument);
            writable.add(comment);
            csvPrinter.printRecord(writable);  // 向.csv文件中添加记录数据
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(0);
            try {
                fileWriter.close();
                csvPrinter.close();
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        }
    }
}
