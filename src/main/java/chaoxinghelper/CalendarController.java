package chaoxinghelper;

import chaoxinghelper.apiclient.ChaoxingApiClient;
import chaoxinghelper.apiclient.ChaoxingTaskRepository;
import chaoxinghelper.apiclient.cookie.ApiClientCookieRepository;
import chaoxinghelper.apiclient.exception.LoginException;
import chaoxinghelper.calendar.EwsCalendarSyncClient;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import lombok.var;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.validation.constraints.Email;
import java.util.Arrays;

@Slf4j
@Controller
public class CalendarController {

    private final ApiClientCookieRepository cookieRepository;

    private final ChaoxingTaskRepository taskRepository;

    private ChaoxingApiClient apiClient = null;

    private EwsCalendarSyncClient calendarSyncClient = null;

    public CalendarController(@NonNull ApiClientCookieRepository cookieRepository, @NonNull ChaoxingTaskRepository taskRepository) {
        this.cookieRepository = cookieRepository;
        this.taskRepository = taskRepository;
    }

    @RequestMapping(path = "/api/settings", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public @ResponseBody
    String setUser(@RequestPart(name = "username") String username,
                   @RequestPart(name = "password") String password,
                   @RequestPart(name = "email") @Email String emailAddress,
                   @RequestPart(name = "emailPassword") String emailPassword) {
        if (username == null || password == null || emailAddress == null || emailPassword == null)
            return "{ \"result\": -1, \"message\": " + "参数为空" + "}";
        try {
            apiClient = new ChaoxingApiClient(username, password, cookieRepository, taskRepository);
            calendarSyncClient = new EwsCalendarSyncClient(emailAddress, emailPassword);
            apiClient.updateTask();
            var tasks = taskRepository.findByUsernameAndPushed(username, false);
            log.info(Arrays.toString(tasks.toArray()));
            for (var task : tasks) {
                calendarSyncClient.addAppointment(task);
                taskRepository.setPushedByIdAndUsername(username, task.getId(), true);
            }
            return "{ \"result\": 0, \"message\": \"OK\" }";
        } catch (java.net.ConnectException e) {
            return "{ \"result\": -1, \"message\": \"" + "服务器网络错误" + "\" }";
        } catch (LoginException e) {
            return "{ \"result\": -1, \"message\": \"" + e.getLocalizedMessage() + "\" }";
        } catch (Exception e) {
            return "{ \"result\": -1, \"message\": \"" + e.getLocalizedMessage() + "\" }";
        }
    }

}
