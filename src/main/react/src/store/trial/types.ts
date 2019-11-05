import {Scale} from "../../pages/Scale";

export interface Trial {
    name: string;
    observations: Observation[];
    selectedObservation: number;
}

export type TrialState = Trial;

export interface Observation {
    scale: Scale;
    selectedIntensity?: number;
}

export const RECEIVED_TRIAL = "RECEIVED_TRIAL";

interface ReceivedTrialAction {
    type: typeof RECEIVED_TRIAL;
    trial: Trial;
}

export type TrialActionTypes = ReceivedTrialAction;
