import {RECEIVED_TRIAL, Trial} from "./types";

export const receivedTrialResponse = (trial: Trial) => ({
    type: RECEIVED_TRIAL,
    trial,
});
