package luncene;

import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.search.highlight.InvalidTokenOffsetsException;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class test {
    public static void main(String [] args){
        try {
            Searcher searcher=new Searcher("Topic");
           searcher.Search("Big Data");

        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        } catch (InvalidTokenOffsetsException e) {
            e.printStackTrace();
        }
        String regexdate="(([a-zA-Z]+)\\s([0-9]{1,2})-*([0-9]{1,2})*,*\\s*[0-9]{4})|(([0-9]{1,2})-*([0-9]{1,2})*\\s([a-zA-Z]+),*\\s*[0-9]{4})";
        String data=
                "Important Dates\n" +"15 March,2019"+
                "Paper Submission Deadline:  March 15, 2019\n" +
                "Author Notification:        April 15, 2019\n" +
                "Camera-Ready Paper Due:     May   15, 2019\n" +
                "Conference Dates:           July  14-17, 2019\n"
                ;
        Pattern pattern=Pattern.compile(regexdate);
        Matcher matcher=pattern.matcher(data);
        if (matcher.find())
            for (int i=0;i<matcher.group().length();i++)
            System.out.println(matcher.group(i));
    }
}
