package org.kozak127.templates.restjpaspring.apple;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Value;

@Value
public class AppleDTO {

    private String id;
    private String name;

    @Builder(toBuilder = true)
    public AppleDTO(@JsonProperty("id") String id,
                    @JsonProperty("name") String name) {
        this.id = id;
        this.name = name;
    }

    public static AppleDTO fromEntity(Apple entity) {
        return AppleDTO.builder()
                .id(entity.getId())
                .name(entity.getName())
                .build();
    }
}
