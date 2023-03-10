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

    //    ????????????API
    @Test
    public void createIndices() throws IOException {
        //1.????????????
        CreateIndexRequest createIndexRequest = new CreateIndexRequest(Constants.Indices_01);
        //2.?????????????????????
        CreateIndexResponse createIndexResponse = restHighLevelClient.indices()
                .create(createIndexRequest, RequestOptions.DEFAULT);
        System.out.println("?????????????????????" + createIndexResponse);
    }

    @Test
    public void getIndices() throws IOException {
        GetIndexRequest getIndexRequest = new GetIndexRequest(Constants.Indices_01);
        boolean exists = restHighLevelClient.indices()
                .exists(getIndexRequest, RequestOptions.DEFAULT);
        System.out.println("???????????????????????????" + exists);
        GetIndexResponse getIndexResponse = restHighLevelClient.indices()
                .get(getIndexRequest, RequestOptions.DEFAULT);
        System.out.println("?????????????????????" + getIndexResponse);
    }

    @Test
    public void delIndices() throws IOException {
        DeleteIndexRequest deleteIndexRequest = new DeleteIndexRequest(Constants.Indices_01);
        AcknowledgedResponse delete = restHighLevelClient.indices()
                .delete(deleteIndexRequest, RequestOptions.DEFAULT);
        System.out.println("?????????????????????" + delete.isAcknowledged());
    }


    //    ????????????API
    @Test
    public void createDoc() throws IOException {
        EsSchool esSchool = new EsSchool("1","??????????????????3", "1,1,0,1", "???????????????????????????");
        IndexRequest indexRequest = new IndexRequest(Constants.Indices_01);
        //?????????put /index/_doc/1
        indexRequest.id("3");
        indexRequest.timeout(TimeValue.timeValueSeconds(1));
        indexRequest.timeout("1s");

        indexRequest.source(JSON.toJSONString(esSchool), XContentType.JSON);

        IndexResponse index = restHighLevelClient.index(indexRequest, RequestOptions.DEFAULT);
        System.out.println("???????????????" + index.toString() + " status=" + index.status());

    }

    //   ??????elasticsearchRestTemplate????????????
    @Test
    public void createDocByRestTemplate() throws IOException {
        EsSchool esSchool = new EsSchool("2","??????????????????ByTemplate", "1,1,0,1", "????????????");
        final EsSchool save = elasticsearchRestTemplate.save(esSchool);
        System.out.println("???????????????" + save);
    }

    //   ??????elasticsearchRestTemplate????????????
    @Test
    public void createDocByRestTemplate2() throws IOException {
        EsSchool2 esSchool2 = new EsSchool2("22","??????????????????ByTemplate", "1,1,0,1", "????????????");
        final EsSchool2 save2 = elasticsearchRestTemplate.save(esSchool2);
        System.out.println("????????????2???" + save2);
    }


    @Test
    public void getDoc() throws IOException {
        GetRequest getRequest = new GetRequest(Constants.Indices_01, "3");
        //?????????????????? _source ???????????????
//        getRequest.fetchSourceContext(new FetchSourceContext(false));
//        getRequest.storedFields("_none_");
        boolean exists = restHighLevelClient.exists(getRequest, RequestOptions.DEFAULT);
        System.out.println("???????????????????????????" + exists);

        GetResponse documentFields = restHighLevelClient.get(getRequest, RequestOptions.DEFAULT);
        System.out.println("?????????????????????" + documentFields.getSourceAsString());
    }

    @Test
    public void updateDoc() throws IOException {
        UpdateRequest updateRequest = new UpdateRequest(Constants.Indices_01, "1");
        updateRequest.timeout("1s");
        EsSchool esSchool = new EsSchool("1","??????????????????", "1,1,0,1", "???????????????????????????2");
        updateRequest.doc(JSON.toJSONString(esSchool), XContentType.JSON);
        UpdateResponse update = restHighLevelClient.update(updateRequest, RequestOptions.DEFAULT);
        System.out.println("?????????????????????status=" + update.status());
    }

    @Test
    public void delDoc() throws IOException {
        DeleteRequest deleteRequest = new DeleteRequest(Constants.Indices_01, "1");
        deleteRequest.timeout("1s");

        DeleteResponse delete = restHighLevelClient.delete(deleteRequest, RequestOptions.DEFAULT);
        System.out.println("?????????????????????status=" + delete.status());
    }

    //??????????????????
    @Test
    public void batchAddDoc() throws IOException {
        BulkRequest bulkRequest = new BulkRequest(Constants.Indices_01);
        bulkRequest.timeout("10s");
        List<EsSchool> list = Lists.newArrayList();
        for (int i = 0; i < 20; i++) {
            EsSchool esSchool = new EsSchool(i+"","*??????????????????" + i, "1,0,1", "???????????????????????????3");
            list.add(esSchool);
        }

        //???????????????
        for (int i = 0; i < list.size(); i++) {
            bulkRequest.add(new IndexRequest(Constants.Indices_01)
                    .id("" + (i +1))
                    .source(JSON.toJSONString(list.get(i)), XContentType.JSON)
            );
        }

        BulkResponse bulk = restHighLevelClient.bulk(bulkRequest, RequestOptions.DEFAULT);
        //hasFailures(): ???????????? ??????false ????????????
        System.out.println("???????????????????????????status=" + bulk.status().getStatus());
        System.out.println("???????????????????????????hasFailures=" + bulk.hasFailures());
    }

    //????????????
    @Test
    public void testSearch() throws IOException {
        SearchRequest searchRequest = new SearchRequest(Constants.Indices_01);
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();

        //termQuery ????????????
        // ??????????????????
        BoolQueryBuilder boolBuilder = QueryBuilders.boolQuery();
        boolBuilder.must(QueryBuilders.termQuery("name", "??????????????????ByTemplate"));

        //????????????
        BoolQueryBuilder keyWordBuilder = QueryBuilders.boolQuery();
        String keyword = "??????";
        keyWordBuilder.should(QueryBuilders.wildcardQuery("name", "*"+keyword+"*"));
        keyWordBuilder.should(QueryBuilders.wildcardQuery("location", "*"+keyword+"*"));
        boolBuilder.must(keyWordBuilder);
        //Bean???????????????@Field( type = FieldType.Keyword)????????????????????????????????????
//        boolBuilder.must(QueryBuilders.termQuery("name.keyword", "??????????????????0"));
//        TermQueryBuilder termQueryBuilder = QueryBuilders.termQuery("name", "??????????????????0");
        //matchAllQuery ????????????
//        MatchAllQueryBuilder matchAllQueryBuilder1 = QueryBuilders.matchAllQuery();
        searchSourceBuilder.query(boolBuilder);
        //????????????
//        searchSourceBuilder.highlighter();
        //?????????
//        searchSourceBuilder.from();
//        searchSourceBuilder.size();
        searchSourceBuilder.timeout(new TimeValue(60, TimeUnit.SECONDS));

        searchRequest.source(searchSourceBuilder);
        SearchResponse search = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);

        System.out.println("???????????????" + JSON.toJSONString(search.getHits()));
        //????????????????????????
        SearchHits hits = search.getHits();
        Arrays.stream(hits.getHits()).forEach(e -> {
            System.out.println(e.getSourceAsString());
        });
    }
}
