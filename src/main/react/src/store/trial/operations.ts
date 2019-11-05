import {Action} from "redux";
import {ThunkAction} from "redux-thunk";
import {Intensity, Scale} from "../../pages/Scale";
import {server} from "../../utils/server";
import {AppState} from "../index";
import {receivedTrialResponse} from "./actions";

export const fetchTrial = (trialName: string): ThunkAction<void, AppState, null, Action<string>> =>
    async (dispatch) =>
        fetch(server.trial(trialName))
            .then((result) => result.json())
            .then((json) => dispatch(receivedTrialResponse(json)));

export const selectPreviousObservation = (trialName: string): ThunkAction<void, AppState, null, Action<string>> =>
    async (dispatch) =>
        fetch(server.selectPreviousObservation(trialName), {method: "POST", credentials: "same-origin"})
            .then((result) => result.json())
            .then((json) => dispatch(receivedTrialResponse(json)));

export const selectNextObservation = (trialName: string): ThunkAction<void, AppState, null, Action<string>> =>
    async (dispatch) =>
        fetch(server.selectNextObservation(trialName), {method: "POST", credentials: "same-origin"})
            .then((result) => result.json())
            .then((json) => dispatch(receivedTrialResponse(json)));

export const logEvent = (trialName: string, scale: Scale, intensity: Intensity): ThunkAction<void, AppState, null, Action<string>> =>
    async (dispatch) =>
        fetch(server.trial(trialName) + "/event",
            {
                    method: "POST",
                    credentials: "same-origin",
                    body: JSON.stringify({
                        scale: scale.name,
                        intensity: intensity.number,
                    }),
                })
            .then((result) => result.json())
            .then((json) => dispatch(receivedTrialResponse(json)));
