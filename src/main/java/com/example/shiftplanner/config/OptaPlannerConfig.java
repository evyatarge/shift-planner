package com.example.shiftplanner.config;

import java.time.Duration;
import java.util.List;

import com.example.shiftplanner.domain.Assignment;
import com.example.shiftplanner.domain.Schedule;
import com.example.shiftplanner.solver.ShiftConstraintProvider;

import org.optaplanner.core.config.solver.SolverConfig;
import org.optaplanner.core.config.solver.termination.TerminationConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OptaPlannerConfig {

    @Bean
    public SolverConfig solverConfig() {
        SolverConfig cfg = new SolverConfig();
        cfg.setSolutionClass(Schedule.class);
        cfg.setEntityClassList(List.of(Assignment.class));
        cfg.setConstraintProviderClass(ShiftConstraintProvider.class);
        cfg.setTerminationConfig(new TerminationConfig().withSpentLimit(Duration.ofSeconds(3)));
        return cfg;
    }
}
