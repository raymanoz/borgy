import {Dispatch} from "redux";
import {Scale} from "../../pages/Scale";
import {EXCLUDE_FROM_TRIAL, INCLUDE_IN_TRIAL, UPDATE_TRIAL_NAME} from "./types";

export const includeInTrial = (scale: Scale) => (dispatch: Dispatch) =>
        dispatch({
            type: INCLUDE_IN_TRIAL,
            scale,
        });

export const excludeFromTrial = (scale: Scale) => (dispatch: Dispatch) =>
        dispatch({
            type: EXCLUDE_FROM_TRIAL,
            scale,
        });

export const updateTrialName = (name: string) => (dispatch: Dispatch) =>
    dispatch({
        type: UPDATE_TRIAL_NAME,
        name,
    });
