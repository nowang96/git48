package cn.itheima.lucene;

import java.io.IOException;
import java.nio.file.Paths;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.LongPoint;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.MatchAllDocsQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.BooleanClause.Occur;
import org.apache.lucene.store.FSDirectory;
import org.junit.Test;

import com.hankcs.lucene.HanLPAnalyzer;

public class QueryIndex {
	//查询索引库的所有文件数据
	@Test
	public void queryAllIndex() throws IOException {
		Query query = new MatchAllDocsQuery();
		doQuery(query);
	}
	//根据词条查询数据
	@Test
	public void queryByTerm() throws IOException {
		//创建支持词条查询的对象
		Query query = new TermQuery(new Term("fileName", "厉"));
		doQuery(query);
	}
	//根据文档大小查询
	@Test
	public void queryByDocumentSize() throws IOException {
		Query query = LongPoint.newRangeQuery("fileSize", 10, 500);
		doQuery(query);
	}
	//组合方式查询
	@Test
	public void queryByBoolean() throws IOException {
		//创建多个词条查询对象
		TermQuery termQuery1 = new TermQuery(new Term("fileName", "厉"));
		TermQuery termQuery2 = new TermQuery(new Term("fileName", "传"));
		//子查询的语句对象指定查询对象的限制
		/*
		 * MUST表示必须匹配
		 * MUST_NOT 不能匹配
		 * SHOULD 可以有
		 * */
		//定义逻辑子查询语句
		BooleanClause clause1 = new BooleanClause(termQuery1, Occur.MUST);
		BooleanClause clause2 = new BooleanClause(termQuery2, Occur.SHOULD);
		//创建逻辑查询对象  并添加自查询语句
		BooleanQuery query = new BooleanQuery.Builder().add(clause1).add(clause2).build();
		doQuery(query);
	}
	//单域字段分词查询
	@Test
	public void queryByStr() throws IOException, ParseException {
		//定义关键词
		String queryStr="传智播客spring";
		//2.创建解析字符串的对象
		//参数1是要解析的域字段，参数2是分词器
		QueryParser parser = new QueryParser("fileName", new HanLPAnalyzer());
		//解析字符串获得查询对象
		Query query = parser.parse(queryStr);
		doQuery(query);
	}
	//多域字段分词查询
	@Test
	public void queryByMultiFiled() throws IOException, ParseException {
		//定义关键词
		String queryStr="传智播客不明觉厉spring";
		//字段域数组
		String[]fields={"fileName","fileContext"};
		//多域字段分词查询对象
		MultiFieldQueryParser parser = new MultiFieldQueryParser(fields, new HanLPAnalyzer());
		Query query = parser.parse(queryStr);
		doQuery(query);
	}
	// 通过查询对象 获得查询结果
	private void doQuery(Query query) throws IOException {
		// 标记索引库磁盘位置
		FSDirectory directory = FSDirectory.open(Paths.get("D:\\luceneIndex"));
		// 创建索引的读取对象
		DirectoryReader reader = DirectoryReader.open(directory);
		// 创建索引查询工具对象(索引搜索工具)
		IndexSearcher searcher = new IndexSearcher(reader);
		// 通过搜索工具亦定义的查询方式查询获得数据
		// TopDocs对象时查询结果 包含文档id的数组和每个文档的得分
		TopDocs topDocs = searcher.search(query, 100);
		// 匹配结果数量
		System.out.println("文档的总命中数量：" + topDocs.totalHits);
		// 查询文档id的数组 和每个文档的得分
		ScoreDoc[] scoreDocs = topDocs.scoreDocs;
		System.out.println(scoreDocs.length);
		for (ScoreDoc scoreDoc : scoreDocs) {
			System.out.println("文档得分："+scoreDoc.score);
			System.out.println("文档id："+scoreDoc.doc);
			//通过文档ID获取文档数据
			Document document = searcher.doc(scoreDoc.doc);
			System.out.println("文档的name:"+document.get("fileName"));
			System.out.println("文档的path:"+document.get("filePath"));
			System.out.println("文档的fileSize:"+document.get("fileSize"));
			System.out.println("======================");
			//System.out.println("文档的fileContext:"+document.get("fileContext"));
		}
	}
}
