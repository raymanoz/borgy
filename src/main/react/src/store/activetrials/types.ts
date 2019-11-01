import {Trials} from "../../pages/Scale";

export type ActiveTrialsState = Trials;

export const RECEIVED_ACTIVE_TRIALS = "RECEIVED_ACTIVE_TRIALS";

interface ReceivedActiveTrialsAction {
    type: typeof RECEIVED_ACTIVE_TRIALS;
    trials: Trials;
}

export type ActiveTrialsActions = ReceivedActiveTrialsAction;
