package chaoxinghelper.apiclient;

import chaoxinghelper.apiclient.cookie.ApiClientCookieJar;
import chaoxinghelper.apiclient.cookie.ApiClientCookieRepository;
import chaoxinghelper.apiclient.exception.CoursesGetException;
import chaoxinghelper.apiclient.exception.LoginException;
import chaoxinghelper.apiclient.exception.NoResponseBodyException;
import chaoxinghelper.apiclient.exception.TaskGetException;
import chaoxinghelper.apiclient.model.ChaoxingTasksResult;
import chaoxinghelper.apiclient.model.LoginResult;
import chaoxinghelper.apiclient.model.MyCoursesResult;
import chaoxinghelper.apiclient.model.PostAccountInfoResult;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import lombok.var;
import okhttp3.*;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.time.Instant;
import java.time.ZoneId;
import java.util.*;
import java.util.regex.Pattern;

@Slf4j
public final class ChaoxingApiClient {

    private static final ZoneId SERVER_ZONEID = ZoneId.of("GMT+8");

    private final String username;

    private final ApiClientCookieRepository cookieRepository;

    private final ChaoxingTaskRepository taskRepository;

    @Getter
    private LoginResult.ChaoxingUserInfo userInfo;

    private OkHttpClient client = new OkHttpClient.Builder()
            .addInterceptor(new ChaoxingApiRequestInterceptor())
            .build();

    private String loginUrl = null;

    private void postAccountInfo(String username, String password) throws NoResponseBodyException, LoginException {
        final String POST_ACCOUNT_INFO_URL = "https://passport2-api.chaoxing.com/v11/loginregister";
        final String USERNAME_PART_NAME = "uname";
        final String PASSWORD_PART_NAME = "code";
        final String LOGIN_TYPE_PART_NAME = "loginType";
        final String ROLE_SELECT_PART_NAME = "roleSelect";
        final Integer LOGIN_TYPE = 1;
        final Boolean ROLE_SELECT = true;

        try {
            var body = new MultipartBody.Builder()
                    .addFormDataPart(USERNAME_PART_NAME, username)
                    .addFormDataPart(PASSWORD_PART_NAME, password)
                    .addFormDataPart(LOGIN_TYPE_PART_NAME, LOGIN_TYPE.toString())
                    .addFormDataPart(ROLE_SELECT_PART_NAME, ROLE_SELECT.toString())
                    .build();
            var request = new Request.Builder().url(POST_ACCOUNT_INFO_URL).post(body).build();
            var response = client.newCall(request).execute();
            if (response.body() == null) {
                throw new NoResponseBodyException();
            }
            String bodyString = response.body().string();
            var mapper = new ObjectMapper();
            var result = mapper.readValue(bodyString, PostAccountInfoResult.class);
            if (result.status) {
                log.info(result.mes);
                loginUrl = result.url;
            } else {
                throw new LoginException(result.mes);
            }
            var cookieJar = new ApiClientCookieJar(username, cookieRepository);
            var cookies = Cookie.parseAll(request.url(), response.headers());
            cookieJar.saveFromResponse(request.url(), cookies);
            client = new OkHttpClient.Builder()
                    .addInterceptor(new ChaoxingApiRequestInterceptor())
                    .cookieJar(new ApiClientCookieJar(username, cookieRepository))
                    .build();
        } catch (JsonParseException e) {
            log.warn("Parse JSON failed", e);
        } catch (JsonMappingException e) {
            log.warn("Map JSON failed", e);
        } catch (IOException e) {
            throw new LoginException(e.getLocalizedMessage());
        }
    }

    private void doLogin() throws NoResponseBodyException, LoginException {
        try {
            assert loginUrl != null;
            Request request = new Request.Builder()
                    .url(loginUrl)
                    .get()
                    .build();
            Response response = client.newCall(request).execute();
            if (response.body() == null) {
                throw new NoResponseBodyException();
            }
            var bodyString = response.body().string();
            LoginResult result = null;
            result = new ObjectMapper().readValue(bodyString, LoginResult.class);
            assert result != null;
            if (result.result != 0) {
                userInfo = result.msg;
            } else {
                throw new LoginException(result.errorMsg);
            }

        } catch (IOException e) {
            throw new LoginException(e.getLocalizedMessage());
        }
    }

