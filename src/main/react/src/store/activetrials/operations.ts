import {Action} from "redux";
import {ThunkAction} from "redux-thunk";
import {server} from "../../utils/server";
import {AppState} from "../index";
import {receivedActiveTrials} from "./actions";

export const refreshTrials = (): ThunkAction<void, AppState, null, Action<string>> =>
    async (dispatch) =>
        fetch(server.trials)
            .then((result) => result.json())
            .then((json) => dispatch(receivedActiveTrials(json)));
