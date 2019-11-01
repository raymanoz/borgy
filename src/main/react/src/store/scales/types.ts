import {Scales} from "../../pages/Scale";

export type ScalesState = Scales;

export const RECEIVED_SCALES = "RECEIVED_SCALES";

interface ReceivedScalesAction {
    type: typeof RECEIVED_SCALES;
    scales: Scales;
}

export type ScalesActionTypes = ReceivedScalesAction;
