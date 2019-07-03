package top.easyboot.springboot.utils.core;

import top.easyboot.springboot.utils.interfaces.core.IPortUniqueId;

public class PortUniqueId implements IPortUniqueId {
    /**
     * connectionId 记录当前到达的排序
     */
    private Integer id;
    /**
     * connectionId 记录当前到达的排序 最大值
     */
    private final int idMax;
    /**
     * 批次
     */
    private Integer batch;
    /**
     * 批次最大值
     */
    private final int batchMax;

    /**
     * 记录批次的最大长度
     */
    private final int batchMaxLen;
    /**
     * 记录最大长度
     */
    private final int idMaxLen;
    /**
     * 记录第一个批次id
     */
    private final String fisrtUniqueId;

    public PortUniqueId(){
        this(0, 65535, 0, 255);
    }
    protected PortUniqueId(int id, int idMax, int batch, int batchMax){
        this.id = id;
        this.idMax = idMax;
        this.idMaxLen = Integer.toHexString(idMax).length();
        this.batch = batch;
        this.batchMax = batchMax;
        this.batchMaxLen = Integer.toHexString(batchMax).length();
        this.fisrtUniqueId = getNextUniqueId(batch, id);
    }

    @Override
    public String getFisrtUniqueId() {
        return fisrtUniqueId;
    }

    @Override
    public String getNextUniqueId() {
        final int nowId;
        final int nowBatch;
        synchronized(id){
            synchronized(batch){
                if (++id>=idMax){
                    id = 0;
                    if (++batch>=batchMax){
                        batch = 0;
                    }
                }
                nowBatch = batch;
                nowId = id;
            }
        }
        return getNextUniqueId(nowBatch, nowId);
    }

    protected String getNextUniqueId(int batch, int id) {
        return StringUtil.getFilling(Integer.toHexString(batch), batchMaxLen, "0") + StringUtil.getFilling(Integer.toHexString(id), idMaxLen, "0");
    }

    protected int getId() {
        return id;
    }

    protected void setId(int id) {
        this.id = id;
    }

    protected int getIdMax() {
        return idMax;
    }

    protected int getBatch() {
        return batch;
    }

    protected void setBatch(int batch) {
        this.batch = batch;
    }

    protected int getBatchMax() {
        return batchMax;
    }

}
