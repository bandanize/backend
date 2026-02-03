package com.bandanize.backend;

import org.springframework.boot.CommandLineRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Autowired;

@Component
public class SchemaFixer implements CommandLineRunner {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public void run(String... args) throws Exception {
        try {
            System.out.println("SchemaFixer: Attempting to fix database schema for optional BPM...");
            // PostgreSQL specific syntax
            jdbcTemplate.execute("ALTER TABLE song_model ALTER COLUMN bpm DROP NOT NULL");
            System.out.println("SchemaFixer: Successfully altered song_model.bpm to be nullable.");
        } catch (Exception e) {
            System.err.println("SchemaFixer: Schema fix skipped or failed: " + e.getMessage());
        }
    }
}
