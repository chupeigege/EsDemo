package vip.aquan.esdemo.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;
import org.springframework.data.elasticsearch.annotations.Setting;
import vip.aquan.esdemo.constants.Constants;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
// 配置分词器才需要
@Setting(settingPath = "/settings.json")
@Document(indexName = Constants.Indices_01)
public class MappingSchool {
    @Id
    @Field( type = FieldType.Keyword)
    private String id;
    @Field( type = FieldType.Keyword)
    private String name;
    @Field( type = FieldType.Keyword)
    private String location;
//    @Field( type = FieldType.Text)
//    @Field( type = FieldType.Text, analyzer = "standard")
//    private String desc;

    @Field( type = FieldType.Keyword)
    private String name2;
    @Field( type = FieldType.Keyword)
    private String location2;
//    @Field( type = FieldType.Text)
//    @Field( type = FieldType.Text, analyzer = "standard")
//    private String desc2;

}
