package buct.huanxinchat.Items;

/**
 * Created by tian on 2018/7/8.
 */

public class ContractItem {

    private String userId;
    private String userNickName;

    public ContractItem(String userId) {
        this.userId = userId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserNickName() {
        return userNickName;
    }

    public void setUserNickName(String userNickName) {
        this.userNickName = userNickName;
    }
}
