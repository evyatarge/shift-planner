package com.example.shiftplanner.store;

import com.example.shiftplanner.domain.Employee;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.*;
import java.nio.file.*;
import java.util.*;

@Component
public class EmployeeStore {

    private final Path dir;
    private final Path file;
    private final ObjectMapper mapper;

    public EmployeeStore(
            @Value("${app.storage.dir:#{systemProperties.userHome + '/.shift-planner'}}") String storageDir) {
        this.dir = Paths.get(storageDir);
        this.file = dir.resolve("employees.json");
        this.mapper = new ObjectMapper()
                .registerModule(new JavaTimeModule())
                .enable(SerializationFeature.INDENT_OUTPUT);
        try {
            Files.createDirectories(dir);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    public synchronized List<Employee> load() {
        if (!Files.exists(file)) return new ArrayList<>();
        try (BufferedReader r = Files.newBufferedReader(file)) {
            Employee[] arr = mapper.readValue(r, Employee[].class);
            return new ArrayList<>(Arrays.asList(arr));
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    public synchronized List<Employee> save(List<Employee> employees) {
        try (BufferedWriter w = Files.newBufferedWriter(file)) {
            mapper.writeValue(w, employees);
            return employees;
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}