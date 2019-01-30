package luncene;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;


import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.highlight.*;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Searcher {
    static String IndexPath="E:\\IDEAProject\\SearchEngine\\src\\index\\";
    static Analyzer analyzer;
    static FSDirectory directory;
    static DirectoryReader directoryReader;
    static IndexSearcher searcher;
    static String querymode;
    private static QueryParser parser;
    public Searcher(String querymode) throws IOException {
        File indexpath=new File(IndexPath);
        directory=  FSDirectory.open(Paths.get(IndexPath));
        directoryReader=DirectoryReader.open(directory);
        searcher=new IndexSearcher(directoryReader);
        analyzer=new StandardAnalyzer();
        parser = new QueryParser(querymode,analyzer);
        this.querymode=querymode;
    }

    public ArrayList<Map<String,String>> Search(String querytext) throws ParseException, IOException, InvalidTokenOffsetsException {
        ArrayList<Map<String,String>> result=new ArrayList<>();
        Map<String,String> map;
        String regex1="[^0-9a-zA-Z@!~#$%^&*(),./;'\"<>: \\-=_+{}\\[\\]\\s]";
        String regex2="(Topic)|(topic)|(Track)|(track)";
        String regexdate="([a-zA-Z]+)\\s[0-9]{1,2},\\s*[0-9]{4}";
        Pattern pa=Pattern.compile(regexdate);
        Matcher ma;
        Query query=parser.parse(querytext);
        ScoreDoc[] scoreDoc=searcher.search(query,1000).scoreDocs;
        QueryScorer scorer= new QueryScorer(query);
        SimpleHTMLFormatter simpleHTMLFormatter = new SimpleHTMLFormatter("<span style='color:red'>", "</span>");
        Highlighter highlighter = new Highlighter(simpleHTMLFormatter,scorer);
        Fragmenter fragmenter=new SimpleSpanFragmenter(scorer,10000);
        highlighter.setTextFragmenter(fragmenter);
        for(int i=0;i<scoreDoc.length;i++){
            Document document=searcher.doc(scoreDoc[i].doc);
            map=new HashMap<>();
            map.put("Url",document.get("Url"));
            String Topic=document.get("Topic").replaceAll(regex1," ");
            if(Topic.length()>1000) Topic=Topic.substring(0,1000)+"...";
            map.put("Topic",Topic);
            map.put("Location",document.get("Location"));
            String Deadline=document.get("Deadline");
            ma=pa.matcher(Deadline);
            if(ma.find()) Deadline=ma.group();
            map.put("Deadline",Deadline);
            map.put("Tptime",document.get("Tptime"));
            map.put("Body",document.get("Body"));
            map.put("Score", String.valueOf(scoreDoc[i].score).substring(0,5));
            map.put("Subject",document.get("Subject").replaceAll(regex1," "));
            String content=document.get(querymode).replaceAll(regex1," ");
            if(content.indexOf(querytext)!=-1&&(querymode.equals("Topic")||querymode.equals("Body")))
                content=content.substring(content.indexOf(querytext));
            if(content.length()>1000) content=content.substring(0,1000)+"...";
            if(querymode.equals("Deadline")){
                ma=pa.matcher(content);
                if(ma.find()) content=ma.group();
            }
            TokenStream tokenStream = analyzer.tokenStream(querytext, new StringReader(content));
            content = highlighter.getBestFragment(tokenStream, content);
            System.out.println(content);
            map.put(querymode,content);
            result.add(map);
        }
        return result;
    }

    public static void setQuerymode(String querymode) {
        Searcher.querymode = querymode;
        parser=new QueryParser(querymode,analyzer);
    }

    public static void main(String [] args){

    }

}
