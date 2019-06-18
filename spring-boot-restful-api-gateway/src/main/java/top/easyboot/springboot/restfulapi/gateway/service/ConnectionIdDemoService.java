package top.easyboot.springboot.restfulapi.gateway.service;

import top.easyboot.springboot.restfulapi.gateway.interfaces.service.IConnectionIdService;
import top.easyboot.springboot.restfulapi.gateway.interfaces.service.ISessionService;
import top.easyboot.springboot.restfulapi.gateway.property.RestfulApiGatewayProperties;

import java.util.HashMap;
import java.util.HashSet;

/**
 * 注意，本类，仅仅是提供一个存储方式的参考，在实际生产环境，需要考虑实际的存储数据库
 */
public class ConnectionIdDemoService extends ConnectionIdAbstractService implements IConnectionIdService {

    private HashSet<String> set = new HashSet();
    private HashMap<String, HashSet<String>> utcMap = new HashMap();
    private HashMap<String, String> cTuMap = new HashMap();

    public ConnectionIdDemoService(RestfulApiGatewayProperties.WebSocket webSocket, ISessionService webSocketSessionService){
        super(webSocket, webSocketSessionService);
    }

    @Override
    public HashSet<String> getConnectionIds() {
        return set;
    }

    @Override
    public HashSet<String> getConnectionIds(String uid) {
        HashSet userCid = utcMap.get(uid);
        return userCid == null ? userCid : new HashSet<>();
    }


    @Override
    public void add(String connectionId) {
        System.out.println("add:[connectionId:"+connectionId+"]");
        set.add(connectionId);
    }

    @Override
    public void remove(String connectionId) {
        System.out.println("remove:[connectionId:"+connectionId+"]");
        set.remove(connectionId);
    }


    @Override
    public String getUid(String connectionId){
        String uid = cTuMap.get(connectionId);
        if (uid == null){

        }
        return uid;
    }

    @Override
    public void bind(String connectionId, String uid){
        cTuMap.put(connectionId, uid);
        HashSet cids = getConnectionIds(uid);
        if (cids == null){
            cids = new HashSet();
        }
        cids.add(connectionId);
        System.out.println("bind:[connectionId:"+connectionId+"][uid:"+uid+"]");
    }

    @Override
    public void unbind(String connectionId, String uid){
        cTuMap.remove(connectionId);
        HashSet cids = getConnectionIds(uid);
        if (cids != null){
            cids.remove(connectionId);
        }
        System.out.println("unbind:[connectionId:"+connectionId+"][uid:"+uid+"]");
    }
}
