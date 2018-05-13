package com.example.demo;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.highlight.Highlighter;
import org.apache.lucene.search.highlight.QueryScorer;
import org.apache.lucene.search.highlight.SimpleHTMLFormatter;
import org.apache.lucene.search.highlight.TextFragment;
import org.apache.lucene.search.highlight.TokenSources;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;
import org.springframework.stereotype.Component;
import org.wltea.analyzer.lucene.IKAnalyzer;
@Component
public class IndexDemo {
    //Lucene索引文件路径
    static String dir="D:\\lucence";
    //定义分词器
    static Analyzer analyzer = new IKAnalyzer();
    /**
     * 封裝一个方法，用于将数据库中的数据解析为一个个关键字词存储到索引文件中
     * @param doc
     */
    public void write(Document doc){
        Directory directory=null;
        IndexWriterConfig config=null;
        IndexWriter iwriter=null;
        try {
            //索引库的存储目录
            directory = FSDirectory.open(new File(dir));
            //关联当前lucence版本和分值器
            config = new IndexWriterConfig(Version.LUCENE_47, analyzer);
            //传入目录和分词器
            iwriter = new IndexWriter(directory, config);
            iwriter.commit();
            //写入到目录文件中
            iwriter.addDocument(doc);
            //提交事务
            iwriter.commit();
            //关闭流
            iwriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    //搜索
    public List<Map> search(String field,String value) throws Exception{

        //索引库的存储目录
        Directory directory = FSDirectory.open(new File(dir));
        //读取索引库的存储目录
        DirectoryReader ireader = DirectoryReader.open(directory);
        //搜索类
        IndexSearcher isearcher = new IndexSearcher(ireader);
        //lucence查询解析器，用于指定查询的属性名和分词器
        QueryParser parser = new QueryParser(Version.LUCENE_47, field, analyzer);
        //搜索
        Query query = parser.parse(value);
        //最终被分词后添加的前缀和后缀处理器，默认是粗体<B></B>
        SimpleHTMLFormatter htmlFormatter = new SimpleHTMLFormatter("<font color=red>","</font>");
        //高亮搜索的词添加到高亮处理器中
        Highlighter highlighter = new Highlighter(htmlFormatter, new QueryScorer(query));

        //获取搜索的结果，指定返回document返回的个数
        ScoreDoc[] hits = isearcher.search(query, null, 5).scoreDocs;
        List<Map> list=new ArrayList<Map>();
        //遍历，输出
        for (int i = 0; i < hits.length; i++) {
            int id = hits[i].doc;
            Document hitDoc = isearcher.doc(hits[i].doc);
            Map map=new HashMap();
            map.put("foodid", hitDoc.get("foodid"));

            //获取到foodname
            String foodname=hitDoc.get("foodname");
            //将查询的词和搜索词匹配，匹配到添加前缀和后缀
            TokenStream tokenStream = TokenSources.getAnyTokenStream(isearcher.getIndexReader(), id, "foodname", analyzer);
            //传入的第二个参数是查询的值
            TextFragment[] frag = highlighter.getBestTextFragments(tokenStream, foodname, false, 10);
            String foodValue="";
            for (int j = 0; j < frag.length; j++) {
                if ((frag[j] != null) && (frag[j].getScore() > 0)) {
                    //获取 foodname 的值
                    foodValue=((frag[j].toString()));
                }
            }
            map.put("foodname", foodValue);

            map.put("price", hitDoc.get("price"));
            map.put("imagepath", hitDoc.get("imagepath"));
            list.add(map);
        }
        ireader.close();
        directory.close();
        return list;
    }
}
