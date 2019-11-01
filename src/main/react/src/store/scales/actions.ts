import {Scales} from "../../pages/Scale";
import {RECEIVED_SCALES} from "./types";

export const receivedScalesResponse = (scales: Scales) => ({
    type: RECEIVED_SCALES,
    scales,
});
