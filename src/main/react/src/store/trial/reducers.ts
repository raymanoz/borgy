import {RECEIVED_TRIAL, TrialActionTypes, TrialState} from "./types";

const initialState: TrialState = {name: "", observations: [], selectedObservation: 0};

export const trialReducer = (state = initialState, action: TrialActionTypes): TrialState =>
    action.type === RECEIVED_TRIAL ? action.trial : state;