    private void login(@NonNull String username, @NonNull String password) throws LoginException, NoResponseBodyException {
        if (cookieRepository.existsByUsername(username)) {
            client = new OkHttpClient.Builder()
                    .addInterceptor(new ChaoxingApiRequestInterceptor())
                    .cookieJar(new ApiClientCookieJar(username, cookieRepository))
                    .build();
            try {
                doLogin();
                return;
            } catch (Exception e) {
                // TODO: 记录日志
                var deleted = cookieRepository.deleteByUsername(username);
                cookieRepository.flush();
            }
        }
        try {
            postAccountInfo(username, password);
        } catch (Exception e) {
            var deleted = cookieRepository.deleteByUsername(username);
            cookieRepository.flush();
            throw new LoginException(e.getMessage());
        }
        try {
            doLogin();
        } catch (Exception e) {
            var deleted = cookieRepository.deleteByUsername(username);
            cookieRepository.flush();
            throw e;
        }
    }

    private List<MyCoursesResult.ChaoxingChannelInfo> getAllChannelInfo() throws NoResponseBodyException, CoursesGetException {
        try {
            final String GET_COURSES_URL = "https://mooc1-api.chaoxing.com/mycourse?rss=1";
            Request request = new Request.Builder()
                    .url(GET_COURSES_URL)
                    .get()
                    .build();
            Response response = client.newCall(request).execute();
            if (response.body() == null) {
                throw new NoResponseBodyException();
            }
            var bodyString = response.body().string();
            MyCoursesResult result = new ObjectMapper().readValue(bodyString, MyCoursesResult.class);
            if (result.result != 0) {
                return result.channelList;
            } else {
                throw new CoursesGetException(result.errorMsg);
            }
        } catch (IOException e) {
            throw new CoursesGetException(e.getLocalizedMessage());
        }
    }

    private List<ChaoxingTasksResult.ChaoxingTaskInfo> getAllTaskInfo(@NonNull Integer courseId, @NonNull Integer classId) throws NoResponseBodyException, TaskGetException {
        try {
            final String GET_TASKS_URL = "https://mobilelearn.chaoxing.com/ppt/activeAPI/taskactivelist";
            final String COURSE_ID_QUERY_KEY = "courseId";
            final String CLASS_ID_QUERY_KEY = "classId";
            final String USER_ID_QUERY_KEY = "uid";
            HttpUrl url = HttpUrl.parse(GET_TASKS_URL);
            assert url != null;
            url = url.newBuilder().addQueryParameter(COURSE_ID_QUERY_KEY, courseId.toString())
                    .addQueryParameter(CLASS_ID_QUERY_KEY, classId.toString())
                    .addQueryParameter(USER_ID_QUERY_KEY, Integer.toString(userInfo.puid))
                    .build();
            Request request = new Request.Builder().url(url).get().build();
            Response response = client.newCall(request).execute();
            if (response.body() == null) {
                throw new NoResponseBodyException();
            }
            var bodyString = response.body().string();
            ChaoxingTasksResult result = new ObjectMapper().readValue(bodyString, ChaoxingTasksResult.class);
            return result.activeList;
        } catch (IOException e) {
            throw new TaskGetException(e.getLocalizedMessage());
        }
    }

    private ChaoxingTask getTask(@NonNull ChaoxingTasksResult.ChaoxingTaskInfo taskInfo, @NonNull String course, long taskId) throws NoResponseBodyException, TaskGetException {
        try {
            Request request = new Request.Builder().url(taskInfo.url).get().build();
            Response response = client.newCall(request).execute();
            if (response.body() == null) {
                throw new NoResponseBodyException();
            }
            var bodyString = response.body().string();
            // TODO: 处理解析不到数据的情况
            Document document = Jsoup.parse(bodyString);
            Elements detailElements = document.body().select("div.zwork");
            Elements deadlineElements = detailElements.select("div.tmTimeNew");
            var deadlineString = deadlineElements.select("h4").text();
            var deadlinePattern = Pattern.compile("(\\d+)-(\\d+)\\s*(\\d+):(\\d+)");
            var matcher = deadlinePattern.matcher(bodyString);
            boolean patternMatched = matcher.find();
            if (!patternMatched)
                throw new TaskGetException("Cannot find deadline: " + deadlineString);
            var monthString = matcher.group(1);
            var dayString = matcher.group(2);
            var hourString = matcher.group(3);
            var minuteString = matcher.group(4);
            int month = Integer.parseInt(monthString);
            int day = Integer.parseInt(dayString);
            int hour = Integer.parseInt(hourString);
            int minute = Integer.parseInt(minuteString);
            Date startDateTime = Date.from(Instant.ofEpochMilli(taskInfo.startTime));
            int year = startDateTime.getYear();
            Date dueDateTime = new Date(year, month - 1, day, hour, minute);
            if (startDateTime.compareTo(dueDateTime) > 0)
                dueDateTime.setYear(dueDateTime.getYear() + 1);
            String detail = detailElements.select("p").text();
            return new ChaoxingTask(taskId, username, course, taskInfo.nameOne, detail, startDateTime, dueDateTime);
        } catch (IOException e) {
            throw new TaskGetException(e.getLocalizedMessage());
        }
    }

