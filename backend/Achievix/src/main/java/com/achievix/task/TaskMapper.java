package com.achievix.task;

import com.achievix.task.dto.TaskDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface TaskMapper {

    @Mapping(target = "id", source = "id")
    @Mapping(target = "goalId", source = "goal.id")
    @Mapping(target = "title", source = "title")
    @Mapping(target = "completed", source = "completed")
    @Mapping(target = "completedAt", source = "completedAt")
    @Mapping(target = "deadline", source = "deadline")
    @Mapping(target = "createdAt", source = "createdAt")
    TaskDTO toDTO(Task task);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "goal", ignore = true)
    @Mapping(target = "title", source = "title")
    @Mapping(target = "completed", ignore = true)
    @Mapping(target = "completedAt", ignore = true)
    @Mapping(target = "deadline", source = "deadline")
    @Mapping(target = "createdAt", ignore = true)
    Task toEntity(TaskDTO taskDTO);
}
