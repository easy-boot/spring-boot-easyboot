package top.easyboot.springboot.utils.entity.core;

import java.util.LinkedHashSet;
import java.util.Set;

public class GlobalUniqueId {
    /**
     * 数据库名字
     */
    private String name;
    /**
     * 可以使用的规则
     */
    private LinkedHashSet<Interval> use;
    /**
     * 排除规则
     */
    private Set<Interval> exclude;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LinkedHashSet<Interval> getUse() {
        return use;
    }

    public void setUse(LinkedHashSet<Interval> use) {
        this.use = use;
    }

    public Set<Interval> getExclude() {
        return exclude;
    }

    public void setExclude(Set<Interval> exclude) {
        this.exclude = exclude;
    }

    public static class Interval{
        private String start;
        private String end;

        public String getStart() {
            return start;
        }

        public void setStart(String start) {
            this.start = start;
        }

        public String getEnd() {
            return end;
        }

        public void setEnd(String end) {
            this.end = end;
        }
    }
}
