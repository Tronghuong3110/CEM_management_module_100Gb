package com.module_service_insert.model;

/**
 * @author Trọng Hướng
 */
public class ClusterModuleModel {
    private Long id;
    private Long cluster_id;
    private Long module_id;
    private String cpuList;
    private String runPath;
    private String configPath;
    private String command;
    private String moduleName;
    private String logPath;

    public ClusterModuleModel() {}

    public ClusterModuleModel(long id, long clusterId, long moduleId, String cpuList, String runPath, String configPath, String command, String moduleName, String logPath) {
        this.id = id;
        this.cluster_id = clusterId;
        this.module_id = moduleId;
        this.cpuList = cpuList;
        this.runPath = runPath;
        this.configPath = configPath;
        this.command = command;
        this.moduleName = moduleName;
        this.logPath = logPath;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getCluster_id() {
        return cluster_id;
    }

    public void setCluster_id(Long cluster_id) {
        this.cluster_id = cluster_id;
    }

    public Long getModule_id() {
        return module_id;
    }

    public void setModule_id(Long module_id) {
        this.module_id = module_id;
    }

    public String getCpuList() {
        return cpuList;
    }

    public void setCpuList(String cpuList) {
        this.cpuList = cpuList;
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

    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
    }

    public String getModuleName() {
        return moduleName;
    }

    public void setModuleName(String moduleName) {
        this.moduleName = moduleName;
    }

    public String getLogPath() {
        return logPath;
    }

    public void setLogPath(String logPath) {
        this.logPath = logPath;
    }

    @Override
    public String toString() {
        return "Cluster_id: " + this.cluster_id +
                ", module_id: " + this.module_id +
                ", cpuList: " + this.cpuList +
                ", runPath: " + this.runPath +
                ", configPath: " + this.configPath;
    }
}
