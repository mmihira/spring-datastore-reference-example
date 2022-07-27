package com.mmihira.example.concepts;

import com.google.cloud.spring.data.datastore.repository.DatastoreRepository;

public interface ParentRepository extends DatastoreRepository<Parent, String> {
}
