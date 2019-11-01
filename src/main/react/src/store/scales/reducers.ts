import {RECEIVED_SCALES, ScalesActionTypes, ScalesState} from "./types";

const initialState: ScalesState = [];

export const scalesReducer = (state = initialState, action: ScalesActionTypes): ScalesState =>
    action.type === RECEIVED_SCALES ? action.scales : state;
