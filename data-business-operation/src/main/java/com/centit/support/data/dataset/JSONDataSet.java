package com.centit.support.data.dataset;

import com.centit.support.data.core.DataSet;

import java.util.Map;

public class JSONDataSet extends FileDataSet{

    /**
     * 读取 dataSet 数据集
     * @param params 模块的自定义参数
     * @return dataSet 数据集
     */
    @Override
    public DataSet load(Map<String, Object> params) {
        return null;
    }

    /**
     * 将 dataSet 数据集 持久化
     *
     * @param dataSet 数据集
     */
    @Override
    public void save(DataSet dataSet) {

    }
}
