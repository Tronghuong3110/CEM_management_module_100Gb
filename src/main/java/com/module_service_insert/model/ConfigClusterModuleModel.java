package com.module_service_insert.model;

/**
 * @author Trọng Hướng
 */
public class ConfigClusterModuleModel {
    private Long id;
    private Long configId;
    private Long clusterModuleId;
    private String logPath;
    private Long pId;
    private String clusterName;
    private ModuleModel moduleModel;
    private String status;
    private String log;
    private String moduleName;
    private String cpuList;

    public ConfigClusterModuleModel() {}

    public ConfigClusterModuleModel(Long configId, Long clusterModuleId, String clusterName, String status, String moduleName) {
        this.configId = configId;
        this.clusterModuleId = clusterModuleId;
        this.clusterName = clusterName;
        this.status = status;
        this.moduleName = moduleName;
    }

    public String getLogPath() {
        return logPath;
    }

    public void setLogPath(String logPath) {
        this.logPath = logPath;
    }

    public Long getpId() {
        return pId;
    }

    public void setpId(Long pId) {
        this.pId = pId;
    }

    public String getClusterName() {
        return clusterName;
    }

    public void setClusterName(String clusterName) {
        this.clusterName = clusterName;
    }

    public ModuleModel getModuleModel() {
        return moduleModel;
    }

    public void setModuleModel(ModuleModel moduleModel) {
        this.moduleModel = moduleModel;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getLog() {
        return log;
    }

    public void setLog(String log) {
        this.log = log;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getConfigId() {
        return configId;
    }

    public void setConfigId(Long configId) {
        this.configId = configId;
    }

    public Long getClusterModuleId() {
        return clusterModuleId;
    }

    public void setClusterModuleId(Long clusterModuleId) {
        this.clusterModuleId = clusterModuleId;
    }

    public String getModuleName() {
        return moduleName;
    }

    public void setModuleName(String moduleName) {
        this.moduleName = moduleName;
    }

    public String getCpuList() {
        return cpuList;
    }

    public void setCpuList(String cpuList) {
        this.cpuList = cpuList;
    }
}
