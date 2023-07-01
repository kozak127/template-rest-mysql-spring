package org.kozak127.templates.restjpaspring.apple;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlMergeMode;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SqlMergeMode(SqlMergeMode.MergeMode.MERGE)
@Sql
@Sql(scripts = "../cleanDatabase.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class AppleIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private AppleRepository appleRepository;

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    @Sql
    void shouldReturnApple() {
        // GIVEN
        String requestAppleId = "apple-id";

        // WHEN
        ResponseEntity<String> response = restTemplate.getForEntity(
                "http://localhost:" + port + "/api/apples/{id}", String.class, requestAppleId);

        // THEN
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getHeaders().getContentType()).isEqualTo(MediaType.APPLICATION_JSON);
        assertThat(response.getBody()).isEqualTo("{\"id\":\"apple-id\",\"name\":\"Red Apple\"}");
    }

    @Test
    @Sql
    void shouldNotReturnApple_invalidId() {
        // GIVEN
        String requestAppleId = "invalid-id";

        // WHEN
        ResponseEntity<String> response = restTemplate.getForEntity(
                "http://localhost:" + port + "/api/apples/{id}", String.class, requestAppleId);

        // THEN
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void shouldSaveApple() {
        // GIVEN
        AppleDTO appleDTO = AppleDTO.builder()
                .name("Red Apple")
                .build();

        // WHEN
        ResponseEntity<AppleDTO> response = restTemplate.postForEntity(
                "http://localhost:" + port + "/api/apples", appleDTO, AppleDTO.class);

        // THEN
        List<Apple> applesInDb = appleRepository.findAll();

        AppleDTO savedAppleDTO = response.getBody();

        assertThat(applesInDb)
                .hasSize(2)
                .containsAnyOf(Apple.fromDto(savedAppleDTO));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getHeaders().getContentType()).isEqualTo(MediaType.APPLICATION_JSON);
    }

    @Test
    @Sql
    void shouldDeleteApple() {
        // GIVEN
        String requestAppleId = "apple-id";

        // WHEN
        restTemplate.delete("http://localhost:" + port + "/api/apples/{id}", requestAppleId);

        // THEN
        List<Apple> applesInDb = appleRepository.findAll();

        Apple apple = Apple.builder()
                .id("apple-id")
                .name("Red Apple")
                .build();

        assertThat(applesInDb)
                .hasSize(1)
                .doesNotContain(apple);
    }

    @Test
    @Sql
    void shouldNotDeleteApple_invalidId() {
        // GIVEN
        String requestAppleId = "invalid-apple-id";

        // WHEN
        restTemplate.delete("http://localhost:" + port + "/api/apples/{id}", requestAppleId);

        // THEN
        List<Apple> applesInDb = appleRepository.findAll();

        Apple apple = Apple.builder()
                .id("apple-id")
                .name("Red Apple")
                .build();

        assertThat(applesInDb)
                .hasSize(2)
                .containsAnyOf(apple);
    }

    @Test
    @Sql
    void shouldUpdateApple() {
        // GIVEN
        String requestAppleId = "apple-id";
        AppleDTO updatedAppleDTO = AppleDTO.builder()
                .id(requestAppleId)
                .name("Green Apple")
                .build();

        // WHEN
        restTemplate.put("http://localhost:" + port + "/api/apples/{id}", updatedAppleDTO, requestAppleId);

        // THEN
        List<Apple> applesInDb = appleRepository.findAll();

        assertThat(applesInDb)
                .hasSize(2)
                .containsAnyOf(Apple.fromDto(updatedAppleDTO));
    }

    @Test
    @Sql
    void shouldNotUpdateApple_invalidId() {
        // GIVEN
        String requestAppleId = "invalid-apple-id";
        AppleDTO updatedAppleDTO = AppleDTO.builder()
                .id("apple-id")
                .name("Green Apple")
                .build();

        // WHEN
        restTemplate.put("http://localhost:" + port + "/api/apples/{id}", updatedAppleDTO, requestAppleId);

        // THEN
        List<Apple> applesInDb = appleRepository.findAll();

        Apple apple = Apple.builder()
                .id("apple-id")
                .name("Red Apple")
                .build();

        assertThat(applesInDb)
                .hasSize(2)
                .containsAnyOf(apple);
    }
}
