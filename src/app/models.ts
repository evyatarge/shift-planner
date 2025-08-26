export interface Employee { id: number; name: string; skills: string[]; active?: boolean; }
export interface Task { id: number; name: string; start: string; end: string; requiredSkills: string[]; requiredEmployees: number; }

export type RestMode = 'HARD' | 'SOFT';
export interface SchedulingSettings { minRestHours: number; restMode: RestMode; }

export interface Availability { employeeId: number; start: string; end: string; } // optional use
export interface ScheduleRequest { employees: Employee[]; tasks: Task[]; availabilities?: Availability[]; }
export interface AssignmentDTO { taskId: number; taskName: string; start: string; end: string; requiredSkills: string[]; employeeId: number|null; employeeName: string|null; }
export interface SolveResponse { assignments: AssignmentDTO[]; score: string; unassignedCount: number; }

// optional explanation of the solution (if requested) for constraint details
export interface Explanation { hardConstraints: Record<string, number>; softConstraints: Record<string, number>; }
export interface ExplainSolveResponse { result: { assignments: AssignmentDTO[]; score: string; unassignedCount: number; }, explanation: Explanation; }
