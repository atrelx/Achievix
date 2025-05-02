package com.achievix.goal;

import com.achievix.goal.dto.GoalDTO;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-05-08T21:46:26+0200",
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
}
