package org.kozak127.templates.restmysqlspring.apple;

import lombok.Builder;
import lombok.Value;

@Value
@Builder(toBuilder = true)
public class Apple {

    private String id;
    private String name;

    public static Apple fromDto(AppleDTO dto) {
        return Apple.builder()
                .id(dto.getId())
                .name(dto.getName())
                .build();
    }
}
