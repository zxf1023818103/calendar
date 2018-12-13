package chaoxinghelper.apiclient.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.ToString;

@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
public class LoginResult {
    public ChaoxingUserInfo msg;
    public int result;
    public String errorMsg;

    @ToString
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ChaoxingUserInfo {
        public static final int ACCOUNT_TYPE_OPAC = 1;
        public static final int EMAIL = 1;
        public static final int FEMALE = 0;
        public static final int HIDDEN_INFO = 1;
        public static final int MALE = 1;
        public static final int PHONE = 2;
        public static final int SECRET = -1;
        public static final int SHOW_INFO = 0;
        public static final int STATE_LOGIN = 1;
        public static final int STATE_LOGING = 2;
        public static final int STATE_LOGOUT = 0;
        public static final int TYPE_CLOSE = 1;
        public static final int TYPE_OPEN = 2;
        public int fid;
        public int rosterrights;
        public int boundaccount;
        public int loginId;
        public String invitecode;
        public String pic;
        public String source;
        public int type;
        public String ranknum;
        public int isCertify;
        public String uname;
        public int copyRight;
        public String schoolname;
        public String phone;
        public boolean bindFanya;
        public String updateWay;
        public String name;
        public String fullpinyin;
        public int status;
        public String roleid;
        public int industry;
        public String nick;
        public int uid;
        public String acttime2;
        public String dxfid;
        public int puid;
        public int rights;
        public int needIntruction;
        public boolean bindOpac;
        public String ppfid;
        public ChaoxingAccountInfo accountInfo;
        public String simplepinyin;
        public int sex;
        public int isNewUser;
        public int maintype;

        @ToString
        @JsonIgnoreProperties(ignoreUnknown = true)
        public static class ChaoxingAccountInfo {

            public ChaoxingFangyaAccountInfo cxFanya;

            public ChaoxingOpacAccountInfo cxOpac;

            public ChaoxingImAccountInfo imAccount;

            @ToString
            @JsonIgnoreProperties(ignoreUnknown = true)
            public static class ChaoxingFangyaAccountInfo {
                public String boundUrl;
                public int copyRight;
                public int cxid;
                public int dxfid;
                public String email;
                public int industry;
                public int isCertify;
                public int loginId;
                public String loginUrl;
                public String nickname;
                public String openid1;
                public String openid2;
                public String openid3;
                public String openid4;
                public String openid5;
                public String phone;
                public String realname;
                public boolean result;
                public String roleid;
                public int schoolid;
                public String schoolname;
                public int sex;
                public int status;
                public long time;
                public String tippwd;
                public String tiptitle;
                public String tipuname;
                public int uid;
                public String uname;
            }

            @ToString
            @JsonIgnoreProperties(ignoreUnknown = true)
            public static class ChaoxingOpacAccountInfo {
                public String boundUrl;
                public int loginId;
                public String loginUrl;
                public String tippwd;
                public String tiptitle;
                public String tipuname;
            }

            @ToString
            @JsonIgnoreProperties(ignoreUnknown = true)
            public static class ChaoxingImAccountInfo {
                public int activated;
                public long created;
                public long modified;
                public String password;
                public String type;
                public int uid;
                public String username;
                public String uuid;
            }
        }
    }
}
