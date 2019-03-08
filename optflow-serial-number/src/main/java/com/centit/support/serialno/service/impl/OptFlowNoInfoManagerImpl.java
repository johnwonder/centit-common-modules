package com.centit.support.serialno.service.impl;

import com.centit.support.algorithm.DatetimeOpt;
import com.centit.support.database.utils.PageDesc;
import com.centit.support.serialno.dao.OptFlowNoInfoDao;
import com.centit.support.serialno.dao.OptFlowNoPoolDao;
import com.centit.support.serialno.po.OptFlowNoInfo;
import com.centit.support.serialno.po.OptFlowNoInfoId;
import com.centit.support.serialno.po.OptFlowNoPool;
import com.centit.support.serialno.po.OptFlowNoPoolId;
import com.centit.support.serialno.service.OptFlowNoInfoManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class OptFlowNoInfoManagerImpl implements OptFlowNoInfoManager {

    public static final Logger logger = LoggerFactory.getLogger(OptFlowNoInfoManager.class);

    @Resource
    @NotNull
    private OptFlowNoInfoDao optFlowNoInfoDao;

    @Resource
    @NotNull
    private OptFlowNoPoolDao optFlowNoPoolDao;


    public synchronized void setOptFlowNoPoolDao(OptFlowNoPoolDao baseDao) {
        this.optFlowNoPoolDao = baseDao;
    }

    /**
     * 获取最新的流水号，并标记+1
     */
    @Override
    public synchronized long newNextLsh(String ownerCode, String codeCode, Date codeBaseDate) {
        java.sql.Date codeDate = DatetimeOpt.convertToSqlDate(codeBaseDate); // DatetimeOpt.convertSqlDate(codeBaseDate);
        OptFlowNoInfoId noId = new OptFlowNoInfoId(ownerCode, codeDate, codeCode);
        OptFlowNoInfo noInfo = optFlowNoInfoDao.getObjectById(noId);
        long nextCode = noInfo == null ? 1L : noInfo.getCurNo()+1;
        //检查新生产的号是否已经被预留
        while (true) {
            OptFlowNoPoolId poolId = new OptFlowNoPoolId(ownerCode, codeDate, codeCode, nextCode);
            OptFlowNoPool poolNo = optFlowNoPoolDao.getObjectById(poolId);
            //没有被预留
            if (poolNo == null) {
                break;
              }
            nextCode++;
        }
        if (noInfo == null) {
            noInfo = new OptFlowNoInfo(noId, nextCode, DatetimeOpt.currentUtilDate());
            optFlowNoInfoDao.saveNewOptFlowNoInfo(noInfo);
        } else {
            noInfo.setCurNo(nextCode);
            noInfo.setLastCodeDate(DatetimeOpt.currentUtilDate());
            optFlowNoInfoDao.updateOptFlowNoInfo(noInfo);
        }
        return nextCode;
    }

    @Override
    @Transactional
    public boolean reserveLsh(String ownerCode, String codeCode, Date codeBaseDate, Long lsh){
        java.sql.Date codeDate = DatetimeOpt.convertToSqlDate(codeBaseDate);
        OptFlowNoInfoId noId = new OptFlowNoInfoId(ownerCode, codeDate, codeCode);
        OptFlowNoInfo noInfo = optFlowNoInfoDao.getObjectById(noId);
        Long cur = noInfo == null ? 0 : noInfo.getCurNo();
        if(lsh > cur) {
            OptFlowNoPoolId poolId = new OptFlowNoPoolId(ownerCode, codeDate, codeCode, lsh);
            OptFlowNoPool dbPool = optFlowNoPoolDao.getObjectById(poolId);
            if(dbPool != null){
                return false;
            }
            OptFlowNoPool pool = new OptFlowNoPool(poolId, DatetimeOpt.currentUtilDate());
            optFlowNoPoolDao.saveNewOptFlowNoPool(pool);
            return true;
        }
        return false;
    }

    /**
     * 以天为单位记录流水号
     */
    @Override
    public long newNextLshBaseDay(String ownerCode, String codeCode, Date codeBaseDate) {
        return newNextLsh(ownerCode, codeCode, DatetimeOpt.truncateToDay(codeBaseDate));
    }

    /**
     * 以月为单位记录流水号
     */
    @Override
    public long newNextLshBaseMonth(String ownerCode, String codeCode, Date codeBaseDate) {
        return newNextLsh(ownerCode, codeCode, DatetimeOpt.truncateToMonth(codeBaseDate));
    }

    /**
     * 以年为单位记录流水号
     */
    @Override
    public long newNextLshBaseYear(String ownerCode, String codeCode, Date codeBaseDate) {
        return newNextLsh(ownerCode, codeCode, DatetimeOpt.truncateToYear(codeBaseDate));
    }

    @Override
    public long newNextLsh(String codeCode) {
        return newNextLsh(DefaultOwnerCode, codeCode, DefaultCodeDate);
    }

    @Override
    public long newNextLsh(String ownerCode, String codeCode) {
        return newNextLsh(ownerCode, codeCode, DefaultCodeDate);
    }

    /**
     * 查看最新流水号
     */
    @Override
    public synchronized long viewNextLsh(String ownerCode, String codeCode, Date codeBaseDate) {
        java.sql.Date codeDate = DatetimeOpt.convertToSqlDate(codeBaseDate);
        OptFlowNoInfoId noId = new OptFlowNoInfoId(ownerCode, codeDate, codeCode);
        OptFlowNoInfo noInfo = optFlowNoInfoDao.getObjectById(noId);
        long nextCode = 1L;
        if (noInfo != null)
            nextCode = noInfo.getCurNo() + 1;
        return nextCode;
    }

    @Override
    public long viewNextLshBaseDay(String ownerCode, String codeCode, Date codeBaseDate) {
        return viewNextLsh(ownerCode, codeCode, DatetimeOpt.truncateToDay(codeBaseDate));
    }

    @Override
    public long viewNextLshBaseMonth(String ownerCode, String codeCode, Date codeBaseDate) {
        return viewNextLsh(ownerCode, codeCode, DatetimeOpt.truncateToMonth(codeBaseDate));
    }

    @Override
    public long viewNextLshBaseYear(String ownerCode, String codeCode, Date codeBaseDate) {
        return viewNextLsh(ownerCode, codeCode, DatetimeOpt.truncateToYear(codeBaseDate));
    }

    @Override
    public long viewNextLsh(String codeCode) {
        return viewNextLsh(DefaultOwnerCode, codeCode, DefaultCodeDate);
    }

    @Override
    public long viewNextLsh(String ownerCode, String codeCode) {
        return viewNextLsh(ownerCode, codeCode, DefaultCodeDate);
    }

    @Override
    public synchronized void recordNextLsh(String ownerCode, String codeCode,
                                           Date codeBaseDate, long currCode) {
        java.sql.Date codeDate = DatetimeOpt.convertToSqlDate(codeBaseDate);
        // 如果是从池中取出的，在池中删除
        OptFlowNoPoolId poolId = new OptFlowNoPoolId(ownerCode, codeDate, codeCode, currCode);

        OptFlowNoPool poolNo = optFlowNoPoolDao.getObjectById(poolId);
        if (poolNo != null) {
            optFlowNoPoolDao.deleteObject(poolNo);
            return;
        }

        OptFlowNoInfoId noId = new OptFlowNoInfoId(ownerCode, codeDate, codeCode);
        OptFlowNoInfo noInfo = optFlowNoInfoDao.getObjectById(noId);
        if (noInfo == null) {
            noInfo = new OptFlowNoInfo(noId, currCode, DatetimeOpt.currentUtilDate());
            optFlowNoInfoDao.saveNewOptFlowNoInfo(noInfo);
        } else {
            if (noInfo.getCurNo() < currCode) {
                //存在跨号，将中间的号码保存至OptFlowNoPool中
                if(currCode-noInfo.getCurNo()>1){
                    long startIdx = noInfo.getCurNo()+1;
                    for(long i = startIdx ;i<currCode;i++){
                        OptFlowNoPoolId cid = new OptFlowNoPoolId(ownerCode, codeDate, codeCode, i);
                        OptFlowNoPool pool = new OptFlowNoPool();
                        pool.setCid(cid);
                        pool.setCreateDate(new Date());
                        optFlowNoPoolDao.saveNewOptFlowNoPool(pool);
                    }
                }
                noInfo.setCurNo(currCode);
                noInfo.setLastCodeDate(DatetimeOpt.currentUtilDate());
                optFlowNoInfoDao.updateOptFlowNoInfo(noInfo);
            }
        }
    }

    @Override
    public void recordNextLshBaseDay(String ownerCode, String codeCode, Date codeBaseDate, long currCode) {
        recordNextLsh(ownerCode, codeCode, DatetimeOpt.truncateToDay(codeBaseDate), currCode);
    }

    @Override
    public void recordNextLshBaseMonth(String ownerCode, String codeCode, Date codeBaseDate, long currCode) {
        recordNextLsh(ownerCode, codeCode, DatetimeOpt.truncateToMonth(codeBaseDate), currCode);
    }

    @Override
    public void recordNextLshBaseYear(String ownerCode, String codeCode, Date codeBaseDate, long currCode) {
        recordNextLsh(ownerCode, codeCode, DatetimeOpt.truncateToYear(codeBaseDate), currCode);
    }

    @Override
    public void recordNextLsh(String codeCode, long currCode) {
        recordNextLsh(DefaultOwnerCode, codeCode, DefaultCodeDate, currCode);
    }

    @Override
    public void recordNextLsh(String ownerCode, String codeCode, long currCode) {
        recordNextLsh(ownerCode, codeCode, DefaultCodeDate, currCode);
    }

    @Override
    public synchronized long assignNextLsh(String ownerCode, String codeCode, Date codeBaseDate) {
        /*Map map=new HashMap();
        map.put("ownerCode", ownerCode);
        map.put("codeCode", codeCode);
        map.put("codeBaseDate", String.valueOf(codeBaseDate));*/

        long minPoolNo = optFlowNoPoolDao.fetchFirstLsh(ownerCode, codeCode, codeBaseDate);
        if (minPoolNo > 0) {
            OptFlowNoPoolId obj = new OptFlowNoPoolId();
            obj.setOwnerCode(ownerCode);
            obj.setCodeDate(DatetimeOpt.convertToSqlDate(codeBaseDate));
            obj.setCodeCode(codeCode);
            obj.setCurNo(minPoolNo);
            optFlowNoPoolDao.deleteObjectById(obj);
            return minPoolNo;
        } else
            return newNextLsh(ownerCode, codeCode, codeBaseDate);
    }

    @Override
    public long assignNextLshBaseDay(String ownerCode, String codeCode, Date codeBaseDate) {
        return assignNextLsh(ownerCode, codeCode, DatetimeOpt.truncateToDay(codeBaseDate));
    }

    @Override
    public long assignNextLshBaseMonth(String ownerCode, String codeCode, Date codeBaseDate) {
        return assignNextLsh(ownerCode, codeCode, DatetimeOpt.truncateToMonth(codeBaseDate));
    }

    @Override
    public long assignNextLshBaseYear(String ownerCode, String codeCode, Date codeBaseDate) {
        return assignNextLsh(ownerCode, codeCode, DatetimeOpt.truncateToYear(codeBaseDate));
    }

    @Override
    public long assignNextLsh(String ownerCode, String codeCode) {
        return assignNextLsh(ownerCode, codeCode, DefaultCodeDate);
    }

    @Override
    public long assignNextLsh(String codeCode) {
        return assignNextLsh(DefaultOwnerCode, codeCode, DefaultCodeDate);
    }

    @Override
    public void releaseLsh(String ownerCode, String codeCode, Date codeBaseDate, long currCode) {
        OptFlowNoPool obj = new OptFlowNoPool();
        obj.setOwnerCode(ownerCode);
        obj.setCodeDate(DatetimeOpt.convertToSqlDate(codeBaseDate));
        obj.setCodeCode(codeCode);
        obj.setCurNo(currCode);
        obj.setCreateDate(DatetimeOpt.currentUtilDate());
        optFlowNoPoolDao.saveNewOptFlowNoPool(obj);
    }

    @Override
    public void releaseLshBaseDay(String ownerCode, String codeCode, Date codeBaseDate, long currCode) {
        releaseLsh(ownerCode, codeCode, DatetimeOpt.truncateToDay(codeBaseDate), currCode);
    }

    @Override
    public void releaseLshBaseMonth(String ownerCode, String codeCode, Date codeBaseDate, long currCode) {
        releaseLsh(ownerCode, codeCode, DatetimeOpt.truncateToMonth(codeBaseDate), currCode);
    }

    @Override
    public void releaseLshBaseYear(String ownerCode, String codeCode, Date codeBaseDate, long currCode) {
        releaseLsh(ownerCode, codeCode, DatetimeOpt.truncateToYear(codeBaseDate), currCode);
    }

    @Override
    public void releaseLsh(String ownerCode, String codeCode, long currCode) {
        releaseLsh(ownerCode, codeCode, DefaultCodeDate, currCode);
    }

    @Override
    public void releaseLsh(String codeCode, long currCode) {
        releaseLsh(DefaultOwnerCode, codeCode, DefaultCodeDate, currCode);
    }

    public List<OptFlowNoPool> listLshInPool(String ownerCode, String codeCode,
                                             Date codeBaseDate, PageDesc pageDesc) {
        Map<String, Object> filterMap = new HashMap<>();

        filterMap.put("ownerCode", ownerCode);
        filterMap.put("codeDate", codeBaseDate);
        filterMap.put("codeCode", codeCode);

        return optFlowNoPoolDao.listLshInPool(filterMap,pageDesc);
    }

    public List<OptFlowNoPool> listLshBaseDayInPool(String ownerCode,
                                                    String codeCode, Date codeBaseDate, PageDesc pageDesc) {
        return listLshInPool(ownerCode, codeCode, DatetimeOpt.truncateToDay(codeBaseDate), pageDesc);
    }

    public List<OptFlowNoPool> listLshBaseMonthInPool(String ownerCode,
                                                      String codeCode, Date codeBaseDate, PageDesc pageDesc) {
        return listLshInPool(ownerCode, codeCode, DatetimeOpt.truncateToMonth(codeBaseDate), pageDesc);
    }

    public List<OptFlowNoPool> listLshBaseYearInPool(String ownerCode,
                                                     String codeCode, Date codeBaseDate, PageDesc pageDesc) {
        return listLshInPool(ownerCode, codeCode, DatetimeOpt.truncateToYear(codeBaseDate), pageDesc);
    }

    public List<OptFlowNoPool> listLshInPool(String ownerCode,
                                             String codeCode, PageDesc pageDesc) {
        return listLshInPool(ownerCode, codeCode, DefaultCodeDate, pageDesc);
    }

    public List<OptFlowNoPool> listLshInPool(String codeCode, PageDesc pageDesc) {
        return listLshInPool(DefaultOwnerCode, codeCode, DefaultCodeDate, pageDesc);
    }

}
