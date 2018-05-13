<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head>
    <meta name="keywords" content="keyword1,keyword2,keyword3">
    <meta name="description" content="this is my page">
    <meta name="content-type" content="text/html; charset=UTF-8">
    <script src="https://code.jquery.com/jquery-3.1.1.min.js"></script>
    <script type="text/javascript">
        $(function(){
            $("#searchBtn").click(function(){
                $.ajax({
                    url:'/searchFood',
                    data:'keyWord='+$("input[name='keyWord']").val(),
                    success:function(jd){
                        $("div[name='c']").remove();
                        for(var i=0;i<jd.length;i++){
                            var html=
                                "<div name='c'>"+
                                "<h3>"+jd[i].foodname+"</h3>"+
                                "<span>"+jd[i].foodname+"价格是："+jd[i].price+"</span>"+
                                "<ht>"+
                                "</div>"
                            $("#foodDiv").append(html);
                        }
                    }
                })
            })
        })
    </script>
</head>
<body>
<div align="center">
    <img src="log.png" style="height:55px;width: 190px;"><br>
    <table>
        <input type="text" name="keyWord" style="width:450px; height:30px;">
        <input type="button" id="searchBtn" value="搜索一下" style="height: 37px;background-color: #38f">
    </table>
    <div id="foodDiv" align="justify">
    </div>
</div>
</body>
</html>