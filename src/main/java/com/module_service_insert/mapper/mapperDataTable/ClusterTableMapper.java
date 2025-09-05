package com.module_service_insert.mapper.mapperDataTable;

import com.module_service_insert.model.ClusterModel;
import com.module_service_insert.model.ClusterModuleModel;
import com.module_service_insert.model.tableData.ClusterTableData;

/**
 * @author Trọng Hướng
 */
public class ClusterTableMapper {

    public static ClusterModel toModel(ClusterTableData clusterTableData) {
        return new ClusterModel(
            clusterTableData.getId(),
            clusterTableData.getName(),
            clusterTableData.getDescription(),
            0,
            clusterTableData.getStatusStr(),
            clusterTableData.getBaseFolder(),
            clusterTableData.getNumberModule()
        );
    }

    public static ClusterTableData toDataModel(ClusterModel clusterModel) {
        return new ClusterTableData(
                clusterModel.getId(),
                clusterModel.getName(),
                clusterModel.getDescription(),
                clusterModel.getStatus(),
                clusterModel.getStatusStr(),
                clusterModel.getNumberModule(),
                clusterModel.getBaseFolder()
        );
    }
}
