package Spider;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import javax.net.ssl.*;
import java.io.*;
import java.net.*;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class spider implements Runnable {
    String url = "https://research.cs.wisc.edu/dbworld/browse.html";
    URL u;
    HashMap<String, ConferenceInformation> InformationMap = new HashMap<>();

    @Override
    public void run() {
        Iterator iterator = InformationMap.entrySet().iterator();
        ConferenceInformation conferenceInformation;
        String weburl, conferencename, filename,deadline;
        int docnum=0;
        while (iterator.hasNext()) {
            docnum++;
            HashMap.Entry entry = (HashMap.Entry) iterator.next();
            conferenceInformation = (ConferenceInformation) entry.getValue();
            weburl = conferenceInformation.WebUrl;
            deadline=conferenceInformation.Deadline;
            conferencename = (String) entry.getKey();
            filename = "E:\\IDEAProject\\SearchEngine\\src\\file\\"+Integer.toString(docnum) + ".txt";
            try {
                PageWrite(weburl,filename,conferencename,deadline);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public class ConferenceInformation {
        public String  WebUrl,  Deadline;
        public ConferenceInformation(ArrayList<String> Table) {
            WebUrl = Table.get(0);
            Deadline = Table.get(1).replaceAll("-"," ");
        }
    }

    public spider() throws IOException {
        u = new URL(url);
        String table = null;
        URLConnection Conection = u.openConnection();
        HttpURLConnection httpURLConnection = (HttpURLConnection) Conection;
        int ResponseCode = httpURLConnection.getResponseCode();
        if (ResponseCode == HttpURLConnection.HTTP_OK) {
            InputStream inputStream = httpURLConnection.getInputStream();
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream,"UTF-8");
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            String line, ConferenceName;
            ArrayList<String> Table = new ArrayList<>();
            int docnum=0;
            while ((line = bufferedReader.readLine()) != null) {
                System.out.println(line);
                if (IsTableElement(line)) {//表中每一行有六项，分别是发布时间，发布类型，发布人，网页链接，截止日期，额外网页
                    bufferedReader.readLine();

                    bufferedReader.readLine();
                    line=bufferedReader.readLine();
                    System.out.println(line);
                    table = GetLink(line);
                    docnum++;
//                    ConferenceName =Integer.toString(docnum) ;
                    ConferenceName=GetConferenceName(line);

                    if (table != null)
                        Table.add(table);
                    else
                        Table.add("WebPage Missing");

                    line = bufferedReader.readLine();
                    System.out.println(line);
                    table = GetDateOrLocation(line);
                    if (table != null)
                        Table.add(table);

                    bufferedReader.readLine();
                    ConstructInformation(ConferenceName, Table);
                    Table.clear();
                }

            }
        } else {
            System.out.println("获取网页失败");
        }
    }



    public boolean IsTableElement(String line) {
        String regex = "<TD[^>]*>(.*?)</TD>";
        final Pattern pa = Pattern.compile(regex, Pattern.DOTALL);
        final Matcher ma = pa.matcher(line);
        if (ma.find()) {
            if (line.contains("<b>"))//表头的提示数据，不是具体的表项。
                return false;
            return true;
        }
        return false;
    }

    public String GetLink(String line) {
        String regex;
        regex = "https?://([^\"]*)\"+?";
        final Pattern pa = Pattern.compile(regex, Pattern.DOTALL);
        final Matcher ma = pa.matcher(line);
        if (ma.find()) {
            String re = ma.group();
            return re.substring(0, re.length() - 1);
        } else
            return null;
    }

    public String GetDateOrLocation(String line) {
        String regex = ">([^>]+?)<";
        final Pattern pa = Pattern.compile(regex, Pattern.DOTALL);
        final Matcher ma = pa.matcher(line);
        if (ma.find()) {
            String re = ma.group();
            return re.substring(1, re.length() - 1);
        } else
            return null;
    }

    public String GetConferenceName(String line) {
        String regex = ">([^>]+)<";
        final Pattern pa = Pattern.compile(regex, Pattern.DOTALL);
        final Matcher ma = pa.matcher(line);
        if (ma.find()) {
            String re = ma.group();
            return re.substring(1, re.length() - 1);
        } else
            return null;
    }

    public void ConstructInformation(String ConferenceName, ArrayList<String> Table) {
        ConferenceInformation node = new ConferenceInformation(Table);
        InformationMap.put(ConferenceName, node);
    }

    public void PageWrite(String weburl, String filename,String subject,String deadline ) throws IOException{
       /* u = new URL(weburl);
//        HttpClient httpClient=
        URLConnection Conection = u.openConnection();
        InputStream inputStream;
        if(weburl.contains("https")){
            TrustManager[] tm = { new MyX509TrustManager() };
            SSLContext sslContext = SSLContext.getInstance("SSL");
            sslContext.init(new KeyManager[0], tm, new SecureRandom());
            // 从上述SSLContext对象中得到SSLSocketFactory对象
            SSLSocketFactory ssf = sslContext.getSocketFactory();
            HttpsURLConnection httpsURLConnection=(HttpsURLConnection) Conection;
            httpsURLConnection.setSSLSocketFactory(ssf);
            httpsURLConnection.setHostnameVerifier(new HostnameVerifier() {
                public boolean verify(String arg0, SSLSession arg1) {
                    return true;
                }
            });
            httpsURLConnection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/61.0.3163.100 Safari/537.36)");
            //防止报403错误。
            httpsURLConnection.setDoOutput(true);
            httpsURLConnection.setDoInput(true);
            httpsURLConnection.setUseCaches(false);
            // 请求的类型
            httpsURLConnection.setRequestMethod("GET");
            if(httpsURLConnection!=null){
                inputStream = httpsURLConnection.getInputStream();
            }
            else{
                System.out.println("无法获取网页");
                return;
            }
        }
        else {
            HttpURLConnection httpURLConnection = (HttpURLConnection) Conection;
            int ResponseCode = httpURLConnection.getResponseCode();
            if (ResponseCode == HttpURLConnection.HTTP_OK) {
                inputStream = httpURLConnection.getInputStream();
            } else {
                System.out.println("无法打开网页");
                return;
            }
        }
        InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
        FileOutputStream fileOutputStream = new FileOutputStream(new File(filename));
        OutputStreamWriter outputStreamWriter = new OutputStreamWriter(fileOutputStream, "UTF-8");
        BufferedWriter bufferedWriter = new BufferedWriter(outputStreamWriter);
        String line;
        while ((line = bufferedReader.readLine()) != null) {
            bufferedWriter.write(line + "\n");
        }
        bufferedWriter.close();
        outputStreamWriter.close();
        fileOutputStream.close();*/
       String WebPage=getSource(weburl);//获取网页内容
       Document document= Jsoup.parse(WebPage);
       StringBuilder stringBuilder=new StringBuilder();
       stringBuilder.append("subject:"+subject+"\n");
       stringBuilder.append("url:"+weburl+"\n");
       stringBuilder.append("deadline:"+deadline+"\n");
       stringBuilder.append(document.text());//将去除标签后的内容添加进去
       String FileContent=stringBuilder.toString();
       File file=new File(filename);
       Writer out =new FileWriter(file);
       out.write(FileContent);
       out.close();
    }
    public String getSource(String url) {
        String html = new String();
        if(url=="WebPage Missing") return " ";
        HttpGet httpget = new HttpGet(url);     //创建Http请求实例，URL 如：https://cd.lianjia.com/
        // 模拟浏览器，避免被服务器拒绝，返回返回403 forbidden的错误信息
        httpget.setHeader("User-Agent",
                "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/63.0.3239.84 Safari/537.36");

        CloseableHttpResponse response = null;
        CloseableHttpClient httpclient = HttpClients.createDefault();   // 使用默认的HttpClient
        try {
            response = httpclient.execute(httpget);
            if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {     // 返回 200 表示成功
                html = EntityUtils.toString(response.getEntity(), "utf-8");     // 获取服务器响应实体的内容
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (response != null) {
                try {
                    response.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return html;
    }
    public class MyX509TrustManager implements X509TrustManager {
        public void checkClientTrusted(X509Certificate[] chain, String authType)
                throws CertificateException
        {
        }

        public void checkServerTrusted(X509Certificate[] chain, String authType)
                throws CertificateException
        {
        }

        public X509Certificate[] getAcceptedIssuers()
        {
            return null;
        }
    }

}
