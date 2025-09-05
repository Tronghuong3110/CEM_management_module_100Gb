package com.module_service_insert.model;

/**
 * @author Trọng Hướng
 */
public class ModuleModel {
    private Long id;
    private String name;
    private String cpuList;
    private String runPath;
    private String configPath;
    private String command;
    private String description;

    public ModuleModel() {}

    public ModuleModel(long id, String name, String description, String command) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.command = command;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCpuList() {
        return cpuList;
    }

    public void setCpuList(String cpuList) {
        this.cpuList = cpuList;
    }

    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
    }

    public String getRunPath() {
        return runPath;
    }

    public void setRunPath(String runPath) {
        this.runPath = runPath;
    }

    public String getConfigPath() {
        return configPath;
    }

    public void setConfigPath(String configPath) {
        this.configPath = configPath;
    }
}
