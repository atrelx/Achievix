package com.achievix.goal;

import com.achievix.goal.dto.CreateGoalDTO;
import com.achievix.goal.dto.GoalDTO;
import com.achievix.goal.dto.GoalDetailsDTO;
import com.achievix.task.Task;
import com.achievix.task.dto.TaskDTO;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-05-09T19:29:40+0200",
    comments = "version: 1.6.0, compiler: javac, environment: Java 22.0.2 (Amazon.com Inc.)"
)
@Component
public class GoalMapperImpl implements GoalMapper {

    @Override
    public GoalDTO toDTO(Goal goal) {
        if ( goal == null ) {
            return null;
        }

        GoalDTO goalDTO = new GoalDTO();

        goalDTO.setId( goal.getId() );
        goalDTO.setTitle( goal.getTitle() );
        goalDTO.setTargetValue( goal.getTargetValue() );
        goalDTO.setCurrentValue( goal.getCurrentValue() );
        goalDTO.setDeadline( goal.getDeadline() );
        goalDTO.setCreatedAt( goal.getCreatedAt() );
        goalDTO.setUpdatedAt( goal.getUpdatedAt() );

        return goalDTO;
    }

    @Override
    public Goal toEntity(GoalDTO goalDTO) {
        if ( goalDTO == null ) {
            return null;
        }

        Goal goal = new Goal();

        goal.setTitle( goalDTO.getTitle() );
        goal.setTargetValue( goalDTO.getTargetValue() );
        goal.setDeadline( goalDTO.getDeadline() );

        return goal;
    }

    @Override
    public Goal toEntity(CreateGoalDTO createGoalDTO) {
        if ( createGoalDTO == null ) {
            return null;
        }

        Goal goal = new Goal();

        goal.setTitle( createGoalDTO.getTitle() );
        goal.setDeadline( createGoalDTO.getDeadline() );

        return goal;
    }

    @Override
    public GoalDetailsDTO toDetailsDTO(Goal goal) {
        if ( goal == null ) {
            return null;
        }

        GoalDetailsDTO goalDetailsDTO = new GoalDetailsDTO();

        goalDetailsDTO.setId( goal.getId() );
        goalDetailsDTO.setTitle( goal.getTitle() );
        goalDetailsDTO.setTargetValue( goal.getTargetValue() );
        goalDetailsDTO.setCurrentValue( goal.getCurrentValue() );
        goalDetailsDTO.setDeadline( goal.getDeadline() );
        goalDetailsDTO.setCreatedAt( goal.getCreatedAt() );
        goalDetailsDTO.setUpdatedAt( goal.getUpdatedAt() );
        goalDetailsDTO.setTasks( taskListToTaskDTOList( goal.getTasks() ) );

        return goalDetailsDTO;
    }

    protected TaskDTO taskToTaskDTO(Task task) {
        if ( task == null ) {
            return null;
        }

        TaskDTO taskDTO = new TaskDTO();

        taskDTO.setId( task.getId() );
        taskDTO.setTitle( task.getTitle() );
        taskDTO.setCompleted( task.getCompleted() );
        taskDTO.setCompletedAt( task.getCompletedAt() );
        taskDTO.setDeadline( task.getDeadline() );
        taskDTO.setCreatedAt( task.getCreatedAt() );

        return taskDTO;
    }

    protected List<TaskDTO> taskListToTaskDTOList(List<Task> list) {
        if ( list == null ) {
            return null;
        }

        List<TaskDTO> list1 = new ArrayList<TaskDTO>( list.size() );
        for ( Task task : list ) {
            list1.add( taskToTaskDTO( task ) );
        }

        return list1;
    }
}
