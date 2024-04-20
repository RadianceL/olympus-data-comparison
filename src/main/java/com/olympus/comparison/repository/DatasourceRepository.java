package com.olympus.comparison.repository;

import com.olympus.comparison.data.TableStructurePO;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * 数据获取
 *
 * @author eddie.lys
 * @since 2024/4/18
 */
@Repository
public interface DatasourceRepository {

    /**
     * 查询数据库表结构
     */
    List<TableStructurePO> describeTableStructure(@Param("tableName") String tableName);
    /**
     * 获取数据表数据
     */
    List<Map<String, Object>> getDetailTableRecord(@Param("tableName") String tableName);
    /**
     * 获取数据表数据
     */
    List<String> getUserRoleCartesianProduct();
    /**
     * 获取数据表数据
     */
    List<RolePermissionsPO> getUserRoleCartesianProductPermissions(@Param("roleIds") List<String> roleIds);
}
