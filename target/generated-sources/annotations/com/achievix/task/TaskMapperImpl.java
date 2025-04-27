package com.achievix.task;

import com.achievix.goal.Goal;
import com.achievix.task.dto.TaskDTO;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-05-01T20:02:08+0200",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 22.0.2 (Amazon.com Inc.)"
)
@Component
public class TaskMapperImpl implements TaskMapper {

    @Override
    public TaskDTO toDTO(Task task) {
        if ( task == null ) {
            return null;
        }

        TaskDTO taskDTO = new TaskDTO();

        taskDTO.setId( task.getId() );
        taskDTO.setGoalId( taskGoalId( task ) );
        taskDTO.setTitle( task.getTitle() );
        taskDTO.setCompleted( task.getCompleted() );
        taskDTO.setCompletedAt( task.getCompletedAt() );
        taskDTO.setDeadline( task.getDeadline() );
        taskDTO.setCreatedAt( task.getCreatedAt() );

        return taskDTO;
    }

    @Override
    public Task toEntity(TaskDTO taskDTO) {
        if ( taskDTO == null ) {
            return null;
        }

        Task task = new Task();

        task.setTitle( taskDTO.getTitle() );
        task.setDeadline( taskDTO.getDeadline() );

        return task;
    }

    private Long taskGoalId(Task task) {
        if ( task == null ) {
            return null;
        }
        Goal goal = task.getGoal();
        if ( goal == null ) {
            return null;
        }
        Long id = goal.getId();
        if ( id == null ) {
            return null;
        }
        return id;
    }
}
