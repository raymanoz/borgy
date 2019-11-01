import {Scale, Scales} from "../../pages/Scale";

export interface NewTrialState {
    trialName: string;
    scales: Scales;
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

export type NewTrialActionTypes = IncludeInTrialAction | ExcludeFromTrialAction | UpdateTrialNameAction;
