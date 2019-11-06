import {Scale, Scales} from "../../pages/Scale";

export interface NewTrialState {
    trialName: string;
    scales: Scales;
}

export interface StartTrial {
    name: string;
    scales: string[];
}

export const INCLUDE_IN_TRIAL = "INCLUDE_IN_TRIAL";

export interface IncludeInTrialAction {
    type: string;
    scale: Scale;
}

export const EXCLUDE_FROM_TRIAL = "EXCLUDE_FROM_TRIAL";

export interface ExcludeFromTrialAction {
    type: string;
    scale: Scale;
}

export const UPDATE_TRIAL_NAME = "UPDATE_TRIAL_NAME";

export interface UpdateTrialNameAction {
    type: string;
    name: string;
}

export const CLEAR_NEW_TRIAL = "CLEAR_NEW_TRIAL";

export interface ClearNewTrialAction {
    type: string;
}

export type NewTrialActionTypes = IncludeInTrialAction | ExcludeFromTrialAction | UpdateTrialNameAction | ClearNewTrialAction;
