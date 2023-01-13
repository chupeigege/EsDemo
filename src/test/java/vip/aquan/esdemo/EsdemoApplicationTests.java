package vip.aquan.esdemo;

import com.alibaba.fastjson.JSON;
import org.assertj.core.util.Lists;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.support.master.AcknowledgedResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.client.indices.CreateIndexResponse;
import org.elasticsearch.client.indices.GetIndexRequest;
import org.elasticsearch.client.indices.GetIndexResponse;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.document.Document;
import org.springframework.data.elasticsearch.core.index.Settings;
import org.springframework.test.context.junit4.SpringRunner;
import vip.aquan.esdemo.constants.Constants;
import vip.aquan.esdemo.entity.EsSchool;
import vip.aquan.esdemo.entity.EsSchool2;
import vip.aquan.esdemo.entity.MappingSchool;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

@RunWith(SpringRunner.class)
@SpringBootTest
class EsdemoApplicationTests {

    @Autowired
    RestHighLevelClient restHighLevelClient;
    @Autowired
    ElasticsearchRestTemplate elasticsearchRestTemplate;

    @BeforeEach
    void setUp() {
        if (!elasticsearchRestTemplate.indexOps(MappingSchool.class).exists()) {
            Document mapping = elasticsearchRestTemplate.indexOps(MappingSchool.class).createMapping(MappingSchool.class);
            final Settings settings = elasticsearchRestTemplate.indexOps(MappingSchool.class).createSettings(MappingSchool.class);
            elasticsearchRestTemplate.indexOps(MappingSchool.class).create(settings, mapping);
        }
    }

    @Test
    void contextLoads() {
    }

    //    测试索引API
    @Test
    public void createIndices() throws IOException {
        //1.创建索引
        CreateIndexRequest createIndexRequest = new CreateIndexRequest(Constants.Indices_01);
        //2.客户端执行请求
        CreateIndexResponse createIndexResponse = restHighLevelClient.indices()
                .create(createIndexRequest, RequestOptions.DEFAULT);
        System.out.println("创建索引结果：" + createIndexResponse);
    }

    @Test
    public void getIndices() throws IOException {
        GetIndexRequest getIndexRequest = new GetIndexRequest(Constants.Indices_01);
        boolean exists = restHighLevelClient.indices()
                .exists(getIndexRequest, RequestOptions.DEFAULT);
        System.out.println("判断索引是否存在：" + exists);
        GetIndexResponse getIndexResponse = restHighLevelClient.indices()
                .get(getIndexRequest, RequestOptions.DEFAULT);
        System.out.println("获取索引结果：" + getIndexResponse);
    }

    @Test
    public void delIndices() throws IOException {
        DeleteIndexRequest deleteIndexRequest = new DeleteIndexRequest(Constants.Indices_01);
        AcknowledgedResponse delete = restHighLevelClient.indices()
                .delete(deleteIndexRequest, RequestOptions.DEFAULT);
        System.out.println("删除索引结果：" + delete.isAcknowledged());
    }


    //    测试文档API
    @Test
    public void createDoc() throws IOException {
        EsSchool esSchool = new EsSchool("1","深圳南山小学3", "1,1,0,1", "这是一座美丽的学校");
        IndexRequest indexRequest = new IndexRequest(Constants.Indices_01);
        //规则：put /index/_doc/1
        indexRequest.id("3");
        indexRequest.timeout(TimeValue.timeValueSeconds(1));
        indexRequest.timeout("1s");

        indexRequest.source(JSON.toJSONString(esSchool), XContentType.JSON);

        IndexResponse index = restHighLevelClient.index(indexRequest, RequestOptions.DEFAULT);
        System.out.println("创建文档：" + index.toString() + " status=" + index.status());

    }

    //   根据elasticsearchRestTemplate创建文档
    @Test
    public void createDocByRestTemplate() throws IOException {
        EsSchool esSchool = new EsSchool("2","深圳南山小学ByTemplate", "1,1,0,1", "模板创建");
        final EsSchool save = elasticsearchRestTemplate.save(esSchool);
        System.out.println("创建文档：" + save);
    }

    //   根据elasticsearchRestTemplate创建文档
    @Test
    public void createDocByRestTemplate2() throws IOException {
        EsSchool2 esSchool2 = new EsSchool2("22","深圳南山小学ByTemplate", "1,1,0,1", "模板创建");
        final EsSchool2 save2 = elasticsearchRestTemplate.save(esSchool2);
        System.out.println("创建文档2：" + save2);
    }


