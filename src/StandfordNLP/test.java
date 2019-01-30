package StandfordNLP;

import edu.stanford.nlp.pipeline.*;

import java.io.*;
import java.util.Properties;
import java.util.stream.Collectors;

public class test {

    public static void main(String[] args) throws IOException {
        // set up pipeline properties
        /*Properties props = new Properties();
        props.setProperty("annotators", "tokenize,ssplit,pos,lemma,ner");
        // example customizations (these are commented out but you can uncomment them to see the results

        // disable fine grained ner
        //props.setProperty("ner.applyFineGrained", "false");

        // customize fine grained ner
        //props.setProperty("ner.fine.regexner.mapping", "example.rules");
        //props.setProperty("ner.fine.regexner.ignorecase", "true");

        // add additional rules
        //props.setProperty("ner.additional.regexner.mapping", "example.rules");
        //props.setProperty("ner.additional.regexner.ignorecase", "true");

        // add 2 additional rules files ; set the first one to be case-insensitive
        //props.setProperty("ner.additional.regexner.mapping", "ignorecase=true,example_one.rules;example_two.rules");

        // set up pipeline
        StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
        // make an example document
        String FilePath="E:\\IDEAProject\\SearchEngine\\src\\file\\2.txt";
        String FileContent;
        File file=new File(FilePath);
        FileInputStream fileInputStream=new FileInputStream(file);
        byte[] FileBuf=new byte[(int) file.length()];
        fileInputStream.read(FileBuf);
        FileContent=new String(FileBuf,"UTF-8");
/*        InputStreamReader inputStreamReader=new InputStreamReader(new FileInputStream(file),"UTF-8");
        BufferedReader bufferedReader=new BufferedReader(inputStreamReader);
        CoreDocument doc = new CoreDocument("Joe Smith is from Seattle,USA. Today is Dec 12th 2019. 2nd Workshop on Benchmarking Cyber-Physical Systems" +
                " and Internet of Things (CPS-IoTBench - co-located with CPS-IoT Week) January 22, 2019");*/
/*       CoreDocument doc=new CoreDocument(FileContent);
        // annotate the document
        pipeline.annotate(doc);
        // view results
        System.out.println("---");
        System.out.println("entities found");
        for (CoreEntityMention em : doc.entityMentions())
            System.out.println("\tdetected entity: \t"+em.text()+"\t"+em.entityType());
        System.out.println("---");
        System.out.println("tokens and ner tags");
        String tokensAndNERTags = doc.tokens().stream().map(token -> "("+token.word()+","+token.ner()+","+token.tag()+")").collect(
                Collectors.joining(" "));
        System.out.println(tokensAndNERTags);*/
        File file =new File("E:\\IDEAProject\\SearchEngine\\src\\file\\889.txt");
        StandfordNLPTextParser standfordNLPTextParser=new StandfordNLPTextParser(file);
        standfordNLPTextParser.Parse();
    }

}