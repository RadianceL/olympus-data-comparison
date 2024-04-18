package com.olympus.comparison.data;

import lombok.Data;

/**
 * 表结构PO
 *
 * @author eddie.lys
 * @since 2024/4/18
 */
@Data
public class TableStructurePO {
    /**
     * 数据表明
     */
    private String columnName;
    /**
     * 数据类型
     */
    private String dataType;
    /**
     * isNullable
     */
    private String isNullable;
    /**
     * 数据差异
     */
    private String columnDefault;
}
