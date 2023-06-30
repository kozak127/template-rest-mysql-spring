package org.kozak127.templates.restmysqlspring.apple;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class AppleIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private TestRestTemplate restTemplate;

    @AfterEach
    void cleanDb() {
        mongoTemplate.remove(new Query(), Apple.class);
    }

    @Test
    void shouldReturnApple() {
        // GIVEN
        Apple apple = Apple.builder()
                .id("apple-id")
                .name("Red Apple")
                .build();
        mongoTemplate.save(apple);

        // WHEN
        ResponseEntity<String> response = restTemplate.getForEntity(
                "http://localhost:" + port + "/api/apples/{id}", String.class, "apple-id");

        // THEN
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getHeaders().getContentType()).isEqualTo(MediaType.APPLICATION_JSON);
        assertThat(response.getBody()).isEqualTo("{\"id\":\"apple-id\",\"name\":\"Red Apple\"}");
    }

    @Test
    void shouldNotReturnApple_invalidId() {
        // GIVEN
        Apple apple = Apple.builder()
                .id("apple-id")
                .name("Red Apple")
                .build();
        mongoTemplate.save(apple);

        // WHEN
        ResponseEntity<String> response = restTemplate.getForEntity(
                "http://localhost:" + port + "/api/apples/{id}", String.class, "invalid-id");

        // THEN
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void shouldSaveApple() {
        // GIVEN
        AppleDTO appleDTO = AppleDTO.builder()
                .id("apple-id")
                .name("Red Apple")
                .build();

        // WHEN
        ResponseEntity<AppleDTO> response = restTemplate.postForEntity(
                "http://localhost:" + port + "/api/apples", appleDTO, AppleDTO.class);

        // THEN
        List<Apple> applesInDb = mongoTemplate.findAll(Apple.class);

        assertThat(applesInDb)
                .hasSize(1)
                .containsExactlyInAnyOrder(Apple.fromDto(appleDTO));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getHeaders().getContentType()).isEqualTo(MediaType.APPLICATION_JSON);
        assertThat(response.getBody()).isEqualTo(appleDTO);
    }

    @Test
    void shouldDeleteApple() {
        // GIVEN
        Apple apple = Apple.builder()
                .id("apple-id")
                .name("Red Apple")
                .build();
        mongoTemplate.save(apple);

        // WHEN
        restTemplate.delete("http://localhost:" + port + "/api/apples/{id}", "apple-id");

        // THEN
        List<Apple> applesInDb = mongoTemplate.findAll(Apple.class);

        assertThat(applesInDb).isEmpty();
    }

    @Test
    void shouldNotDeleteApple_invalidId() {
        // GIVEN
        Apple apple = Apple.builder()
                .id("apple-id")
                .name("Red Apple")
                .build();
        mongoTemplate.save(apple);

        // WHEN
        restTemplate.delete("http://localhost:" + port + "/api/apples/{id}", "invalid-apple-id");

        // THEN
        List<Apple> applesInDb = mongoTemplate.findAll(Apple.class);

        assertThat(applesInDb)
                .hasSize(1)
                .containsExactlyInAnyOrder(apple);
    }

    @Test
    void shouldUpdateApple() {
        // GIVEN
        Apple apple = Apple.builder()
                .id("apple-id")
                .name("Red Apple")
                .build();
        mongoTemplate.save(apple);

        AppleDTO updatedAppleDTO = AppleDTO.builder()
                .id("apple-id")
                .name("Green Apple")
                .build();

        // WHEN
        restTemplate.put("http://localhost:" + port + "/api/apples/{id}", updatedAppleDTO, "apple-id");

        // THEN
        List<Apple> applesInDb = mongoTemplate.findAll(Apple.class);

        assertThat(applesInDb)
                .hasSize(1)
                .containsExactlyInAnyOrder(Apple.fromDto(updatedAppleDTO));
    }

    @Test
    void shouldNotUpdateApple_invalidId() {
        // GIVEN
        Apple apple = Apple.builder()
                .id("apple-id")
                .name("Red Apple")
                .build();
        mongoTemplate.save(apple);

        AppleDTO updatedAppleDTO = AppleDTO.builder()
                .id("apple-id")
                .name("Green Apple")
                .build();

        // WHEN
        restTemplate.put("http://localhost:" + port + "/api/apples/{id}", updatedAppleDTO, "invalid-apple-id");

        // THEN
        List<Apple> applesInDb = mongoTemplate.findAll(Apple.class);

        assertThat(applesInDb)
                .hasSize(1)
                .containsExactlyInAnyOrder(apple);
    }
}
