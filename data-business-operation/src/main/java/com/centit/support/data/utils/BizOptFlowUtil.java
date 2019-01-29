package com.centit.support.data.utils;

import com.centit.support.data.bizopt.DataLoadSupplier;
import com.centit.support.data.bizopt.PersistenceOperation;
import com.centit.support.data.core.BizOperation;
import com.centit.support.data.core.BizOptFlow;

public abstract class BizOptFlowUtil {

    public static int runDataExchange(
        DataLoadSupplier loadData, PersistenceOperation saveData){
        BizOptFlow bof = new BizOptFlow().setSupplier(loadData).addOperation(saveData);
        return bof.run();
    }

    public static int runDataExchange(
        DataLoadSupplier loadData, BizOperation dataTrans, PersistenceOperation saveData){
        BizOptFlow bof = new BizOptFlow().setSupplier(loadData)
            .addOperation(dataTrans)
            .addOperation(saveData);
        return bof.run();
    }
}
