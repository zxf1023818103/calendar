package chaoxinghelper.calendar;

import chaoxinghelper.apiclient.ChaoxingTask;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import lombok.var;
import microsoft.exchange.webservices.data.core.ExchangeService;
import microsoft.exchange.webservices.data.core.enumeration.misc.ExchangeVersion;
import microsoft.exchange.webservices.data.core.enumeration.property.BodyType;
import microsoft.exchange.webservices.data.core.enumeration.property.WellKnownFolderName;
import microsoft.exchange.webservices.data.core.enumeration.service.DeleteMode;
import microsoft.exchange.webservices.data.core.service.item.Appointment;
import microsoft.exchange.webservices.data.core.service.item.Task;
import microsoft.exchange.webservices.data.credential.ExchangeCredentials;
import microsoft.exchange.webservices.data.credential.WebCredentials;
import microsoft.exchange.webservices.data.property.complex.MessageBody;
import microsoft.exchange.webservices.data.property.complex.StringList;
import microsoft.exchange.webservices.data.property.complex.recurrence.pattern.Recurrence;
import microsoft.exchange.webservices.data.search.FolderView;
import microsoft.exchange.webservices.data.search.ItemView;

import java.net.URI;
import java.time.Instant;
import java.util.Date;

@Slf4j
public class EwsCalendarSyncClient {

    private final ExchangeService service = new ExchangeService(ExchangeVersion.Exchange2010_SP2);

    public EwsCalendarSyncClient(@NonNull String emailAddress, @NonNull String password) throws Exception {
        ExchangeCredentials credentials = new WebCredentials(emailAddress, password);
        service.setCredentials(credentials);
        //service.autodiscoverUrl(emailAddress, redirectionUrl -> redirectionUrl.startsWith("https://"));
        service.setUrl(URI.create("https://outlook.office365.com/ews/Exchange.asmx"));
    }

    public void addTask(ChaoxingTask chaoxingTask) throws Exception {
        Task task = new Task(service);
        task.setStartDate(chaoxingTask.getStartDateTime());
        task.setDueDate(chaoxingTask.getDueDateTime());
        MessageBody body = new MessageBody(BodyType.Text, chaoxingTask.getDetail());
        task.setBody(body);
        task.setSubject(chaoxingTask.getCourse() + ' ' + chaoxingTask.getName());
        /// 设置提醒间隔为每天提醒一次
        task.setRecurrence(new Recurrence.DailyPattern(task.getStartDate(), 1));
        var categories = new StringList();
        task.setCategories(categories);
        task.save();
    }

    public void deleteAllTask() throws Exception {
        var folders = service.findFolders(WellKnownFolderName.Tasks, new FolderView(1));
        if (folders != null && !folders.getFolders().isEmpty()) {
            for (var folder : folders.getFolders()) {
                var items = folder.findItems(new ItemView(folder.getTotalCount()));
                for (var item : items.getItems())
                    item.delete(DeleteMode.HardDelete);
            }
        }
    }

    public void addAppointment(ChaoxingTask task) throws Exception {
        Appointment appointment = new Appointment(service);
        appointment.setStart(task.getStartDateTime());
        appointment.setEnd(task.getDueDateTime());
        appointment.setSubject(task.getName());
        MessageBody body = new MessageBody(BodyType.Text, task.getCourse() + System.lineSeparator() + task.getDetail());
        appointment.setBody(body);
        var now = Date.from(Instant.now());
        //appointment.setRecurrence(new Recurrence.DailyPattern(new Date(now.getYear(), now.getMonth(), now.getDay(), 8, 0), 1));
        appointment.save(WellKnownFolderName.Calendar);
    }

    public void deleteAppointment() throws Exception {
        var folders = service.findFolders(WellKnownFolderName.Calendar, new FolderView(10));
        for (var folder : folders.getFolders()) {
            var count = folder.getTotalCount();
            var appointments = folder.findItems(new ItemView(50));
            while (true) {
                for (var appointment : appointments) {
                    appointment.delete(DeleteMode.HardDelete);
                }
                if (appointments.isMoreAvailable()) {
                    appointments = folder.findItems(new ItemView(50, appointments.getNextPageOffset()));
                } else break;
            }
        }
    }

}
