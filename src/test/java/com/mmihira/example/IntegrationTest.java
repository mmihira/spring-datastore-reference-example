package com.mmihira.example;

import com.mmihira.example.concepts.Child;
import com.mmihira.example.concepts.ChildRepository;
import com.mmihira.example.concepts.Parent;
import com.mmihira.example.concepts.ParentRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.DatastoreEmulatorContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = Example.class)
public class IntegrationTest {
    protected static final String REFERENCE_EMULATOR_IMAGE_NAME = "gcr.io/google.com/cloudsdktool/cloud-sdk:emulators";
    protected static final String[] DATASTORE_EMULATOR_STARTUP_COMMAND = new String[]{"/bin/sh", "-c", "gcloud beta emulators datastore start --consistency 1.0 --project test-project --host-port 0.0.0.0:8081"};

    @DynamicPropertySource
    static void emulatorProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.cloud.gcp.datastore.host", datastore::getEmulatorEndpoint);
    }

    @Container
    public static DatastoreEmulatorContainer datastore =
            new DatastoreEmulatorContainer(DockerImageName.parse(REFERENCE_EMULATOR_IMAGE_NAME))
                    .withCommand(DATASTORE_EMULATOR_STARTUP_COMMAND);

    @Autowired
    private ParentRepository parentRepository;

    @Autowired
    private ChildRepository childRepository;

    @Test
    void test() {
        // create parent with child
        Child child = Child.builder().id(UUID.randomUUID().toString()).value("childFoo").build();
        Parent parent = Parent.builder().id(UUID.randomUUID().toString()).value("parentFoo").child(child).build();
        Parent savedParent = parentRepository.save(parent);

        // retrieve the saved parent
        Parent retrievedParent = parentRepository.findById(savedParent.getId()).get();
        assertThat(retrievedParent.getValue()).isEqualTo("parentFoo");
        assertThat(retrievedParent.getChild().getValue()).isEqualTo("childFoo");

        // try saving the child (LazyReference)
        // error on save here: com.google.cloud.spring.data.datastore.core.mapping.DatastoreDataException: Cloud Datastore can only allocate IDs for Long and Key properties.
        // Cannot allocate for type: class java.lang.String
        Child retrievedChild = retrievedParent.getChild();
        retrievedChild.setValue("bar");
        childRepository.save(retrievedChild);
    }
}
