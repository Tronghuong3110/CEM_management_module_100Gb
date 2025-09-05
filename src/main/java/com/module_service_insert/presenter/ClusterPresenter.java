package com.module_service_insert.presenter;

import com.module_service_insert.exception.DaoException;
import com.module_service_insert.mapper.mapperDataTable.ClusterTableMapper;
import com.module_service_insert.model.ClusterModel;
import com.module_service_insert.model.ClusterModuleModel;
import com.module_service_insert.model.tableData.ClusterTableData;
import com.module_service_insert.model.tableData.ModuleTableData;
import com.module_service_insert.service.IClusterService;
import com.module_service_insert.service.impl.ClusterServiceImpl;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Trọng Hướng
 */
public class ClusterPresenter {
    private final IClusterService clusterService;
    private static ClusterPresenter instance;

    private ClusterPresenter() {
        this.clusterService = new ClusterServiceImpl();
    }

    public static ClusterPresenter getInstance() {
        if (instance == null) {
            instance = new ClusterPresenter();
        }
        return instance;
    }

    public long save(ClusterTableData clusterTableData, List<ModuleTableData> moduleTableDatas) throws DaoException {
        ClusterModel clusterModel = ClusterTableMapper.toModel(clusterTableData);
        return clusterService.save(clusterModel, moduleTableDatas);
    }

    public void update(ClusterTableData newClusterTableData, List<ModuleTableData> updatedModulesOfCluster, List<Long> moduleIdsToDelete) throws DaoException {
        ClusterModel clusterModel = ClusterTableMapper.toModel(newClusterTableData);
        clusterService.update(clusterModel, updatedModulesOfCluster, moduleIdsToDelete);
    }

    public void delete(long id, List<Long> clusterModuleIds) throws DaoException {
        clusterService.delete(id, clusterModuleIds);
    }

    public List<ClusterTableData> findAll() throws DaoException {
        List<ClusterModel> clusterModels = clusterService.findAll();
        List<ClusterTableData> clusterTableDatas = new ArrayList<>();
        for(ClusterModel clusterModel : clusterModels) {
            clusterTableDatas.add(new ClusterTableData(
                    clusterModel.getId(),
                    clusterModel.getName(),
                    clusterModel.getStatusStr(),
                    clusterModel.getNumberModule(),
                    clusterModel.getBaseFolder()
            ));
        }
        return clusterTableDatas;
    }

    public List<ModuleTableData> findAllByCluster(long clusterId) {
        List<ClusterModuleModel> clusterModuleModels = clusterService.findAllByCluster(clusterId);
        List<ModuleTableData> moduleTableDatas = new ArrayList<>();
        for(ClusterModuleModel clusterModuleModel : clusterModuleModels){
            moduleTableDatas.add(new ModuleTableData(
                    clusterModuleModel.getModuleName(),
                    clusterModuleModel.getCommand(),
                    "",
                    clusterModuleModel.getCpuList(),
                    clusterModuleModel.getRunPath(),
                    clusterModuleModel.getConfigPath(),
                    clusterModuleModel.getId(),
                    clusterModuleModel.getLogPath(),
                    ""
            ));
        }
        return moduleTableDatas;
    }
}
