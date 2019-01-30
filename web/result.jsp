<%@ page import="Spider.spider" %>
<%@ page import="static sun.misc.MessageUtils.out" %>
<%@ page import="luncene.Searcher" %>
<%@ page import="java.util.Map" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="org.apache.lucene.queryparser.classic.ParseException" %>
<%@ page import="org.apache.lucene.search.highlight.InvalidTokenOffsetsException" %>
<%--
  Created by IntelliJ IDEA.
  User: acer
  Date: 2018/12/9
  Time: 19:44
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=GBK" language="java" %>


<%
    String querytext,querymode,history,querypage;
    request.setCharacterEncoding("UTF-8");
    querytext=request.getParameter("query");
    querymode=request.getParameter("querymode");
    System.out.println(querytext);
    System.out.println(querymode);
%>

<html>
<head>
    <title>搜索结果</title>
</head>
<body bgcolor="#f0ffff">
<br><br>
<form method="post" action="result.jsp">
    <select id="mymodeselect" name="querymode" style="width: 80px;height: 40px">
        <option value="Topic">Topic</option>
        <option value="Deadline">Deadline</option>
        <option value="Body">Body</option>
        <option value="Tptime">Date</option>
    </select>
    <input type = "text" name = "query" style = "width:400px;height:40px" id="kw" value="<%=querytext%>"><%--填入刚刚搜索过的值--%>
    <input type = "submit" value = "搜索" style = "width:80px;height:40px" id="su">
    <input type="submit" value="跳转到" style="width: 80px;height: 40px" id="pas">
    <input type="text" name="page" style="width: 80px;height: 30px" id="pa">
    页
</form>
<%
    Searcher search=new Searcher(querymode);
    ArrayList<Map<String,String>> result = null;
    Map<String,String> map;
    try {
        result=search.Search(querytext);
    } catch (ParseException e) {
        e.printStackTrace();
    } catch (InvalidTokenOffsetsException e) {
        e.printStackTrace();
    }
    int i,Page;
    Page=1;
    querypage=request.getParameter("page");//获取页数
    if(querypage!=null&&querypage.length()!=0)
        Page=Integer.parseInt(querypage);
    if(result!=null&&result.size()>0&&Page<=result.size()/5+1){

        out.println("<font color=\"gray\" size=\"3\">");
        out.println("<div>");
        out.println("总计"+result.size()+"个结果"+"  "+(result.size()/5+1)+"页");
        out.println("当前在第"+Page+"页");
        out.println("</div>");
        out.println("</font>");

        out.println("<table border=\"1\">");
        out.println("<thead>");
        out.println("<tr>");
        out.println("<th>Subject</th>");
        out.println("<th>Deadline</th>");
        out.println("<th>Location</th>");
        out.println("<th>Date</th>");
        out.println("</tr>");
        out.println("</thead>");
        String strSubject,strDeadline,strLocation,strDate,strTopic,strUrl,strScore;
        for(i=(Page-1)*5;i<result.size()&&i<(Page-1)*5+5;i++){
            map=result.get(i);
            strSubject=map.get("Subject");
            strUrl=map.get("Url");
            strDeadline=map.get("Deadline");
            strLocation=map.get("Location");
            strDate=map.get("Tptime");
            strTopic=map.get("Topic");
            strScore=map.get("Score");
            out.println("<tbody>");

            out.println("<tr>");

            out.println("<td>");
            out.println("<font color = \"blue\" size = \"4\">");
            out.print("<a href='"); out.print(strUrl);out.print("'>");out.print(strSubject);out.println("</a>");
            out.println("</font>" + "<br>");
            out.println("</td>");

            out.println("<td>"+strDeadline+"</td>");
            out.println("<td>"+strLocation+"</td>");
            out.println("<td>"+strDate+"</td>");

            out.println("</tr>");
            out.println("<tr>");
            out.println("<td>");
            out.println("<pre style=\"white-space: pre-line;\">") ;
            out.println("Topics:\r\n"+strTopic);
            out.println("</pre>");
            out.println("</td>");
            out.println("<td>Score:\r\n"+strScore+"</td>");
            out.println("</tr>");
            out.println("</tbody>");
        }
        out.println("</table>");
    }
    else if(result!=null&&Page>result.size()/10+1){
        out.println("<font color=\"red\" size=\"4\">"+"超出页面范围"+"</font>");
    }
    else{
        out.println("<font color=\"red\" size=\"4\">"+"抱歉未找到:"+querytext+"</font><br>");
    }
%>
</body>
</html>
