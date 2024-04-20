package com.olympus.comparison.runner;

import com.olympus.comparison.data.TableStructurePO;
import com.olympus.comparison.repository.DatasourceRepository;
import com.olympus.comparison.repository.RolePermissionsPO;
import com.olympus.dynamic.config.DynamicDatabaseConfiguration;
import com.olympus.dynamic.core.DatasourceSelectorHolder;
import jakarta.annotation.Resource;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.*;

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

    /**
     * 对比角色变化
     */
    private void comparisonSysRole(Map<String, List<Map<String, Object>>> detailTableRecordMap) {
        // 最外层的主键，字段ID<两个库值>
        Map<String, Map<String, Map.Entry<Object, Object>>> comparisonMap = new HashMap<>();
        for (String datasource : detailTableRecordMap.keySet()) {
            List<Map<String, Object>> maps = detailTableRecordMap.get(datasource);
            for (Map<String, Object> map : maps) {
                Object roleId = map.get("role_id");
                Map<String, Map.Entry<Object, Object>> entry = comparisonMap.get(roleId.toString());
                if (Objects.isNull(entry)) {
                    entry = new HashMap<>();
                }
                // 字段ID <两个库值>
                Map<String, Map.Entry<Object, Object>> finalEntry = entry;
                map.forEach((k, v) -> {
                    if (Objects.nonNull(finalEntry.get(k))) {
                        finalEntry.get(k).setValue(v);
                    }else {
                        finalEntry.put(k,new AbstractMap.SimpleEntry<>(v, null));
                    }
                });
                comparisonMap.put(roleId.toString(), finalEntry);
            }
        }
        System.out.printf("%5s %80s %80s %80s \n", "id", "字段名称", "原始值", "目标值");
        comparisonMap.forEach((roleId, record) -> record.forEach((fieldName, value) -> {
            if (
                    fieldName.equals("creation_date") ||
                    fieldName.equals("created_by")||
                    fieldName.equals("last_updated_by")||
                    fieldName.equals("last_update_date")
            ) {
                return;
            }
            if (!value.getKey().equals(value.getValue())) {
                System.out.printf("%5s %80s %80s %80s \n", roleId, fieldName, value.getKey(), value.getValue());
            }
        }));
    }

    /**
     * 对比角色权限变化权限
     */
    private void comparisonSysPermission(Map<String, List<Map<String, Object>>> detailTableRecordMap) {
        // 最外层的主键，字段ID<两个库值>
        Map<String, Map<String, Map.Entry<Object, Object>>> comparisonMap = new HashMap<>();
        for (String datasource : detailTableRecordMap.keySet()) {
            List<Map<String, Object>> maps = detailTableRecordMap.get(datasource);
            for (Map<String, Object> map : maps) {
                Object roleId = map.get("id");
                Map<String, Map.Entry<Object, Object>> entry = comparisonMap.get(roleId.toString());
                if (Objects.isNull(entry)) {
                    entry = new HashMap<>();
                }
                // 字段ID <两个库值>
                Map<String, Map.Entry<Object, Object>> finalEntry = entry;
                map.forEach((k, v) -> {
                    if (Objects.nonNull(finalEntry.get(k))) {
                        finalEntry.get(k).setValue(v);
                    }else {
                        finalEntry.put(k,new AbstractMap.SimpleEntry<>(v, null));
                    }
                });
                comparisonMap.put(roleId.toString(), finalEntry);
            }
        }
        System.out.printf("%5s %80s %80s %80s \n", "id", "字段名称", "STG值", "PROD值");
        comparisonMap.forEach((roleId, record) -> record.forEach((fieldName, value) -> {
            if (
                            fieldName.equals("creation_date") ||
                            fieldName.equals("created_by")||
                            fieldName.equals("last_updated_by")||
                            fieldName.equals("last_update_date")
            ) {
                return;
            }
            if (!value.getKey().equals(value.getValue())) {
                System.out.printf("%5s %80s %80s %80s \n", roleId, fieldName, value.getKey(), value.getValue());
            }
        }));
    }

    /**
     * 对比用户角色变化权限
     */
    private void comparisonSysUserPermission(Map<String, Map<String, List<RolePermissionsPO>>> detailTableRecordMap) {
        Map<String, Map<String, Map.Entry<List<String>, List<String>>>> comparisonMap = new HashMap<>();
        for (String datasource : detailTableRecordMap.keySet()) {
            Map<String, List<RolePermissionsPO>> maps = detailTableRecordMap.get(datasource);
            maps.forEach((userRoleCartesianProductKey, rolePermissionsList) -> {
                Map<String, Map.Entry<List<String>, List<String>>> entry = comparisonMap.get(userRoleCartesianProductKey);
                if (Objects.isNull(entry)) {
                    entry = new HashMap<>();
                }

                List<String> rolePermissions = rolePermissionsList.stream().map(e -> e.getPermissionId() + "-" + e.getPermissionTitle()).toList();

                // 字段ID <两个库值>
                if (Objects.nonNull(entry.get(userRoleCartesianProductKey))) {
                    entry.get(userRoleCartesianProductKey).setValue(rolePermissions);
                }else {
                    entry.put(userRoleCartesianProductKey, new AbstractMap.SimpleEntry<>(rolePermissions, null));
                }
                comparisonMap.put(userRoleCartesianProductKey, entry);
            });
        }

        comparisonMap.forEach((roleId, record) -> record.forEach((fieldName, value) -> {

            // 没考虑空的情况
            if (!value.getKey().equals(value.getValue())) {
                List<String> tempList = new ArrayList<>(value.getKey());
                // stg比生产多什么
                tempList.removeAll(value.getValue());
                // 生产比stg多什么
                value.getValue().removeAll(value.getKey());

                System.out.printf("%5s %80s %80s %80s \n", roleId, fieldName, tempList, value.getValue());
            }
        }));

    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        Map<String, List<Map<String, Object>>> detailTableRecordMap = new HashMap<>(4);

        Map<String, List<Map<String, Object>>> detailTablePermissionsRecordMap = new HashMap<>(4);

        Map<String, Map<String, List<RolePermissionsPO>>> detailUserRoleCartesianProductMap = new HashMap<>(4);

        for (String datasourceName : dynamicDatabaseConfiguration.getDatabaseConnectionConfig().keySet()) {
            DatasourceSelectorHolder.setCurrentDatabase(datasourceName);
            List<Map<String, Object>> detailTableRecordList = datasourceRepository.getDetailTableRecord("sys_role");
            detailTableRecordMap.put(datasourceName, detailTableRecordList);

            List<Map<String, Object>> detailTablePermissionRecordList = datasourceRepository.getDetailTableRecord("sys_permission");
            detailTablePermissionsRecordMap.put(datasourceName, detailTablePermissionRecordList);

            // 取出所有类型的组合
            List<String> userRoleCartesianProductList = datasourceRepository.getUserRoleCartesianProduct();
            Map<String, List<RolePermissionsPO>> rolePermissionMap = new HashMap<>();
            for (String userRoleCartesianProduct : userRoleCartesianProductList) {
                List<String> permissionIds;
                if (StringUtils.contains(userRoleCartesianProduct,',')) {
                    String[] split = userRoleCartesianProduct.split(",");
                    permissionIds = new ArrayList<>(List.of(split));
                }else {
                    permissionIds = Collections.singletonList(userRoleCartesianProduct);
                }
                System.out.print(".");
                List<RolePermissionsPO> userRoleCartesianProductPermissions = datasourceRepository.getUserRoleCartesianProductPermissions(permissionIds);
                rolePermissionMap.put(userRoleCartesianProduct, userRoleCartesianProductPermissions);
            }
            detailUserRoleCartesianProductMap.put(datasourceName, rolePermissionMap);

            DatasourceSelectorHolder.clear();
        }

        comparisonSysRole(detailTableRecordMap);

        comparisonSysPermission(detailTablePermissionsRecordMap);

        comparisonSysUserPermission(detailUserRoleCartesianProductMap);
    }
}
