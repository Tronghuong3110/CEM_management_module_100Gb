package com.module_service_insert.presenter;

import com.module_service_insert.exception.DaoException;
import com.module_service_insert.mapper.mapperDataTable.ModuleTableMapper;
import com.module_service_insert.model.ClusterModuleModel;
import com.module_service_insert.model.ModuleModel;
import com.module_service_insert.model.tableData.ModuleTableData;
import com.module_service_insert.service.IModuleService;
import com.module_service_insert.service.impl.ModuleServiceImpl;
import jdk.javadoc.doclet.DocletEnvironment;
import org.controlsfx.control.ListSelectionView;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Trọng Hướng
 */
public class ModulePresenter {
    private final IModuleService moduleService;
    private static ModulePresenter instance;

    private ModulePresenter() {
        this.moduleService = new ModuleServiceImpl();
    }

    public static ModulePresenter getInstance() {
        if (instance == null) {
            instance = new ModulePresenter();
        }
        return instance;
    }

    public long save(ModuleTableData moduleTableData) throws DaoException {
        ModuleModel moduleModel = ModuleTableMapper.toModel(moduleTableData);
        return moduleService.save(moduleModel);
    }

    public void update(ModuleTableData newModuleTableData) throws DaoException {
        ModuleModel moduleModel = ModuleTableMapper.toModel(newModuleTableData);
        moduleService.update(moduleModel);
    }

    public void delete(long id) throws DaoException {
        moduleService.delete(id);
    }

    public List<ModuleTableData> findAll() throws DaoException {
        List<ModuleModel> moduleModels = moduleService.findAll();
        List<ModuleTableData> moduleTableDatas = new ArrayList<>();
        for(ModuleModel moduleModel : moduleModels){
            moduleTableDatas.add(new ModuleTableData(
                moduleModel.getId(),
                moduleModel.getName(),
                moduleModel.getCommand(),
                moduleModel.getDescription()
            ));
        }
        return moduleTableDatas;
    }
}
