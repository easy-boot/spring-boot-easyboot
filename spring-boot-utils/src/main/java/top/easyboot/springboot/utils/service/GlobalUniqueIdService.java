package top.easyboot.springboot.utils.service;

import top.easyboot.springboot.utils.exception.GlobalUniqueIdException;
import top.easyboot.springboot.utils.interfaces.service.IGlobalUniqueIdService;

import java.util.LinkedHashSet;
import java.util.Iterator;
import java.util.Set;

public class GlobalUniqueIdService implements IGlobalUniqueIdService, Comparable<GlobalUniqueIdService> {
    private final String name;
    /**
     * 待使用的id池
     */
    private final Set<String> unusedIds;
    /**
     * 等待上报....
     */
    private final Set<String> waitReportIds;
    /**
     * 管理
     */
    private final Manage manage;

    public GlobalUniqueIdService(){
        this("default");
    }
    public GlobalUniqueIdService(String name){
        this(name, new GlobalUniqueIdManageService());
    }
    public GlobalUniqueIdService(String n, Manage m){
        name = n;
        manage = m;
        unusedIds = new LinkedHashSet<>();
        waitReportIds = new LinkedHashSet<>();
        m.register(this);
    }

    @Override
    public int compareTo(GlobalUniqueIdService o) {
        int len1 = name.length();
        int len2 = getName().length();
        int lim = Math.min(len1, len2);
        char v1[] = name.toCharArray();
        char v2[] = getName().toCharArray();

        int k = 0;
        while (k < lim) {
            char c1 = v1[k];
            char c2 = v2[k];
            if (c1 != c2) {
                return c1 - c2;
            }
            k++;
        }
        return len1 - len2;
    }

    @Override
    public String createUniqueId() throws GlobalUniqueIdException {
        int times = 0;
        while (true){
            // 试图获取一个全局id
            final String unusedId = getNextUnusedId();
            // 获取失败
            if (unusedId == null){
                if (times++>5){
                    throw new GlobalUniqueIdException(GlobalUniqueIdException.E_CREATE_UNIQUE_ID_FAIL);
                }else{
                    // 通过管理器注册的时候，会再次分配id
                    manage.register(this);
                }
            }else{
                synchronized (waitReportIds){
                    // 添加到等待上报id池中
                    waitReportIds.add(unusedId);
                }
                return unusedId;
            }
        }
    }

    public Set<String> getWaitReportId(){
        Set<String> res = new LinkedHashSet();
        synchronized (waitReportIds){
            Iterator<String> iterator = waitReportIds.iterator();
            while (iterator.hasNext()){
                res.add(iterator.next());
            }
        }
        return res;
    }
    /**
     * 取得 全局唯一id 的个数
     * @return 全局唯一id的个数
     */
    public int getUnusedIdSize() {
        synchronized (unusedIds){
            return unusedIds == null ? 0 : unusedIds.size();
        }
    }
    /**
     * 取得所有的未使用的全局唯一id
     * @return 全局唯一id
     */
    public Set<String> getAllUnusedId(){
        Set<String> res = new LinkedHashSet();
        synchronized (unusedIds){
            Iterator<String> unusedIdIterator = unusedIds.iterator();
            while (unusedIdIterator.hasNext()){
                res.add(unusedIdIterator.next());
            }
        }
        return res;
    }
    /**
     * 获取下一个全局唯一id
     * @return 全局唯一id
     */
    public String getNextUnusedId(){
        synchronized (unusedIds){
            // 没有初始化
            if (unusedIds != null && unusedIds.size()>0){
                Iterator<String> unusedIdIterator = unusedIds.iterator();
                if (unusedIdIterator != null && unusedIdIterator.hasNext()) {
                    final String unusedId = unusedIdIterator.next();
                    unusedIdIterator.remove();
                    return unusedId;
                }
            }
        }
        return null;
    }

    public String getName() {
        return name;
    }

    /**
     *
     * @param ids
     */
    public void addUnusedIds(Set<String> ids){
        if (ids == null){
            return;
        }
        synchronized (unusedIds){
            Iterator<String> unusedIdIterator = ids.iterator();
            while (unusedIdIterator.hasNext()){
                unusedIds.add(unusedIdIterator.next());
            }
        }
    }
    public interface Manage {
        void register(GlobalUniqueIdService service);
    }
}
