package chaoxinghelper.apiclient;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class TaskUpdateResult {

    private long taskId;

    private TaskUpdateType updateType;

}
