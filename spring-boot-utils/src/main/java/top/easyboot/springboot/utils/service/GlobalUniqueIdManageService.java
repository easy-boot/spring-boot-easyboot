package top.easyboot.springboot.utils.service;

import java.util.*;

public class GlobalUniqueIdManageService extends GlobalUniqueIdManageAbstractService<String> {

    public GlobalUniqueIdManageService() {
        super();
    }
    @Override
    protected Set<String> getUnusedIds(String name) {
        return null;
    }

    @Override
    protected void freedUnusedIds(String name, Set<String> ids) {

    }

    @Override
    protected void releaseUnusedIds(String name, Set<String> ids) {

    }
}
