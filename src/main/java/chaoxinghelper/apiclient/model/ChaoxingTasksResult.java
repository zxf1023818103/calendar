package chaoxinghelper.apiclient.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.ToString;

import java.util.List;

@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
public class ChaoxingTasksResult {
    public List<ChaoxingTaskInfo> activeList;
    public String msg;
    public String errorMsg;
    public int count;
    public int status;

    @ToString
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ChaoxingTaskInfo {
        public static final int UNFINISHED = 1;
        public static final int FINISHED = 2;

        public String nameTwo;
        public String teachingPlan;
        public String groupId;
        public int isLook;
        public int releaseNum;
        public String url;
        public String picUrl;
        public String classId;
        public int attendNum;
        public String activeType;
        public String nameOne;
        public Long startTime;
        public String id;
        public String nameFour;
        public int status;
    }
}
