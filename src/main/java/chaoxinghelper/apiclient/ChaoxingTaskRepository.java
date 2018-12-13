package chaoxinghelper.apiclient;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

public interface ChaoxingTaskRepository extends JpaRepository<ChaoxingTask, Long> {

    @Transactional
    List<ChaoxingTask> findByUsernameAndId(String username, Long id);

    @Transactional
    List<ChaoxingTask> findByUsername(String username);

    @Transactional
    List<ChaoxingTask> findByUsernameAndPushed(String username, boolean pushed);

    @Transactional
    @Modifying
    int deleteByUsernameAndId(String username, Long id);

    @Modifying
    @Transactional
    @Query(nativeQuery = true,
            value = "MERGE INTO CHAOXING_TASKS (ID, USERNAME, COURSE, NAME, DETAIL, START_DATE_TIME, DUE_DATE_TIME, PUSHED)" +
                    "  KEY (ID, USERNAME)" +
                    "  VALUES ( :id, :username, :course, :name, :detail, :startDateTime, :dueDateTime, :pushed );")
    int mergeByIdAndUsername(@Param("id") Long id,
                             @Param("username") String username,
                             @Param("course") String course,
                             @Param("name") String name,
                             @Param("detail") String detail,
                             @Param("startDateTime") Date startDateTime,
                             @Param("dueDateTime") Date dueDateTime,
                             @Param("pushed") Boolean pushed);

    @Modifying
    @Transactional
    @Query(nativeQuery = true, value = "UPDATE CHAOXING_TASKS SET PUSHED = :pushed WHERE ID = :id AND USERNAME = :username")
    int setPushedByIdAndUsername(@Param("username") String username, @Param("id") Long id, @Param("pushed") boolean pushed);

}