    @Test
    public void getDoc() throws IOException {
        GetRequest getRequest = new GetRequest(Constants.Indices_01, "3");
        //不获取返回的 _source 的上下文：
//        getRequest.fetchSourceContext(new FetchSourceContext(false));
//        getRequest.storedFields("_none_");
        boolean exists = restHighLevelClient.exists(getRequest, RequestOptions.DEFAULT);
        System.out.println("判断文档是否存在：" + exists);

        GetResponse documentFields = restHighLevelClient.get(getRequest, RequestOptions.DEFAULT);
        System.out.println("获取文档内容：" + documentFields.getSourceAsString());
    }

    @Test
    public void updateDoc() throws IOException {
        UpdateRequest updateRequest = new UpdateRequest(Constants.Indices_01, "1");
        updateRequest.timeout("1s");
        EsSchool esSchool = new EsSchool("1","深圳福田小学", "1,1,0,1", "这是一座美丽的学校2");
        updateRequest.doc(JSON.toJSONString(esSchool), XContentType.JSON);
        UpdateResponse update = restHighLevelClient.update(updateRequest, RequestOptions.DEFAULT);
        System.out.println("更新文档结果：status=" + update.status());
    }

    @Test
    public void delDoc() throws IOException {
        DeleteRequest deleteRequest = new DeleteRequest(Constants.Indices_01, "1");
        deleteRequest.timeout("1s");

        DeleteResponse delete = restHighLevelClient.delete(deleteRequest, RequestOptions.DEFAULT);
        System.out.println("删除文档结果：status=" + delete.status());
    }

    //批量保存数据
    @Test
    public void batchAddDoc() throws IOException {
        BulkRequest bulkRequest = new BulkRequest(Constants.Indices_01);
        bulkRequest.timeout("10s");
        List<EsSchool> list = Lists.newArrayList();
        for (int i = 0; i < 20; i++) {
            EsSchool esSchool = new EsSchool(i+"","*深圳随机小学" + i, "1,0,1", "这是一座美丽的学校3");
            list.add(esSchool);
        }

        //批处理请求
        for (int i = 0; i < list.size(); i++) {
            bulkRequest.add(new IndexRequest(Constants.Indices_01)
                    .id("" + (i +1))
                    .source(JSON.toJSONString(list.get(i)), XContentType.JSON)
            );
        }

        BulkResponse bulk = restHighLevelClient.bulk(bulkRequest, RequestOptions.DEFAULT);
        //hasFailures(): 是否失败 返回false 代表成功
        System.out.println("批量保存文档结果：status=" + bulk.status().getStatus());
        System.out.println("批量保存文档结果：hasFailures=" + bulk.hasFailures());
    }

    //搜索数据
    @Test
    public void testSearch() throws IOException {
        SearchRequest searchRequest = new SearchRequest(Constants.Indices_01);
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();

        //termQuery 精确匹配
        // 符合条件查询
        BoolQueryBuilder boolBuilder = QueryBuilders.boolQuery();
        boolBuilder.must(QueryBuilders.termQuery("name", "深圳南山小学ByTemplate"));

        //模糊查询
        BoolQueryBuilder keyWordBuilder = QueryBuilders.boolQuery();
        String keyword = "小学";
        keyWordBuilder.should(QueryBuilders.wildcardQuery("name", "*"+keyword+"*"));
        keyWordBuilder.should(QueryBuilders.wildcardQuery("location", "*"+keyword+"*"));
        boolBuilder.must(keyWordBuilder);
        //Bean的属性没有@Field( type = FieldType.Keyword)才需要这样写，否则查不到
//        boolBuilder.must(QueryBuilders.termQuery("name.keyword", "深圳随机小学0"));
//        TermQueryBuilder termQueryBuilder = QueryBuilders.termQuery("name", "深圳随机小学0");
        //matchAllQuery 匹配所有
//        MatchAllQueryBuilder matchAllQueryBuilder1 = QueryBuilders.matchAllQuery();
        searchSourceBuilder.query(boolBuilder);
        //设置高亮
//        searchSourceBuilder.highlighter();
        //分页：
//        searchSourceBuilder.from();
//        searchSourceBuilder.size();
        searchSourceBuilder.timeout(new TimeValue(60, TimeUnit.SECONDS));

        searchRequest.source(searchSourceBuilder);
        SearchResponse search = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);

        System.out.println("搜索结果：" + JSON.toJSONString(search.getHits()));
        //打印命中文档数据
        SearchHits hits = search.getHits();
        Arrays.stream(hits.getHits()).forEach(e -> {
            System.out.println(e.getSourceAsString());
        });
    }
}
