package cn.itheima.lucene;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.io.FileUtils;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.document.LongPoint;
import org.apache.lucene.document.StoredField;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.store.FSDirectory;
import org.junit.Test;

import com.hankcs.lucene.HanLPAnalyzer;

public class CreateIndex {
	@Test
	public void createIndex() throws IOException {
		// 1.保存数据到索引库需要使用索引的写入对象
		// directory 标识索引库磁盘存储路径
		FSDirectory directory = FSDirectory.open(Paths.get("D:\\luceneIndex"));
		// 创建分词器对象
		Analyzer analyzer = new HanLPAnalyzer();
		// 创建索引录入工具的配置对象 将分词器交给配置对象
		IndexWriterConfig config = new IndexWriterConfig(analyzer);
		// 生成索引录入工具
		IndexWriter writer = new IndexWriter(directory, config);
		// 读取数据来源 io流读取数据文件
		File fileDir = new File("F:\\培训课程\\项目阶段\\lucene\\资料\\searchsource");
		// 获取当前文件夹的所有文件
		File[] files = fileDir.listFiles();
		int i = 0;
		for (File file : files) {
			i++;
			// 循环打印文件信息
			System.out.println("文件名：" + file.getName());
			System.out.println("文件路径" + file.getPath());
			System.out.println("文件内容" + FileUtils.readFileToString(file));
			System.out.println("文件大小" + FileUtils.sizeOf(file));
			System.out.println("------------------------");
			// 循环一次封装一个Document对象
			Document document = new Document();
			/*
			 * StringField 域字段特点：不分词 支持查询 用于存储唯一标识 00112233 TextField 分词存储 支持索引查询 常用类型域字段
			 * LongPoint 数值类型域字段 分词 查询 用于数值的范围查询 不存储在索引库 StroeField 只做数据存储 不支持索引查询
			 * Store.YES/NO 是否存储当前数据
			 */
			document.add(new StringField("fileNum", "0000" + i, Store.YES));
			document.add(new TextField("fileName", file.getName(), Store.YES));
			document.add(new StoredField("filePath", file.getPath()));
			document.add(new TextField("fileContext", FileUtils.readFileToString(file), Store.YES));
			document.add(new LongPoint("fileSize", FileUtils.sizeOf(file)));
			// 将当前封装的document文档对象添加到写入对象
			writer.addDocument(document);
		}
		writer.commit();
		writer.close();
	}

	/*
	 * 删除索引库的数据
	 */
	@Test
	public void deleteIndex() throws IOException {
		// 标记索引库位置
		FSDirectory directory = FSDirectory.open(Paths.get("D:\\luceneIndex"));
		// 定义分词器
		HanLPAnalyzer analyzer = new HanLPAnalyzer();
		// 定义IndexWriteConfig
		IndexWriterConfig config = new IndexWriterConfig(analyzer);
		// 创建indexWrite工具
		IndexWriter writer = new IndexWriter(directory, config);
		// 执行操作
		// 删除所有索引
		writer.deleteAll();
		// 根据查询结果删除数据
		// Query query = new TermQuery(new Term("fileName", "spring"));
		// writer.deleteDocuments(query);
		writer.commit();
		writer.close();
	}

	/*
	 * 删除索引库的数据
	 */
	@Test
	public void updateIndex() throws IOException {
		// 标记索引库位置
		FSDirectory directory = FSDirectory.open(Paths.get("D:\\luceneIndex"));
		// 定义分词器
		HanLPAnalyzer analyzer = new HanLPAnalyzer();
		// 定义IndexWriteConfig
		IndexWriterConfig config = new IndexWriterConfig(analyzer);
		// 创建indexWrite工具
		IndexWriter writer = new IndexWriter(directory, config);
		// 创建document存储索引数据
		Document document = new Document();
		document.add(new TextField("fileName", "测试修改唯一", Store.YES));
		document.add(new TextField("fileContext", "测试00001更新", Store.YES));
		// 执行操作
		writer.updateDocument(new Term("fileNum", "00001"), document);
	}
}
