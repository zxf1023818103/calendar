import chaoxinghelper.apiclient.exception.CoursesGetException;
import chaoxinghelper.apiclient.exception.LoginException;
import chaoxinghelper.apiclient.exception.NoResponseBodyException;
import chaoxinghelper.apiclient.exception.TaskGetException;
import org.junit.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class ChaoxingApiClientTests {


    @Test
    public void testSuccessfulLogin() throws LoginException, NoResponseBodyException, CoursesGetException, TaskGetException {
        final String username = "13155702970";
        final String password = "zxf19990605";
    }

}
