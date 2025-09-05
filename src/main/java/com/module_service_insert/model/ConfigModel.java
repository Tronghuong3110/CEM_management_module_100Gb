package com.module_service_insert.model;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Trọng Hướng
 */
public class ConfigModel {
    private Long id;
    private String name;
    private List<ConfigClusterModuleModel> moduleConfigs;

    public ConfigModel() {}

    public ConfigModel(String name) {
        moduleConfigs = new ArrayList<>();
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
