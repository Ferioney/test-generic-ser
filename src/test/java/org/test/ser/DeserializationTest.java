package org.test.ser;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.cfg.ConstructorDetector;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule;
import org.junit.jupiter.api.Test;
import org.test.ser.domain.Cat;
import org.test.ser.domain.Dog;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;


public class DeserializationTest {

    private final ObjectMapper OBJECT_MAPPER = create();

    /**
     * Without children object works as expected
     */
    @Test
    void canDeserializeWhenNoChildren() throws JsonProcessingException {
        Dog actualFromJson = readFromFile("dog_no_empty_constructor_and_no_children_object.json", new TypeReference<>() {
        });

        assertThat(actualFromJson.getName()).isEqualTo("Mark Parent");
        assertThat(actualFromJson.getChildren()).isNull();
        assertThat(actualFromJson.getParent()).isNull();
    }

    /**
     * Class has empty constructor. With children object works as expected
     */
    @Test
    void canDeserializeWhenHasChildrenAndEmptyConstructor() throws JsonProcessingException {
        Cat actualFromJson = readFromFile("cat_empty_constructor_and_has_children_object.json", new TypeReference<>() {
        });

        assertThat(actualFromJson.getName()).isEqualTo("Mark Parent");
        assertThat(actualFromJson.getParent()).isNull();

        assertThat(actualFromJson.getChildren().getName()).isEqualTo("Yuki children");
        assertThat(actualFromJson.getChildren().getChildren()).isNull();
        assertThat(actualFromJson.getChildren().getParent()).isEqualTo(actualFromJson);

    }

    /**
     * Exception:
     * com.fasterxml.jackson.databind.deser.UnresolvedForwardReference:
     * Could not resolve Object Id [1] (for [simple type, class org.test.ser.domain.Dog]).
     * at [Source: REDACTED (`StreamReadFeature.INCLUDE_SOURCE_IN_LOCATION` disabled); line: 1, column: 148]
     * (through reference chain: org.test.ser.domain.Dog["children"]->org.test.ser.domain.Dog["parent"])
     */
    @Test
    void canNotDeserializeWhenHasChildren() throws JsonProcessingException {
        Dog actualFromJson = readFromFile("dog_no_empty_constructor_and_has_children_object.json", new TypeReference<>() {
        });
        assertThat(actualFromJson.getName()).isEqualTo("Mark Parent");
        assertThat(actualFromJson.getParent()).isNull();

        assertThat(actualFromJson.getChildren().getName()).isEqualTo("Yuki children");
        assertThat(actualFromJson.getChildren().getChildren()).isNull();
        assertThat(actualFromJson.getChildren().getParent()).isEqualTo(actualFromJson);
    }

    private <T> T readFromFile(String fileName, TypeReference<T> valueTypeRef) {
        Path filePath = Paths.get("src", "test", "resources", fileName);
        try (Stream<String> lines = Files.lines(filePath)) {
            String data = lines.collect(Collectors.joining("\n"));
            return OBJECT_MAPPER.readValue(data, valueTypeRef);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static ObjectMapper create() {
        ObjectMapper mapper = JsonMapper.builder()
                .constructorDetector(ConstructorDetector.USE_PROPERTIES_BASED)
                .build()
                .registerModule(new ParameterNamesModule())
                .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                .setSerializationInclusion(JsonInclude.Include.NON_NULL);

        mapper.setVisibility(mapper.getSerializationConfig().getDefaultVisibilityChecker()
                .withCreatorVisibility(JsonAutoDetect.Visibility.PUBLIC_ONLY)
                .withGetterVisibility(JsonAutoDetect.Visibility.NONE)
                .withIsGetterVisibility(JsonAutoDetect.Visibility.NONE)
                .withFieldVisibility(JsonAutoDetect.Visibility.ANY));

        return mapper;
    }
}
