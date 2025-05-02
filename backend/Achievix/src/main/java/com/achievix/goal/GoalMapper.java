package com.achievix.goal;

import com.achievix.goal.dto.CreateGoalDTO;
import com.achievix.goal.dto.GoalDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface GoalMapper {

    @Mapping(target = "id", source = "id")
    @Mapping(target = "title", source = "title")
    @Mapping(target = "targetValue", source = "targetValue")
    @Mapping(target = "currentValue", source = "currentValue")
    @Mapping(target = "deadline", source = "deadline")
    @Mapping(target = "createdAt", source = "createdAt")
    @Mapping(target = "updatedAt", source = "updatedAt")
    GoalDTO toDTO(Goal goal);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "title", source = "title")
    @Mapping(target = "targetValue", source = "targetValue")
    @Mapping(target = "currentValue", ignore = true)
    @Mapping(target = "deadline", source = "deadline")
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Goal toEntity(GoalDTO goalDTO);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "title", source = "createGoalDTO.title")
    @Mapping(target = "targetValue", source = "createGoalDTO.targetValue")
    @Mapping(target = "currentValue", ignore = true)
    @Mapping(target = "deadline", source = "createGoalDTO.deadline")
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Goal toEntity(CreateGoalDTO createGoalDTO);
}
