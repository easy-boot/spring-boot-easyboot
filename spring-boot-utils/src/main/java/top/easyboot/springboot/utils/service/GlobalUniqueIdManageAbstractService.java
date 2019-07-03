package top.easyboot.springboot.utils.service;

import top.easyboot.springboot.utils.interfaces.service.IGlobalUniqueIdManageService;

import java.util.*;

public abstract class GlobalUniqueIdManageAbstractService implements IGlobalUniqueIdManageService {
    protected final TreeSet<GlobalUniqueIdService> serviceSet;
    protected final HashMap<String, LinkedHashSet<String>> unusedIdMap;
    protected final HashMap<String, LinkedHashSet<String>> waitReportIdMap;

    public GlobalUniqueIdManageAbstractService() {
        this.serviceSet = new TreeSet();
        this.unusedIdMap = new HashMap<>();
        this.waitReportIdMap = new HashMap<>();
    }

    @Override
    public void register(GlobalUniqueIdService service) {
        if (!serviceSet.contains(service)){
            serviceSet.add(service);
            String name = service.getName();
            if (!unusedIdMap.containsKey(name)){
                unusedIdMap.put(name, new LinkedHashSet<>());
            }
            if (!waitReportIdMap.containsKey(name)){
                waitReportIdMap.put(name, new LinkedHashSet<>());
            }
        }
        refreshWaitReportId(service);
        if (service.getUnusedIdSize()<1){
            addUnusedIds(service);
        }
    }

    protected void refreshUnusedId(GlobalUniqueIdService service){
        if (serviceSet.contains(service)){
            String name = service.getName();
            Set<String> unusedIds = service.getAllUnusedId();
            final HashSet<String> unusedIdSet = unusedIdMap.get(name);
            synchronized (unusedIdSet){
                final Iterator<String> iterator = unusedIds.iterator();
                while (iterator.hasNext()){
                    unusedIdSet.add(iterator.next());
                }
            }
        }
    }
    protected void refreshWaitReportId(GlobalUniqueIdService service){
        if (serviceSet.contains(service)){
            String name = service.getName();
            Set<String> waitReportIds = service.getWaitReportId();
            final HashSet<String> waitReportIdSet = waitReportIdMap.get(name);
            synchronized (waitReportIdSet){
                final Iterator<String> iterator = waitReportIds.iterator();
                while (iterator.hasNext()){
                    waitReportIdSet.add(iterator.next());
                }
            }
        }
    }
    protected void unregister(GlobalUniqueIdService service) {
        if (serviceSet.contains(service)){
            serviceSet.remove(service);
            refreshUnusedId(service);
            refreshWaitReportId(service);
        }
    }
    public void unregister() {
        for (GlobalUniqueIdService service : serviceSet) {
            unregister(service);
        }
        for (final String name : waitReportIdMap.keySet()) {
            final HashSet<String> waitReportIdSet = waitReportIdMap.get(name);
            final HashSet<String> releaseUnusedIds;
            synchronized (waitReportIdSet){
                releaseUnusedIds = (HashSet<String>)waitReportIdSet.clone();
            }
            releaseUnusedIds(name, releaseUnusedIds);
        }
        for (final String name : unusedIdMap.keySet()) {
            final HashSet<String> unusedIdSet = unusedIdMap.get(name);
            final HashSet<String> freedUnusedIds;
            synchronized (unusedIdSet){
                freedUnusedIds = (HashSet<String>)unusedIdSet.clone();
            }
            freedUnusedIds(name, freedUnusedIds);
        }
    }
    protected void addUnusedIds(GlobalUniqueIdService service){
        service.addUnusedIds(getUnusedIds(service.getName()));
    }

    /**
     * 释放[保存]尚未使用的全局唯一id
     * @param name 项目
     * @param ids id集合
     */
    protected abstract void freedUnusedIds(String name, Set<String> ids);

    /**
     * 发布[保存]已经使用的全局唯一id
     * @param name 项目
     * @param ids id集合
     */
    protected abstract void releaseUnusedIds(String name, Set<String> ids);

    /**
     * 冻结[申请]尚未使用的全局唯一id
     * @param name
     * @return
     */
    protected abstract Set<String> getUnusedIds(String name);
}
