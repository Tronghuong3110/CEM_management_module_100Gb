package com.module_service_insert.model;

/**
 * @author Trọng Hướng
 */
public class ClusterModel {
    private Long id;
    private String name;
    private String description;
    private int status;
    private String statusStr;
    private String baseFolder;
    private int numberModule;

    public ClusterModel() {}

    public ClusterModel(long id, String name, String description, int status, String statusStr, String baseFolder, int numberModule) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.status = status;
        this.statusStr = statusStr;
        this.baseFolder = baseFolder;
        this.numberModule = numberModule;
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

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getStatusStr() {
        return statusStr;
    }

    public void setStatusStr(String statusStr) {
        this.statusStr = statusStr;
    }

    public String getBaseFolder() {
        return baseFolder;
    }

    public void setBaseFolder(String baseFolder) {
        this.baseFolder = baseFolder;
    }

    public int getNumberModule() {
        return numberModule;
    }

    public void setNumberModule(int numberModule) {
        this.numberModule = numberModule;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "Name: " + this.name +
                " Description: " + this.description +
                " Status: " + this.statusStr +
                " baseFolder: " + this.baseFolder;
    }
}
