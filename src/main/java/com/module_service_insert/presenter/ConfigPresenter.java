package com.module_service_insert.presenter;

import com.module_service_insert.exception.DaoException;
import com.module_service_insert.model.ConfigClusterModuleModel;
import com.module_service_insert.model.ConfigModel;
import com.module_service_insert.model.tableData.ConfigClusterModuleTableData;
import com.module_service_insert.model.tableData.ModuleTableData;
import com.module_service_insert.service.IConfigService;
import com.module_service_insert.service.impl.ConfigServiceImpl;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Trọng Hướng
 */
public class ConfigPresenter {
    private final IConfigService configService;
    private static ConfigPresenter instance;

    private ConfigPresenter() {
        configService = new ConfigServiceImpl();
    }

    public static ConfigPresenter getInstance() {
        if (instance == null) {
            instance = new ConfigPresenter();
        }
        return instance;
    }

    // lấy theo tên config
    public List<ConfigClusterModuleTableData> findAll(long configId) {
        List<ConfigClusterModuleModel> configClusterModuleModels = configService.findAllConfigModule(configId);
        List<ConfigClusterModuleTableData> configClusterModuleTableDatas = new ArrayList<>();
        for(ConfigClusterModuleModel configClusterModuleModel : configClusterModuleModels){
            configClusterModuleTableDatas.add(new ConfigClusterModuleTableData(
                configClusterModuleModel.getModuleModel().getName(),
                configClusterModuleModel.getModuleModel().getCommand(),
                configClusterModuleModel.getCpuList(),
                configClusterModuleModel.getLogPath(),
                configClusterModuleModel.getStatus(),
                "Đây là log test",
                configClusterModuleModel.getClusterName(),
                123
            ));
        }
        return configClusterModuleTableDatas;
    }

    public long save(ConfigModel configModel, List<ModuleTableData> modulesOfCluster) throws DaoException {
        return configService.save(configModel, modulesOfCluster);
    }

    public void delete(List<Long> configIds) throws DaoException {
        configService.delete(configIds);
    }

    public List<ConfigModel> findAllConfig() throws DaoException {
        return configService.findAll();
    }
}
