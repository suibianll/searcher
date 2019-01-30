package StandfordNLP;

import edu.stanford.nlp.pipeline.CoreDocument;
import edu.stanford.nlp.pipeline.CoreEntityMention;
import edu.stanford.nlp.pipeline.CoreSentence;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;

import javax.xml.stream.Location;
import java.io.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StandfordNLPTextParser {//利用自然语言处理工具来处理文本以获得时间、地点、主题等信息
    private  String Deadline;
    private  String TpTime;
    private  String Topic;
    private  String Location;
    private  String Subject;
    private  String Url;//网页的网址
    private  boolean TopicFlag;
    static File file;
    public StandfordNLPTextParser(File file){
        this.file=file;
    }
    private void GetTopics() throws IOException {//首先判断文本中是否包含topic或者interest等语句，如果有就可以直接进行抽取
        InputStreamReader reader=new InputStreamReader(new FileInputStream(file),"UTF-8");
        BufferedReader bufferedReader= new BufferedReader(reader);
        String line;
        StringBuilder stringBuilder=new StringBuilder();
        String regex = "[a-z]";
        String regexTopic="(Topic)|(Interest)|(Track)";
        final Pattern pa = Pattern.compile(regex, Pattern.DOTALL);
        Matcher matcher;
        while((line=bufferedReader.readLine())!=null){
            if(line.contains("Topic")||line.contains("topic")||line.contains("Interest")||line.contains("interest")||line.contains("track")||line.contains("Track")){
                line=bufferedReader.readLine();
                TopicFlag=true;
                while(line!=null){
                    matcher=pa.matcher(line);
                    if(matcher.find()) break;
                    else line=bufferedReader.readLine();
                }
                do{
                    stringBuilder.append(line);
                    stringBuilder.append("\r\n");
                    System.out.println(line);
                    line=bufferedReader.readLine();
                    if(line==null) break;
                    matcher=pa.matcher(line);
                }while (matcher.find());
            }
        }
        if(stringBuilder!=null)
            Topic=stringBuilder.toString();
    }
    public  void Parse() throws IOException {
        Properties props=new Properties();
        props.setProperty("annotators", "tokenize,ssplit,pos,lemma,ner");
        StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
        List<String> postag;//获取一条语句中各词的属性
        int NonNum=0;//记录语句中名词的数目
        boolean UrlFlag=false;//url
        boolean SubjectFlag=false;
        boolean DeadlineFlag=false;//截止日期
        boolean TpFlag=false;//举办时间
        boolean LocationFlag=false;//举办地点
        boolean ReadnewLine=false;
        //boolean TopicFlag=false;//主题

        InputStreamReader reader=new InputStreamReader(new FileInputStream(file),"GBK");
        BufferedReader bufferedReader= new BufferedReader(reader);
        String line = null;

        GetTopics();
        Iterator<CoreEntityMention> iterator,iterator1;
        CoreEntityMention em;
        CoreSentence sentence;
        String regex = "[a-zA-Z0-9]";
        String regexdate="(([a-zA-Z]+)\\s([0-9]{1,2})[a-zA-Z]{0,2}-*([0-9]{1,2})*[a-zA-Z]{0,2},*\\s*[0-9]{4})|(([0-9]{1,2})[a-zA-Z]{0,2}-*([0-9]{1,2})*[a-zA-Z]{0,2}\\s([a-zA-Z]+),*\\s*[0-9]{4})";
        final Pattern pa1 = Pattern.compile(regex, Pattern.DOTALL);
        final Pattern pa2=Pattern.compile(regexdate);
        Matcher matcher;
        while(true) {
            if(!ReadnewLine) {
                line = bufferedReader.readLine();
            }
            else
                ReadnewLine=false;
            if(line==null) break;
            {
                matcher=pa1.matcher(line);
                if(!matcher.find())
                    continue;//空行不用分析
            }
            CoreDocument doc= new CoreDocument(line);
            pipeline.annotate(doc);//对文件内容进行解析
            System.out.println(doc.text());
            System.out.println();
            if(line.contains("url")&&!UrlFlag) {
                Url = line.substring(4);
                UrlFlag=true;
            }
            if (line.contains("deadline") || line.contains("final")||line.contains("Deadline")) {
                if (!DeadlineFlag) {
                    matcher=pa2.matcher(line);
                    if(matcher.find())
                        Deadline =matcher.group() ;
                    DeadlineFlag = true;
                }
            }
            if(line.contains("subject")&&!SubjectFlag){
                Subject=line.substring(8);
                SubjectFlag=true;
            }
            iterator = doc.entityMentions().iterator();
            matcher=pa2.matcher(line);//查找是否有会议举办时间
            if(matcher.find()) {
                while (iterator.hasNext()) {
                    em = iterator.next();
                    System.out.println(em.entityType() + ":" + em.text());
                    if (em.entityType().equals("CITY") || em.entityType().equals("COUNTRY")) {//会议举办时间常和举办地点连在一起
                        if(!TpFlag){
                            TpTime=matcher.group();
                            TpFlag=true;
                        }
                        if (!LocationFlag) {
                            Location = em.text();
                            if (iterator.hasNext()) {
                                em = iterator.next();
                                if (em.entityType().equals("COUNTRY") || em.entityType().equals("CITY"))
                                    Location = Location + " " + em.text();
                            }
                            LocationFlag = true;
                        }
                    }
                }
                if(!LocationFlag){
                    line=bufferedReader.readLine();//多读取了一行
                    if(line==null) break;
                    ReadnewLine=true;
                    {
                        matcher=pa1.matcher(line);
                        if(!matcher.find())
                            continue;//空行不用分析
                    }
                    System.out.println(line);
                    if(line==null) continue;
                    CoreDocument doc1=new CoreDocument(line);
                    pipeline.annotate(doc1);
                    System.out.println(doc1.entityMentions());
                    iterator1 = doc1.entityMentions().iterator();
                    while (iterator1.hasNext()) {
                        em = iterator1.next();
                        System.out.println(em.entityType() + ":" + em.text());
                        if (em.entityType().equals("CITY") || em.entityType().equals("COUNTRY")) {//会议举办时间常和举办地点连在一起
                            if(!TpFlag){
                                TpTime=matcher.group();
                                TpFlag=true;
                            }
                            if (!LocationFlag) {
                                Location = em.text();
                                if (iterator1.hasNext()) {
                                    em = iterator1.next();
                                    if (em.entityType().equals("COUNTRY") || em.entityType().equals("CITY"))
                                        Location = Location + "," + em.text();
                                }
                                LocationFlag = true;
                            }
                        }
                    }
                }
            }
            if(!TopicFlag){
                for(int i=0;i<doc.sentences().size();i++){
                    sentence=doc.sentences().get(i);
                    postag=  sentence.posTags();
                    System.out.println(postag);
                    for(int j=0;j<postag.size();j++){
                        if(postag.get(j).equals("NN"))
                            NonNum++;
                    }
                    if((float)NonNum/(float) postag.size()>0.6&&postag.size()>4){//句子中名词占比非常高，则将其判定为主题句
                        Topic+=(sentence.text())+"\n";
                        System.out.println(sentence.text());
                    }
                }
            }


        }
        System.out.println();
    }


    public  String getUrl() {
        return Url;
    }

    public  String getDeadline() {
        return Deadline;
    }

    public  String getLocation() {
        return Location;
    }

    public  String getSubject() {
        return Subject;
    }

    public String getTopic() {
        return Topic;
    }

    public String getTpTime() {
        return TpTime;
    }

    public static void main(String [] args){
        File file =new File("E:\\IDEAProject\\SearchEngine\\src\\file\\10.txt");

    }
}
