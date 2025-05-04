export interface AuthRequest {
  email: string
  password: string
}

export interface GoalDTO {
  id: number
  title: string
  targetValue: number
  currentValue: number
  deadline: string | null
  createdAt: string
  updatedAt: string | null
}

export interface GoalDetailsDTO {
  id: number
  title: string
  targetValue: number
  currentValue: number
  deadline: string | null
  createdAt: string
  updatedAt: string | null
  tasks: TaskDTO[]
}

export interface GoalCreateDTO {
    title: string
    deadline: string
}

export interface DashboardDTO {
  completedGoals: number
  completedTasks: number
  activeGoals: number
  activeTasks: number
  goalsCompletedByPeriod: { [key: string]: number }
  tasksCompletedByPeriod: { [key: string]: number }
  completedGoalsArchive: ArchiveEntry[]
  completedTasksArchive: ArchiveEntry[]
}

export interface ArchiveEntry {
  title: string
  completedAt: string | null
}

export interface TaskDTO {
  id: number
  goalId: number
  title: string
  completed: boolean
  completedAt: string | null
  deadline: string | null
  createdAt: string
}

export interface TaskCreateDTO {
  title: string
  goalId: number
  deadline: string | null
}

export interface TaskCompletedEventDTO {
  taskId: number
  title: string
  goalId: number
  completedAt: string
}
