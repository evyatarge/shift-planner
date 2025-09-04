package com.example.shiftplanner.store;

import com.example.shiftplanner.domain.Task;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.*;
import java.nio.file.*;
import java.util.*;

@Component
public class TaskStore {
    private final Path dir;
    private final Path file;
    private final ObjectMapper mapper;

    public TaskStore(@Value("${app.storage.dir:#{systemProperties['user.home'] + '/.shift-planner'}}") String storageDir) {
        this.dir = Paths.get(storageDir);
        this.file = dir.resolve("tasks.json");
        this.mapper = new ObjectMapper()
                .registerModule(new JavaTimeModule())
                .enable(SerializationFeature.INDENT_OUTPUT);
        try { Files.createDirectories(dir); } catch (IOException e) { throw new UncheckedIOException(e); }
    }

    public synchronized List<Task> load() {
        if (!Files.exists(file)) return new ArrayList<>();
        try (BufferedReader r = Files.newBufferedReader(file)) {
            Task[] arr = mapper.readValue(r, Task[].class);
            return new ArrayList<>(Arrays.asList(arr));
        } catch (IOException e) { throw new UncheckedIOException(e); }
    }

    public synchronized List<Task> save(List<Task> tasks) {
        try (BufferedWriter w = Files.newBufferedWriter(file)) {
            mapper.writeValue(w, tasks);
            return tasks;
        } catch (IOException e) { throw new UncheckedIOException(e); }
    }
}