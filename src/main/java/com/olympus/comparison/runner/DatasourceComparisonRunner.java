package com.olympus.comparison.runner;

import com.olympus.comparison.data.TableStructurePO;
import com.olympus.comparison.repository.DatasourceRepository;
import com.olympus.dynamic.config.DynamicDatabaseConfiguration;
import com.olympus.dynamic.core.DatasourceSelectorHolder;
import jakarta.annotation.Resource;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * @author eddie.lys
 * @since 2024/4/18
 */
@Component
public class DatasourceComparisonRunner implements ApplicationRunner {

    @Resource
    private DynamicDatabaseConfiguration dynamicDatabaseConfiguration;

    @Resource
    private DatasourceRepository datasourceRepository;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        for (String datasourceName : dynamicDatabaseConfiguration.getDatabaseConnectionConfig().keySet()) {
            DatasourceSelectorHolder.setCurrentDatabase(datasourceName);
            List<TableStructurePO> tableStructureInfoList = datasourceRepository.describeTableStructure("case_info_sample_stock");
            System.out.println(tableStructureInfoList);

            List<Map<String, Object>> detailTableRecordList = datasourceRepository.getDetailTableRecord("case_info_sample_stock");
            System.out.println(detailTableRecordList.size());
            DatasourceSelectorHolder.clear();
        }
    }
}
