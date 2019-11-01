import {Trials} from "../../pages/Scale";
import {RECEIVED_ACTIVE_TRIALS} from "./types";

export const receivedActiveTrials = (trials: Trials) => ({
   type: RECEIVED_ACTIVE_TRIALS,
   trials,
});
