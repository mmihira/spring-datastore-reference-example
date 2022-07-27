package com.mmihira.example.concepts;

import com.google.cloud.spring.data.datastore.core.mapping.Entity;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;

@Data
@Entity
@Builder
public class Child {
    @Id
    private String id;

    private String value;
}
