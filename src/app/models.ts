export interface Employee { id: number; name: string; skills: string[]; }
export interface Task { id: number; name: string; start: string; end: string; requiredSkills: string[]; requiredEmployees: number; }
export interface Availability { employeeId: number; start: string; end: string; } // optional use
export interface ScheduleRequest { employees: Employee[]; tasks: Task[]; availabilities?: Availability[]; }
export interface AssignmentDTO { taskId: number; taskName: string; start: string; end: string; requiredSkills: string[]; employeeId: number|null; employeeName: string|null; }
export interface SolveResponse { assignments: AssignmentDTO[]; score: string; unassignedCount: number; }
