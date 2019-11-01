import {ActiveTrialsActions, ActiveTrialsState, RECEIVED_ACTIVE_TRIALS} from "./types";

const initialState: ActiveTrialsState = [];

export const activeTrialsReducer = (state = initialState, action: ActiveTrialsActions): ActiveTrialsState =>
    action.type === RECEIVED_ACTIVE_TRIALS ? action.trials : state;
