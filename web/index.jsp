<%@ page import="Spider.spider" %><%--
  Created by IntelliJ IDEA.
  User: acer
  Date: 2018/12/9
  Time: 18:59
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="Java" pageEncoding="UTF-8"%>

<html>
<head>
  <title>Search</title>
</head>
<body>

<br><br>
<form method = "POST" action = "result.jsp" >

  <p align = "center"><font size = "12" face="Microsoft YaHei" color = "#f4a460">Searcher</font></p><br><br>
  <p align = "center">
    <font size = "12">
      <select id="mymodeselect" name="querymode" style="width: 80px;height: 40px">
        <option value="Topic">Topic</option>
        <option value="Deadline">Deadline</option>
        <option value="Body">Body</option>
        <option value="Tptime">Date</option>
      </select>
      <input type = "text" name = "query" style = "width:400px;height:40px" id="kw"><input type = "submit" value = "搜索" style = "width:80px;height:40px" id="su">
    </font>
  </p>
</form>
<br><br>

<style>
  body
  {
    background:url(back.jpg);
    background-size:100% 100%;
    background-repeat:no-repeat;
    padding-top:80px;
  }
</style>

</body>
</html>
