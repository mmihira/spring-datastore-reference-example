package com.mmihira.example.concepts;

import com.google.cloud.spring.data.datastore.core.mapping.Entity;
import com.google.cloud.spring.data.datastore.core.mapping.LazyReference;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;

@Data
@Entity
@Builder
public class Parent {
    @Id
    private String id;

    private String value;

    @LazyReference
    private Child child;

}
