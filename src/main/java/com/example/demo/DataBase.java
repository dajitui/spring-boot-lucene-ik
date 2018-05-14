package com.example.demo;

import java.util.List;
import java.util.Map;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
public class DataBase {
    @Autowired
    DataBaseDao dao;
    @Autowired
    IndexDemo indexDemo;

    @GetMapping("/createIndex")
    public String createIndex(){
        //查询数据库，必须要批量查询
        int fc = dao.foodcount();//查询总行数    8
        int start = 0;  //开始位置
        int rows = 10;   //每页行数

        while(start<=fc&&rows<=fc){
            //每拉取一次数据
            List<Map<String, Object>> queryFood=dao.queryFood(start, rows);
            //获取字段
            for(int i=0;i<queryFood.size();i++){
                //获取每行数据
                Map<String, Object> lineData = queryFood.get(i);
                //创建Document对象
                Document doc = new Document();
                //获取每列数据
                Field foodid=new Field("foodid",lineData.get("foodid").toString(),TextField.TYPE_STORED);
                Field foodname=new Field("foodname",lineData.get("foodname").toString(),TextField.TYPE_STORED);
                Field price=new Field("price",lineData.get("price").toString(),TextField.TYPE_STORED);
                Field imagepath=new Field("imagepath",lineData.get("imagepath").toString(),TextField.TYPE_STORED);
                foodname.setBoost(4f);
                //添加到Document中
                doc.add(foodid);
                doc.add(foodname);
                doc.add(price);
                doc.add(imagepath);
                //调用，创建索引库
                indexDemo.write(doc);
                System.out.println("id:"+lineData.get("foodid").toString());
            }
            start=start+10;
        }
        return "成功";
    }
    //搜索，实现高亮
    @GetMapping("/searchFood")
    public List<Map> getFood(String keyWord) throws Exception{
        System.out.println("keyWord"+keyWord);
        return indexDemo.search("foodname", keyWord);
    }

}