    public ChaoxingApiClient(@NonNull String username, @NonNull String password, @NonNull ApiClientCookieRepository cookieRepository, @NonNull ChaoxingTaskRepository taskRepository) throws LoginException, NoResponseBodyException {
        this.cookieRepository = cookieRepository;
        this.taskRepository = taskRepository;
        this.username = username;
        login(username, password);
    }

    public List<ChaoxingTask> getAllTasks() throws CoursesGetException, NoResponseBodyException, TaskGetException {
        client = new OkHttpClient.Builder()
                .addInterceptor(new ChaoxingApiRequestInterceptor())
                .cookieJar(new ApiClientCookieJar(username, cookieRepository))
                .build();
        List<ChaoxingTask> tasks = new ArrayList<>();
        var allChannelInfo = getAllChannelInfo();
        for (var channelInfo : allChannelInfo) {
            var allCourseData = channelInfo.content.course.data;
            for (var courseData : allCourseData) {
                var allTaskInfo = getAllTaskInfo(courseData.id, channelInfo.key);
                for (var taskInfo : allTaskInfo) {
                    if (taskInfo.status == ChaoxingTasksResult.ChaoxingTaskInfo.UNFINISHED) {
                        HttpUrl url = HttpUrl.parse(taskInfo.url);
                        if (url != null) {
                            var taskIdString = url.queryParameter("taskrefId");
                            if (taskIdString != null && !taskIdString.isEmpty()) {
                                try {
                                    long taskId = Long.parseLong(taskIdString);
                                    tasks.add(getTask(taskInfo, courseData.name, taskId));
                                } catch (Exception e) {
                                    log.info(e.getLocalizedMessage());
                                }
                            }
                        }
                    }
                }
            }
        }
        return tasks;
    }

    /**
     * 获取哪些任务被添加/删除/更改了，返回它们的 ID
     *
     * @param existingTasks 更新之前的所有任务
     * @param fetchedTasks  从服务器获得的更新之后的所有任务
     * @return 任务更新细节
     */
    private static List<TaskUpdateResult> getTaskUpdateResults(@NonNull List<ChaoxingTask> existingTasks, @NonNull List<ChaoxingTask> fetchedTasks) {
        List<Long> existingTaskIds = new ArrayList<>();
        List<Long> fetchedTaskIds = new ArrayList<>();
        existingTasks.forEach(task -> existingTaskIds.add(task.getId()));
        fetchedTasks.forEach(task -> fetchedTaskIds.add(task.getId()));
        List<Long> newTaskIds = new ArrayList<>(fetchedTaskIds);
        newTaskIds.removeAll(existingTaskIds);
        List<Long> deletedTaskIds = new ArrayList<>(existingTaskIds);
        deletedTaskIds.removeAll(fetchedTaskIds);
        List<ChaoxingTask> updatedTasks = new ArrayList<>(existingTasks);
        updatedTasks.removeAll(existingTasks);
        List<Long> updatedTaskIds = new ArrayList<>();
        updatedTasks.forEach(task -> updatedTaskIds.add(task.getId()));
        updatedTaskIds.removeAll(newTaskIds);
        List<TaskUpdateResult> updateResults = new LinkedList<>();
        newTaskIds.forEach(id -> updateResults.add(new TaskUpdateResult(id, TaskUpdateType.NEW)));
        deletedTaskIds.forEach(id -> updateResults.add(new TaskUpdateResult(id, TaskUpdateType.DELETED)));
        updatedTaskIds.forEach(id -> updateResults.add(new TaskUpdateResult(id, TaskUpdateType.UPDATED)));
        return updateResults;
    }

    public void updateTask() throws TaskGetException, CoursesGetException, NoResponseBodyException {
        var fetchedTasks = getAllTasks();
        Map<Long, ChaoxingTask> fetchedIdTaskMap = new HashMap<>();
        fetchedTasks.forEach(task -> fetchedIdTaskMap.put(task.getId(), task));
        var existingTasks = taskRepository.findByUsername(username);
        var updateResults = getTaskUpdateResults(existingTasks, fetchedTasks);
        for (var result : updateResults) {
            var id = result.getTaskId();
            var task = fetchedIdTaskMap.get(id);
            switch (result.getUpdateType()) {
                case UPDATED:
                    taskRepository.mergeByIdAndUsername(id, username, task.getCourse(), task.getName(), task.getDetail(), task.getStartDateTime(), task.getDueDateTime(), false);
                    break;
                case DELETED:
                    taskRepository.deleteByUsernameAndId(username, id);
                    break;
                case NEW:
                    taskRepository.mergeByIdAndUsername(id, username, task.getCourse(), task.getName(), task.getDetail(), task.getStartDateTime(), task.getDueDateTime(), false);
            }
        }
    }

}
