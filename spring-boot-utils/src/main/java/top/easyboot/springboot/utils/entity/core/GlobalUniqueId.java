package top.easyboot.springboot.utils.entity.core;

import java.util.LinkedHashSet;
import java.util.Set;

public class GlobalUniqueId {
    /**
     * 数据库collection、table名字
     */
    protected String name;
    /**
     * 可以使用的规则
     */
    protected LinkedHashSet<Interval> unused;
    /**
     * 已经使用规则
     */
    protected Set<Interval> used;
    /**
     * 排除规则
     */
    protected Set<Interval> exclude;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LinkedHashSet<Interval> getUnused() {
        return unused;
    }

    public void setUnused(LinkedHashSet<Interval> unused) {
        this.unused = unused;
    }

    public Set<Interval> getUsed() {
        return used;
    }

    public void setUsed(Set<Interval> used) {
        this.used = used;
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
