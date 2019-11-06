import {History} from "history";
import {Action, Dispatch} from "redux";
import {ThunkAction} from "redux-thunk";
import {Scale} from "../../pages/Scale";
import {server} from "../../utils/server";
import {AppState} from "../index";
import {CLEAR_NEW_TRIAL, EXCLUDE_FROM_TRIAL, INCLUDE_IN_TRIAL, StartTrial, UPDATE_TRIAL_NAME} from "./types";

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

export const startTrial = (newTrial: StartTrial, history: History): ThunkAction<void, AppState, null, Action<string>> =>
    async (dispatch) =>
        fetch(server.trials, {method: "POST", body: JSON.stringify(newTrial)})
        // TODO : Handle failure response
            .then((result) => result.json())
            .then((json) => history.push(json.url))
            .then(() => dispatch({type: CLEAR_NEW_TRIAL}));
