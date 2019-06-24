package top.easyboot.springboot.authorization.entity;

import com.fasterxml.jackson.core.JsonProcessingException;
import top.easyboot.springboot.restfulapi.util.Jackson;

public class AuthorizationData {
    private String card;
    private String key;

    public String getCard() {
        return card;
    }

    public void setCard(String card) {
        this.card = card;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    @Override
    public String toString() {
        try {
            return Jackson.toJson(this);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return "";
        }
    }
}
