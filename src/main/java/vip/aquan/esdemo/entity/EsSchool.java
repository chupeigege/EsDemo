package vip.aquan.esdemo.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;
import vip.aquan.esdemo.constants.Constants;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(indexName = Constants.Indices_01)
public class EsSchool {
    @Id
    @Field( type = FieldType.Keyword)
    private String id;
    @Field( type = FieldType.Keyword)
    private String name;
    @Field( type = FieldType.Keyword)
    private String location;
    @Field( type = FieldType.Text)
//    @Field( type = FieldType.Text, analyzer = "standard")
    private String desc;

}
