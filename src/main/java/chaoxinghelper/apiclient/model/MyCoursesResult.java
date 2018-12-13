package chaoxinghelper.apiclient.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.ToString;

import java.util.List;

@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
public class MyCoursesResult {
    public int result;
    public String msg;
    public String errorMsg;
    public List<ChaoxingChannelInfo> channelList;
    public boolean hasMore;

    @ToString
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ChaoxingChannelInfo {
        public int cfid;
        public int norder;
        public String cataName;
        public String cataid;
        public int id;
        public int topsign;
        public int key;
        public ChaoxingChannelContent content;

        @ToString
        @JsonIgnoreProperties(ignoreUnknown = true)
        public static class ChaoxingChannelContent {
            public int studentcount;
            public String chatid;
            public int isFiled;
            public int isthirdaq;
            public boolean isstart;
            public int isretire;
            public String name;
            public ChaoxingCourseInfo course;
            public int information;
            public int roletype;
            public int id;
            public int state;
            public String bbsid;
            public int discuss;

            @ToString
            @JsonIgnoreProperties(ignoreUnknown = true)
            public static class ChaoxingCourseInfo {
                public List<ChaoxingCourseData> data;

                @ToString
                @JsonIgnoreProperties(ignoreUnknown = true)
                public static class ChaoxingCourseData {
                    public String teacherfactor;
                    public String name;
                    public int defaultShowCatalog;
                    public int id;
                    public int appData;
                }
            }
        }
    }
}
