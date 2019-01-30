package luncene;

import StandfordNLP.StandfordNLPTextParser;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.document.*;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.store.IOContext;
import org.apache.lucene.store.RAMDirectory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;

public class Index {
    private static String FilePath="E:\\IDEAProject\\SearchEngine\\src\\file\\";
    private static String IndexPath="E:\\IDEAProject\\SearchEngine\\src\\index\\";

    static Analyzer analyzer;              //分词器

    static Directory fsdirectory;          //索引放到磁盘中
    static Directory ramdirectory;         //索引放到内存中
    static IndexWriterConfig writerConfig; //索引配置
    static IndexWriter writer;              //索引写
    static File []files;                    //一个目录下的所有文件
//    Document document;

    private static void Init() throws IOException {
        analyzer=new StandardAnalyzer();
        writerConfig=new IndexWriterConfig(analyzer);
        writerConfig.setOpenMode(IndexWriterConfig.OpenMode.CREATE_OR_APPEND);// 设置索引库的打开模式：新建、追加、新建或追加
        writerConfig.setRAMBufferSizeMB(4000);
        ramdirectory=new RAMDirectory();
        fsdirectory=FSDirectory.open(Paths.get(IndexPath));
        writer=new IndexWriter(ramdirectory,writerConfig);
        files=new File(FilePath).listFiles();
    }

    private static void CreateIndex(File file) throws IOException {
        StandfordNLPTextParser standfordNLPTextParser=new StandfordNLPTextParser(file);
        standfordNLPTextParser.Parse();
        Document document=new Document();
        String Deadline=standfordNLPTextParser.getDeadline();
        if(Deadline==null) Deadline="Missing";
        String Location=standfordNLPTextParser.getLocation();
        if(Location==null) Location="Missing";
        String Topic=standfordNLPTextParser.getTopic();
        if(Topic==null) Topic="Missing";
        String Url=standfordNLPTextParser.getUrl();//
        if(Url==null) Url="Missing";
        String Tptime=standfordNLPTextParser.getTpTime();//获取会议举行时间
        if(Tptime==null) Tptime="Missing";
        String Suject=standfordNLPTextParser.getSubject();
        if(Suject==null) Suject="Missing";
        document.add(new TextField("Deadline",Deadline, Field.Store.YES));
        document.add(new TextField("Location",Location, Field.Store.YES));
        document.add(new TextField("Topic",Topic, Field.Store.YES));
        document.add(new TextField("Url",Url, Field.Store.YES));
        document.add(new TextField("Tptime",Tptime, Field.Store.YES));
        document.add((new TextField("Subject",Suject, Field.Store.YES)));
        String FileContent;
        FileInputStream fileInputStream=new FileInputStream(file);
        byte[] FileBuf=new byte[(int) file.length()];
        fileInputStream.read(FileBuf);
        FileContent=new String(FileBuf,"UTF-8");
        /*读入文件内容*/
        document.add(new TextField("Body",FileContent, Field.Store.YES));
        writer.addDocument(document);
        writer.flush();
        writer.commit();
    }

    public static void main(String[] args) throws IOException {
        Init();
        int i=0;
        for(File file:files){
            i++;
            System.out.println("***************为第"+i+"个文件建立索引******************");
            CreateIndex(file);
        }
        writer.forceMerge(1);
        writer.close();

        for(String file:ramdirectory.listAll()){
            fsdirectory.copyFrom(ramdirectory, file, file, IOContext.DEFAULT);
        }

    }
}
